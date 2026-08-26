package com.openai.simplecalculator;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    private final CalculatorEngine engine = new CalculatorEngine();
    private TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(18, 18, 18));
        getWindow().setNavigationBarColor(Color.rgb(18, 18, 18));
        setContentView(buildUi());
    }

    private View buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(24), dp(16), dp(20));
        root.setBackgroundColor(Color.rgb(18, 18, 18));

        display = new TextView(this);
        display.setText(engine.getDisplay());
        display.setTextColor(Color.WHITE);
        display.setTextSize(48);
        display.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        display.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        display.setPadding(dp(8), 0, dp(8), dp(12));
        display.setSingleLine(true);
        display.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        root.addView(display, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.2f));

        GridLayout grid = new GridLayout(this);
        grid.setColumnCount(4);
        grid.setRowCount(5);
        grid.setUseDefaultMargins(false);
        root.addView(grid, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 4.8f));

        String[][] labels = {
                {"C", "⌫", "+/−", "÷"},
                {"7", "8", "9", "×"},
                {"4", "5", "6", "−"},
                {"1", "2", "3", "+"},
                {"0", ".", "=", "="}
        };

        for (int r = 0; r < labels.length; r++) {
            for (int c = 0; c < labels[r].length; c++) {
                if (r == 4 && c == 3) continue;
                String label = labels[r][c];
                Button button = makeButton(label);
                GridLayout.Spec row = GridLayout.spec(r, 1, 1f);
                GridLayout.Spec col = (r == 4 && c == 2)
                        ? GridLayout.spec(c, 2, 2f)
                        : GridLayout.spec(c, 1, 1f);
                GridLayout.LayoutParams p = new GridLayout.LayoutParams(row, col);
                p.width = 0;
                p.height = 0;
                p.setMargins(dp(5), dp(5), dp(5), dp(5));
                grid.addView(button, p);
            }
        }
        return root;
    }

    private Button makeButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(24);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setGravity(Gravity.CENTER);
        b.setBackgroundColor(buttonColor(label));
        b.setOnClickListener(v -> handle(label));
        return b;
    }

    private int buttonColor(String label) {
        if (label.equals("+") || label.equals("−") || label.equals("×") || label.equals("÷") || label.equals("=")) {
            return Color.rgb(255, 149, 0);
        }
        if (label.equals("C") || label.equals("⌫") || label.equals("+/−")) {
            return Color.rgb(90, 90, 90);
        }
        return Color.rgb(45, 45, 45);
    }

    private void handle(String label) {
        if (label.length() == 1 && Character.isDigit(label.charAt(0))) {
            engine.inputDigit(label.charAt(0));
        } else {
            switch (label) {
                case ".": engine.inputDecimal(); break;
                case "C": engine.clear(); break;
                case "⌫": engine.backspace(); break;
                case "+/−": engine.toggleSign(); break;
                case "+": engine.chooseOperation('+'); break;
                case "−": engine.chooseOperation('-'); break;
                case "×": engine.chooseOperation('×'); break;
                case "÷": engine.chooseOperation('÷'); break;
                case "=": engine.equalsPress(); break;
            }
        }
        display.setText(engine.getDisplay());
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
