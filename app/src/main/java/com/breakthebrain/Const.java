package com.breakthebrain;

/**
 * Game constants.
 */
public class Const {
    // Vertex buffer size;
    public static final int VERTEX_BUFFER_SIZE = 60000;
    // Texture buffer size.
    public static final int TEXTURE_BUFFER_SIZE = 60000;

    // Square texture coordinates.
    public static final float[] SQUARE_TEXTURE_COORDINATES = new float[] {0, 1, 0, 0, 1, 0, 1, 1};

    // State numbers.
    public static final int MAIN_MENU = 0;
    public static final int LOADING = 1;
    public static final int LEVEL = 2;

    // Empty drawable object state.
    public static final String EMPTY_STATE = "";
    // Drag drawable object state.
    public static final String DRAG_ANIMATION = "drag";
    // Normal state animation.
    public static final String NORMAL_STATE_ANIMATION = "normal";

    // Delay between animation frames.
    public static final int ANIMATION_DELAY = 150;
}
