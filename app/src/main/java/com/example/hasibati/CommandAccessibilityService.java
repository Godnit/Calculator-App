package com.example.hasibati;
import android.accessibilityservice.AccessibilityService;import android.view.accessibility.AccessibilityEvent;import android.view.accessibility.AccessibilityNodeInfo;import android.os.Bundle;
public class CommandAccessibilityService extends AccessibilityService{
 public static String pendingExpression="";
 public void onAccessibilityEvent(AccessibilityEvent e){if(pendingExpression.isEmpty())return;AccessibilityNodeInfo root=getRootInActiveWindow();if(root==null)return;AccessibilityNodeInfo edit=findEdit(root);if(edit!=null){Bundle b=new Bundle();b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,pendingExpression);edit.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,b);}AccessibilityNodeInfo eq=findText(root,"=");if(eq!=null){eq.performAction(AccessibilityNodeInfo.ACTION_CLICK);pendingExpression="";}}
 AccessibilityNodeInfo findEdit(AccessibilityNodeInfo n){if(n.isEditable())return n;for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo x=n.getChild(i);if(x!=null){AccessibilityNodeInfo y=findEdit(x);if(y!=null)return y;}}return null;}
 AccessibilityNodeInfo findText(AccessibilityNodeInfo n,String s){if(s.equals(n.getText()))return n;for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo x=n.getChild(i);if(x!=null){AccessibilityNodeInfo y=findText(x,s);if(y!=null)return y;}}return null;}
 public void onInterrupt(){}
}
