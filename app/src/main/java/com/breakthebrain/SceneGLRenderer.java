package com.breakthebrain;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * OpenGL renderer class.
 */
public class SceneGLRenderer implements GLSurfaceView.Renderer {
    Game mGame;

    private Context mContext;
    private FloatBuffer mVertexData;
    private FloatBuffer mTextureCoordinates;
    private float[] mMatrix = new float[16];
    private int mMatrixLocation;
    private int mPositionLocation;
    private int mATextureLocation;
    private int mUTextureUnitLocation;
    private int mProgramId;

    private final Object mLock = new Object();

    /**
     * Gl renderer constructor.
     * @param context application context.
     */
    public SceneGLRenderer(final Context context) {
        mContext = context;
        mGame = new Game(mContext, mLock);
    }

    @Override
    public void onDrawFrame(final GL10 arg0) {
        synchronized (mLock) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            mGame.checkState(mVertexData, mTextureCoordinates);
            mGame.draw(mMatrixLocation, mMatrix);
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
        mGame.recalculateProjection(width, height);
    }

    /**
     * Touch event.
     * @param e motion event.
     */
    public void touchEvent(final MotionEvent e) {
        mGame.touchEvent(e);
    }

   /* public void loadMainMenu() {
        //: TODO get coins and life count from storage.
        synchronized (mLock) {
            mUpBarHandler = new UpBarHandler(mContext, this, 5, 100);
            mUpBar = new SceneHolder(mUpBarHandler);

            mMainMenuHandler = new MainMenuHandler(mContext, this);
            mMainMenu = new SceneHolder(mMainMenuHandler);
        }
    }*/


    /**
     * Prepares buffers.
     */
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

    /**
     * Binds shader data.
     */
    private void bindData(){
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

    /*
    @Override
    public void onLevelLoaded() {
        synchronized (mLock) {
            mUpBarHandler.toDefaultState();
            mState = 2;
            mObjectsToLoadTextures.clear();
            mTexturesToLoad.clear();
            mObjectsToLoadTextures = mLevelHandler.getObjectsToLoadTextures();
            mTexturesToLoad = mLevelHandler.getObjectsToLoadAnimationTextures();
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
                mTexturesToRemove = mLevelHandler.getScene().getTextures();
            }
            mState = Const.STATE_MAIN_MENU;
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
            mTexturesToLoad.clear();
            mObjectsToLoadTextures = mMainMenuHandler.getObjectsToLoadTextures();
            mTexturesToLoad = mMainMenuHandler.getObjectsToLoadAnimationTextures();
            mObjectsToLoadTextures.putAll(mUpBarHandler.getObjectsToLoadTextures());
            mTexturesToLoad.addAll(mUpBarHandler.getObjectsToLoadAnimationTextures());
            mIsStateChanged = true;
        }
    }

    @Override
    public void onLoadingLoaded() {
        synchronized (mLock) {
            mState = 1;
            mObjectsToLoadTextures.clear();
            mTexturesToLoad.clear();
            mObjectsToLoadTextures = mLoadingHandler.getObjectsToLoadTextures();
            mTexturesToLoad = mLoadingHandler.getObjectsToLoadAnimationTextures();
            mIsStateChanged = true;
        }
    }

    @Override
    public void onLoadingCompleted() {
        if (mLevelHandler != null) {
            mTexturesToRemove = mLevelHandler.getScene().getTextures();
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
    }*/
}

