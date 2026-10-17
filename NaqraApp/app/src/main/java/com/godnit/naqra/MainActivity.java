package com.godnit.naqra;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String PREFS = "naqra_preferences";
    private static final String KEY_COUNT = "count";

    private SharedPreferences preferences;
    private int count;
    private TextView countText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#123B35"));
        getWindow().setNavigationBarColor(Color.parseColor("#123B35"));
        getWindow().getDecorView().setSystemUiVisibility(0);

        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        count = preferences.getInt(KEY_COUNT, 0);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setPadding(dp(24), dp(28), dp(24), dp(24));
        root.setBackgroundColor(Color.parseColor("#F3F7F4"));

        TextView title = text("نَقْرة", 32, "#123B35", true);
        root.addView(title, matchWrap());

        TextView subtitle = text("عدادك اليومي البسيط", 16, "#60756F", false);
        LayoutParams subtitleParams = matchWrap();
        subtitleParams.topMargin = dp(8);
        root.addView(subtitle, subtitleParams);

        View topSpace = new View(this);
        root.addView(topSpace, new LayoutParams(1, dp(34)));

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(18), dp(30), dp(18), dp(30));
        card.setBackground(rounded("#FFFFFF", 24));
        card.setElevation(dp(3));

        TextView label = text("العدد الحالي", 16, "#60756F", false);
        card.addView(label, matchWrap());

        countText = text(toArabicDigits(count), 60, "#123B35", true);
        LayoutParams countParams = matchWrap();
        countParams.topMargin = dp(12);
        card.addView(countText, countParams);

        TextView saved = text("يُحفظ تلقائيًا على جهازك", 13, "#84958F", false);
        LayoutParams savedParams = matchWrap();
        savedParams.topMargin = dp(14);
        card.addView(saved, savedParams);

        root.addView(card, new LayoutParams(-1, dp(230)));

        View gap = new View(this);
        root.addView(gap, new LayoutParams(1, dp(26)));

        Button plus = button("+ زيادة العدد", "#123B35", "#FFFFFF");
        root.addView(plus, new LayoutParams(-1, dp(58)));
        plus.setOnClickListener(view -> {
            if (count < Integer.MAX_VALUE) {
                count++;
                saveAndRender();
            }
        });

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER);
        LayoutParams actionsParams = new LayoutParams(-1, dp(58));
        actionsParams.topMargin = dp(14);

        Button undo = button("تراجع −", "#DCEAE2", "#123B35");
        Button reset = button("تصفير", "#FBE6E2", "#8D3D33");

        LayoutParams half = new LayoutParams(0, -1, 1);
        half.setMargins(dp(4), 0, dp(4), 0);
        actions.addView(undo, half);
        LayoutParams secondHalf = new LayoutParams(0, -1, 1);
        secondHalf.setMargins(dp(4), 0, dp(4), 0);
        actions.addView(reset, secondHalf);
        root.addView(actions, actionsParams);

        undo.setOnClickListener(view -> {
            if (count > 0) {
                count--;
                saveAndRender();
            }
        });

        reset.setOnClickListener(view -> {
            count = 0;
            saveAndRender();
        });

        TextView footer = text("صنعناه لك ❤️", 13, "#84958F", false);
        LayoutParams footerParams = matchWrap();
        footerParams.topMargin = dp(30);
        root.addView(footer, footerParams);

        setContentView(root);
    }

    private void saveAndRender() {
        preferences.edit().putInt(KEY_COUNT, count).apply();
        countText.setText(toArabicDigits(count));
    }

    private TextView text(String value, float size, String color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(Color.parseColor(color));
        view.setGravity(Gravity.CENTER);
        view.setTextDirection(View.TEXT_DIRECTION_RTL);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        return view;
    }

    private Button button(String label, String background, String foreground) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextSize(17);
        button.setTextColor(Color.parseColor(foreground));
        button.setAllCaps(false);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setBackground(rounded(background, 18));
        button.setPadding(dp(12), 0, dp(12), 0);
        return button;
    }

    private GradientDrawable rounded(String color, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor(color));
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private LayoutParams matchWrap() {
        return new LayoutParams(-1, -2);
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private String toArabicDigits(int number) {
        return String.valueOf(number)
                .replace('0', '٠')
                .replace('1', '١')
                .replace('2', '٢')
                .replace('3', '٣')
                .replace('4', '٤')
                .replace('5', '٥')
                .replace('6', '٦')
                .replace('7', '٧')
                .replace('8', '٨')
                .replace('9', '٩');
    }
}
