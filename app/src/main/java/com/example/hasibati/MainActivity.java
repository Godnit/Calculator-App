package com.example.hasibati;
import android.app.Activity;import android.os.Bundle;import android.webkit.WebView;import android.webkit.WebSettings;import android.view.View;
public class MainActivity extends Activity{public void onCreate(Bundle b){super.onCreate(b);WebView w=new WebView(this);w.setBackgroundColor(0xff0b1220);WebSettings s=w.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);w.setOverScrollMode(View.OVER_SCROLL_NEVER);w.loadUrl("file:///android_asset/index.html");setContentView(w);}}
