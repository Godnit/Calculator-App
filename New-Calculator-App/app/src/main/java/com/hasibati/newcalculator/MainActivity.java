package com.hasibati.newcalculator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

public class MainActivity extends Activity {
 public void onCreate(Bundle b){super.onCreate(b);
  LinearLayout l=new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL);
  EditText e=new EditText(this); e.setTextSize(28); l.addView(e);
  String[] bs={"7","8","9","+","4","5","6","-","1","2","3","*","0","C","=","/"};
  for(String s:bs){ Button x=new Button(this); x.setText(s); l.addView(x); }
  setContentView(l);
 }
}
