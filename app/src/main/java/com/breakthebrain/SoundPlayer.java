package com.breakthebrain;

import android.content.Context;
import android.media.MediaPlayer;

public class SoundPlayer {
    static MediaPlayer mp;
    public static void playBackgroundMusic(final Context context) {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
        }
        mp = MediaPlayer.create(context, R.raw.main_theme);
        mp.setLooping(true);
        mp.start();
    }
    public static void stopBackgroundMusic(final Context context) {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
        }
    }
    public static void playDragSound(final Context context) {
        MediaPlayer.create(context, R.raw.drag).start();
    }
    public static void playClickSound(final Context context) {
        MediaPlayer.create(context, R.raw.click).start();
    }
    public static void playMistakeSound(final Context context) {
        MediaPlayer.create(context, R.raw.mistake).start();
    }
    public static void playWinSound(final Context context) {
        MediaPlayer.create(context, R.raw.win).start();
    }
}
