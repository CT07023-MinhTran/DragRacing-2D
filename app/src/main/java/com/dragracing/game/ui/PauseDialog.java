package com.dragracing.game.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import com.dragracing.game.R;

public class PauseDialog extends Dialog {

    public interface PauseActionCallback {
        void onContinue();
        void onSetting();
        void onQuit();
    }

    private final PauseActionCallback callback;

    public PauseDialog(Context context, PauseActionCallback callback) {
        super(context);
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pause);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        setCancelable(false);

        // Apply Pixel Font
        Typeface pixelFont = androidx.core.content.res.ResourcesCompat.getFont(getContext(), R.font.pixel_font);
        applyFontToViewTree(findViewById(android.R.id.content), pixelFont);

        Button btnContinue = findViewById(R.id.btnContinue);
        Button btnSetting = findViewById(R.id.btnSetting);
        Button btnQuit = findViewById(R.id.btnQuit);

        btnContinue.setOnClickListener(v -> {
            dismiss();
            callback.onContinue();
        });

        btnSetting.setOnClickListener(v -> {
            callback.onSetting();
        });

        btnQuit.setOnClickListener(v -> {
            dismiss();
            callback.onQuit();
        });
    }

    private void applyFontToViewTree(View root, Typeface font) {
        if (root == null || font == null) return;
        if (root instanceof TextView) {
            ((TextView) root).setTypeface(font);
        } else if (root instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyFontToViewTree(group.getChildAt(i), font);
            }
        }
    }
}
