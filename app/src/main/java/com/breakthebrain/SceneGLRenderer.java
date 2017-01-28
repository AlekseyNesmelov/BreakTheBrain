package com.breakthebrain;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.MotionEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenGL renderer class.
 */
public class SceneGLRenderer implements GLSurfaceView.Renderer, GameListener {
    private MainMenuHandler mMainMenuHandler;
    private SceneHolder mMainMenu;

    private LoadingHandler mLoadingHandler;
    private SceneHolder mLoading;

    private UpBarHandler mUpBarHandler;
    private SceneHolder mUpBar;

    private int mLevelNumber = 1;
    private SceneHolder.SceneHolderHandler mLevelHandler;
    private SceneHolder mLevel;

    private Context mContext;
    private FloatBuffer mVertexData;
    private FloatBuffer mTextureCoordinates;
    private float[] mMatrix = new float[16];
    private int mColorLocation;
    private int mMatrixLocation;
    private int mPositionLocation;
    private int mATextureLocation;
    private int mUTextureUnitLocation;
    private int mProgramId;

    private Map<DrawableObject, Bitmap> mObjectsToLoadTextures = new HashMap<>();
    private List<TextureTemplate> mObjectsToLoadAnimationTextures = new ArrayList<>();
    private int[] texturesToRemove;

    private float mScaleFactorX = 1;
    private float mScaleFactorY = 1;
    private static float mScreenWidth;
    private static float mScreenHeight;

    private final Object mLock = new Object();

    private GameExitListener mGameExitListener;

    private boolean mIsStateChanged = false;
    private int mState = -1;

