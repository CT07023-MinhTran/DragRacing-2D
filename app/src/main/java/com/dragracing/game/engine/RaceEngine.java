package com.dragracing.game.engine;

import com.dragracing.game.data.Car;

public class RaceEngine {
    public enum RaceState {
        STAGING,
        COUNTDOWN,
        RACING,
        FINISHED
    }

    public static final double DISTANCE_1_4 = 402.336;
    public static final double DISTANCE_1_2 = 804.672;
    public static final double DISTANCE_1 = 1609.344;
    public static final double DISTANCE_2 = 3218.688;

    private final double raceDistance;
    private final CarPhysics playerCar;
    private final CarPhysics opponentCar;
    private final String raceMode; // "QUICK", "CAREER", "TEST"
    private final int difficultyLevel; // 1 to 5
    private final boolean playerWinTarget;

    private RaceState state = RaceState.STAGING;
    private double raceTimer = 0.0;
    private double countdownTimer = 0.0;

    // Christmas Tree Lights (0: off, 1: yellow1, 2: yellow2, 3: yellow3, 4: green, 5: red false start)
    private int treeLightState = 0;
    private boolean falseStart = false;

    // AI timing
    private double aiReactionTimer = 0.0;
    private double aiReactionDelay = 0.35;
    private boolean aiLaunched = false;
    private boolean aiUsedNitro = false;

    // Player metrics
    private double playerReactionTime = 0.0;
    private double playerElapsedTime = 0.0;
    private double playerTopSpeedKmh = 0.0;
    private int playerPerfectShifts = 0;
    private int playerGoodShifts = 0;
    private boolean playerFinished = false;

    // Opponent metrics
    private double opponentElapsedTime = 0.0;
    private double opponentTopSpeedKmh = 0.0;
    private boolean opponentFinished = false;

    private boolean playerWon = false;

    public RaceEngine(Car playerCarData, Car opponentCarData, String raceMode, int difficultyLevel, double raceDistance) {
        this.playerCar = new CarPhysics(playerCarData);
        this.opponentCar = opponentCarData != null ? new CarPhysics(opponentCarData) : null;
        this.raceMode = raceMode;
        this.difficultyLevel = difficultyLevel;
        this.raceDistance = raceDistance;
        this.playerWinTarget = "QUICK".equals(raceMode)
                && Math.random() < getPlayerWinChance(difficultyLevel);

        // Tune AI reaction delay based on difficulty
        this.aiReactionDelay = Math.max(0.08, 0.40 - (difficultyLevel * 0.06));
        if (opponentCar != null && "QUICK".equals(raceMode)) {
            opponentCar.setPerformanceMultiplier(playerWinTarget ? 0.90 : 1.10);
            if (playerWinTarget) {
                aiReactionDelay += 0.12;
            }
        }
    }

    private double getPlayerWinChance(int difficulty) {
        switch (difficulty) {
            case 1: return 0.90;
            case 2: return 0.50;
            case 3: return 0.15;
            case 4: return 0.05;
            default: return 0.50;
        }
    }

    public void startCountdown() {
        if (state == RaceState.STAGING) {
            state = RaceState.COUNTDOWN;
            countdownTimer = 0.0;
            treeLightState = 0;
        }
    }

    public void playerLaunch() {
        if (state == RaceState.COUNTDOWN) {
            if (treeLightState < 4) {
                // False Start!
                falseStart = true;
                treeLightState = 5; // RED light
                state = RaceState.FINISHED;
                playerFinished = true;
                playerWon = false;
                return;
            }
        }

        if (state == RaceState.RACING && !playerCar.isHasLaunched()) {
            playerCar.launch();
            playerReactionTime = raceTimer;
        }
    }

    public CarPhysics.ShiftResult playerShiftUp() {
        if (state != RaceState.RACING) return CarPhysics.ShiftResult.NONE;
        CarPhysics.ShiftResult res = playerCar.shiftUp();
        if (res == CarPhysics.ShiftResult.PERFECT) {
            playerPerfectShifts++;
        } else if (res == CarPhysics.ShiftResult.GOOD || res == CarPhysics.ShiftResult.OVER_REV) {
            playerGoodShifts++;
        }
        return res;
    }

    public void playerShiftDown() {
        if (state == RaceState.RACING) {
            playerCar.shiftDown();
        }
    }

    public boolean playerNitro() {
        if (state == RaceState.RACING) {
            return playerCar.activateNitro();
        }
        return false;
    }

