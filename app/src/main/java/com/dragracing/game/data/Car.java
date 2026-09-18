package com.dragracing.game.data;

import android.graphics.Color;
import java.io.Serializable;

public class Car implements Serializable {
    public enum BodyType {
        TUNER,
        MUSCLE,
        SUPER,
        DRAGSTER,
        CLASSIC
    }

    private String id;
    private String name;
    private String carClass;
    private int price;
    private int color;
    private double baseHorsepower;
    private double baseWeight; // in kg
    private double baseGrip; // 0.8 to 1.5
    private double baseShiftTime; // 0.1 to 0.4
    private int maxRpm;
    private int idleRpm;
    private int optimalShiftMinRpm;
    private int optimalShiftMaxRpm;
    private double[] gearRatios;
    private double finalDrive;
    private BodyType bodyType;
    private String imageResourceName;

    // Upgrade levels: 0 to 5
    private int engineLevel = 0;
    private int turboLevel = 0;
    private int nitroLevel = 0;
    private int tiresLevel = 0;
    private int gearboxLevel = 0;
    private int weightLevel = 0;

    public Car(String id, String name, String carClass, int price, int color,
               double baseHorsepower, double baseWeight, double baseGrip, double baseShiftTime,
               int maxRpm, int idleRpm, int optimalShiftMinRpm, int optimalShiftMaxRpm,
               double[] gearRatios, double finalDrive, BodyType bodyType) {
            this(id, name, carClass, price, color, baseHorsepower, baseWeight, baseGrip, baseShiftTime,
                maxRpm, idleRpm, optimalShiftMinRpm, optimalShiftMaxRpm,
                gearRatios, finalDrive, bodyType, null);
            }

            public Car(String id, String name, String carClass, int price, int color,
                   double baseHorsepower, double baseWeight, double baseGrip, double baseShiftTime,
                   int maxRpm, int idleRpm, int optimalShiftMinRpm, int optimalShiftMaxRpm,
                   double[] gearRatios, double finalDrive, BodyType bodyType,
                   String imageResourceName) {
        this.id = id;
        this.name = name;
        this.carClass = carClass;
        this.price = price;
        this.color = color;
        this.baseHorsepower = baseHorsepower;
        this.baseWeight = baseWeight;
        this.baseGrip = baseGrip;
        this.baseShiftTime = baseShiftTime;
        this.maxRpm = maxRpm;
        this.idleRpm = idleRpm;
        this.optimalShiftMinRpm = optimalShiftMinRpm;
        this.optimalShiftMaxRpm = optimalShiftMaxRpm;
        this.gearRatios = gearRatios;
        this.finalDrive = finalDrive;
        this.bodyType = bodyType;
        this.imageResourceName = imageResourceName;
    }

    // Effective stats calculating upgrades
    public double getEffectiveHorsepower() {
        // Engine gives +12% per level, Turbo gives +15% per level
        double multiplier = 1.0 + (engineLevel * 0.12) + (turboLevel * 0.15);
        return baseHorsepower * multiplier;
    }

    public double getEffectiveWeight() {
        // Weight reduction gives -4% weight per level
        double reductionFactor = 1.0 - (weightLevel * 0.04);
        return baseWeight * reductionFactor;
    }

    public double getEffectiveGrip() {
        // Tires give +12% grip per level
        return baseGrip * (1.0 + tiresLevel * 0.12);
    }

    public double getShiftTimeSeconds() {
        // baseShiftTime level reduces it down to 0.08s minimum
        return Math.max(0.08, baseShiftTime - (gearboxLevel * 0.05));
    }

    public double getNitroBoostFactor() {
        if (nitroLevel <= 0) return 1.0;
        // Nitro adds 25% to 75% acceleration boost
        return 1.25 + (nitroLevel - 1) * 0.10;
    }

    public double getNitroDurationSeconds() {
        if (nitroLevel <= 0) return 0.0;
        return 2.5 + (nitroLevel * 0.5); // 3.0s to 5.0s
    }

    public int getUpgradeCost(String upgradeType) {
        int level = getUpgradeLevel(upgradeType);
        if (level >= 5) return 0; // Maxed out
        int baseCost = price / 5;
        if (baseCost < 300) baseCost = 300;
        return (int) (baseCost * (1.0 + level * 0.75));
    }

    public int getUpgradeLevel(String upgradeType) {
        switch (upgradeType) {
            case "engine": return engineLevel;
            case "turbo": return turboLevel;
            case "nitro": return nitroLevel;
            case "tires": return tiresLevel;
            case "gearbox": return gearboxLevel;
            case "weight": return weightLevel;
            default: return 0;
        }
    }

    public boolean upgrade(String upgradeType) {
        switch (upgradeType) {
            case "engine":
                if (engineLevel < 5) { engineLevel++; return true; }
                break;
            case "turbo":
                if (turboLevel < 5) { turboLevel++; return true; }
                break;
            case "nitro":
                if (nitroLevel < 5) { nitroLevel++; return true; }
                break;
            case "tires":
                if (tiresLevel < 5) { tiresLevel++; return true; }
                break;
            case "gearbox":
                if (gearboxLevel < 5) { gearboxLevel++; return true; }
                break;
            case "weight":
                if (weightLevel < 5) { weightLevel++; return true; }
                break;
        }
        return false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCarClass() { return carClass; }
    public int getPrice() { return price; }
    public int getColor() { return color; }
    public void setColor(int color) { this.color = color; }
    public double getBaseHorsepower() { return baseHorsepower; }
    public double getBaseWeight() { return baseWeight; }
    public double getBaseGrip() { return baseGrip; }
    public double getBaseShiftTime() { return baseShiftTime; }
    public int getMaxRpm() { return maxRpm; }
    public int getIdleRpm() { return idleRpm; }
    public int getOptimalShiftMinRpm() { return optimalShiftMinRpm; }
    public int getOptimalShiftMaxRpm() { return optimalShiftMaxRpm; }
    public double[] getGearRatios() { return gearRatios; }
    public double getFinalDrive() { return finalDrive; }
    public BodyType getBodyType() { return bodyType; }
    public String getImageResourceName() { return imageResourceName; }
    public void setImageResourceName(String imageResourceName) { this.imageResourceName = imageResourceName; }

    public int getEngineLevel() { return engineLevel; }
    public void setEngineLevel(int engineLevel) { this.engineLevel = engineLevel; }
    public int getTurboLevel() { return turboLevel; }
    public void setTurboLevel(int turboLevel) { this.turboLevel = turboLevel; }
    public int getNitroLevel() { return nitroLevel; }
    public void setNitroLevel(int nitroLevel) { this.nitroLevel = nitroLevel; }
    public int getTiresLevel() { return tiresLevel; }
    public void setTiresLevel(int tiresLevel) { this.tiresLevel = tiresLevel; }
    public int getGearboxLevel() { return gearboxLevel; }
    public void setGearboxLevel(int gearboxLevel) { this.gearboxLevel = gearboxLevel; }
    public int getWeightLevel() { return weightLevel; }
    public void setWeightLevel(int weightLevel) { this.weightLevel = weightLevel; }
}
