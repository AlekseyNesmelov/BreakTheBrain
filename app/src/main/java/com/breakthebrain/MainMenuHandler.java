package com.breakthebrain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainMenuHandler implements SceneHolder.SceneHolderHandler {
    private DrawableScene mScene;
    private DrawableObject mMat;
    private DrawableObject mStartButton;
    private DrawableObject mExitButton;
    private boolean mIsStartPressed = false;
    private boolean mIsExitPressed = false;
    private GameListener mLevelListener;
    private Context mContext;

    private Map<DrawableObject, Bitmap> mObjectsToLoadTextures = new HashMap<>();
    private List<TextureTemplate> mObjectsToLoadAnimationTextures = new ArrayList<>();

    public MainMenuHandler(final Context context, final GameListener levelListener) {
        mContext = context;
        mLevelListener = levelListener;
    }

    @Override
    public void processBeforeDraw(final int matrixLocation, final float[] matrix, final int colorLocation,
                                  final float scaleFactorX, final float scaleFactorY) {
    }

    @Override
    public void processAfterDraw(int matrixLocation, float[] matrix, int colorLocation, float scaleFactorX, float scaleFactorY) {
    }

    @Override
    public void processTouch(MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();
        final float glX = SceneGLRenderer.getXByScreenX(x);
        final float glY = SceneGLRenderer.getYByScreenY(y);
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (mStartButton.isInside(glX, glY)) {
                mStartButton.animateLoop("press");
                mIsStartPressed = true;
            } else if (mExitButton.isInside(glX, glY)) {
                mExitButton.animateLoop("press");
                mIsExitPressed = true;
            }
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            mStartButton.stopAnimation();
            mExitButton.stopAnimation();
            if (mIsStartPressed && mStartButton.isInside(glX, glY)) {
                mLevelListener.onStartButtonPressed();
            } else if (mIsExitPressed && mExitButton.isInside(glX, glY)) {
                mLevelListener.onExit();
            }
            mIsStartPressed = false;
            mIsExitPressed = false;
        }
    }

    @Override
    public void processLevelThread() {

    }

    @Override
    public void stopLevelThread() {

    }

    @Override
    public void initLevel() {
        mScene = new DrawableScene(1);

        mMat = new DrawableObject(2, 2);
        final Bitmap iconMat = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.mat), SceneGLRenderer.getScreenWidth(), SceneGLRenderer.getScreenHeight());
        mObjectsToLoadTextures.put(mMat, iconMat);
        mScene.addToLayer(0, mMat);

        mStartButton = new DrawableObject(0.7f, 0.7f);
        mStartButton.makeSquare();
        final Bitmap iconStartButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.start_button);
        mObjectsToLoadTextures.put(mStartButton, iconStartButton);
        mScene.addToLayer(0, mStartButton);

        final Bitmap startButtonAnimation = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.start_button_pressed);
        final TextureTemplate startButtonAnimationTemplate = new TextureTemplate();
        startButtonAnimationTemplate.bitmap = startButtonAnimation;
        startButtonAnimationTemplate.animationName = "press";
        startButtonAnimationTemplate.object = mStartButton;
        mObjectsToLoadAnimationTextures.add(startButtonAnimationTemplate);

        mExitButton = new DrawableObject(0.3f, 0.3f);
        mExitButton.setX(0.6f);
        mExitButton.setY(0.8f);
        mExitButton.makeSquare();
        final Bitmap iconExitButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.exit_button);
        mObjectsToLoadTextures.put(mExitButton, iconExitButton);
        mScene.addToLayer(0, mExitButton);

        final Bitmap exitButtonAnimation = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.exit_button_pressed);
        final TextureTemplate exitButtonAnimationTemplate = new TextureTemplate();
        exitButtonAnimationTemplate.bitmap = exitButtonAnimation;
        exitButtonAnimationTemplate.animationName = "press";
        exitButtonAnimationTemplate.object = mExitButton;
        mObjectsToLoadAnimationTextures.add(exitButtonAnimationTemplate);
    }

    @Override
    public void callLoaded() {
        mLevelListener.onMenuLoaded();
    }

    @Override
    public DrawableScene getScene() {
        return mScene;
    }

    @Override
    public Map<DrawableObject, Bitmap> getObjectsToLoadTextures() {
        return mObjectsToLoadTextures;
    }

    @Override
    public List<TextureTemplate> getObjectsToLoadAnimationTextures() {
        return mObjectsToLoadAnimationTextures;
    }
}
