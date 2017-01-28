package com.breakthebrain;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

/**
 * Scene holder class.
 */
public class SceneHolder {
    private SceneHolderThread mSceneHolderThread = new SceneHolderThread();
    private SceneHolderHandler mSceneHolderHandler;
    private DrawableScene mScene;

    public SceneHolder(final SceneHolderHandler levelHandler) {
        levelHandler.initLevel();
        mSceneHolderHandler = levelHandler;
        mScene = levelHandler.getScene();
    }

    public void touchEvent(final MotionEvent e) {
        mSceneHolderHandler.processTouch(e);
        mScene.touchEvent(e);
    }

    public void startLevelThread() {
        mSceneHolderThread.start();
    }

    public int putToBuffer(final int startPos, final FloatBuffer vertexData, final FloatBuffer textureCoordinates) {
        return mScene.putToBuffer(startPos, vertexData, textureCoordinates);
    }

    public void draw(final int matrixLocation, final float[] matrix, final int colorLocation,
                     final float scaleFactorX, final float scaleFactorY) {
        mSceneHolderHandler.processBeforeDraw(matrixLocation, matrix, colorLocation, scaleFactorX, scaleFactorY);
        mScene.draw(matrixLocation, matrix, colorLocation, scaleFactorX, scaleFactorY);
        mSceneHolderHandler.processAfterDraw(matrixLocation, matrix, colorLocation, scaleFactorX, scaleFactorY);
    }

    private class SceneHolderThread extends Thread{
        @Override
        public void run() {
            mSceneHolderHandler.processLevelThread();
        }
    }

    /**
     * Scene holder handler.
     */
    public interface SceneHolderHandler {
        void processBeforeDraw(final int matrixLocation, final float[] matrix, final int colorLocation,
                               final float scaleFactorX, final float scaleFactorY);
        void processAfterDraw(final int matrixLocation, final float[] matrix, final int colorLocation,
                         final float scaleFactorX, final float scaleFactorY);
        void processTouch(final MotionEvent e);
        void processLevelThread();
        void stopLevelThread();
        void initLevel();
        DrawableScene getScene();
        void callLoaded();
        Map<DrawableObject, Bitmap> getObjectsToLoadTextures();
        List<TextureTemplate> getObjectsToLoadAnimationTextures();
    }
}
