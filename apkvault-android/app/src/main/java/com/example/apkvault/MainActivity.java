package com.example.apkvault;

import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.*;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    private static final int PICK_TREE=10;
    private LinearLayout root,list;
    private TextView folderLabel;
    private Uri folder;
    private ApplicationInfo pendingApp;
    private android.content.SharedPreferences prefs;
    private boolean dark;
    private boolean settingsOpen;
    private int accent;

    @Override public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("settings",MODE_PRIVATE); dark=prefs.getBoolean("dark",false); accent=prefs.getInt("accent",0); restoreFolder(); buildUi();}
    @Override protected void onResume(){super.onResume(); if(list!=null) refreshApps();}
    private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    private int accentColor(){return new int[]{0xff1565c0,0xff00897b,0xff6a1b9a,0xffef6c00}[Math.max(0,Math.min(3,accent))];}
    private TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(dark?Color.WHITE:0xff263238);t.setPadding(dp(10),dp(7),dp(10),dp(7));return t;}
    private GradientDrawable round(int c,int r){GradientDrawable d=new GradientDrawable();d.setColor(c);d.setCornerRadius(dp(r));return d;}
    private void buildUi(){
        settingsOpen=false; folderLabel=null;
        getWindow().setStatusBarColor(accentColor()); getWindow().setNavigationBarColor(dark?0xff121212:0xfff7f9fc);
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);root.setPadding(dp(10),dp(8),dp(10),0);root.setBackgroundColor(dark?0xff121212:0xfff7f9fc);
        LinearLayout toolbar=new LinearLayout(this);toolbar.setGravity(Gravity.CENTER_VERTICAL);toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        TextView title=text("حافظ التطبيقات",24);title.setTextColor(accentColor());title.setTypeface(null,1);toolbar.addView(title,new LinearLayout.LayoutParams(0,dp(56),1));
        Button settings=new Button(this);settings.setText("⚙");settings.setTextSize(23);settings.setTextColor(accentColor());settings.setAllCaps(false);settings.setBackgroundColor(Color.TRANSPARENT);settings.setOnClickListener(v->showSettings());toolbar.addView(settings,new LinearLayout.LayoutParams(dp(58),dp(56)));root.addView(toolbar);
        ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);list.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);scroll.addView(list);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);refreshApps();
    }
    private void refreshApps(){if(list==null)return;list.removeAllViews();PackageManager pm=getPackageManager();ArrayList<ApplicationInfo> now=new ArrayList<>();for(ApplicationInfo a:pm.getInstalledApplications(PackageManager.GET_META_DATA))if(pm.getLaunchIntentForPackage(a.packageName)!=null&&!a.packageName.equals(getPackageName()))now.add(a);Collections.sort(now,(a,b)->pm.getApplicationLabel(a).toString().compareToIgnoreCase(pm.getApplicationLabel(b).toString()));for(ApplicationInfo a:now)addRow(a);}
    private void addRow(ApplicationInfo app){PackageManager pm=getPackageManager();LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(dp(8),dp(5),dp(8),dp(5));row.setBackgroundColor(dark?0xff1e1e1e:0xfff0f5ff);ImageView icon=new ImageView(this);icon.setImageDrawable(pm.getApplicationIcon(app));icon.setPadding(dp(4),dp(4),dp(4),dp(4));row.addView(icon,new LinearLayout.LayoutParams(dp(58),dp(64)));TextView name=text(pm.getApplicationLabel(app).toString(),17);name.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);name.setLayoutParams(new LinearLayout.LayoutParams(0,dp(64),1));row.addView(name);Button save=new Button(this);save.setText("حفظ APK");save.setTextColor(Color.WHITE);save.setTextSize(13);save.setAllCaps(false);save.setBackground(round(accentColor(),9));save.setOnClickListener(v->saveApk(app));row.addView(save,new LinearLayout.LayoutParams(dp(118),dp(52)));list.addView(row);View line=new View(this);line.setBackgroundColor(dark?0xff333333:0xffd8e0ea);list.addView(line,new LinearLayout.LayoutParams(-1,1));}
    private void chooseFolder(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);startActivityForResult(i,PICK_TREE);}
    private void saveApk(ApplicationInfo app){if(folder==null){pendingApp=app;chooseFolder();return;}new CopyTask(app).execute();}
    private class CopyTask extends AsyncTask<Void,Integer,Exception>{private ApplicationInfo app;private AlertDialog dialog;private ProgressBar progress;private TextView status;CopyTask(ApplicationInfo a){app=a;}protected void onPreExecute(){LinearLayout box=new LinearLayout(MainActivity.this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(24),dp(10),dp(24),dp(10));status=text("جارٍ تجهيز النسخة…",14);box.addView(status);progress=new ProgressBar(MainActivity.this,null,android.R.attr.progressBarStyleHorizontal);progress.setMax(100);box.addView(progress,new LinearLayout.LayoutParams(-1,dp(20)));dialog=new AlertDialog.Builder(MainActivity.this).setTitle("حفظ التطبيق").setView(box).setCancelable(false).create();dialog.show();}protected Exception doInBackground(Void...v){try{PackageInfo pi=getPackageManager().getPackageInfo(app.packageName,0);String label=getPackageManager().getApplicationLabel(app).toString().replaceAll("[^\\p{L}\\p{N}._-]","_");String file=label+"-"+pi.versionName+".apk";Uri parent=DocumentsContract.buildDocumentUriUsingTree(folder,DocumentsContract.getTreeDocumentId(folder));Uri out=DocumentsContract.createDocument(getContentResolver(),parent,"application/vnd.android.package-archive",file);if(out==null)throw new IOException("تعذر إنشاء الملف");long total=new File(app.sourceDir).length(),copied=0;try(InputStream in=new FileInputStream(app.sourceDir);OutputStream os=getContentResolver().openOutputStream(out)){byte[] buf=new byte[8192];int n;while((n=in.read(buf))!=-1){os.write(buf,0,n);copied+=n;publishProgress((int)(copied*100/Math.max(1,total)));}}return null;}catch(Exception e){return e;}}protected void onProgressUpdate(Integer...p){progress.setProgress(p[0]);status.setText("تم نسخ "+p[0]+"٪");}protected void onPostExecute(Exception e){if(dialog!=null)dialog.dismiss();Toast.makeText(MainActivity.this,e==null?"تم حفظ التطبيق بنجاح":"فشل الحفظ: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
    @Override protected void onActivityResult(int req,int res,Intent data){super.onActivityResult(req,res,data);if(req!=PICK_TREE||res!=RESULT_OK||data==null)return;Uri u=data.getData();folder=u;try{getContentResolver().takePersistableUriPermission(u,data.getFlags()&(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION));}catch(Exception ignored){}prefs.edit().putString("folder_uri",u.toString()).apply();if(settingsOpen)showSettings();if(pendingApp!=null){ApplicationInfo a=pendingApp;pendingApp=null;new CopyTask(a).execute();}}
    private void restoreFolder(){String saved=prefs.getString("folder_uri",null);if(saved==null)return;try{folder=Uri.parse(saved);getContentResolver().takePersistableUriPermission(folder,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);}catch(Exception e){folder=null;prefs.edit().remove("folder_uri").apply();}}
    private String folderName(){if(folder==null)return "غير محدد";try{Uri d=DocumentsContract.buildDocumentUriUsingTree(folder,DocumentsContract.getTreeDocumentId(folder));Cursor c=getContentResolver().query(d,new String[]{OpenableColumns.DISPLAY_NAME},null,null,null);if(c!=null&&c.moveToFirst()){String n=c.getString(0);c.close();return n;}if(c!=null)c.close();}catch(Exception ignored){}return "تم الاختيار";}
    @Override public void onBackPressed(){if(settingsOpen)buildUi();else super.onBackPressed();}
    private void showSettings(){
        settingsOpen=true; list=null;
        LinearLayout page=new LinearLayout(this); page.setOrientation(LinearLayout.VERTICAL); page.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); page.setPadding(dp(16),dp(8),dp(16),dp(16)); page.setBackgroundColor(dark?0xff121212:0xfff7f9fc);
        getWindow().setStatusBarColor(accentColor()); getWindow().setNavigationBarColor(dark?0xff121212:0xfff7f9fc);
        LinearLayout header=new LinearLayout(this); header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); header.setGravity(Gravity.CENTER_VERTICAL);
        Button back=new Button(this); back.setText("رجوع ←"); back.setContentDescription("الرجوع إلى التطبيقات"); back.setTextColor(accentColor()); back.setBackgroundColor(Color.TRANSPARENT); back.setOnClickListener(v->buildUi()); header.addView(back,new LinearLayout.LayoutParams(dp(100),dp(56)));
        TextView heading=text("الإعدادات",24); heading.setTextColor(accentColor()); header.addView(heading,new LinearLayout.LayoutParams(0,dp(56),1)); page.addView(header);
        folderLabel=text("مجلد الحفظ: "+folderName(),16); page.addView(folderLabel);
        Button choose=new Button(this); choose.setText(folder==null?"اختيار مجلد الحفظ":"تغيير مجلد الحفظ"); choose.setTextColor(Color.WHITE); choose.setBackground(round(accentColor(),8)); choose.setOnClickListener(v->{pendingApp=null;chooseFolder();}); page.addView(choose,new LinearLayout.LayoutParams(-1,dp(48)));
        Switch night=new Switch(this); night.setText("الوضع الليلي"); night.setTextColor(dark?Color.WHITE:0xff263238); night.setPadding(dp(10),dp(20),dp(10),dp(20)); night.setChecked(dark); night.setOnCheckedChangeListener((button,checked)->{dark=checked;prefs.edit().putBoolean("dark",dark).apply();showSettings();}); page.addView(night);
        page.addView(text("لون التطبيق",18));
        String[] names={"أزرق","أخضر","بنفسجي","برتقالي"}; int[] cs={0xff1565c0,0xff00897b,0xff6a1b9a,0xffef6c00};
        for(int i=0;i<cs.length;i++){final int k=i;Button b=new Button(this);b.setText(names[i]+(accent==i?" ✓":""));b.setTextColor(Color.WHITE);b.setBackground(round(cs[i],8));b.setOnClickListener(v->{accent=k;prefs.edit().putInt("accent",k).apply();showSettings();});LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(48));lp.topMargin=dp(10);page.addView(b,lp);}
        setContentView(page);
    }
}
