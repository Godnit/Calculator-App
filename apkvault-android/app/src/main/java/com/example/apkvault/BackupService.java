package com.example.apkvault;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.*;
import android.provider.DocumentsContract;
import java.io.*;

public class BackupService extends Service {
    private static final String CHANNEL="backup";
    @Override public void onCreate(){super.onCreate(); if(Build.VERSION.SDK_INT>=26){NotificationChannel c=new NotificationChannel(CHANNEL,"حفظ التطبيقات",NotificationManager.IMPORTANCE_LOW);getSystemService(NotificationManager.class).createNotificationChannel(c);}}
    @Override public int onStartCommand(Intent intent,int flags,int startId){
        String label="جاري حفظ التطبيق"; startForeground(7,notification(label,0));
        new Thread(()->copy(intent,startId)).start(); return START_NOT_STICKY;
    }
    private Notification notification(String text,int p){Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL):new Notification.Builder(this);return b.setContentTitle("حافظ التطبيقات").setContentText(text).setSmallIcon(com.example.apkvault.R.drawable.ic_launcher).setProgress(100,p,false).build();}
    private void copy(Intent i,int id){String error=null;try{String source=i.getStringExtra("source");Uri tree=Uri.parse(i.getStringExtra("folder"));Uri parent=DocumentsContract.buildDocumentUriUsingTree(tree,DocumentsContract.getTreeDocumentId(tree));String file=safeName(getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(i.getStringExtra("package"),0)).toString())+".apk";Uri out=DocumentsContract.createDocument(getContentResolver(),parent,"application/vnd.android.package-archive",file);if(out==null)throw new IOException("تعذر إنشاء الملف");long total=new File(source).length(),done=0,lastUpdate=0;int lastPercent=-1;try(InputStream in=new BufferedInputStream(new FileInputStream(source),262144);OutputStream os=new BufferedOutputStream(getContentResolver().openOutputStream(out),262144)){byte[] buf=new byte[262144];int n;while((n=in.read(buf))!=-1){os.write(buf,0,n);done+=n;int p=(int)(done*100/Math.max(1,total));long now=SystemClock.elapsedRealtime();if(p!=lastPercent&&now-lastUpdate>=300){lastUpdate=now;lastPercent=p;send(p,false,null);((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).notify(7,notification("جاري الحفظ: "+p+"٪",p));}}os.flush();}}catch(Exception e){error=e.getMessage();}send(100,true,error);stopForeground(true);stopSelf(id);}
    private String safeName(String label){StringBuilder s=new StringBuilder();for(char ch:label.toCharArray()){if(ch<32||"/\\:*?\"<>|".indexOf(ch)>=0)s.append('_');else s.append(ch);}String name=s.toString().trim();return name.isEmpty()?"Application":name;}
    private void send(int p,boolean done,String error){Intent b=new Intent("com.example.apkvault.PROGRESS");b.setPackage(getPackageName());b.putExtra("progress",p);b.putExtra("done",done);if(error!=null)b.putExtra("error",error);sendBroadcast(b);}
    @Override public android.os.IBinder onBind(Intent i){return null;}
}