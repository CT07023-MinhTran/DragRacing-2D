package com.dragracing.game.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class SoundManager {
    private static SoundManager instance;
    private final Context context;
    private final Vibrator vibrator;

    private boolean soundEnabled = true;
    private boolean hapticsEnabled = true;

    // Real-time engine sound generator using AudioTrack
    private AudioTrack engineTrack;
    private Thread audioThread;
    private volatile boolean isRunning = false;
    private volatile double currentRpm = 1000.0;
    private volatile boolean isNitroPlaying = false;
    private volatile float engineVolume = 1.0f;

    private static final int SAMPLE_RATE = 22050;

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public static synchronized SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void startEngineAudio() {
        if (!soundEnabled || isRunning) return;
        isRunning = true;

        int bufferSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
        );
        if (bufferSize <= 0) bufferSize = 4096;

        try {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            AudioFormat format = new AudioFormat.Builder()
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .build();

            engineTrack = new AudioTrack(
                    attributes,
                    format,
                    bufferSize,
                    AudioTrack.MODE_STREAM,
                    AudioManager.AUDIO_SESSION_ID_GENERATE
            );

            engineTrack.play();
            engineVolume = 1.0f;

            audioThread = new Thread(() -> {
                short[] buffer = new short[512];
                double phase = 0.0;
                double phase2 = 0.0;
                double noisePhase = 0.0;

                while (isRunning) {
                    // Fundamental engine cylinder firing frequency (RPM / 60 * 2 for 4-cylinder, etc.)
                    double freq = Math.max(35.0, (currentRpm / 60.0) * 2.5);
                    double phaseInc = 2.0 * Math.PI * freq / SAMPLE_RATE;
                    double phase2Inc = 2.0 * Math.PI * (freq * 2.0) / SAMPLE_RATE;

                    float vol = engineVolume;

                    for (int i = 0; i < buffer.length; i++) {
                        // Blend fundamental triangle/sawtooth wave + harmonic + slight noise
                        double val1 = Math.sin(phase);
                        double val2 = 0.45 * Math.sin(phase2);
                        double distortion = (val1 > 0 ? 0.3 : -0.3);
                        double sample = (val1 + val2 + distortion) * 0.45;

                        if (isNitroPlaying) {
                            // Add white noise for nitro hiss
                            double noise = (Math.random() * 2.0 - 1.0) * 0.4;
                            sample = sample * 0.7 + noise;
                        }

                        // Apply current fading volume factor
                        sample *= vol;

                        // Scale to 16-bit PCM
                        buffer[i] = (short) (sample * 16000.0);

                        phase += phaseInc;
                        if (phase > 2.0 * Math.PI) phase -= 2.0 * Math.PI;

                        phase2 += phase2Inc;
                        if (phase2 > 2.0 * Math.PI) phase2 -= 2.0 * Math.PI;
                    }

                    if (engineTrack != null && engineTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        engineTrack.write(buffer, 0, buffer.length);
                    }
                }
            });
            audioThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateEngineRpm(double rpm, boolean nitroActive) {
        this.currentRpm = rpm;
        this.isNitroPlaying = nitroActive;
    }

    public void setEngineVolume(float volume) {
        this.engineVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public void stopEngineAudio() {
        isRunning = false;
        if (audioThread != null) {
            try {
                audioThread.join(250);
            } catch (InterruptedException ignored) {}
            audioThread = null;
        }
        if (engineTrack != null) {
            try {
                engineTrack.stop();
                engineTrack.release();
            } catch (Exception ignored) {}
            engineTrack = null;
        }
    }

    public void playBeep(float frequency, int durationMs) {
        if (!soundEnabled) return;
        new Thread(() -> {
            try {
                int samples = (SAMPLE_RATE * durationMs) / 1000;
                short[] buffer = new short[samples];
                double phaseInc = 2.0 * Math.PI * frequency / SAMPLE_RATE;
                double phase = 0;
                for (int i = 0; i < samples; i++) {
                    double envelope = (i < 50 ? (double) i / 50.0 : (double) (samples - i) / (double) samples);
                    buffer[i] = (short) (Math.sin(phase) * 18000.0 * envelope);
                    phase += phaseInc;
                }
                AudioTrack toneTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC, SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT,
                        buffer.length * 2, AudioTrack.MODE_STATIC
                );
                toneTrack.write(buffer, 0, buffer.length);
                toneTrack.play();
                Thread.sleep(durationMs + 50);
                toneTrack.release();
            } catch (Exception ignored) {}
        }).start();
    }

    public void playShiftSound() {
        playBeep(280.0f, 60);
        vibrate(35);
    }

    public void playPerfectShiftSound() {
        playBeep(880.0f, 100);
        vibrate(50);
    }

    public void playLaunchSound() {
        vibrate(90);
    }

    public void playWinSound() {
        new Thread(() -> {
            playBeep(523.25f, 120); // C5
            try { Thread.sleep(130); } catch (Exception ignored) {}
            playBeep(659.25f, 120); // E5
            try { Thread.sleep(130); } catch (Exception ignored) {}
            playBeep(783.99f, 240); // G5
        }).start();
        vibrate(150);
    }

    public void vibrate(long milliseconds) {
        if (!hapticsEnabled || vibrator == null || !vibrator.hasVibrator()) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(milliseconds);
            }
        } catch (Exception ignored) {}
    }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        if (!soundEnabled) stopEngineAudio();
    }
    public boolean isHapticsEnabled() { return hapticsEnabled; }
    public void setHapticsEnabled(boolean hapticsEnabled) { this.hapticsEnabled = hapticsEnabled; }
}
