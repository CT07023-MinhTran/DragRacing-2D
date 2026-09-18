package com.dragracing.game.ui;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.dragracing.game.R;
import com.dragracing.game.data.Car;
import com.dragracing.game.data.PlayerData;
import java.util.List;
import java.util.Locale;

public class GarageActivity extends AppCompatActivity {
    private PlayerData playerData;
    private List<Car> allCars;
    private int currentCarIndex = 0;

    private TextView tvPlayerCash, tvCarName, tvCarTier;
    private TextView tvSpecHP, tvSpecWeight, tvSpecGrip, tvSpecShiftTime, tvSpecRpmRange, tvSpecNitro;
    private Button btnPrevCar, btnNextCar, btnBuyOrSelect, btnBack;
    private ImageButton btnPaint;
    private View paintPalettePanel;
    private TextView tvUpgradePreview;
    private String selectedUpgradeType;
    private FrameLayout carPreviewContainer;
    private CarPreviewView previewView;
    private ViewGroup upgradesContainer;
    private LinearLayout leftStats;
    private LinearLayout rightStats;

    private final int[] availableColors = new int[]{
            Color.parseColor("#E53935"), // Red
            Color.parseColor("#1565C0"), // Deep Blue
            Color.parseColor("#EEEEEE"), // Pearl White
            Color.parseColor("#212121"), // Stealth Black
            Color.parseColor("#FF6D00"), // Sunset Orange
            Color.parseColor("#FFD600"), // Racing Yellow
            Color.parseColor("#00E676"), // Acid Green
            Color.parseColor("#7C4DFF"), // Neon Purple
            Color.parseColor("#00E5FF")  // Cyan
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garage);
        hideSystemUI();

        playerData = PlayerData.getInstance(this);
        allCars = playerData.getAllCars();

        // Find index of currently selected car
        String currentId = playerData.getCurrentCar().getId();
        for (int i = 0; i < allCars.size(); i++) {
            if (allCars.get(i).getId().equals(currentId)) {
                currentCarIndex = i;
                break;
            }
        }

        initViews();
        updateUI();
        
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

