package com.breakthebrain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LevelHandler1 implements SceneHolder.SceneHolderHandler, DrawableScene.TouchObjectListener{
    private DrawableScene mScene;
    private DrawableObject mMat;
    private DrawableObject mPenguinRed;
    private DrawableObject mPenguinGreen;
    private DrawableObject mHouseRed;
    private DrawableObject mHouseGreen;
    private GameListener mLevelListener;
    private Context mContext;

    private List<TextureTemplate> mTexturesToLoad = new CopyOnWriteArrayList<>();

    /**
     * Level 1 handler.
     * @param context application context.
     * @param levelListener level listener.
     */
    public LevelHandler1(final Context context, final GameListener levelListener) {
        mContext = context;
        mLevelListener = levelListener;
    }

    @Override
    public void processBeforeDraw(final int matrixLocation, final float[] matrix) {
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
        mScene.setTouchObjectListener(this);

        // Mat
        mMat = new DrawableObject(2, 2, DrawableObject.NORMAL_SPRITE);
        final Bitmap iconMat = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.level1_mat), Game.getScreenWidth(), Game.getScreenHeight());
        final TextureTemplate matTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconMat, mMat);
        mTexturesToLoad.add(matTemplate);
        mScene.addToLayer(0, mMat);

        // Red house.
        mHouseRed = new DrawableObject(0.3f, 0.3f, DrawableObject.SQUARE_SPRITE);
        mHouseRed.setX(0.5f);
        final Bitmap iconHouseRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_home_red);
        final Bitmap inHouseRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_home_red_in);
        final TextureTemplate iconRedHouseTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconHouseRed, mHouseRed);
        final TextureTemplate inRedHouseRedTemplate = new TextureTemplate(Const.FULL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                inHouseRed, mHouseRed);
        mTexturesToLoad.add(iconRedHouseTemplate);
        mTexturesToLoad.add(inRedHouseRedTemplate);
        mScene.addToLayer(0, mHouseRed);

        // Green house.
        mHouseGreen = new DrawableObject(0.3f, 0.3f, DrawableObject.SQUARE_SPRITE);
        mHouseGreen.setX(-0.5f);
        final Bitmap iconHouseGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_home_green);
        final Bitmap inHouseGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_home_green_in);
        final TextureTemplate iconGreenHouseTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconHouseGreen, mHouseGreen);
        final TextureTemplate inGreenHouseTemplate = new TextureTemplate(Const.FULL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                inHouseGreen, mHouseGreen);
        mTexturesToLoad.add(iconGreenHouseTemplate);
        mTexturesToLoad.add(inGreenHouseTemplate);
        mScene.addToLayer(0, mHouseGreen);

        // Red penguin.
        mPenguinRed = new DrawableObject(0.25f, 0.25f, DrawableObject.SQUARE_SPRITE);
        mPenguinRed.setDraggable(true);
        mPenguinRed.setX(-0.5f);
        mPenguinRed.setY(-0.5f);
        mPenguinRed.animateLoop(Const.NORMAL_ANIMATION_STATE);
        final Bitmap iconPenguinRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_red);
        final Bitmap normalAnimationPenguinRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_red_animation1);
        final Bitmap dragAnimationPenguinRed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_red_animation2);
        final TextureTemplate penguinRedTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconPenguinRed, mPenguinRed);
        final TextureTemplate penguinRedNormalAnimationTemplate = new TextureTemplate(Const.NORMAL_ANIMATION_STATE,
                TextureTemplate.ANIMATION_TEXTURE, normalAnimationPenguinRed, mPenguinRed);
        final TextureTemplate penguinRedDragAnimationTemplate = new TextureTemplate(Const.DRAG_ANIMATION,
                TextureTemplate.ANIMATION_TEXTURE, dragAnimationPenguinRed, mPenguinRed);
        mTexturesToLoad.add(penguinRedTemplate);
        mTexturesToLoad.add(penguinRedNormalAnimationTemplate);
        mTexturesToLoad.add(penguinRedDragAnimationTemplate);
        mScene.addToLayer(0, mPenguinRed);

        mPenguinGreen = new DrawableObject(0.25f, 0.25f, DrawableObject.SQUARE_SPRITE);
        mPenguinGreen.setDraggable(true);
        mPenguinGreen.setX(0.5f);
        mPenguinGreen.setY(-0.5f);
        mPenguinGreen.animateLoop(Const.NORMAL_ANIMATION_STATE);
        final Bitmap iconPenguinGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_green);
        final Bitmap normalAnimationPenguinGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_green_animation1);
        final Bitmap dragAnimationPenguinGreen = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.penguin_green_animation2);
        final TextureTemplate penguinGreenTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconPenguinGreen, mPenguinGreen);
        final TextureTemplate penguinGreenNormalAnimationTemplate = new TextureTemplate(Const.NORMAL_ANIMATION_STATE,
                TextureTemplate.ANIMATION_TEXTURE, normalAnimationPenguinGreen, mPenguinGreen);
        final TextureTemplate penguinGreenDragAnimationTemplate = new TextureTemplate(Const.DRAG_ANIMATION,
                TextureTemplate.ANIMATION_TEXTURE, dragAnimationPenguinGreen, mPenguinGreen);
        mTexturesToLoad.add(penguinGreenTemplate);
        mTexturesToLoad.add(penguinGreenNormalAnimationTemplate);
        mTexturesToLoad.add(penguinGreenDragAnimationTemplate);
        mScene.addToLayer(0, mPenguinGreen);
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
        if (object.equals(mPenguinRed)) {
            if (mHouseRed.isInside(mPenguinRed.getX(), mPenguinRed.getY())) {
                mPenguinRed.setVisible(false);
                mPenguinRed.setDraggable(false);
                mHouseRed.setState(Const.FULL_STATE);
            } else if (mHouseGreen.isInside(mPenguinRed.getX(), mPenguinRed.getY())) {
                mPenguinRed.setX(-0.5f);
                mPenguinRed.setY(-0.5f);
                mLevelListener.onLevelMistake();
            }
        }
        if (object.equals(mPenguinGreen)) {
            if (mHouseGreen.isInside(mPenguinGreen.getX(), mPenguinGreen.getY())) {
                mPenguinGreen.setVisible(false);
                mPenguinGreen.setDraggable(false);
                mHouseGreen.setState(Const.FULL_STATE);
            } else if (mHouseRed.isInside(mPenguinGreen.getX(), mPenguinGreen.getY())) {
                mPenguinGreen.setX(0.5f);
                mPenguinGreen.setY(-0.5f);
                mLevelListener.onLevelMistake();
            }
        }
        if (!mPenguinRed.isVisible() && !mPenguinGreen.isVisible()) {
            mLevelListener.onLevelCompleted();
        }
    }
}