    public SceneGLRenderer(final Context context) {
        mContext = context;
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        synchronized (mLock) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

            if (mIsStateChanged) {
                if (texturesToRemove != null) {
                    String s  ="";
                    for (int i = 0; i < texturesToRemove.length; i++) s+= texturesToRemove[i] + ", ";
                    Log.d("Textures removed", s + "");
                    GLES20.glDeleteTextures(texturesToRemove.length, texturesToRemove, 0);
                    texturesToRemove = null;
                }

                for (final DrawableObject obj : mObjectsToLoadTextures.keySet()) {
                    final Bitmap bmp = mObjectsToLoadTextures.get(obj);
                    final int textureId = loadTexture(bmp);
                    bmp.recycle();
                    obj.setTextureId(textureId);
                }
                mObjectsToLoadTextures.clear();

                for (final TextureTemplate animationTemplate : mObjectsToLoadAnimationTextures) {
                    final Bitmap bmp = animationTemplate.bitmap;
                    final List<Integer> textures = new ArrayList<>();
                    final int count = bmp.getWidth() / bmp.getHeight();
                    for (int i = 0; i < count; i++) {
                        final Bitmap resizedBitmap = Bitmap.createBitmap(
                                bmp, bmp.getHeight() * i, 0, bmp.getHeight(), bmp.getHeight(), null, false);
                        final int textureId = loadTexture(resizedBitmap);
                        Log.d("New texture loaded", textureId + "");
                        resizedBitmap.recycle();
                        textures.add(textureId);
                    }
                    bmp.recycle();
                    animationTemplate.object.addAnimation(animationTemplate.animationName, textures);
                }
                mObjectsToLoadAnimationTextures.clear();

                switch (mState) {
                    case Const.MAIN_MENU:
                        mVertexData.clear();
                        mTextureCoordinates.clear();
                        mMainMenu.putToBuffer(0, mVertexData, mTextureCoordinates);
                        break;
                    case Const.LOADING:
                        mVertexData.clear();
                        mTextureCoordinates.clear();
                        mLoading.putToBuffer(0, mVertexData, mTextureCoordinates);
                        break;
                    case Const.LEVEL:
                        mVertexData.clear();
                        mTextureCoordinates.clear();
                        int pos = mLevel.putToBuffer(0, mVertexData, mTextureCoordinates);
                        mUpBar.putToBuffer(pos, mVertexData, mTextureCoordinates);
                        break;
                    default:
                        break;
                }

                mIsStateChanged = false;
            }

            GLES20.glEnable(GL10.GL_BLEND);
            GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
            switch (mState) {
                case Const.MAIN_MENU:
                    mMainMenu.draw(mMatrixLocation, mMatrix, mColorLocation, mScaleFactorX, mScaleFactorY);
                    break;
                case Const.LOADING:
                    mLoading.draw(mMatrixLocation, mMatrix, mColorLocation, mScaleFactorX, mScaleFactorY);
                    break;
                case Const.LEVEL:
                    mLevel.draw(mMatrixLocation, mMatrix, mColorLocation, mScaleFactorX, mScaleFactorY);
                    mUpBar.draw(mMatrixLocation, mMatrix, mColorLocation, mScaleFactorX, mScaleFactorY);
                    break;
                default:
                    break;
            }
            GLES20.glDisable(GL10.GL_BLEND);
        }
    }

    @Override
    public void onSurfaceCreated(final GL10 arg0, final EGLConfig arg1) {
        GLES20.glClearColor(1f, 1f, 1f, 1f);
        final int vertexShaderId = Shader.createShader(GLES20.GL_VERTEX_SHADER, Shader.VERTEX_SHADER);
        final int fragmentShaderId = Shader.createShader(GLES20.GL_FRAGMENT_SHADER, Shader.FRAGMENT_SHADER);
        mProgramId = Shader.createProgram(vertexShaderId, fragmentShaderId);
        GLES20.glUseProgram(mProgramId);
        prepareBuffers();
        bindData();
    }

    @Override
    public void onSurfaceChanged(final GL10 arg0, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
        createProjection(width, height);
        loadMainMenu();
        mMainMenuHandler.callLoaded();
    }

    public void setGameExitListener(final GameExitListener listener) {
        mGameExitListener = listener;
    }

    public void loadMainMenu() {
        synchronized (mLock) {
            mUpBarHandler = new UpBarHandler(mContext, this);
            mUpBar = new SceneHolder(mUpBarHandler);

            mMainMenuHandler = new MainMenuHandler(mContext, this);
            mMainMenu = new SceneHolder(mMainMenuHandler);
        }
    }

    public void touchEvent(final MotionEvent e) {
        switch (mState) {
            case Const.MAIN_MENU:
                mMainMenu.touchEvent(e);
                break;
            case Const.LEVEL:
                mLevel.touchEvent(e);
                mUpBar.touchEvent(e);
                break;
            default:
                break;
        }
    }

    public static float getXByScreenX(final float screenX) {
        return screenX / mScreenWidth * 2 - 1;
    }

    public static float getYByScreenY(final float screenY) {
        return 1 - screenY / mScreenHeight * 2;
    }

    public static float getScreenWidth() {
        return mScreenWidth;
    }

    public static float getScreenHeight() {
        return mScreenHeight;
    }

    private void prepareBuffers() {
        mVertexData = ByteBuffer
                .allocateDirect(Const.VERTEX_BUFFER_SIZE * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mTextureCoordinates = ByteBuffer
                .allocateDirect(Const.TEXTURE_BUFFER_SIZE * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    private void createProjection(int width, int height) {
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

    private void bindData(){
        mColorLocation = GLES20.glGetUniformLocation(mProgramId, "u_Color");
        mPositionLocation = GLES20.glGetAttribLocation(mProgramId, "a_Position");
        mMatrixLocation = GLES20.glGetUniformLocation(mProgramId, "u_Matrix");
        mATextureLocation = GLES20.glGetAttribLocation(mProgramId, "a_Texture");
        mUTextureUnitLocation = GLES20.glGetUniformLocation(mProgramId, "u_TextureUnit");
        mPositionLocation = GLES20.glGetAttribLocation(mProgramId, "a_Position");

        mVertexData.position(0);
        GLES20.glVertexAttribPointer(mPositionLocation, 2, GLES20.GL_FLOAT,
                false, 0, mVertexData);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        mTextureCoordinates.position(0);
        GLES20.glVertexAttribPointer(mATextureLocation, 2, GLES20.GL_FLOAT,
                false, 0, mTextureCoordinates);
        GLES20.glEnableVertexAttribArray(mATextureLocation);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(mUTextureUnitLocation, 0);
    }

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
        synchronized (mLock) {
            mUpBarHandler.toDefaultState();
            mState = 2;
            mObjectsToLoadTextures.clear();
            mObjectsToLoadAnimationTextures.clear();
            mObjectsToLoadTextures = mLevelHandler.getObjectsToLoadTextures();
            mObjectsToLoadAnimationTextures = mLevelHandler.getObjectsToLoadAnimationTextures();
            mIsStateChanged = true;
        }
    }

    @Override
    public void onLevelMistake() {
        if (mUpBarHandler.getLivesCount() > 0) {
            mUpBarHandler.setLivesCount(mUpBarHandler.getLivesCount() - 1);
        } else if (mUpBarHandler.getLivesCount() == 0) {

        }
    }

    @Override
    public void onLevelCompleted() {
        mUpBarHandler.levelCompleted();
    }

    @Override
    public void onLevelRestarted() {

    }

    @Override
    public void onReturnToMenu() {
        synchronized (mLock) {
            if (mLevelHandler != null) {
                texturesToRemove = mLevelHandler.getScene().getTextures();
            }
            mState = Const.MAIN_MENU;
            mIsStateChanged = true;
        }
    }

    @Override
    public void onMenuClosed() {

    }

    @Override
    public void onStartButtonPressed() {
        mLoadingHandler = new LoadingHandler(mContext, this, mLevelNumber);
        mLoading = new SceneHolder(mLoadingHandler);
        mLoadingHandler.callLoaded();
    }

    @Override
    public void onMenuLoaded() {
        synchronized (mLock) {
            mState = 0;
            mObjectsToLoadTextures.clear();
            mObjectsToLoadAnimationTextures.clear();
            mObjectsToLoadTextures = mMainMenuHandler.getObjectsToLoadTextures();
            mObjectsToLoadAnimationTextures = mMainMenuHandler.getObjectsToLoadAnimationTextures();
            mObjectsToLoadTextures.putAll(mUpBarHandler.getObjectsToLoadTextures());
            mObjectsToLoadAnimationTextures.addAll(mUpBarHandler.getObjectsToLoadAnimationTextures());
            mIsStateChanged = true;
        }
    }

    @Override
    public void onLoadingLoaded() {
        synchronized (mLock) {
            mState = 1;
            mObjectsToLoadTextures.clear();
            mObjectsToLoadAnimationTextures.clear();
            mObjectsToLoadTextures = mLoadingHandler.getObjectsToLoadTextures();
            mObjectsToLoadAnimationTextures = mLoadingHandler.getObjectsToLoadAnimationTextures();
            mIsStateChanged = true;
        }
    }

    @Override
    public void onLoadingCompleted() {
        if (mLevelHandler != null) {
            texturesToRemove = mLevelHandler.getScene().getTextures();
        }
        switch (mLevelNumber) {
            case 1:
                mLevelHandler = new LevelHandler1(mContext, this);
                mLevel = new SceneHolder(mLevelHandler);
                mLevelHandler.callLoaded();
                break;
            case 2:
                mLevelHandler = new LevelHandler2(mContext, this);
                mLevel = new SceneHolder(mLevelHandler);
                mLevelHandler.callLoaded();
                break;
            default:
                break;
        }
    }

    @Override
    public void onKeyPressed() {

    }

    @Override
    public void onNextLevel() {
        synchronized (mLock) {
            mLevelNumber++;
            onStartButtonPressed();
        }
    }

    @Override
    public void onExit() {
        if (mGameExitListener != null) {
            mGameExitListener.onExit();
        }
    }
}

