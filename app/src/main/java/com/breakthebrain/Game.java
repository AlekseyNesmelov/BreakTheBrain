package com.breakthebrain;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.microedition.khronos.opengles.GL10;

/**
 * Game class.
 */
public class Game implements GameListener{
    private static float mScaleFactorX = 1;
    private static float mScaleFactorY = 1;
    private static float mScreenWidth = 240;
    private static float mScreenHeight = 320;

    private MainMenuHandler mMainMenuHandler;
    private SceneHolder mMainMenu;

    private LoadingHandler mLoadingHandler;
    private SceneHolder mLoading;

    private UpBarHandler mUpBarHandler;
    private SceneHolder mUpBar;

    private int mLevelNumber = 1;
    private SceneHolder.SceneHolderHandler mLevelHandler;
    private SceneHolder mLevel;

    private List<TextureTemplate> mTexturesToLoad = new CopyOnWriteArrayList<>();
    private int[] mTexturesToRemove;

    private boolean mIsStateChanged = true;
    private int mState = Const.STATE_MAIN_MENU;

    private Object mLock;
    private Context mContext;

    private int mLifeCount = 5;
    private int mCoinCount = 100;

    /**
     * Game constructor.
     * @param context application context.
     * @param lock semaphore lock.
     */
    public Game(final Context context, final Object lock) {
        mContext = context;
        mLock = lock;
        //: TODO get lifes and coins from storage.
    }

    /**
     * Gets x by screen x.
     * @param screenX screen x.
     * @return openGL x.
     */
    public static float getXByScreenX(final float screenX) {
        return screenX / mScreenWidth * 2 - 1;
    }

    /**
     * Gets y by screen y.
     * @param screenY screen y.
     * @return openGL y.
     */
    public static float getYByScreenY(final float screenY) {
        return 1 - screenY / mScreenHeight * 2;
    }

    /**
     * Gets screen width.
     * @return screen width.
     */
    public static float getScreenWidth() {
        return mScreenWidth;
    }

    /**
     * Gets screen height.
     * @return screen height.
     */
    public static float getScreenHeight() {
        return mScreenHeight;
    }

    /**
     * Gets screen x factor.
     * @return screen x factor.
     */
    public static float getScreenXFactor() {
        return mScaleFactorX;
    }

    /**
     * Gets screen y factor.
     * @return screen y factor.
     */
    public static float getScreenYFactor() {
        return mScaleFactorY;
    }

    /**
     * Check game state.
     * @param vertexData vertex buffer.
     * @param textureCoordinates texture coordinates.
     */
    public void checkState(final FloatBuffer vertexData, final FloatBuffer textureCoordinates) {
        if (mIsStateChanged) {
            vertexData.clear();
            textureCoordinates.clear();
            switch (mState) {
                case Const.STATE_MAIN_MENU:
                    mMainMenuHandler = new MainMenuHandler(mContext, this);
                    mMainMenu = new SceneHolder(mMainMenuHandler);
                    mTexturesToLoad = mMainMenuHandler.getTexturesToLoad();
                    mMainMenu.putToBuffer(0, vertexData, textureCoordinates);
                    break;
                case Const.STATE_LOADING:
                    mLoadingHandler = new LoadingHandler(mContext, this, mLevelNumber);
                    mLoading = new SceneHolder(mLoadingHandler);
                    mTexturesToLoad = mLoadingHandler.getTexturesToLoad();
                    mLoading.putToBuffer(0, vertexData, textureCoordinates);
                    break;
                case Const.STATE_LEVEL:
                    int pos = mLevel.putToBuffer(0, vertexData, textureCoordinates);
                    mUpBar.putToBuffer(pos, vertexData, textureCoordinates);
                    break;
                default:
                    break;
            }
            if (mTexturesToRemove != null) {
                GLES20.glDeleteTextures(mTexturesToRemove.length, mTexturesToRemove, 0);
                mTexturesToRemove = null;
            }

            for (final TextureTemplate template : mTexturesToLoad) {
                final int type = template.getType();
                final String name = template.getName();
                final Bitmap bmp = template.getBitmap();
                final List<DrawableObject> owners = template.getOwners();
                switch (type) {
                    case TextureTemplate.ANIMATION_TEXTURE:
                        final List<Integer> textures = new ArrayList<>();
                        final int count = bmp.getWidth() / bmp.getHeight();
                        for (int i = 0; i < count; i++) {
                            final Bitmap resizedBitmap = Bitmap.createBitmap(
                                    bmp, bmp.getHeight() * i, 0, bmp.getHeight(), bmp.getHeight(), null, false);
                            final int textureId = loadTexture(resizedBitmap);
                            resizedBitmap.recycle();
                            textures.add(textureId);
                        }
                        for (final DrawableObject owner : owners) {
                            owner.addAnimation(name, textures);
                        }
                        break;
                    case TextureTemplate.SIMPLE_TEXTURE:
                        final int textureId = loadTexture(bmp);
                        for (final DrawableObject owner : owners) {
                            owner.addTexture(name, textureId);
                        }
                        break;
                }
                bmp.recycle();
            }
            mTexturesToLoad.clear();

            mIsStateChanged = false;
        }
    }

