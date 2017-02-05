package com.breakthebrain;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * Sound player class.
 */
public class SoundPlayer {
    private static MediaPlayer mp;

    /**
     * Starts playing background music.
     * @param context application context.
     */
    public static void playBackgroundMusic(final Context context) {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
        }
        mp = MediaPlayer.create(context, R.raw.main_theme);
        mp.setLooping(true);
        mp.start();
    }

    /**
     * Stops playing background music.
     */
    public static void stopBackgroundMusic() {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
        }
    }

    /**
     * Plays drag sound.
     * @param context application context.
     */
    public static void playDragSound(final Context context) {
        MediaPlayer.create(context, R.raw.drag).start();
    }

    /**
     * Plays click sound.
     * @param context application context.
     */
    public static void playClickSound(final Context context) {
        MediaPlayer.create(context, R.raw.click).start();
    }

    /**
     * Plays mistake sound.
     * @param context application context.
     */
    public static void playMistakeSound(final Context context) {
        MediaPlayer.create(context, R.raw.mistake).start();
    }

    /**
     * Plays win sound.
     * @param context application context.
     */
    public static void playWinSound(final Context context) {
        MediaPlayer.create(context, R.raw.win).start();
    }
}
