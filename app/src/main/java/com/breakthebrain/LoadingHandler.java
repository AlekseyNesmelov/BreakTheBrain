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

public class LoadingHandler implements SceneHolder.SceneHolderHandler {
    private DrawableScene mScene;
    private DrawableObject mMat;
    private DrawableObject mFirstNumber;
    private DrawableObject mSecondNumber;
    private GameListener mLevelListener;
    private Context mContext;
    private int mLevelNumber = 1;

    private Map<DrawableObject, Bitmap> mObjectsToLoadTextures = new HashMap<>();
    private List<TextureTemplate> mObjectsToLoadAnimationTextures = new ArrayList<>();

    long mAnimationTime = 0;
    float mCurMatX = 1;
    float mCurFirstNumberX = 0;
    float mCurSecondNumberX = 2f;

    public LoadingHandler(final Context context, final GameListener levelListener, final int levelNumber) {
        mContext = context;
        mLevelListener = levelListener;
        mLevelNumber = levelNumber;
    }

    @Override
    public void processBeforeDraw(final int matrixLocation, final float[] matrix, final int colorLocation,
                                  final float scaleFactorX, final float scaleFactorY) {
        final long time = System.currentTimeMillis();
        if (time - mAnimationTime > 30) {
            if (mCurMatX > -1f) {
                mMat.setX(mCurMatX);
                mFirstNumber.setX(mCurFirstNumberX);
                mSecondNumber.setX(mCurSecondNumberX);
                mCurMatX -= 0.02f;
                mCurFirstNumberX-= 0.02f;
                mCurSecondNumberX-= 0.02f;
            } else {
                mLevelListener.onLoadingCompleted();
            }
            mAnimationTime = time;
        }
    }

    @Override
    public void processAfterDraw(final int matrixLocation, final float[] matrix, final int colorLocation,
                                 final float scaleFactorX, final float scaleFactorY) {
    }

    @Override
    public void processTouch(final MotionEvent e) {
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

        mMat = new DrawableObject(4, 2);
        mMat.setX(mCurMatX);
        final Bitmap iconMat = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.loading_mat_small), SceneGLRenderer.getScreenWidth(), SceneGLRenderer.getScreenHeight());
        mObjectsToLoadTextures.put(mMat, iconMat);

        final int textSize = 250;
        final Paint textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setARGB(205, 0x00, 0x00, 0x00);

        mFirstNumber = new DrawableObject(0.7f, 0.7f);
        mFirstNumber.makeSquare();
        mFirstNumber.setX(mCurFirstNumberX);
        final Bitmap iconFirstNumber = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(iconFirstNumber);
        if (mLevelNumber > 1) {
            final String textNumber1 = (mLevelNumber - 1) + "";
            final float[] widths = new float[textNumber1.length()];
            textPaint.getTextWidths(textNumber1, 0, textNumber1.length(), widths);
            float sum = 0;
            for (int i = 0; i < widths.length; i++) {
                sum+= widths[i];
            }

            canvas.drawText(textNumber1, (iconFirstNumber.getWidth() - sum) / 2, (iconFirstNumber.getHeight() + textSize) / 2, textPaint);
        }
        mObjectsToLoadTextures.put(mFirstNumber, iconFirstNumber);

        mSecondNumber = new DrawableObject(0.8f, 0.8f);
        mSecondNumber.makeSquare();
        mSecondNumber.setX(mCurSecondNumberX);
        final Bitmap iconSecondNumber = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(iconSecondNumber);
        final String textNumber2 = (mLevelNumber) + "";
        final float[] widths = new float[textNumber2.length()];
        textPaint.getTextWidths(textNumber2, 0, textNumber2.length(), widths);
        float sum = 0;
        for (int i = 0; i < widths.length; i++) {
            sum+= widths[i];
        }
        canvas.drawText(textNumber2, (iconSecondNumber.getWidth() - sum) / 2, (iconSecondNumber.getHeight() + textSize) / 2, textPaint);
        mObjectsToLoadTextures.put(mSecondNumber, iconSecondNumber);

        mScene.addToLayer(0, mMat);
        mScene.addToLayer(0, mFirstNumber);
        mScene.addToLayer(0, mSecondNumber);
    }

    @Override
    public void callLoaded() {
        mLevelListener.onLoadingLoaded();
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
