package com.breakthebrain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpBarHandler implements SceneHolder.SceneHolderHandler {
    private int mLivesCount = 5;
    private int mCoinCount = 150;
    private DrawableScene mScene;
    private DrawableObject mMat;
    private DrawableObject mHeart;
    private DrawableObject mCoin;
    private DrawableObject mBackButton;
    private DrawableObject mKeyButton;
    private DrawableObject mRedScreen;
    private DrawableObject mCompletedScreen;
    private DrawableObject mNextButton;
    private boolean mIsBackPressed = false;
    private boolean mIsKeyPressed = false;
    private boolean mIsNextPressed = false;
    private GameListener mLevelListener;
    private Context mContext;

    private Map<DrawableObject, Bitmap> mObjectsToLoadTextures = new HashMap<>();
    private List<TextureTemplate> mObjectsToLoadAnimationTextures = new ArrayList<>();

    private long mRedScreenScreenCount = 0;

    public UpBarHandler(final Context context, final GameListener levelListener) {
        mContext = context;
        mLevelListener = levelListener;
    }

    @Override
    public void processBeforeDraw(final int matrixLocation, final float[] matrix, final int colorLocation,
                                  final float scaleFactorX, final float scaleFactorY) {
        long time = System.currentTimeMillis();
        if (time - mRedScreenScreenCount > 500) {
            mRedScreen.setVisible(false);
        }
    }

    @Override
    public void processAfterDraw(int matrixLocation, float[] matrix, int colorLocation, float scaleFactorX, float scaleFactorY) {
        float curX = -0.85f;
        for (int i = 0; i < mLivesCount; i++) {
            mHeart.setX(curX);
            curX+= 0.18f;
            mHeart.draw(matrixLocation, matrix, colorLocation, scaleFactorX, scaleFactorY);
        }
        mCompletedScreen.draw(matrixLocation, matrix, colorLocation, scaleFactorX, scaleFactorY);
        mNextButton.draw(matrixLocation, matrix, colorLocation, scaleFactorX, scaleFactorY);
    }

    @Override
    public void processTouch(final MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();
        final float glX = SceneGLRenderer.getXByScreenX(x);
        final float glY = SceneGLRenderer.getYByScreenY(y);
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (mNextButton.isVisible()) {
                if (mNextButton.isInside(glX, glY)) {
                    mNextButton.animateLoop("press");
                    mIsNextPressed = true;
                }
            } else {
                if (mBackButton.isInside(glX, glY)) {
                    mBackButton.animateLoop("press");
                    mIsBackPressed = true;
                } else if (mKeyButton.isInside(glX, glY)) {
                    mKeyButton.animateLoop("press");
                    mIsKeyPressed = true;
                }
            }
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            mBackButton.stopAnimation();
            mKeyButton.stopAnimation();
            mNextButton.stopAnimation();
            if (mNextButton.isVisible()) {
                if (mIsNextPressed && mNextButton.isInside(glX, glY)) {
                    mLevelListener.onNextLevel();
                }
            } else {
                if (mIsBackPressed && mBackButton.isInside(glX, glY)) {
                    mLevelListener.onReturnToMenu();
                } else if (mIsKeyPressed && mKeyButton.isInside(glX, glY)) {
                    mLevelListener.onKeyPressed();
                }
            }
            mIsBackPressed = false;
            mIsKeyPressed = false;
            mIsNextPressed = false;
        }
    }

    public void setLivesCount(final int livesCount) {
        if (livesCount < mLivesCount) {
            mRedScreenScreenCount = System.currentTimeMillis();
            mRedScreen.setVisible(true);
        }
        mLivesCount = livesCount;
        if (mLivesCount <= 0) {
            mHeart.setVisible(false);
        }
    }

    public void levelCompleted() {
        mCompletedScreen.setVisible(true);
        mNextButton.setVisible(true);
    }

    public int getLivesCount() {
        return mLivesCount;
    }

    @Override
    public void processLevelThread() {
    }

    @Override
    public void stopLevelThread() {
    }

    @Override
    public void initLevel() {
        mScene = new DrawableScene(2);

        mRedScreen = new DrawableObject(2f, 2f);
        mRedScreen.setVisible(false);
        final Bitmap iconRedScreen = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.red_screen), SceneGLRenderer.getScreenWidth(), SceneGLRenderer.getScreenHeight());
        mObjectsToLoadTextures.put(mRedScreen, iconRedScreen);
        mScene.addToLayer(1, mRedScreen);

        mCompletedScreen = new DrawableObject(2f, 2f);
        mCompletedScreen.setVisible(false);
        final Bitmap iconCompletedScreen = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.level_completed), SceneGLRenderer.getScreenWidth(), SceneGLRenderer.getScreenHeight());
        mObjectsToLoadTextures.put(mCompletedScreen, iconCompletedScreen);
        mScene.addToLayer(1, mCompletedScreen);

        mNextButton = new DrawableObject(0.2f, 0.2f);
        mNextButton.makeSquare();
        mNextButton.setY(-0.3f);
        mNextButton.setVisible(false);
        final Bitmap iconNextButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.next);
        mObjectsToLoadTextures.put(mNextButton, iconNextButton);
        mScene.addToLayer(1, mNextButton);

        final Bitmap nextButtonAnimation = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.next_pressed);
        final TextureTemplate nextButtonAnimationTemplate = new TextureTemplate();
        nextButtonAnimationTemplate.bitmap = nextButtonAnimation;
        nextButtonAnimationTemplate.animationName = "press";
        nextButtonAnimationTemplate.object = mNextButton;
        mObjectsToLoadAnimationTextures.add(nextButtonAnimationTemplate);

        mMat = new DrawableObject(2f, 0.2f);
        mMat.setY(0.9f);
        final Bitmap iconMat = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.up_bar);
        mObjectsToLoadTextures.put(mMat, iconMat);
        mScene.addToLayer(0, mMat);

        mBackButton = new DrawableObject(0.18f, 0.18f);
        mBackButton.makeSquare();
        mBackButton.setX(0.85f);
        mBackButton.setY(0.9f);
        final Bitmap iconBackButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back);
        mObjectsToLoadTextures.put(mBackButton, iconBackButton);
        mScene.addToLayer(0, mBackButton);

        final Bitmap backButtonAnimation = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back_pressed);
        final TextureTemplate backButtonAnimationTemplate = new TextureTemplate();
        backButtonAnimationTemplate.bitmap = backButtonAnimation;
        backButtonAnimationTemplate.animationName = "press";
        backButtonAnimationTemplate.object = mBackButton;
        mObjectsToLoadAnimationTextures.add(backButtonAnimationTemplate);

        mKeyButton = new DrawableObject(0.18f, 0.18f);
        mKeyButton.makeSquare();
        mKeyButton.setX(0.55f);
        mKeyButton.setY(0.9f);
        final Bitmap iconKeyButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.key);
        mObjectsToLoadTextures.put(mKeyButton, iconKeyButton);
        mScene.addToLayer(0, mKeyButton);

        final Bitmap keyButtonAnimation = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.key_pressed);
        final TextureTemplate keyButtonAnimationTemplate = new TextureTemplate();
        keyButtonAnimationTemplate.bitmap = keyButtonAnimation;
        keyButtonAnimationTemplate.animationName = "press";
        keyButtonAnimationTemplate.object = mKeyButton;
        mObjectsToLoadAnimationTextures.add(keyButtonAnimationTemplate);

        mHeart = new DrawableObject(0.18f, 0.18f);
        mHeart.makeSquare();
        mHeart.setX(-0.85f);
        mHeart.setY(0.9f);
        final Bitmap iconHeart = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart);
        mObjectsToLoadTextures.put(mHeart, iconHeart);
        mScene.addToLayer(0, mHeart);

        final int textSize = 70;
        final Paint textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setARGB(255, 0, 0, 0);

        mCoin = new DrawableObject(0.18f, 0.18f);
        mCoin.makeSquare();
        mCoin.setX(0.2f);
        mCoin.setY(0.9f);
        final Bitmap iconCoin = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.coin), 200, 200);
        final Canvas canvas = new Canvas(iconCoin);
        canvas.drawText(mCoinCount + "", 30, 130, textPaint);
        mObjectsToLoadTextures.put(mCoin, iconCoin);
        mScene.addToLayer(0, mCoin);
    }

    public void toDefaultState() {
        mCompletedScreen.setVisible(false);
        mNextButton.setVisible(false);
        mRedScreen.setVisible(false);
    }

    @Override
    public void callLoaded() {
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

