package com.dragracing.game.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.dragracing.game.R;
import com.dragracing.game.data.PlayerData;
import com.dragracing.game.data.Car;
import com.dragracing.game.render.TrackRenderer;
import java.util.Locale;

public class CareerActivity extends AppCompatActivity {
    private PlayerData playerData;
    private TextView tvCareerCash;
    private LinearLayout stagesContainer;

    private static final String[] RIVAL_NAMES = new String[]{
            "Tay đua tập sự (Street Rookie)",
            "Kẻ đi đêm (Midnight Club)",
            "Chuyên gia bám đường (Grip Master)",
            "Huyền thoại xa lộ (Highway Legend)",
            "BẬC THẦY TỐC ĐỘ (SPEED KING)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_career);
        hideSystemUI();

        playerData = PlayerData.getInstance(this);

        tvCareerCash = findViewById(R.id.tvCareerCash);
        stagesContainer = findViewById(R.id.stagesContainer);
        Button btnBack = findViewById(R.id.btnBackCareer);
        btnBack.setOnClickListener(v -> finish());

        buildStages();
        
        // Apply Pixel Font
        Typeface pixelFont = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.pixel_font);
        applyFontToViewTree(findViewById(android.R.id.content), pixelFont);
    }

    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    private void buildStages() {
        tvCareerCash.setText(String.format(Locale.US, "$%,d", playerData.getCash()));
        stagesContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        Typeface pixelFont = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.pixel_font);
        
        int currentStage = playerData.getCareerStage(); // 1 to 5+
        int currentStep = playerData.getCareerStep(); // 0 to 4

        for (int i = 0; i < RIVAL_NAMES.length; i++) {
            final int stageNum = i + 1;
            View row = inflater.inflate(R.layout.item_career_stage, stagesContainer, false);
            TextView tvNum = row.findViewById(R.id.tvStageNumber);
            TextView tvName = row.findViewById(R.id.tvRivalName);
            TextView tvPrize = row.findViewById(R.id.tvStagePrize);
            TextView tvOpponent = row.findViewById(R.id.tvStageOpponent);
            TextView tvTrack = row.findViewById(R.id.tvStageTrack);
            Button btnAction = row.findViewById(R.id.btnStageAction);

            tvNum.setText(String.valueOf(stageNum));
            applyFontToViewTree(row, pixelFont);
                boolean stageIsBoss = stageNum == currentStage && currentStep >= 4;
                Car previewOpponent = getCareerOpponent(stageNum, currentStep, stageIsBoss);
                TrackRenderer.EnvironmentType previewEnvironment =
                    TrackRenderer.EnvironmentType.careerEnvironment(stageNum, currentStep, stageIsBoss);
            
            if (stageNum < currentStage) {
                // Completed
                tvName.setText(String.format(Locale.US, "Stage %d: HOÀN THÀNH", stageNum));
                tvPrize.setText(RIVAL_NAMES[i]);
                tvPrize.setTextColor(Color.GRAY);
                tvOpponent.setText(String.format(Locale.US, "Đối thủ: %s", previewOpponent.getName()));
                tvTrack.setText(String.format(Locale.US, "Đường đua: %s", previewEnvironment.getDisplayName()));
                tvNum.setBackgroundColor(Color.parseColor("#00C853"));
                btnAction.setText("ĐUA LẠI");
                btnAction.setEnabled(true);
                btnAction.setBackgroundColor(Color.parseColor("#37474F"));
                btnAction.setOnClickListener(v -> launchCareerRace(stageNum, true));
            } else if (stageNum == currentStage) {
                // Current active stage
                String stageTitle = RIVAL_NAMES[i];
                if (currentStep < 4) {
                    tvName.setText(String.format(Locale.US, "Stage %d: %s", stageNum, stageTitle));
                    tvPrize.setText(String.format(Locale.US, "Vòng loại: %d/4", currentStep + 1));
                    tvOpponent.setText(String.format(Locale.US, "Đối thủ: %s", previewOpponent.getName()));
                    tvTrack.setText(String.format(Locale.US, "Đường đua: %s", previewEnvironment.getDisplayName()));
                    btnAction.setText("TIẾP TỤC");
                } else {
                    tvName.setText(String.format(Locale.US, "Stage %d: TRẬN ĐẤU BOSS!", stageNum));
                    tvPrize.setText(String.format(Locale.US, "Đối thủ: %s", stageTitle));
                    tvOpponent.setText(String.format(Locale.US, "Xe đối thủ: %s", previewOpponent.getName()));
                    tvTrack.setText(String.format(Locale.US, "Đường đua: %s", previewEnvironment.getDisplayName()));
                    tvPrize.setTextColor(Color.parseColor("#FFD600"));
                    btnAction.setText("QUYẾT ĐẤU");
                }
                
                tvNum.setBackgroundColor(Color.parseColor("#FFD600"));
                btnAction.setEnabled(true);
                btnAction.setBackgroundColor(Color.parseColor("#E53935"));
                btnAction.setOnClickListener(v -> launchCareerRace(stageNum, currentStep >= 4));
            } else {
                // Locked
                tvName.setText(String.format(Locale.US, "Stage %d: ???", stageNum));
                tvPrize.setText("TRẠNG THÁI: ĐANG KHÓA");
                tvOpponent.setText("");
                tvTrack.setText("");
                tvPrize.setTextColor(Color.RED);
                tvNum.setBackgroundColor(Color.parseColor("#424242"));
                btnAction.setText("KHÓA");
                btnAction.setEnabled(false);
                btnAction.setBackgroundColor(Color.parseColor("#212121"));
            }

            stagesContainer.addView(row);
        }
    }

    private void launchCareerRace(int stage, boolean isBoss) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("race_mode", "CAREER");
        intent.putExtra("difficulty", stage);
        intent.putExtra("is_boss", isBoss);
        intent.putExtra("career_step", playerData.getCareerStep());
        intent.putExtra("environment_index", TrackRenderer.EnvironmentType.careerEnvironment(
                stage, playerData.getCareerStep(), isBoss).ordinal());
        startActivity(intent);
    }

    private Car getCareerOpponent(int stage, int step, boolean boss) {
        java.util.List<Car> allCars = playerData.getAllCars();
        int index = boss ? stage - 1 : Math.max(0, stage - 2 + (step % 2));
        index = Math.min(allCars.size() - 1, index);
        return allCars.get(index);
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

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        buildStages();
    }
}
