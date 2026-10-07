package com.example.apkvault;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.*;
import android.provider.DocumentsContract;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;

public class MainActivity extends Activity {
    private static final int PICK_TREE = 10, PICK_APK = 11;
    private LinearLayout list; private TextView folderLabel; private Uri folder;
    private android.content.SharedPreferences prefs;
    private final ArrayList<ApplicationInfo> apps = new ArrayList<>();

    @Override public void onCreate(Bundle b) { super.onCreate(b); prefs=getSharedPreferences("settings",MODE_PRIVATE); restoreFolder(); buildUi(); }
    @Override protected void onResume() { super.onResume(); if(list!=null) refreshApps(); }
    private int dp(int n) { return (int)(n * getResources().getDisplayMetrics().density + .5f); }
    private TextView text(String s, int size) { TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(Color.DKGRAY); t.setPadding(dp(12),dp(8),dp(12),dp(8)); return t; }
    private void buildUi() {
        LinearLayout root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL); root.setPadding(dp(12),dp(10),dp(12),0);
        TextView title=text("حافظ التطبيقات",24); title.setTextColor(Color.rgb(13,71,161)); title.setGravity(Gravity.RIGHT); root.addView(title);
        TextView info=text("احفظ نسخة APK من التطبيقات المثبتة، ثم افتحها للتثبيت من الملفات.",14); root.addView(info);
        LinearLayout bar=new LinearLayout(this); bar.setGravity(Gravity.CENTER_VERTICAL); bar.setPadding(0,dp(4),0,dp(4));
        folderLabel=text(folder==null?"مجلد الحفظ غير محدد":"مجلد الحفظ: تم الاختيار",13); folderLabel.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1)); bar.addView(folderLabel);
        Button choose=new Button(this); choose.setText("اختيار المجلد"); choose.setTextSize(12); choose.setMinHeight(0); choose.setPadding(dp(8),0,dp(8),0); choose.setOnClickListener(v -> chooseFolder()); bar.addView(choose); root.addView(bar);
        ScrollView scroll=new ScrollView(this); list=new LinearLayout(this); list.setOrientation(LinearLayout.VERTICAL); scroll.addView(list); root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root); refreshApps();
    }
    private void refreshApps() {
        if(list==null) return; list.removeAllViews(); apps.clear();
        PackageManager pm=getPackageManager(); List<ApplicationInfo> all=pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for(ApplicationInfo a:all) if(pm.getLaunchIntentForPackage(a.packageName)!=null && !a.packageName.equals(getPackageName())) apps.add(a);
        Collections.sort(apps,(a,b)->pm.getApplicationLabel(a).toString().compareToIgnoreCase(pm.getApplicationLabel(b).toString()));
        for(ApplicationInfo a:apps) addRow(a);
    }
    private void addRow(ApplicationInfo app) {
        PackageManager pm=getPackageManager(); LinearLayout row=new LinearLayout(this); row.setGravity(Gravity.CENTER_VERTICAL); row.setPadding(dp(8),dp(5),dp(8),dp(5)); row.setLayoutDirection(View.LAYOUT_DIRECTION_LTR); row.setBackgroundColor(0xfff5f8ff);
        ImageView icon=new ImageView(this); icon.setImageDrawable(pm.getApplicationIcon(app)); icon.setPadding(dp(4),dp(4),dp(4),dp(4)); row.addView(icon,new LinearLayout.LayoutParams(dp(50),dp(50)));
        TextView name=text(pm.getApplicationLabel(app).toString(),16); name.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1)); row.addView(name);
        Button save=new Button(this); save.setText("حفظ APK"); save.setTextColor(Color.WHITE); save.setTextSize(13); save.setAllCaps(false); save.setBackground(round(0xff1565c0,dp(8))); save.setOnClickListener(v->saveApk(app)); row.addView(save,new LinearLayout.LayoutParams(dp(112),dp(48)));
        list.addView(row); View line=new View(this); line.setBackgroundColor(0xffdddddd); list.addView(line,new LinearLayout.LayoutParams(-1,1));
    }
    private GradientDrawable round(int color,int radius){ GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d; }
    private void chooseFolder() { Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE); i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION); startActivityForResult(i,PICK_TREE); }
    private void pickApk() { Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT); i.setType("application/vnd.android.package-archive"); i.addCategory(Intent.CATEGORY_OPENABLE); i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); startActivityForResult(i,PICK_APK); }
    private void saveApk(ApplicationInfo app) {
        if(folder==null){ Toast.makeText(this,"اختر مجلد الحفظ أولاً",Toast.LENGTH_SHORT).show(); return; }
        try {
            PackageInfo pi=getPackageManager().getPackageInfo(app.packageName,0); String label=getPackageManager().getApplicationLabel(app).toString().replaceAll("[^\\p{L}\\p{N}._-]","_");
            String fileName=label+"-"+pi.versionName+".apk";
            Uri parent=DocumentsContract.buildDocumentUriUsingTree(folder,DocumentsContract.getTreeDocumentId(folder));
            Uri out=DocumentsContract.createDocument(getContentResolver(),parent,"application/vnd.android.package-archive",fileName);
            if(out==null) throw new IOException("تعذر إنشاء الملف");
            try(InputStream in=new FileInputStream(app.sourceDir); OutputStream os=getContentResolver().openOutputStream(out)){ byte[] buf=new byte[8192]; int n; while((n=in.read(buf))!=-1) os.write(buf,0,n); }
            Toast.makeText(this,"تم حفظ: "+fileName,Toast.LENGTH_LONG).show();
        } catch(Exception e){ Toast.makeText(this,"فشل الحفظ: "+e.getMessage(),Toast.LENGTH_LONG).show(); }
    }
    @Override protected void onActivityResult(int req,int res,Intent data){ super.onActivityResult(req,res,data); if(res!=RESULT_OK||data==null)return; Uri u=data.getData();
        if(req==PICK_TREE){ folder=u; try{getContentResolver().takePersistableUriPermission(u,data.getFlags()&(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION));}catch(Exception ignored){} prefs.edit().putString("folder_uri",u.toString()).apply(); folderLabel.setText("مجلد الحفظ: تم الاختيار"); }
        else if(req==PICK_APK){ Intent i=new Intent(Intent.ACTION_VIEW,u); i.setDataAndType(u,"application/vnd.android.package-archive"); i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); try{startActivity(i);}catch(Exception e){Toast.makeText(this,"لا يوجد مثبت APK متاح",Toast.LENGTH_LONG).show();} }
    }
    private void restoreFolder(){ String saved=prefs.getString("folder_uri",null); if(saved==null)return; try{ folder=Uri.parse(saved); getContentResolver().takePersistableUriPermission(folder,Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION); }catch(Exception e){ folder=null; prefs.edit().remove("folder_uri").apply(); } }
}
