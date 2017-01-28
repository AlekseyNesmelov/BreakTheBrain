package com.breakthebrain;

/**
 * Game events listener.
 */
public interface GameListener {
    // Level loading completed.
    void onLevelLoaded();
    // Level mistake.
    void onLevelMistake();
    // Level completed.
    void onLevelCompleted();
    // Return to menu from level.
    void onReturnToMenu();
    // Start button pressed.
    void onStartButtonPressed();
    // Menu loaded.
    void onMenuLoaded();
    // Loading menu loaded.
    void onLoadingLoaded();
    // Loading completed.
    void onLoadingCompleted();
    // Key pressed.
    void onKeyPressed();
    // To next level.
    void onNextLevel();
    // Exit game.
    void onExit();
}
