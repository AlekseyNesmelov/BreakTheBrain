package com.breakthebrain;

import android.view.MotionEvent;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Drawable scene class.
 */
public class DrawableScene {
    // Layers list.
    private final List<DrawableLayer> mLayers = new ArrayList<>();
    // Object touching listener.
    private TouchObjectListener mTouchObjectListener;

    // Objects under dragging (finger -> object).
    private Map<Integer, DrawableObject> mDraggableObjects = new HashMap<>();

    /**
     * Drawable scene constructor.
     * @param layerCount layer count.
     */
    public DrawableScene(final int layerCount) {
        for (int i = 0; i < layerCount; i++) {
            mLayers.add(new DrawableLayer());
        }
    }

    /**
     * Sets object touch listener.
     * @param touchObjectListener touch object listener to set.
     */
    public void setTouchObjectListener(final TouchObjectListener touchObjectListener) {
        mTouchObjectListener = touchObjectListener;
    }

    /**
     * Process touch event.
     * @param e motion event.
     */
    public void touchEvent(final MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            final int pointCounter = e.getPointerCount();
            for (int p = 0; p < pointCounter; p++) {
                final float x = e.getX(p);
                final float y = e.getY(p);
                final float glX = SceneGLRenderer.getXByScreenX(x);
                final float glY = SceneGLRenderer.getYByScreenY(y);
                final DrawableObject object = mDraggableObjects.get(p);
                if (object != null) {
                    object.setX(glX);
                    object.setY(glY);
                    object.animateLoop(Const.DRAG_ANIMATION);
                }
            }
        } else if (e.getAction() == MotionEvent.ACTION_DOWN || e.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            final int actionIndex = e.getActionIndex();
            final float x = e.getX(actionIndex);
            final float y = e.getY(actionIndex);
            final float glX = SceneGLRenderer.getXByScreenX(x);
            final float glY = SceneGLRenderer.getYByScreenY(y);
            for (final DrawableLayer layer : mLayers) {
                final DrawableObject object = layer.getDraggableObjectInside(glX, glY);
                if (object != null) {
                    boolean exists = false;
                    for (final Integer point : mDraggableObjects.keySet()) {
                        final DrawableObject obj = mDraggableObjects.get(point);
                        if (obj.equals(object)) {
                            exists = true;
                            break;
                        }
                    }

                    if (!exists && !mDraggableObjects.containsKey(actionIndex)) {
                        mDraggableObjects.put(actionIndex, object);
                        break;
                    }
                }
            }
        } else if (e.getAction() == MotionEvent.ACTION_UP || e.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            final DrawableObject obj = mDraggableObjects.get(e.getActionIndex());
            if (obj != null) {
                if (mTouchObjectListener != null) {
                    mTouchObjectListener.onObjectDropped(obj);
                }
                obj.animateLoop(Const.NORMAL_STATE_ANIMATION);
            }
            mDraggableObjects.remove(e.getActionIndex());
            if (e.getAction() == MotionEvent.ACTION_UP) {
                for (final Integer p : mDraggableObjects.keySet()) {
                    final DrawableObject o = mDraggableObjects.get(p);
                    if (mTouchObjectListener != null) {
                        mTouchObjectListener.onObjectDropped(o);
                    }
                    o.animateLoop(Const.NORMAL_STATE_ANIMATION);
                }
                mDraggableObjects.clear();
            }
        }
    }

    /**
     * Puts scene to buffer.
     * @param startPos start pos in buffer.
     * @param vertexBuffer OpenGL vertex buffer.
     * @param textureCoordinates OpenGL texture coordinates.
     * @return new start pos in buffer.
     */
    public int putToBuffer(final int startPos, final FloatBuffer vertexBuffer, final FloatBuffer textureCoordinates) {
        int pos = startPos;
        for (final DrawableLayer layer : mLayers) {
            pos = layer.putToBuffer(pos, vertexBuffer, textureCoordinates);
        }
        return pos;
    }

    /**
     * Draws scene.
     * @param matrixLocation OpenGL matrix location.
     * @param modelMatrix OpenGL model matrix.
     */
    public void draw(final int matrixLocation, final float[] modelMatrix) {
        for (final DrawableLayer layer : mLayers) {
            layer.draw(matrixLocation, modelMatrix);
        }
    }

    /**
     * Adds object to layer.
     * @param layerNum layer number.
     * @param object object to add.
     */
    public void addToLayer(final int layerNum, final DrawableObject object) {
        if (-1 < layerNum && layerNum < mLayers.size()) {
            final DrawableLayer layer = mLayers.get(layerNum);
            layer.add(object);
        }
    }

    /**
     * Gets textures.
     * @return texture ids.
     */
    public int[] getTextures() {
        final List<Integer> textureIds = new ArrayList<>();
        for (final DrawableLayer layer : mLayers) {
            textureIds.addAll(layer.getTextureIds());
        }
        int count = 0;
        final int[] textures = new int[textureIds.size()];
        for (int i = 0; i < textureIds.size(); i++) {
            textures[count++] = textureIds.get(i);
        }
        return textures;
    }

    /**
     * Touch object listener.
     */
    public interface TouchObjectListener {
        // Is called when object is dropped.
        void onObjectDropped(final DrawableObject object);
    }
}