    private void initViews() {
        tvPlayerCash = findViewById(R.id.tvPlayerCash);
        tvCarName = findViewById(R.id.tvCarName);
        tvCarTier = findViewById(R.id.tvCarTier);
        tvSpecHP = findViewById(R.id.tvSpecHP);
        tvSpecWeight = findViewById(R.id.tvSpecWeight);
        tvSpecGrip = findViewById(R.id.tvSpecGrip);
        tvSpecShiftTime = findViewById(R.id.tvSpecShiftTime);
        tvSpecRpmRange = findViewById(R.id.tvSpecRpmRange);
        tvSpecNitro = findViewById(R.id.tvSpecNitro);

        btnPrevCar = findViewById(R.id.btnPrevCar);
        btnNextCar = findViewById(R.id.btnNextCar);
        btnBuyOrSelect = findViewById(R.id.btnBuyOrSelect);
        btnBack = findViewById(R.id.btnBack);
        btnPaint = findViewById(R.id.btnPaint);
        paintPalettePanel = findViewById(R.id.paintPalettePanel);
        tvUpgradePreview = findViewById(R.id.tvUpgradePreview);
        leftStats = findViewById(R.id.leftStats);
        rightStats = findViewById(R.id.rightStats);

        carPreviewContainer = findViewById(R.id.carPreviewContainer);
        upgradesContainer = findViewById(R.id.upgradesContainer);

        previewView = new CarPreviewView(this, getSelectedCar());
        previewView.setGrounded(true);
        carPreviewContainer.addView(previewView);
        createStatViews();
        tvSpecHP = findViewById(R.id.tvSpecHP);
        tvSpecWeight = findViewById(R.id.tvSpecWeight);
        tvSpecGrip = findViewById(R.id.tvSpecGrip);
        tvSpecShiftTime = findViewById(R.id.tvSpecShiftTime);
        tvSpecRpmRange = findViewById(R.id.tvSpecRpmRange);
        tvSpecNitro = findViewById(R.id.tvSpecNitro);

        btnBack.setOnClickListener(v -> finish());

        btnPrevCar.setOnClickListener(v -> {
            if (currentCarIndex > 0) {
                currentCarIndex--;
                selectedUpgradeType = null;
                updateUI();
            }
        });

        btnNextCar.setOnClickListener(v -> {
            if (currentCarIndex < allCars.size() - 1) {
                currentCarIndex++;
                selectedUpgradeType = null;
                updateUI();
            }
        });

        populateColorPalette();
        btnPaint.setOnClickListener(v -> {
            boolean visible = paintPalettePanel.getVisibility() == View.VISIBLE;
            if (visible) {
                paintPalettePanel.animate().translationX(paintPalettePanel.getWidth()).setDuration(220)
                        .withEndAction(() -> paintPalettePanel.setVisibility(View.GONE)).start();
            } else {
                paintPalettePanel.setTranslationX(paintPalettePanel.getWidth());
                paintPalettePanel.setVisibility(View.VISIBLE);
                paintPalettePanel.animate().translationX(0).setDuration(220).start();
            }
        });

        btnBuyOrSelect.setOnClickListener(v -> {
            Car car = getSelectedCar();
            boolean isOwned = playerData.isCarOwned(car.getId());
            if (isOwned) {
                playerData.setCurrentCarId(car.getId());
                Toast.makeText(this, "Đã chọn " + car.getName(), Toast.LENGTH_SHORT).show();
            } else {
                if (playerData.buyCar(car)) {
                    Toast.makeText(this, "Chúc mừng! Bạn đã mua " + car.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Không đủ tiền để mua xe này!", Toast.LENGTH_SHORT).show();
                }
            }
            updateUI();
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

    private void populateColorPalette() {
        LinearLayout container = findViewById(R.id.colorPaletteContainer);
        if (container == null) return;
        container.removeAllViews();

        int size = (int) (28 * getResources().getDisplayMetrics().density);
        int margin = (int) (6 * getResources().getDisplayMetrics().density);

        for (int color : availableColors) {
            View colorView = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.setMargins(margin, 0, margin, 0);
            colorView.setLayoutParams(lp);

            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(color);
            gd.setStroke((int) (1.5f * getResources().getDisplayMetrics().density), Color.parseColor("#444444"));
            gd.setCornerRadius(4 * getResources().getDisplayMetrics().density);
            colorView.setBackground(gd);

            colorView.setOnClickListener(v -> {
                Car car = getSelectedCar();
                car.setColor(color);
                playerData.saveCar(car);
                previewView.setCar(car);
            });
            container.addView(colorView);
        }
    }

    private Car getSelectedCar() {
        return allCars.get(currentCarIndex);
    }

    private void updateUI() {
        Car car = getSelectedCar();
        boolean isOwned = playerData.isCarOwned(car.getId());
        boolean isCurrent = car.getId().equals(playerData.getCurrentCar().getId());

        tvPlayerCash.setText(String.format(Locale.US, "$%,d", playerData.getCash()));
        tvCarName.setText(car.getName());
        tvCarTier.setText(car.getCarClass());

        tvSpecHP.setText(String.format(Locale.US, "%.0f HP", car.getEffectiveHorsepower()));
        tvSpecWeight.setText(String.format(Locale.US, "%.0f kg", car.getEffectiveWeight()));
        tvSpecGrip.setText(String.format(Locale.US, "%.2f", car.getEffectiveGrip()));
        tvSpecShiftTime.setText(String.format(Locale.US, "%.2fs", car.getShiftTimeSeconds()));
        tvSpecRpmRange.setText(String.format(Locale.US, "%d-%d", car.getOptimalShiftMinRpm(), car.getOptimalShiftMaxRpm()));
        
        if (car.getNitroLevel() > 0) {
            tvSpecNitro.setText(String.format(Locale.US, "+%.0f%% (%.1fs)", 
                (car.getNitroBoostFactor() - 1.0) * 100, car.getNitroDurationSeconds()));
            tvSpecNitro.setTextColor(Color.parseColor("#00E5FF"));
        } else {
            tvSpecNitro.setText("CHƯA CÓ");
            tvSpecNitro.setTextColor(Color.GRAY);
        }

        previewView.setCar(car);
        updateStatBars(car);

        btnPrevCar.setEnabled(currentCarIndex > 0);
        btnNextCar.setEnabled(currentCarIndex < allCars.size() - 1);

        if (isCurrent) {
            btnBuyOrSelect.setText("ĐANG CHỌN");
            btnBuyOrSelect.setEnabled(false);
            btnBuyOrSelect.setBackgroundColor(Color.parseColor("#455A64"));
        } else if (isOwned) {
            btnBuyOrSelect.setText("CHỌN XE");
            btnBuyOrSelect.setEnabled(true);
            btnBuyOrSelect.setBackgroundColor(Color.parseColor("#00C853"));
        } else {
            btnBuyOrSelect.setText(String.format(Locale.US, "MUA ($%,d)", car.getPrice()));
            btnBuyOrSelect.setEnabled(playerData.getCash() >= car.getPrice());
            btnBuyOrSelect.setBackgroundColor(Color.parseColor("#E53935"));
        }

        buildUpgradesList(car, isOwned);
    }

    private void createStatViews() {
        leftStats.removeAllViews();
        rightStats.removeAllViews();
        addStatView(leftStats, "CÔNG SUẤT", R.id.tvSpecHP);
        addStatView(leftStats, "ĐỘ BÁM", R.id.tvSpecGrip);
        addStatView(leftStats, "NITRO", R.id.tvSpecNitro);
        addStatView(rightStats, "TRỌNG LƯỢNG", R.id.tvSpecWeight);
        addStatView(rightStats, "SANG SỐ", R.id.tvSpecShiftTime);
        addStatView(rightStats, "RPM", R.id.tvSpecRpmRange);
    }

    private void addStatView(LinearLayout parent, String label, int id) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setPadding(6, 3, 6, 3);
        TextView title = new TextView(this);
        title.setText(label);
        title.setTextColor(Color.WHITE);
        title.setTextSize(9);
        TextView value = new TextView(this);
        value.setId(id);
        value.setTextColor(Color.WHITE);
        value.setTextSize(11);
        value.setGravity(android.view.Gravity.END);
        row.addView(title, new LinearLayout.LayoutParams(-1, -2));
        row.addView(value, new LinearLayout.LayoutParams(-1, -2));
        parent.addView(row, new LinearLayout.LayoutParams(-1, -2));
        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setId(View.generateViewId());
        bar.setTag(id);
        bar.setMax(100);
        parent.addView(bar, new LinearLayout.LayoutParams(-1, 7));
    }

    private void buildUpgradesList(Car car, boolean isOwned) {
        upgradesContainer.removeAllViews();

        String[] types = new String[]{"engine", "turbo", "nitro", "tires", "gearbox", "weight"};
        String[] titles = new String[]{
                "Động cơ (Engine)",
                "Bộ tăng áp (Turbo)",
                "Bình Nitrous (NOS)",
                "Lốp đua bám đường (Tires)",
                "Hộp số thể thao (Gearbox)",
                "Giảm trọng lượng (Weight)"
        };

        LayoutInflater inflater = LayoutInflater.from(this);
        Typeface pixelFont = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.pixel_font);

        for (int i = 0; i < types.length; i++) {
            final String type = types[i];
            View row = inflater.inflate(R.layout.item_upgrade, upgradesContainer, false);
                GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams(
                    GridLayout.spec(i / 3, 1, 1.0f), GridLayout.spec(i % 3, 1, 1.0f));
                gridParams.width = 0;
                gridParams.setMargins(4, 4, 4, 4);
                row.setLayoutParams(gridParams);
            TextView tvTitle = row.findViewById(R.id.tvUpgradeTitle);
            TextView tvCost = row.findViewById(R.id.tvUpgradeCost);
            ImageView ivIcon = row.findViewById(R.id.ivUpgradeIcon);
            ProgressBar levelProgress = row.findViewById(R.id.progressUpgradeLevel);
            Button btnUpgrade = row.findViewById(R.id.btnUpgradeAction);

            tvTitle.setText(titles[i]);
            tvTitle.setVisibility(View.GONE);
            int level = car.getUpgradeLevel(type);
            int cost = car.getUpgradeCost(type);
            ivIcon.setImageResource(getUpgradeIcon(type));
            levelProgress.setProgress(level);
            levelProgress.setProgressTintList(android.content.res.ColorStateList.valueOf(Color.rgb(255, 193, 7)));
            levelProgress.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(70, 78, 82)));
            ivIcon.setOnClickListener(v -> {
                selectedUpgradeType = type.equals(selectedUpgradeType) ? null : type;
                updateUI();
            });
            tvCost.setVisibility(selectedUpgradeType != null && selectedUpgradeType.equals(type)
                    ? View.VISIBLE : View.GONE);
            tvCost.setText(level >= 5 ? "MAX" : String.format(Locale.US, "$%,d", cost));
            
            // Apply font to new row
            applyFontToViewTree(row, pixelFont);

            if (!isOwned) {
                btnUpgrade.setText("MUA XE TRƯỚC");
                btnUpgrade.setVisibility(View.GONE);
                btnUpgrade.setEnabled(false);
                btnUpgrade.setBackgroundColor(Color.parseColor("#424242"));
            } else if (level >= 5) {
                btnUpgrade.setText("MAX");
                btnUpgrade.setVisibility(selectedUpgradeType != null && selectedUpgradeType.equals(type)
                        ? View.VISIBLE : View.GONE);
                btnUpgrade.setEnabled(false);
                btnUpgrade.setBackgroundColor(Color.parseColor("#00E676"));
            } else {
                btnUpgrade.setText("NÂNG CẤP");
                btnUpgrade.setVisibility(selectedUpgradeType != null && selectedUpgradeType.equals(type)
                        ? View.VISIBLE : View.GONE);
                btnUpgrade.setEnabled(true);
                btnUpgrade.setOnClickListener(v -> {
                    if (!type.equals(selectedUpgradeType)) {
                        selectedUpgradeType = type;
                        updateUI();
                        return;
                    }
                    if (playerData.spendCash(cost)) {
                        car.upgrade(type);
                        playerData.saveCar(car);
                        updateUI();
                    } else {
                        Toast.makeText(this, "Không đủ tiền nâng cấp!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            upgradesContainer.addView(row);
        }
    }

    private int getUpgradeIcon(String type) {
        switch (type) {
            case "engine": return R.drawable.engine;
            case "turbo": return R.drawable.turbo;
            case "nitro": return R.drawable.nitrous;
            case "tires": return R.drawable.tyres;
            case "gearbox": return R.drawable.gearbox;
            case "weight": return R.drawable.weight;
            default: return R.drawable.engine;
        }
    }

    private int getUpgradePreviewPercent(Car car, String type, int level) {
        double value;
        double max;
        switch (type) {
            case "engine":
                value = car.getBaseHorsepower() * (1.0 + level * 0.12 + car.getTurboLevel() * 0.15);
                max = 1800.0;
                break;
            case "turbo":
                value = car.getBaseHorsepower() * (1.0 + car.getEngineLevel() * 0.12 + level * 0.15);
                max = 1800.0;
                break;
            case "nitro":
                value = 1.0 + (level > 0 ? 0.25 + (level - 1) * 0.10 : 0.0);
                max = 1.75;
                break;
            case "tires":
                value = car.getBaseGrip() * (1.0 + level * 0.12);
                max = 2.5;
                break;
            case "gearbox":
                value = 0.45 - Math.max(0.08, car.getBaseShiftTime() - level * 0.05);
                max = 0.37;
                break;
            case "weight":
                value = 1.0 - (car.getBaseWeight() * (1.0 - level * 0.04) / 2500.0);
                return clampPercent((float) (value * 100.0));
            default:
                return 0;
        }
        return clampPercent((float) (value / max * 100.0));
    }

    private int clampPercent(float value) {
        return Math.max(0, Math.min(100, Math.round(value)));
    }

    private void updateStatBars(Car car) {
        setStatBar(car, tvSpecHP, clampPercent((float) (car.getEffectiveHorsepower() / 1800.0 * 100.0)), Color.rgb(229, 57, 53));
        setStatBar(car, tvSpecWeight, clampPercent((float) ((1.0 - car.getEffectiveWeight() / 2500.0) * 100.0)), Color.rgb(255, 193, 7));
        setStatBar(car, tvSpecGrip, clampPercent((float) (car.getEffectiveGrip() / 2.5 * 100.0)), Color.rgb(0, 200, 83));
        setStatBar(car, tvSpecShiftTime, clampPercent((float) ((0.45 - car.getShiftTimeSeconds()) / 0.37 * 100.0)), Color.rgb(0, 176, 255));
        setStatBar(car, tvSpecRpmRange, clampPercent((float) (car.getMaxRpm() / 15000.0 * 100.0)), Color.rgb(255, 214, 0));
        setStatBar(car, tvSpecNitro, clampPercent((float) ((car.getNitroBoostFactor() - 1.0) / 0.75 * 100.0)), Color.rgb(124, 77, 255));
        if (selectedUpgradeType == null) {
            tvUpgradePreview.setVisibility(View.GONE);
        } else {
            int level = car.getUpgradeLevel(selectedUpgradeType);
            int cost = car.getUpgradeCost(selectedUpgradeType);
            tvUpgradePreview.setVisibility(View.VISIBLE);
            tvUpgradePreview.setText(level >= 5
                    ? "ĐÃ ĐẠT CẤP TỐI ĐA"
                    : String.format(Locale.US, "%s  |  CẤP %d -> %d  |  $%,d",
                    selectedUpgradeType.toUpperCase(Locale.US), level, level + 1, cost));
        }
    }

    private void setStatBar(Car car, TextView valueView, int percent, int color) {
        ViewGroup row = (ViewGroup) valueView.getParent();
        ViewGroup container = (ViewGroup) row.getParent();
        ProgressBar bar = null;
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof ProgressBar && valueView.getId() == (int) child.getTag()) {
                bar = (ProgressBar) child;
            }
        }
        if (bar == null) {
            bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            bar.setMax(100);
            bar.setTag(valueView.getId());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 7);
            params.topMargin = 3;
            container.addView(bar, params);
        }
        bar.setProgressTintList(android.content.res.ColorStateList.valueOf(color));
        bar.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(70, 78, 82)));
        bar.setProgress(percent);
        int preview = percent;
        if (selectedUpgradeType != null && matchesStat(selectedUpgradeType, valueView.getId())) {
            preview = getUpgradePreviewPercent(car, selectedUpgradeType,
                    Math.min(5, car.getUpgradeLevel(selectedUpgradeType) + 1));
        }
        bar.setSecondaryProgress(preview);
    }

    private boolean matchesStat(String type, int id) {
        return ("engine".equals(type) || "turbo".equals(type)) && id == R.id.tvSpecHP
                || "weight".equals(type) && id == R.id.tvSpecWeight
                || "tires".equals(type) && id == R.id.tvSpecGrip
                || "gearbox".equals(type) && id == R.id.tvSpecShiftTime
                || "nitro".equals(type) && id == R.id.tvSpecNitro;
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
        updateUI();
    }
}
