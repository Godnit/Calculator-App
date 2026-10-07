package com.example.hasibati;

import android.app.*;import android.os.*;import android.content.*;import android.content.pm.*;import android.graphics.Color;import android.graphics.drawable.GradientDrawable;import android.provider.Settings;import android.speech.tts.TextToSpeech;import android.view.*;import android.view.inputmethod.EditorInfo;import android.widget.*;import java.text.Normalizer;import java.util.*;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener{
 EditText command; TextView result; TextToSpeech tts; boolean advanced=false; final int blue=Color.rgb(75,115,235);
 public void onCreate(Bundle b){super.onCreate(b);tts=new TextToSpeech(this,this);buildUi();}
 TextView label(String s,float size){TextView v=new TextView(this);v.setText(s);v.setTextColor(Color.WHITE);v.setTextSize(size);v.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);v.setTextDirection(View.TEXT_DIRECTION_RTL);return v;}
 GradientDrawable shape(int c){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(22);return g;}
 void buildUi(){
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(24,22,24,16);root.setBackgroundColor(Color.rgb(7,16,30));
  TextView title=label("نفّذ",30);title.setTypeface(null,1);root.addView(title,new LinearLayout.LayoutParams(-1,55));
  TextView sub=label("مساعد ذكي للتطبيقات والحساب",16);sub.setTextColor(Color.rgb(180,195,220));root.addView(sub,new LinearLayout.LayoutParams(-1,42));
  LinearLayout modes=new LinearLayout(this);modes.setPadding(0,4,0,4);
  TextView basic=mode("أساسي محلي"), adv=mode("متقدم محلي");modes.addView(basic,new LinearLayout.LayoutParams(0,58,1));modes.addView(adv,new LinearLayout.LayoutParams(0,58,1));root.addView(modes);
  command=new EditText(this);command.setHint("مثال: افتح الحاسبة واحسب 500 - 42");command.setTextColor(Color.WHITE);command.setHintTextColor(Color.rgb(145,155,175));command.setTextSize(17);command.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);command.setTextDirection(View.TEXT_DIRECTION_RTL);command.setSingleLine(true);command.setPadding(18,0,18,0);command.setImeOptions(EditorInfo.IME_ACTION_DONE);command.setBackground(shape(Color.rgb(23,35,57)));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,68);cp.topMargin=14;root.addView(command,cp);
  TextView run=label("تنفيذ الأمر",18);run.setGravity(Gravity.CENTER);run.setTextColor(Color.WHITE);run.setBackground(shape(blue));run.setPadding(0,0,0,0);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,62);rp.topMargin=14;root.addView(run,rp);
  result=label("جاهز. اكتب أمراً مثل: افتح المعرض أو افتح MX Player",16);result.setTextColor(Color.rgb(215,225,242));result.setPadding(0,22,0,0);root.addView(result,new LinearLayout.LayoutParams(-1,0,1));
  TextView access=label("⚙  تفعيل إمكانية الوصول",15);access.setTextColor(Color.rgb(125,180,255));access.setGravity(Gravity.CENTER);access.setPadding(0,8,0,8);access.setOnClickListener(v->{try{startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));}catch(Exception e){}});root.addView(access,new LinearLayout.LayoutParams(-1,46));TextView help=label("المتقدم ينفذ الكتابة والبحث داخل التطبيقات بعد تفعيل خدمة إمكانية الوصول.",13);help.setTextColor(Color.rgb(145,155,175));help.setGravity(Gravity.RIGHT|Gravity.BOTTOM);root.addView(help,new LinearLayout.LayoutParams(-1,58));
  basic.setOnClickListener(v->{advanced=false;say("الوضع الأساسي: فتح التطبيقات والحساب المحلي");});adv.setOnClickListener(v->{advanced=true;say("الوضع المتقدم: تنفيذ خطوات داخل التطبيقات");});run.setOnClickListener(v->execute());command.setOnEditorActionListener((v,a,e)->{execute();return true;});setContentView(root);
 }
 TextView mode(String s){TextView b=label(s,15);b.setGravity(Gravity.CENTER);b.setTextColor(Color.WHITE);b.setPadding(6,0,6,0);b.setBackground(shape(Color.rgb(35,57,94)));return b;}
 String norm(String s){s=Normalizer.normalize(s,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT);return s.replace('أ','ا').replace('إ','ا').replace('آ','ا').replace('ة','ه').replace('ى','ي').replaceAll("[^\\p{L}\\p{Nd}]+"," ").trim();}
 void say(String s){result.setText(s);if(tts!=null)tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"answer");}
 void execute(){
  String q=command.getText().toString().trim();if(q.isEmpty()){say("اكتب أمراً أولاً");return;}String a=norm(q);
  String expr=extractExpression(q);
  if(expr!=null&&(a.contains("حاسبه")||a.contains("احسب")||a.contains("عمليه"))){CommandAccessibilityService.pendingExpression=expr;openCalculator("سأفتح الحاسبة وأكتب "+expr);return;}
  if(a.contains("معرض")||a.contains("صور")||a.contains("استديو")){openGallery();return;}
  if(a.contains("ملفات")||a.contains("مدير الملفات")||a.contains("اداره الملفات")||a.contains("file manager")){CommandAccessibilityService.pendingSearch=searchText(a);openFiles(a);return;}
  if(a.contains("mx player")||a.contains("ام اكس")||a.contains("اكس")||a.contains("مشغل الوسائط")||a.contains("مشغل")||a.contains("media player")){CommandAccessibilityService.pendingSearch=searchText(a);openNamedPackage("com.mxtech.videoplayer.ad","MX Player");return;}
  if(a.contains("كروم")||a.contains("كرووم")||a.contains("جوجل كروم")||a.contains("chrome")||a.contains("google chrome")){CommandAccessibilityService.pendingSearch=searchText(a);openNamedPackage("com.android.chrome","Chrome");return;}
  if(a.contains("يوتيوب")||a.contains("youtube")){CommandAccessibilityService.pendingSearch=searchText(a);openNamedPackage("com.google.android.youtube","YouTube");return;}
  if(a.contains("شات جي بي تي")||a.contains("تسات جي بي تي")||a.contains("chatgpt")||a.contains("chat gpt")){CommandAccessibilityService.pendingSearch=searchText(a);openNamedPackage("com.openai.chatgpt","ChatGPT");return;}
  if(a.contains("واتساب")||a.contains("whatsapp")){CommandAccessibilityService.pendingSearch=searchText(a);openNamedPackage("com.whatsapp","WhatsApp");return;}
  if(a.contains("تلجرام")||a.contains("تليجرام")||a.contains("telegram")){CommandAccessibilityService.pendingSearch=searchText(a);openNamedPackage("org.telegram.messenger","Telegram");return;}
  if(a.contains("كاميرا")||a.contains("camera")){open(new Intent("android.media.action.IMAGE_CAPTURE"),"سأفتح الكاميرا");return;}
  if(a.contains("اعدادات")||a.contains("ضبط")){open(new Intent(Settings.ACTION_SETTINGS),"سأفتح الإعدادات");return;}
  if(expr!=null){CommandAccessibilityService.pendingExpression=expr;openCalculator("سأفتح الحاسبة وأكتب "+expr);return;}
  if(a.startsWith("افتح ")||a.startsWith("شغل ")||a.startsWith("ادخل ")||a.startsWith("انتقل")){launchBestApp(a);return;}
  say("لم أفهم اسم التطبيق. اكتب: افتح ثم اسم التطبيق، أو استخدم الوضع المتقدم للبحث والكتابة داخله.");
 }
 String extractExpression(String a){String x=a.replace('×','*').replace('÷','/').replace('−','-');x=x.replace('٠','0').replace('١','1').replace('٢','2').replace('٣','3').replace('٤','4').replace('٥','5').replace('٦','6').replace('٧','7').replace('٨','8').replace('٩','9');java.util.regex.Matcher m=java.util.regex.Pattern.compile("([0-9]+(?:\\.[0-9]+)?\\s*[+\\-*/]\\s*[0-9]+(?:\\.[0-9]+)?(?:\\s*[+\\-*/]\\s*[0-9]+(?:\\.[0-9]+)?)*)").matcher(x);return m.find()?m.group(1).replaceAll("\\s+",""):null;}
 String searchText(String a){int p=a.indexOf("ابحث عن ");if(p<0)p=a.indexOf("ابحث في ");if(p>=0){String s=a.substring(p+8).replaceAll("ثم شغل.*","").trim();s=s.replaceFirst("^(اغنيه|انشوده|مقطع|فيديو|عن)\\s+","").trim();return s;}return "";}
 void openCalculator(String m){Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR);open(i,m);}
 void openGallery(){Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_GALLERY);try{startActivity(i);say("سأفتح المعرض");}catch(Exception e){open(new Intent(Intent.ACTION_VIEW).setType("image/*"),"سأفتح الصور");}}
 void openFiles(String q){
  Intent files=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_FILES);
  List<ResolveInfo> apps=getPackageManager().queryIntentActivities(files,PackageManager.MATCH_DEFAULT_ONLY);
  for(ResolveInfo ri:apps){String n=norm(ri.loadLabel(getPackageManager()).toString());if(n.contains("ملف")||n.contains("مدير")||n.contains("file")||n.contains("files")){files.setPackage(ri.activityInfo.packageName);open(files,"سأفتح إدارة الملفات");return;}}
  String[] known={"com.google.android.documentsui","com.android.documentsui","com.mi.android.globalFileexplorer","com.coloros.filemanager","com.sec.android.app.myfiles"};
  for(String p:known){Intent i=getPackageManager().getLaunchIntentForPackage(p);if(i!=null){open(i,"سأفتح إدارة الملفات");return;}}
  try{startActivity(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE));say("سأفتح مدير الملفات");}catch(Exception e){say("لم أجد تطبيق إدارة الملفات");}
}
void openNamedPackage(String pkg,String name){Intent i=getPackageManager().getLaunchIntentForPackage(pkg);if(i==null){launchBestApp(norm(name));return;}open(i,"سأفتح "+name+(CommandAccessibilityService.pendingSearch.isEmpty()?"":" وأبحث عن "+CommandAccessibilityService.pendingSearch));}
 void launchBestApp(String q){
  String wanted=q.replaceFirst("^(افتح|شغل|ادخل|انتقل|اذهب الى|اذهب إلى)\\s*","").trim();wanted=wanted.replaceFirst("\\s+(وابحث|ثم ابحث|و اكتب|ثم اكتب).*","").trim();String w=norm(wanted);ApplicationInfo best=null;int score=0;
  for(ApplicationInfo x:getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)){if(x.packageName.equals(getPackageName()))continue;String n=norm(getPackageManager().getApplicationLabel(x).toString());int s=0;if(n.equals(w))s=100;else if(w.contains(n)||n.contains(w))s=70;for(String t:w.split(" "))if(t.length()>2&&n.contains(t))s+=15;if(s>score){score=s;best=x;}}
  if(best!=null&&score>=25){Intent i=getPackageManager().getLaunchIntentForPackage(best.packageName);if(i!=null){CommandAccessibilityService.pendingSearch=searchText(w);open(i,"سأفتح "+getPackageManager().getApplicationLabel(best));return;}}
  say("لم أجد تطبيقاً باسم "+wanted);
 }
 void open(Intent i,String m){try{startActivity(i);say(m);}catch(Exception e){say("لا يوجد تطبيق مناسب لهذا الأمر");}}
 double eval(String s){return new Object(){int p=-1,c;void n(){c=++p<s.length()?s.charAt(p):-1;}boolean eat(int x){while(c==' ')n();if(c==x){n();return true;}return false;}double parse(){n();double v=expr();if(p<s.length())throw new RuntimeException();return v;}double expr(){double v=term();for(;;){if(eat('+'))v+=term();else if(eat('-'))v-=term();else return v;} }double term(){double v=factor();for(;;){if(eat('*'))v*=factor();else if(eat('/'))v/=factor();else return v;} }double factor(){if(eat('+'))return factor();if(eat('-'))return -factor();int st=p;while((c>='0'&&c<='9')||c=='.')n();return Double.parseDouble(s.substring(st,p));}}.parse();}
 public void onInit(int s){if(s==TextToSpeech.SUCCESS)tts.setLanguage(new Locale("ar"));}protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