    public void update(double dt) {
        // Countdown handling
        if (state == RaceState.COUNTDOWN) {
            countdownTimer += dt;
            if (countdownTimer >= 2.0) {
                treeLightState = 4; // GREEN!
                state = RaceState.RACING;
                raceTimer = 0.0;
                if (!playerCar.isHasLaunched()) {
                    playerCar.launch();
                    playerReactionTime = 0.0;
                }
            } else if (countdownTimer >= 1.5) {
                treeLightState = 3; // Yellow 3
            } else if (countdownTimer >= 1.0) {
                treeLightState = 2; // Yellow 2
            } else if (countdownTimer >= 0.5) {
                treeLightState = 1; // Yellow 1
            }

            // Staging revving
            playerCar.update(dt);
            if (opponentCar != null) {
                opponentCar.setThrottle(0.85);
                opponentCar.update(dt);
            }
            return;
        }

        if (state == RaceState.RACING) {
            raceTimer += dt;

            // Player car update
            playerCar.update(dt);
            if (playerCar.getSpeedKmh() > playerTopSpeedKmh) {
                playerTopSpeedKmh = playerCar.getSpeedKmh();
            }

            if (!playerFinished && playerCar.getDistance() >= raceDistance) {
                playerFinished = true;
                playerElapsedTime = raceTimer;
                if (opponentCar == null || !opponentFinished) {
                    playerWon = true;
                }
            }

            // AI Opponent update
            if (opponentCar != null) {
                aiReactionTimer += dt;
                if (!aiLaunched && aiReactionTimer >= aiReactionDelay) {
                    aiLaunched = true;
                    opponentCar.launch();
                }

                if (aiLaunched) {
                    opponentCar.setThrottle(playerWinTarget ? 0.92 : 1.0);

                    // AI shifting logic
                    int shiftTargetRpm = opponentCar.getCar().getOptimalShiftMinRpm() + (difficultyLevel * 100);
                    if (opponentCar.getRpm() >= shiftTargetRpm) {
                        opponentCar.shiftUp();
                    }

                    // AI Nitro in 2nd or 3rd gear
                    if (!aiUsedNitro && opponentCar.getCurrentGear() >= 2 && Math.random() < 0.2) {
                        aiUsedNitro = true;
                        opponentCar.activateNitro();
                    }
                }

                opponentCar.update(dt);
                if (opponentCar.getSpeedKmh() > opponentTopSpeedKmh) {
                    opponentTopSpeedKmh = opponentCar.getSpeedKmh();
                }

                if (!opponentFinished && opponentCar.getDistance() >= raceDistance) {
                    opponentFinished = true;
                    opponentElapsedTime = raceTimer;
                    if (!playerFinished) {
                        playerWon = false;
                    }
                }
            }

            // Check if race is over
            // We wait until both cars have crossed the finish line COMPLETELY before showing results.
            // After crossing the line, cars will decelerate to 0.
            double finishBuffer = 8.0;
            boolean playerPast = playerCar.getDistance() >= raceDistance + finishBuffer;
            boolean opponentPast = opponentCar == null || opponentCar.getDistance() >= raceDistance + finishBuffer;

            if (playerFinished) {
                playerCar.setThrottle(0.0);
                if (playerPast) {
                    playerCar.setBraking(true);
                }
            }

            if (opponentCar != null && opponentFinished) {
                opponentCar.setThrottle(0.0);
                if (opponentPast) {
                    opponentCar.setBraking(true);
                }
            }

            if (playerFinished && playerCar.getSpeed() <= 0.1) {
                state = RaceState.FINISHED;
            }
        }
    }

    public int calculatePrizeMoney() {
        if (falseStart) return 50;
        // Adjust prize based on distance
        double distanceMultiplier = 1.0 + (raceDistance / DISTANCE_1_4 - 1.0) * 0.5;
        int basePrize = playerWon ? (800 + difficultyLevel * 400) : (250 + difficultyLevel * 100);
        
        // Bonus for Career Boss races
        if (playerWon && "CAREER".equals(raceMode)) {
            basePrize *= 1.5;
        }

        int prize = (int) (basePrize * distanceMultiplier);
        
        if (playerCar.isPerfectLaunch()) prize += 250;
        prize += playerPerfectShifts * 100;
        prize += playerGoodShifts * 40;
        return prize;
    }

    // Getters
    public CarPhysics getPlayerCar() { return playerCar; }
    public CarPhysics getOpponentCar() { return opponentCar; }
    public RaceState getState() { return state; }
    public int getTreeLightState() { return treeLightState; }
    public boolean isFalseStart() { return falseStart; }
    public double getRaceTimer() { return raceTimer; }
    public double getPlayerReactionTime() { return playerReactionTime; }
    public double getPlayerElapsedTime() { return playerElapsedTime; }
    public double getPlayerTopSpeedKmh() { return playerTopSpeedKmh; }
    public double getOpponentElapsedTime() { return opponentElapsedTime; }
    public double getOpponentTopSpeedKmh() { return opponentTopSpeedKmh; }
    public int getPlayerPerfectShifts() { return playerPerfectShifts; }
    public boolean isPlayerWon() { return playerWon; }
    public boolean isPlayerFinished() { return playerFinished; }
    public double getRaceDistance() { return raceDistance; }
}
