package com.dragracing.game.engine;

import com.dragracing.game.data.Car;

public class CarPhysics {
    public enum ShiftResult {
        NONE, GOOD, PERFECT, OVER_REV
    }

    public enum LaunchResult {
        NONE, GOOD, PERFECT, BAD
    }

    private final Car car;
    private double performanceMultiplier = 1.0;
    private int currentGear = 1; // 1 to 6
    private double speed = 0.0; // m/s
    private double distance = 0.0; // meters
    private double rpm = 1000.0;
    private double throttle = 0.0; // 0.0 to 1.0
    private boolean isBraking = false;

    private boolean isShifting = false;
    private double shiftTimer = 0.0;
    private double shiftDuration = 0.25;

    // Nitro state
    private boolean nitroActive = false;
    private double nitroTimeRemaining = 0.0;
    private boolean nitroUsed = false;

    // Launch mechanics
    private boolean hasLaunched = false;
    private LaunchResult lastLaunchResult = LaunchResult.NONE;
    private double wheelSpinTimer = 0.0;

    // Feedback
    private ShiftResult lastShiftResult = ShiftResult.NONE;
    private double shiftFeedbackTimer = 0.0;
    private boolean isLaunchFeedback = false;

    // Visuals / Dynamics
    private float suspensionPitch = 0.0f; // degrees of tilt
    private boolean exhaustPop = false;
    private double exhaustPopTimer = 0.0;

    // Constants
    private static final double WHEEL_RADIUS = 0.32; // meters
    private static final double AIR_DENSITY = 1.225;
    private static final double DRAG_COEFFICIENT = 0.32;
    private static final double FRONTAL_AREA = 2.0;

    public CarPhysics(Car car) {
        this.car = car;
        this.rpm = car.getIdleRpm();
        this.shiftDuration = car.getShiftTimeSeconds();
        this.nitroTimeRemaining = car.getNitroDurationSeconds();
    }

    public void setThrottle(double throttle) {
        this.throttle = Math.max(0.0, Math.min(1.0, throttle));
    }

    public void setPerformanceMultiplier(double multiplier) {
        performanceMultiplier = Math.max(0.5, multiplier);
    }

    public void setBraking(boolean braking) {
        this.isBraking = braking;
    }

    public void launch() {
        if (hasLaunched) return;
        hasLaunched = true;

        // Check launch RPM quality
        int minLaunch = car.getOptimalShiftMinRpm();
        int maxLaunch = car.getOptimalShiftMaxRpm();
        if (rpm >= minLaunch && rpm <= maxLaunch) {
            lastLaunchResult = LaunchResult.PERFECT;
            wheelSpinTimer = 0.0;
        } else if (rpm > maxLaunch) {
            // Over-rev launch -> Bad Launch
            lastLaunchResult = LaunchResult.BAD;
            double grip = car.getEffectiveGrip();
            wheelSpinTimer = Math.max(0.1, 0.6 - (grip * 0.2));
        } else {
            // Under-rev -> Good Launch (or just launch)
            lastLaunchResult = LaunchResult.GOOD;
            rpm = Math.max(car.getIdleRpm(), rpm * 0.7);
        }
        shiftFeedbackTimer = 1.5;
        isLaunchFeedback = true;
    }

    public boolean activateNitro() {
        if (!hasLaunched || nitroUsed || car.getNitroLevel() <= 0 || nitroTimeRemaining <= 0.0) {
            return false;
        }
        nitroActive = true;
        nitroUsed = true;
        return true;
    }

    public ShiftResult shiftUp() {
        if (!hasLaunched || isShifting || currentGear >= car.getGearRatios().length) {
            return ShiftResult.NONE;
        }

        // Evaluate shift quality
        if (rpm < car.getOptimalShiftMinRpm()) {
            lastShiftResult = ShiftResult.GOOD;
        } else if (rpm <= car.getOptimalShiftMaxRpm()) {
            lastShiftResult = ShiftResult.PERFECT;
            // Bonus speed boost for perfect shift
            speed *= 1.025;
        } else {
            lastShiftResult = ShiftResult.OVER_REV;
            speed *= 0.96;
        }

        shiftFeedbackTimer = 1.2; // Show on screen for 1.2 seconds
        isLaunchFeedback = false;
        isShifting = true;
        shiftTimer = shiftDuration;
        currentGear++;

        // Exhaust pop on upshift
        exhaustPop = true;
        exhaustPopTimer = 0.2;

        return lastShiftResult;
    }

    public void shiftDown() {
        if (!hasLaunched || isShifting || currentGear <= 1) {
            return;
        }
        isShifting = true;
        shiftTimer = shiftDuration * 0.8;
        currentGear--;
    }

