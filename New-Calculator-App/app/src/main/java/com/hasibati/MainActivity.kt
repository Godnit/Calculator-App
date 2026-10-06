package com.hasibati

import android.app.Activity
import android.os.Bundle
import android.widget.*

class MainActivity: Activity(){
 override fun onCreate(b:Bundle?){super.onCreate(b)
  val v=LinearLayout(this); v.orientation=LinearLayout.VERTICAL
  val input=EditText(this); val btn=Button(this); btn.text="احسب"
  val out=TextView(this)
  btn.setOnClickListener{out.text=input.text.toString()}
  v.addView(input);v.addView(btn);v.addView(out);setContentView(v)
 }
}
