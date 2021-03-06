package com.breakthebrain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelHandler2 implements SceneHolder.SceneHolderHandler, DrawableScene.TouchObjectListener{
    private DrawableScene mScene;
    private DrawableObject mMat;
    private DrawableObject mWord;
    private DrawableObject mHouseWord;
    private DrawableObject mPenguinRed;
    private DrawableObject mPenguinGreen;
    private DrawableObject mHouseRed;
    private DrawableObject mHouseGreen;
    private GameListener mLevelListener;
    private Context mContext;

    private List<TextureTemplate> mTexturesToLoad = new ArrayList<>();

    public LevelHandler2(final Context context, final GameListener levelListener) {
        mContext = context;
        mLevelListener = levelListener;
    }

    @Override
    public void processBeforeDraw(final int matrixLocation, final float[] matrix) {
    }

    @Override
    public void processAfterDraw(int matrixLocation, float[] matrix) {
    }

    @Override
    public void processTouch(MotionEvent e) {
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
        /*mScene.setTouchObjectListener(this);

        mMat = new DrawableObject(2, 2);
        final Bitmap iconMat = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.level2_mat), SceneGLRenderer.getScreenWidth(), SceneGLRenderer.getScreenHeight());
        mObjectsToLoadTextures.put(mMat, iconMat);
        mScene.addToLayer(0, mMat);

        mHouseWord = new DrawableObject(0.3f, 0.101f);
        mHouseWord.setY(0.31f);
        mHouseWord.setX(0.4f);

        mHouseRed = new DrawableObject(0.3f, 0.3f);
        mHouseRed.makeSquare();
        mHouseRed.setX(0.5f);
        final Bitmap iconHouseRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_home_red);
        mObjectsToLoadTextures.put(mHouseRed, iconHouseRed);
        mScene.addToLayer(0, mHouseRed);

        final Bitmap inAnimationHouseRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_home_red_in);
        final TextureTemplate inAnimationTemplateHouseRed = new TextureTemplate();
        inAnimationTemplateHouseRed.bitmap = inAnimationHouseRed;
        inAnimationTemplateHouseRed.animationName = "in";
        inAnimationTemplateHouseRed.object = mHouseRed;
        mObjectsToLoadAnimationTextures.add(inAnimationTemplateHouseRed);

        mHouseGreen = new DrawableObject(0.3f, 0.3f);
        mHouseGreen.makeSquare();
        mHouseGreen.setX(-0.5f);
        final Bitmap iconHouseGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_home_green);
        mObjectsToLoadTextures.put(mHouseGreen, iconHouseGreen);
        mScene.addToLayer(0, mHouseGreen);

        final Bitmap inAnimationHouseGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_home_green_in);
        final TextureTemplate inAnimationTemplateHouseGreen = new TextureTemplate();
        inAnimationTemplateHouseGreen.bitmap = inAnimationHouseGreen;
        inAnimationTemplateHouseGreen.animationName = "in";
        inAnimationTemplateHouseGreen.object = mHouseGreen;
        mObjectsToLoadAnimationTextures.add(inAnimationTemplateHouseGreen);

        mPenguinRed = new DrawableObject(0.2f, 0.2f);
        mPenguinRed.setDraggable(true);
        mPenguinRed.makeSquare();
        mPenguinRed.setX(-0.5f);
        mPenguinRed.setY(-0.5f);
        mPenguinRed.animateLoop("dance");
        final Bitmap iconPenguinRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_red);
        mObjectsToLoadTextures.put(mPenguinRed, iconPenguinRed);
        mScene.addToLayer(0, mPenguinRed);

        mPenguinGreen = new DrawableObject(0.2f, 0.2f);
        mPenguinGreen.setDraggable(true);
        mPenguinGreen.makeSquare();
        mPenguinGreen.setX(0.5f);
        mPenguinGreen.setY(-0.5f);
        mPenguinGreen.animateLoop("dance");
        final Bitmap iconPenguinGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_green);
        mObjectsToLoadTextures.put(mPenguinGreen, iconPenguinGreen);
        mScene.addToLayer(0, mPenguinGreen);

        final Bitmap danceAnimationPenguinRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_red_animation1);
        final TextureTemplate danceAnimationTemplatePenguinRed = new TextureTemplate();
        danceAnimationTemplatePenguinRed.bitmap = danceAnimationPenguinRed;
        danceAnimationTemplatePenguinRed.animationName = "dance";
        danceAnimationTemplatePenguinRed.object = mPenguinRed;
        mObjectsToLoadAnimationTextures.add(danceAnimationTemplatePenguinRed);

        final Bitmap dragAnimationPenguinRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_red_animation2);
        final TextureTemplate dragAnimationTemplatePenguinRed = new TextureTemplate();
        dragAnimationTemplatePenguinRed.bitmap = dragAnimationPenguinRed;
        dragAnimationTemplatePenguinRed.animationName = "drag";
        dragAnimationTemplatePenguinRed.object = mPenguinRed;
        mObjectsToLoadAnimationTextures.add(dragAnimationTemplatePenguinRed);

        final Bitmap danceAnimationPenguinGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_green_animation1);
        final TextureTemplate danceAnimationTemplatePenguinGreen = new TextureTemplate();
        danceAnimationTemplatePenguinGreen.bitmap = danceAnimationPenguinGreen;
        danceAnimationTemplatePenguinGreen.animationName = "dance";
        danceAnimationTemplatePenguinGreen.object = mPenguinGreen;
        mObjectsToLoadAnimationTextures.add(danceAnimationTemplatePenguinGreen);

        final Bitmap dragAnimationPenguinGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_green_animation2);
        final TextureTemplate dragAnimationTemplatePenguinGreen = new TextureTemplate();
        dragAnimationTemplatePenguinGreen.bitmap = dragAnimationPenguinGreen;
        dragAnimationTemplatePenguinGreen.animationName = "drag";
        dragAnimationTemplatePenguinGreen.object = mPenguinGreen;
        mObjectsToLoadAnimationTextures.add(dragAnimationTemplatePenguinGreen);

        mWord = new DrawableObject(0.828f, 0.102f);
        mWord.setDraggable(true);
        mWord.setY(0.45f);
        mWord.setX(0.26f);
        final Bitmap iconWord = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.level2_word_penguin);
        mObjectsToLoadTextures.put(mWord, iconWord);
        mScene.addToLayer(0, mWord);*/
    }

    @Override
    public DrawableScene getScene() {
        return mScene;
    }

    @Override
    public List<TextureTemplate> getTexturesToLoad() {
        return mTexturesToLoad;
    }

    @Override
    public void onObjectDropped(final DrawableObject object) {
        /*if (object.equals(mPenguinRed)) {
            if (mHouseRed.isInside(mPenguinRed.getX(), mPenguinRed.getY())) {
                mPenguinRed.setVisible(false);
                mHouseRed.animateLoop("in");
            } else if (mHouseGreen.isInside(mPenguinRed.getX(), mPenguinRed.getY()) ||
                    mHouseWord.isInside(mPenguinRed.getX(), mPenguinRed.getY())) {
                mPenguinRed.setX(-0.5f);
                mPenguinRed.setY(-0.5f);
                mLevelListener.onLevelMistake();
            }
        }
        if (object.equals(mPenguinGreen)) {
            if (mHouseGreen.isInside(mPenguinGreen.getX(), mPenguinGreen.getY())) {
                mPenguinGreen.setVisible(false);
                mHouseGreen.animateLoop("in");
            } else if (mHouseRed.isInside(mPenguinGreen.getX(), mPenguinGreen.getY()) ||
                    mHouseWord.isInside(mPenguinGreen.getX(), mPenguinGreen.getY())) {
                mPenguinGreen.setX(0.5f);
                mPenguinGreen.setY(-0.5f);
                mLevelListener.onLevelMistake();
            }
        }
        if (object.equals(mWord)) {
            if (mHouseWord.isInside(mWord.getX(), mWord.getY())) {
                mWord.setVisible(false);
            } else if (mHouseRed.isInside(mWord.getX(), mWord.getY()) ||
                    mHouseGreen.isInside(mWord.getX(), mWord.getY())) {
                mWord.setY(0.45f);
                mWord.setX(0.26f);
                mLevelListener.onLevelMistake();
            }
        }
        if (!mPenguinRed.isVisible() && !mPenguinGreen.isVisible() && !mWord.isVisible()) {
            mLevelListener.onLevelCompleted();
        }*/

    }
}
