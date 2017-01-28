package com.breakthebrain;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

/**
 * OpenGL Surface View.
 */
public class SceneView extends GLSurfaceView {
    private SceneGLRenderer mRenderer;
    private GameExitListener mGameExitListener;

    public SceneView(final Context context) {
        super(context);
        init();
    }

    public SceneView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setGameExitListener(final GameExitListener listener) {
        mRenderer.setGameExitListener(listener);
    }

    @Override
    public boolean onTouchEvent(final MotionEvent e) {
        mRenderer.touchEvent(e);
        return true;
    }

    /**
     * Initiates map view.
     */
    private void init() {
        setEGLContextClientVersion(2);
        mRenderer = new SceneGLRenderer(getContext());
        setRenderer(mRenderer);
    }
}