    public void update(double dt) {
        if (shiftFeedbackTimer > 0) {
            shiftFeedbackTimer -= dt;
            if (shiftFeedbackTimer <= 0) {
                lastShiftResult = ShiftResult.NONE;
            }
        }

        if (exhaustPopTimer > 0) {
            exhaustPopTimer -= dt;
            if (exhaustPopTimer <= 0) {
                exhaustPop = false;
            }
        }

        if (nitroActive) {
            nitroTimeRemaining -= dt;
            if (nitroTimeRemaining <= 0) {
                nitroActive = false;
                nitroTimeRemaining = 0;
            }
        }

        // Pre-launch staging (stationary, revving in neutral/clutch held)
        if (!hasLaunched) {
            double targetRpm = car.getIdleRpm() + throttle * (car.getMaxRpm() - car.getIdleRpm());
            
            // Adjust revSpeed based on car's horsepower
            // Low HP (~150) -> ~5250 RPM/sec (easier to control)
            // High HP (~1500) -> ~25500 RPM/sec (harder to control)
            double hp = car.getEffectiveHorsepower() * performanceMultiplier;
            double revSpeed = 3000.0 + (hp * 15.0); 
            
            if (rpm < targetRpm) {
                rpm = Math.min(targetRpm, rpm + revSpeed * dt);
            } else {
                rpm = Math.max(targetRpm, rpm - revSpeed * 0.8 * dt);
            }
            // Rev limiter bouncing
            if (rpm >= car.getMaxRpm() - 50) {
                rpm = car.getMaxRpm() - 150 + (Math.random() * 120 - 60);
            }
            return;
        }

        // During shifting
        if (isShifting) {
            shiftTimer -= dt;
            if (shiftTimer <= 0) {
                isShifting = false;
            }
        }

        double ratio = car.getGearRatios()[currentGear - 1] * car.getFinalDrive();

        // Calculate expected RPM based on vehicle speed
        if (!isShifting) {
            double wheelRps = speed / (2.0 * Math.PI * WHEEL_RADIUS);
            double calculatedRpm = wheelRps * ratio * 60.0;

            if (currentGear == 1 && calculatedRpm < car.getIdleRpm() * 1.5) {
                // Clutch slipping in 1st gear launch
                rpm = Math.max(calculatedRpm, Math.min(car.getMaxRpm(), rpm));
                if (rpm > calculatedRpm + 100) {
                    rpm -= 2500.0 * dt;
                }
            } else {
                rpm = Math.max(car.getIdleRpm(), calculatedRpm);
            }
        } else {
            // RPM drops smoothly during shift
            rpm = Math.max(car.getIdleRpm(), rpm - 7500.0 * dt);
        }

        // Rev limiter check
        boolean onRevLimiter = false;
        if (rpm >= car.getMaxRpm()) {
            rpm = car.getMaxRpm();
            onRevLimiter = true;
        }

        // Wheel spin countdown
        double effectiveGrip = car.getEffectiveGrip();
        if (wheelSpinTimer > 0) {
            wheelSpinTimer -= dt;
            effectiveGrip *= 0.65; // Reduced traction while spinning
        }

        // Drive Force calculation
        double driveForce = 0.0;
        if (throttle > 0 && !isShifting && !onRevLimiter) {
            double effectiveHp = car.getEffectiveHorsepower() * performanceMultiplier;
            if (nitroActive) {
                effectiveHp *= car.getNitroBoostFactor();
            }

            // Power curve: peaks at ~80% of max RPM
            double rpmRatio = Math.max(0.1, rpm / car.getMaxRpm());
            double powerFactor = Math.sin(Math.min(Math.PI, rpmRatio * (Math.PI / 0.85)));
            if (powerFactor < 0.25) powerFactor = 0.25;

            double enginePowerWatts = (effectiveHp * 745.7) * powerFactor * throttle;
            double engineRadPerSec = Math.max(50.0, rpm * (Math.PI / 30.0));
            double engineTorque = enginePowerWatts / engineRadPerSec;

            double wheelTorque = engineTorque * ratio * 0.90; // 90% drivetrain efficiency
            driveForce = (wheelTorque / WHEEL_RADIUS) * effectiveGrip;

            // Traction limit: normal force * friction
            double maxTractionForce = car.getEffectiveWeight() * 9.81 * effectiveGrip * 1.35;
            if (driveForce > maxTractionForce) {
                driveForce = maxTractionForce;
            }
        }

        // Resistance forces
        double dragForce = 0.5 * AIR_DENSITY * DRAG_COEFFICIENT * FRONTAL_AREA * speed * speed;
        double rollingResistance = 0.015 * car.getEffectiveWeight() * 9.81;
        double brakingForce = isBraking ? 12000.0 : 0.0; // Strong braking force

        double netForce = driveForce - dragForce - rollingResistance - brakingForce;
        if (speed <= 0.01 && netForce < 0) {
            netForce = 0;
        }

        double mass = car.getEffectiveWeight();
        double acceleration = netForce / mass;

        speed = Math.max(0.0, speed + acceleration * dt);
        distance += speed * dt;

        // Suspension pitch calculation (squats under acceleration, dips when shifting)
        float targetPitch = (float) Math.max(-1.5f, Math.min(3.5f, acceleration * 0.35f));
        if (isShifting) targetPitch = -0.8f;
        suspensionPitch += (targetPitch - suspensionPitch) * 10.0f * (float) dt;
    }

    // Getters
    public Car getCar() { return car; }
    public int getCurrentGear() { return currentGear; }
    public double getSpeed() { return speed; }
    public double getSpeedKmh() { return speed * 3.6; }
    public double getDistance() { return distance; }
    public double getRpm() { return rpm; }
    public double getThrottle() { return throttle; }
    public boolean isShifting() { return isShifting; }
    public boolean isNitroActive() { return nitroActive; }
    public boolean isNitroUsed() { return nitroUsed; }
    public double getNitroTimeRemaining() { return nitroTimeRemaining; }
    public double getNitroPercent() {
        double max = car.getNitroDurationSeconds();
        if (max <= 0) return 0.0;
        return Math.max(0.0, Math.min(1.0, nitroTimeRemaining / max));
    }
    public boolean isHasLaunched() { return hasLaunched; }
    public LaunchResult getLastLaunchResult() { return lastLaunchResult; }
    public boolean isPerfectLaunch() { return lastLaunchResult == LaunchResult.PERFECT; }
    public boolean isWheelSpinning() { return wheelSpinTimer > 0; }
    public ShiftResult getLastShiftResult() { return lastShiftResult; }
    public boolean isLaunchFeedback() { return isLaunchFeedback; }
    public float getSuspensionPitch() { return suspensionPitch; }
    public boolean isExhaustPop() { return exhaustPop || nitroActive; }
}
