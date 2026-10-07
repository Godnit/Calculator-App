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
  Button basic=mode("أساسي محلي"), adv=mode("متقدم محلي");modes.addView(basic,new LinearLayout.LayoutParams(0,58,1));modes.addView(adv,new LinearLayout.LayoutParams(0,58,1));root.addView(modes);
  command=new EditText(this);command.setHint("مثال: افتح الحاسبة واحسب 500 - 42");command.setTextColor(Color.WHITE);command.setHintTextColor(Color.rgb(145,155,175));command.setTextSize(17);command.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);command.setTextDirection(View.TEXT_DIRECTION_RTL);command.setSingleLine(true);command.setPadding(18,0,18,0);command.setImeOptions(EditorInfo.IME_ACTION_DONE);command.setBackground(shape(Color.rgb(23,35,57)));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,68);cp.topMargin=14;root.addView(command,cp);
  Button run=new Button(this);run.setText("تنفيذ الأمر");run.setTextSize(18);run.setTextColor(Color.WHITE);run.setMinHeight(0);run.setAllCaps(false);run.setBackground(shape(blue));LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,62);rp.topMargin=14;root.addView(run,rp);
  result=label("جاهز. اكتب أمراً مثل: افتح المعرض أو افتح MX Player",16);result.setTextColor(Color.rgb(215,225,242));result.setPadding(0,22,0,0);root.addView(result,new LinearLayout.LayoutParams(-1,0,1));
  TextView help=label("المتقدم ينفذ الكتابة والبحث داخل التطبيقات بعد تفعيل خدمة إمكانية الوصول من إعدادات الهاتف.",13);help.setTextColor(Color.rgb(145,155,175));help.setGravity(Gravity.RIGHT|Gravity.BOTTOM);root.addView(help,new LinearLayout.LayoutParams(-1,72));
  basic.setOnClickListener(v->{advanced=false;say("الوضع الأساسي: فتح التطبيقات والحساب المحلي");});adv.setOnClickListener(v->{advanced=true;say("الوضع المتقدم: تنفيذ خطوات داخل التطبيقات");});run.setOnClickListener(v->execute());command.setOnEditorActionListener((v,a,e)->{execute();return true;});setContentView(root);
 }
 Button mode(String s){Button b=new Button(this);b.setText(s);b.setTextSize(15);b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setMinHeight(0);b.setGravity(Gravity.CENTER);b.setTextDirection(View.TEXT_DIRECTION_RTL);b.setBackground(shape(Color.rgb(35,57,94)));return b;}
 String norm(String s){s=Normalizer.normalize(s,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT);return s.replace('أ','ا').replace('إ','ا').replace('آ','ا').replace('ة','ه').replace('ى','ي').replaceAll("[^\\p{L}\\p{Nd}]+"," ").trim();}
 void say(String s){result.setText(s);if(tts!=null)tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"answer");}
 void execute(){
  String q=command.getText().toString().trim();if(q.isEmpty()){say("اكتب أمراً أولاً");return;}String a=norm(q);
  String expr=extractExpression(q);
  if(expr!=null&&(a.contains("حاسبه")||a.contains("احسب")||a.contains("عمليه"))){CommandAccessibilityService.pendingExpression=expr;openCalculator("سأفتح الحاسبة وأكتب "+expr);return;}
  if(a.contains("معرض")||a.contains("صور")||a.contains("استديو")){openGallery();return;}
  if(a.contains("ملفات")||a.contains("مدير الملفات")||a.contains("اداره الملفات")||a.contains("file manager")){CommandAccessibilityService.pendingSearch=searchText(a);openFiles(a);return;}
  if(a.contains("mx player")||a.contains("ام اكس")||a.contains("مشغل الوسائط")||a.contains("مشغل")){CommandAccessibilityService.pendingSearch=searchText(a);openNamedPackage("com.mxtech.videoplayer.ad","MX Player");return;}
  if(a.contains("كروم")||a.contains("chrome")){openNamedPackage("com.android.chrome","Chrome");return;}\n  if(a.contains("يوتيوب")||a.contains("youtube")){CommandAccessibilityService.pendingSearch=searchText(a);openNamedPackage("com.google.android.youtube","YouTube");return;}
  if(a.contains("اعدادات")||a.contains("ضبط")){open(new Intent(Settings.ACTION_SETTINGS),"سأفتح الإعدادات");return;}
  if(expr!=null&&!a.contains("افتح")){try{double n=eval(expr);say("الناتج هو "+(n==(long)n?String.valueOf((long)n):String.valueOf(n)));return;}catch(Exception e){}}
  if(a.startsWith("افتح ")||a.startsWith("شغل ")||a.startsWith("ادخل ")||a.startsWith("انتقل")){launchBestApp(a);return;}
  say("لم أفهم اسم التطبيق. اكتب: افتح ثم اسم التطبيق، أو استخدم الوضع المتقدم للبحث والكتابة داخله.");
 }
 String extractExpression(String a){String x=a.replace('×','*').replace('÷','/').replace('−','-');x=x.replace('٠','0').replace('١','1').replace('٢','2').replace('٣','3').replace('٤','4').replace('٥','5').replace('٦','6').replace('٧','7').replace('٨','8').replace('٩','9');java.util.regex.Matcher m=java.util.regex.Pattern.compile("([0-9]+(?:\\.[0-9]+)?\\s*[+\\-*/]\\s*[0-9]+(?:\\.[0-9]+)?(?:\\s*[+\\-*/]\\s*[0-9]+(?:\\.[0-9]+)?)*)").matcher(x);return m.find()?m.group(1).replaceAll("\\s+",""):null;}
 String searchText(String a){int p=a.indexOf("ابحث عن ");if(p<0)p=a.indexOf("ابحث في ");if(p>=0)return a.substring(p+8).replaceAll("ثم شغل.*","").trim();return "";}
 void openCalculator(String m){Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR);open(i,m);}
 void openGallery(){Intent i=new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_GALLERY);try{startActivity(i);say("سأفتح المعرض");}catch(Exception e){open(new Intent(Intent.ACTION_VIEW).setType("image/*"),"سأفتح الصور");}}
 void openFiles(String q){
  ApplicationInfo best=null;int score=0;
  for(ApplicationInfo x:getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)){
   if(x.packageName.equals(getPackageName()))continue;String n=norm(getPackageManager().getApplicationLabel(x).toString()+" "+x.packageName);int s=0;
   if(n.contains("file")||n.contains("files")||n.contains("manager")||n.contains("ملف")||n.contains("مدير"))s=80;
   if(s>score){score=s;best=x;}
  }
  if(best!=null){Intent i=getPackageManager().getLaunchIntentForPackage(best.packageName);if(i!=null){open(i,"سأفتح إدارة الملفات");return;}}
  try{startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_FILES));say("سأفتح إدارة الملفات");}catch(Exception e){try{startActivity(new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE));say("سأفتح مدير الملفات");}catch(Exception z){say("لم أجد تطبيق إدارة الملفات");}}}
 void openNamedPackage(String pkg,String name){Intent i=getPackageManager().getLaunchIntentForPackage(pkg);if(i==null){launchBestApp(norm(name));return;}open(i,"سأفتح "+name+(CommandAccessibilityService.pendingSearch.isEmpty()?"":" وأبحث عن "+CommandAccessibilityService.pendingSearch));}
 void launchBestApp(String q){
  String wanted=q.replaceFirst("^(افتح|شغل|ادخل|انتقل)\\s*","").trim();String w=norm(wanted);ApplicationInfo best=null;int score=0;
  for(ApplicationInfo x:getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA)){if(x.packageName.equals(getPackageName()))continue;String n=norm(getPackageManager().getApplicationLabel(x).toString());int s=0;if(n.equals(w))s=100;else if(w.contains(n)||n.contains(w))s=70;for(String t:w.split(" "))if(t.length()>2&&n.contains(t))s+=15;if(s>score){score=s;best=x;}}
  if(best!=null&&score>=25){Intent i=getPackageManager().getLaunchIntentForPackage(best.packageName);if(i!=null){CommandAccessibilityService.pendingSearch=searchText(w);open(i,"سأفتح "+getPackageManager().getApplicationLabel(best));return;}}
  say("لم أجد تطبيقاً باسم "+wanted);
 }
 void open(Intent i,String m){try{startActivity(i);say(m);}catch(Exception e){say("لا يوجد تطبيق مناسب لهذا الأمر");}}
 double eval(String s){return new Object(){int p=-1,c;void n(){c=++p<s.length()?s.charAt(p):-1;}boolean eat(int x){while(c==' ')n();if(c==x){n();return true;}return false;}double parse(){n();double v=expr();if(p<s.length())throw new RuntimeException();return v;}double expr(){double v=term();for(;;){if(eat('+'))v+=term();else if(eat('-'))v-=term();else return v;} }double term(){double v=factor();for(;;){if(eat('*'))v*=factor();else if(eat('/'))v/=factor();else return v;} }double factor(){if(eat('+'))return factor();if(eat('-'))return -factor();int st=p;while((c>='0'&&c<='9')||c=='.')n();return Double.parseDouble(s.substring(st,p));}}.parse();}
 public void onInit(int s){if(s==TextToSpeech.SUCCESS)tts.setLanguage(new Locale("ar"));}protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
