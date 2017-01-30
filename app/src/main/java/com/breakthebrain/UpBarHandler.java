package com.breakthebrain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import java.util.ArrayList;
import java.util.List;

public class UpBarHandler implements SceneHolder.SceneHolderHandler {
    private int mLivesCount;
    private int mCoinCount;
    private DrawableScene mScene;
    private DrawableObject mMat;
    private List<DrawableObject> mHearts;
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

    private List<TextureTemplate> mTexturesToLoad = new ArrayList<>();

    private long mRedScreenScreenTimeCount = 0;

    /**
     * Up bar handler constructor.
     * @param context application context.
     * @param levelListener level listener.
     */
    public UpBarHandler(final Context context, final GameListener levelListener) {
        mContext = context;
        mLevelListener = levelListener;
        mLivesCount = 5;
        mCoinCount = 100;
    }

    @Override
    public void processBeforeDraw(final int matrixLocation, final float[] matrix) {
        long time = System.currentTimeMillis();
        if (mRedScreen.isVisible() && time - mRedScreenScreenTimeCount > Const.RED_SCREEN_DELAY) {
            mRedScreen.setVisible(false);
        }
    }

    @Override
    public void processAfterDraw(int matrixLocation, float[] matrix) {
    }

    @Override
    public void processTouch(final MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();
        final float glX = Game.getXByScreenX(x);
        final float glY = Game.getYByScreenY(y);
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (mNextButton.isVisible()) {
                if (mNextButton.isInside(glX, glY)) {
                    mNextButton.setState(Const.BUTTON_PRESSED_STATE);
                    mIsNextPressed = true;
                }
            } else {
                if (mBackButton.isInside(glX, glY)) {
                    mBackButton.setState(Const.BUTTON_PRESSED_STATE);
                    mIsBackPressed = true;
                } else if (mKeyButton.isInside(glX, glY)) {
                    mKeyButton.setState(Const.BUTTON_PRESSED_STATE);
                    mIsKeyPressed = true;
                }
            }
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            mBackButton.setState(Const.NORMAL_STATE);
            mKeyButton.setState(Const.NORMAL_STATE);
            mNextButton.setState(Const.NORMAL_STATE);
            if (mNextButton.isVisible()) {
                if (mIsNextPressed && mNextButton.isInside(glX, glY)) {
                    SoundPlayer.playClickSound(mContext);
                    mLevelListener.onNextLevel();
                }
            } else {
                if (mIsBackPressed && mBackButton.isInside(glX, glY)) {
                    SoundPlayer.playClickSound(mContext);
                    mLevelListener.onReturnToMenu();
                } else if (mIsKeyPressed && mKeyButton.isInside(glX, glY)) {
                    SoundPlayer.playClickSound(mContext);
                    mLevelListener.onKeyPressed();
                }
            }
            mIsBackPressed = false;
            mIsKeyPressed = false;
            mIsNextPressed = false;
        }
    }

    /**
     * Sets lives count.
     * @param livesCount lives count tot set.
     */
    public void setLivesCount(final int livesCount) {
        final int prevLivesCount = mLivesCount;
        mLivesCount = Math.max(Const.MIN_LIVES_COUNT, Math.min(livesCount, Const.MAX_LIVES_COUNT));
        if (prevLivesCount > mLivesCount) {
            mRedScreenScreenTimeCount = System.currentTimeMillis();
            mRedScreen.setVisible(true);
            SoundPlayer.playMistakeSound(mContext);
        }

        for (int i = Const.MIN_LIVES_COUNT; i < mLivesCount; i++) {
            mHearts.get(i).setVisible(true);
        }
        for (int i = mLivesCount; i < Const.MAX_LIVES_COUNT; i++) {
            mHearts.get(i).setVisible(false);
        }
    }

    /**
     * Level completed.
     */
    public void levelCompleted() {
        mCompletedScreen.setVisible(true);
        mNextButton.setVisible(true);
        SoundPlayer.playWinSound(mContext);
    }

    /**
     * Gets lives count.
     * @return lives count.
     */
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
    public void init() {
        mScene = new DrawableScene(2);

        // Red screen.
        mRedScreen = new DrawableObject(2f, 2f, DrawableObject.NORMAL_SPRITE);
        mRedScreen.setVisible(false);
        final Bitmap iconRedScreen = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.red_screen), Game.getScreenWidth(), Game.getScreenHeight());
        final TextureTemplate redScreenTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconRedScreen, mRedScreen);
        mTexturesToLoad.add(redScreenTemplate);
        mScene.addToLayer(1, mRedScreen);

        // Completed screen.
        mCompletedScreen = new DrawableObject(2f, 2f, DrawableObject.NORMAL_SPRITE);
        mCompletedScreen.setVisible(false);
        final Bitmap iconCompletedScreen = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.level_completed), Game.getScreenWidth(), Game.getScreenHeight());
        final TextureTemplate completedTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconCompletedScreen, mCompletedScreen);
        mTexturesToLoad.add(completedTemplate);
        mScene.addToLayer(1, mCompletedScreen);

        // Next button.
        mNextButton = new DrawableObject(0.2f, 0.2f, DrawableObject.SQUARE_SPRITE);
        mNextButton.setY(-0.3f);
        mNextButton.setVisible(false);
        final Bitmap iconNextButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.next);
        final Bitmap iconNextButtonPressed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.next_pressed);
        final TextureTemplate nextButtonTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconNextButton, mNextButton);
        final TextureTemplate nextButtonPressedTemplate = new TextureTemplate(Const.BUTTON_PRESSED_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconNextButtonPressed, mNextButton);
        mTexturesToLoad.add(nextButtonTemplate);
        mTexturesToLoad.add(nextButtonPressedTemplate);
        mScene.addToLayer(1, mNextButton);

        // Mat.
        mMat = new DrawableObject(2f, 0.2f, DrawableObject.NORMAL_SPRITE);
        mMat.setY(0.9f);
        final Bitmap iconMat = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.up_bar);
        final TextureTemplate matTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconMat, mMat);
        mTexturesToLoad.add(matTemplate);
        mScene.addToLayer(0, mMat);

        // Back button.
        mBackButton = new DrawableObject(0.18f, 0.18f, DrawableObject.SQUARE_SPRITE);
        mBackButton.setX(0.85f);
        mBackButton.setY(0.9f);
        final Bitmap iconBackButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back);
        final Bitmap iconBackButtonPressed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.back_pressed);
        final TextureTemplate backButtonTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconBackButton, mBackButton);
        final TextureTemplate backButtonPressedTemplate = new TextureTemplate(Const.BUTTON_PRESSED_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconBackButtonPressed, mBackButton);
        mTexturesToLoad.add(backButtonTemplate);
        mTexturesToLoad.add(backButtonPressedTemplate);
        mScene.addToLayer(0, mBackButton);

        // Key button.
        mKeyButton = new DrawableObject(0.18f, 0.18f, DrawableObject.SQUARE_SPRITE);
        mKeyButton.setX(0.55f);
        mKeyButton.setY(0.9f);
        final Bitmap iconKeyButton = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.key);
        final Bitmap iconKeyButtonPressed = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.key_pressed);
        final TextureTemplate keyButtonTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconKeyButton, mKeyButton);
        final TextureTemplate keyButtonPressedTemplate = new TextureTemplate(Const.BUTTON_PRESSED_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconKeyButtonPressed, mKeyButton);
        mTexturesToLoad.add(keyButtonTemplate);
        mTexturesToLoad.add(keyButtonPressedTemplate);
        mScene.addToLayer(0, mKeyButton);

        // Hearts.
        mHearts = new ArrayList<>();
        final Bitmap iconHeart = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.heart);
        float curX = -0.85f;
        for (int i = 0; i < (Const.MAX_LIVES_COUNT - Const.MIN_LIVES_COUNT); i++) {
            final DrawableObject heart = new DrawableObject(0.18f, 0.18f, DrawableObject.SQUARE_SPRITE);
            heart.setX(curX);
            heart.setY(0.9f);
            curX+= 0.18f;
            mHearts.add(heart);
            mScene.addToLayer(0, heart);
        }
        final TextureTemplate heartTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconHeart, mHearts);
        mTexturesToLoad.add(heartTemplate);

        // Coins.
        final int textSize = 70;
        final Paint textPaint = new Paint();
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        textPaint.setARGB(255, 0, 0, 0);

        mCoin = new DrawableObject(0.18f, 0.18f, DrawableObject.SQUARE_SPRITE);
        mCoin.setX(0.2f);
        mCoin.setY(0.9f);
        final Bitmap iconCoin = Utils.getResizedBitmap(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.coin), 200, 200);
        final Canvas canvas = new Canvas(iconCoin);
        canvas.drawText(mCoinCount + "", 30, 130, textPaint);
        final TextureTemplate coinTemplate = new TextureTemplate(Const.NORMAL_STATE, TextureTemplate.SIMPLE_TEXTURE,
                iconCoin, mCoin);
        mTexturesToLoad.add(coinTemplate);
        mScene.addToLayer(0, mCoin);
    }

    @Override
    public List<TextureTemplate> getTexturesToLoad() {
       return mTexturesToLoad;
    }

    @Override
    public DrawableScene getScene() {
        return mScene;
    }
}