    /**
     * Draws game.
     * @param matrixLocation matrix location.
     * @param matrix model matrix.
     */
    public void draw(final int matrixLocation, final float[] matrix) {
        GLES20.glEnable(GL10.GL_BLEND);
        GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
        switch (mState) {
            case Const.STATE_MAIN_MENU:
                mMainMenu.draw(matrixLocation, matrix);
                break;
            case Const.STATE_LOADING:
                mLoading.draw(matrixLocation, matrix);
                break;
            case Const.STATE_LEVEL:
                mLevel.draw(matrixLocation, matrix);
                mUpBar.draw(matrixLocation, matrix);
                break;
            default:
                break;
        }
        GLES20.glDisable(GL10.GL_BLEND);
    }

    /**
     * Touch event.
     * @param e motion event.
     */
    public void touchEvent(final MotionEvent e) {
        switch (mState) {
            case Const.STATE_MAIN_MENU:
                mMainMenu.touchEvent(e);
                break;
            case Const.STATE_LEVEL:
                mLevel.touchEvent(e);
                mUpBar.touchEvent(e);
                break;
            default:
                break;
        }
    }

    /**
     * Recalculates projection parameters.
     * @param width screen width.
     * @param height screen height.
     */
    public void recalculateProjection(final int width, final int height) {
        float ratio;
        if (width > height) {
            ratio = (float) width / height;
            mScaleFactorX = 1;
            mScaleFactorY = ratio;
        } else {
            ratio = (float) height / width;
            mScaleFactorX = ratio;
            mScaleFactorY = 1;
        }
        mScreenWidth = width;
        mScreenHeight = height;
    }

    /**
     * Load texture from bitmap.
     * @param bitmap bitmap to load texture.
     * @return texture id.
     */
    private int loadTexture(final Bitmap bitmap) {
        final int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        return texture[0];
    }

    @Override
    public void onLevelLoaded() {

    }

    @Override
    public void onLevelMistake() {
        mUpBarHandler.setLivesCount(--mLifeCount);
    }

    @Override
    public void onLevelCompleted() {
        mUpBarHandler.levelCompleted();
    }

    @Override
    public void onReturnToMenu() {
        synchronized (mLock) {
            final int[] upBarTextures = mUpBarHandler.getScene().getTextures();
            final int[] levelTextures = mLevelHandler.getScene().getTextures();
            int c = 0;
            mTexturesToRemove = new int[upBarTextures.length + levelTextures.length];
            for (int i = 0; i < upBarTextures.length; i++) {
                mTexturesToRemove[c++] = upBarTextures[i];
            }
            for (int i = 0; i < levelTextures.length; i++) {
                mTexturesToRemove[c++] = levelTextures[i];
            }
            mState = Const.STATE_MAIN_MENU;
            mIsStateChanged = true;
        }
    }

    @Override
    public void onStartButtonPressed() {
        synchronized (mLock) {
            mTexturesToRemove = mMainMenuHandler.getScene().getTextures();
            mState = Const.STATE_LOADING;
            mIsStateChanged = true;
        }
    }

    @Override
    public void onMenuLoaded() {

    }

    @Override
    public void onLoadingCompleted() {
        synchronized (mLock) {
            mTexturesToRemove = mLoadingHandler.getScene().getTextures();
            mUpBarHandler = new UpBarHandler(mContext, this);
            mUpBar = new SceneHolder(mUpBarHandler);
            switch (mLevelNumber) {
                case 1:
                    mLevelHandler = new LevelHandler1(mContext, this);
                    mLevel = new SceneHolder(mLevelHandler);
                    break;
                case 2:
                    mLevelHandler = new LevelHandler2(mContext, this);
                    mLevel = new SceneHolder(mLevelHandler);
                    break;
                default:
                    break;
            }
            mTexturesToLoad = mLevelHandler.getTexturesToLoad();
            mTexturesToLoad.addAll(mUpBarHandler.getTexturesToLoad());
            mState = Const.STATE_LEVEL;
            mIsStateChanged = true;
        }
    }

    @Override
    public void onKeyPressed() {

    }

    @Override
    public void onNextLevel() {
        synchronized (mLock) {
            mLevelNumber++;
            final int[] upBarTextures = mUpBarHandler.getScene().getTextures();
            final int[] levelTextures = mLevelHandler.getScene().getTextures();
            int c = 0;
            mTexturesToRemove = new int[upBarTextures.length + levelTextures.length];
            for (int i = 0; i < upBarTextures.length; i++) {
                mTexturesToRemove[c++] = upBarTextures[i];
            }
            for (int i = 0; i < levelTextures.length; i++) {
                mTexturesToRemove[c++] = levelTextures[i];
            }
            mState = Const.STATE_LOADING;
            mIsStateChanged = true;
        }
    }
}
