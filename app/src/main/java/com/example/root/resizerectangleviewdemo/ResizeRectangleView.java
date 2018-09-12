package com.example.root.resizerectangleviewdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ResizeRectangleView extends View {

    private final int DIRECTION_RIGHT_DOWN = 0x11;
    private final int DIRECTION_RIGHT_UP = 0x12;
    private final int DIRECTION_LEFT_DOWN = 0x13;
    private final int DIRECTION_LEFT_UP = 0x14;

    private int mCurrentDirection = DIRECTION_RIGHT_DOWN;

    private Point mPoint0;
    private Point mPoint1;
    private Point mPoint2;
    private Point mPoint3;

    private Paint mRectanglePaint;
    private Paint mLine1Paint;
    private Paint mLine2Paint;
    private Paint mLine3Paint;
    private Paint mLine4Paint;


    private CropVisionFinishCallback mUpCallback;

    private boolean isUp = false;
    private boolean isClean = false;
    private boolean isCropOk = false;
    private int mCanvasWidth;
    private int mCanvasHeight;

    private static final String TAG = "ResizeRectangleView";

    /**
     *
     *
     */

    public ResizeRectangleView(Context context) {
        this(context, null);
    }

    public ResizeRectangleView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ResizeRectangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setmUpCallback(CropVisionFinishCallback callback) {
        mUpCallback = callback;
    }

    private void init(Context context) {
//        setFocusable(true);
        mPoint0 = new Point();
        mPoint1 = new Point();
        mPoint2 = new Point();
        mPoint3 = new Point();

        mRectanglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectanglePaint.setARGB(0xFF / 2, 0xFF, 0, 0);

        mLine1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLine2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLine3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLine4Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mLine1Paint.setColor(Color.RED);
        mLine2Paint.setColor(Color.MAGENTA);
        mLine3Paint.setColor(Color.BLUE);
        mLine4Paint.setColor(Color.GREEN);
        mLine1Paint.setStrokeWidth(context.getResources().getDimension(R.dimen.resize_rect_bound_width));
        mLine2Paint.setStrokeWidth(context.getResources().getDimension(R.dimen.resize_rect_bound_width));
        mLine3Paint.setStrokeWidth(context.getResources().getDimension(R.dimen.resize_rect_bound_width));
        mLine4Paint.setStrokeWidth(context.getResources().getDimension(R.dimen.resize_rect_bound_width));

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvasWidth = canvas.getWidth();
        mCanvasHeight = canvas.getHeight();
        if (!isUp) {
            canvas.drawRect(mPoint0.x, mPoint0.y, mPoint2.x, mPoint2.y, mRectanglePaint);
        } else {
            canvas.drawLine(mPoint0.x, mPoint0.y, mPoint1.x, mPoint1.y, mLine1Paint);
            canvas.drawLine(mPoint0.x, mPoint0.y, mPoint3.x, mPoint3.y, mLine2Paint);
            canvas.drawLine(mPoint2.x, mPoint2.y, mPoint1.x, mPoint1.y, mLine3Paint);
            canvas.drawLine(mPoint2.x, mPoint2.y, mPoint3.x, mPoint3.y, mLine4Paint);
            isUp = false;
        }

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        return super.dispatchTouchEvent(event);
    }

    int startX;
    int startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int x0 = (int) event.getX();
                int y0 = (int) event.getY();
                startX = x0;
                startY = y0;
                init4Point(startX, startY);
                Log.e(TAG, "onTouchEvent: action down");
                break;
            case MotionEvent.ACTION_MOVE:
                int x1 = (int) event.getX();
                int y1 = (int) event.getY();
                mPoint1.x = x1;
                mPoint2.x = x1;
                mPoint2.y = y1;
                mPoint3.y = y1;
                if (x1 - startX > 0) {
                    if (y1 - startY > 0) {
                        mCurrentDirection = DIRECTION_RIGHT_DOWN;
                    } else {
                        mCurrentDirection = DIRECTION_RIGHT_UP;
                    }

                } else {
                    if (y1 - startY > 0) {
                        mCurrentDirection = DIRECTION_LEFT_DOWN;
                    } else {
                        mCurrentDirection = DIRECTION_LEFT_UP;
                    }
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isUp = true;
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                Log.e(TAG, "onTouchEvent: upX=" + upX + "--upY=" + upY);
                int left, top, right, bottom;
                switch (mCurrentDirection) {
                    case DIRECTION_RIGHT_DOWN:
                        left = mPoint0.x;
                        top = mPoint0.y;
                        right = mPoint2.x;
                        bottom = mPoint2.y;
                        break;
                    case DIRECTION_RIGHT_UP:
                        left = mPoint0.x;
                        top = mPoint2.y;
                        right = mPoint2.x;
                        bottom = mPoint0.y;
                        break;
                    case DIRECTION_LEFT_DOWN:
                        left = mPoint2.x;
                        top = mPoint0.y;
                        right = mPoint0.x;
                        bottom = mPoint2.y;
                        break;
                    case DIRECTION_LEFT_UP:
                        left = mPoint2.x;
                        top = mPoint2.y;
                        right = mPoint0.x;
                        bottom = mPoint0.y;
                        break;
                    default:
                        left = mPoint0.x;
                        top = mPoint0.y;
                        right = mPoint2.x;
                        bottom = mPoint2.y;
                        break;
                }
                if (mUpCallback != null) {

                    if (mPoint2.x > mCanvasWidth) mPoint2.x = mCanvasWidth;

                    //Adjusts the point coordinate from the view coordinate system to the preview's coordinate
                    // system.
//                    float widthScale = (float) CameraHelper.getInstance().getPreviewSize().getWidth() / (float) mCanvasWidth;
//                    float heightScale = (float) CameraHelper.getInstance().getPreviewSize().getHeight() / (float) mCanvasHeight;

//                    Log.e(TAG, "onTouchEvent: previewWidth = " + CameraHelper.getInstance().getPreviewSize().getWidth());
//                    Log.e(TAG, "onTouchEvent: previewHeight = " + CameraHelper.getInstance().getPreviewSize().getHeight());
//                    Log.e(TAG, "onTouchEvent: widthScale = " + widthScale);
//                    Log.e(TAG, "onTouchEvent: heightScale = " + heightScale);

//                    int translateLeft = (int) (left * widthScale);
//                    int translateTop = (int) (top * heightScale);
//                    int translateRight = (int) (right * widthScale);
//                    int translateBottom = (int) (bottom * heightScale);

//                    Log.e(TAG, "onTouchEvent: mPoint0.x = " + mPoint0.x + "--mPoint0.y=" + mPoint0.y
//                            + "--mPoint1.x=" + mPoint1.x + "--mPoint1.y=" + mPoint1.y
//                            + "--mPoint2.x=" + mPoint2.x + "--mPoint1.y=" + mPoint2.y
//                            + "--mPoint3.x=" + mPoint3.x + "--mPoint1.y=" + mPoint3.y);
//                    Log.e(TAG, "onTouchEvent: translateLeft = " + translateLeft + "--translateTop=" + translateTop + "--translateRight=" + translateRight + "--translateBottom=" + translateBottom);
//                    int rectWidth = translateRight - translateLeft;
//                    int rectHeight = translateBottom - translateTop;
//                    if (rectWidth < 100 || rectHeight < 100) {
//                        Toast.makeText(getContext(), "所划区域太小,请重新选择区域", Toast.LENGTH_SHORT).show();
//                        isClean = true;
//                    } else {
                    isCropOk = true;
                        mUpCallback.onCropVisionAreaFinish(new Rect(left, top, right, bottom));
//                    }
                }
                invalidate();
                break;
        }
        invalidate();
        return true;
    }


    private void init4Point(int x, int y) {
        mPoint1.x = x;
        mPoint2.x = x;
        mPoint3.x = x;
        mPoint0.x = x;

        mPoint0.y = y;
        mPoint1.y = y;
        mPoint2.y = y;
        mPoint3.y = y;
    }

    /**
     * 获取所划区域对应的Rect
     */
    public interface CropVisionFinishCallback {
        void onCropVisionAreaFinish(Rect rect);
    }
}
