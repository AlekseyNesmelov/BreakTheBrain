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

    private List<TextureTemplate> mTexturesToLoad = new ArrayList<>();

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
    public void processBeforeDraw(final int matrixLocation, final float[] matrix) {
        final long time = System.currentTimeMillis();
        if (time - mAnimationTime > Const.LOADING_ANIMATION_DELAY) {
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
    public void processAfterDraw(final int matrixLocation, final float[] matrix) {
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
    public void init() {
        mScene = new DrawableScene(1);

        mMat = new DrawableObject(4, 2, DrawableObject.NORMAL_SPRITE);
        mMat.setX(mCurMatX);
        final Bitmap iconMat = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.loading_mat_small), Game.getScreenWidth(), Game.getScreenHeight());
        final TextureTemplate matTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconMat, mMat);
        mTexturesToLoad.add(matTemplate);
        mScene.addToLayer(0, mMat);

        // Numbers.
        final int textSize = 250;
        final Paint textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setARGB(205, 0x00, 0x00, 0x00);

        // First number
        mFirstNumber = new DrawableObject(0.7f, 0.7f, DrawableObject.SQUARE_SPRITE);
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
        final TextureTemplate firstNumberTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconFirstNumber, mFirstNumber);
        mTexturesToLoad.add(firstNumberTemplate);
        mScene.addToLayer(0, mFirstNumber);

        mSecondNumber = new DrawableObject(0.8f, 0.8f, DrawableObject.SQUARE_SPRITE);
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
        final TextureTemplate secondNumberTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconSecondNumber, mSecondNumber);
        mTexturesToLoad.add(secondNumberTemplate);
        mScene.addToLayer(0, mSecondNumber);
    }

    @Override
    public DrawableScene getScene() {
        return mScene;
    }

    @Override
    public List<TextureTemplate> getTexturesToLoad() {
        return mTexturesToLoad;
    }
}
