package com.mahmoudshabat.restaurants.utils.customMap;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

public class MapWrapperLayout extends FrameLayout {
    private OnTouchesListener mOnTouchesListener;

    public interface OnTouchesListener {
        void onTouchEnded(MotionEvent motionEvent);

        void onTouchStarted(MotionEvent motionEvent);
    }


    public void setOnTouchesListener(OnTouchesListener mOnTouchesListener) {
        this.mOnTouchesListener = mOnTouchesListener;
    }

    private int fingers = 0;
    private GoogleMap googleMap;
    private long lastZoomTime = 0;
    private float lastSpan = -1;
    private Handler handler = new Handler();

    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    public MapWrapperLayout(Context context) {
        super(context);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapWrapperLayout(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }


    public void init(GoogleMap map) {
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (lastSpan == -1) {
                    lastSpan = detector.getCurrentSpan();
                } else if (detector.getEventTime() - lastZoomTime >= 50) {
                    lastZoomTime = detector.getEventTime();
                    googleMap.animateCamera(CameraUpdateFactory.zoomBy(getZoomValue(detector.getCurrentSpan(), lastSpan)), 50, null);
                    lastSpan = detector.getCurrentSpan();
                }
                return false;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                lastSpan = -1;
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                lastSpan = -1;

            }
        });
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {

                disableScrolling();
                googleMap.animateCamera(CameraUpdateFactory.zoomIn(), 400, null);

                return true;
            }
        });
        googleMap = map;
    }

    private float getZoomValue(float currentSpan, float lastSpan) {
        double value = (Math.log(currentSpan / lastSpan) / Math.log(1.55d));
        return (float) value;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(gestureDetector!=null){
        gestureDetector.onTouchEvent(ev);
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
                fingers = fingers + 1;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                fingers = fingers - 1;
                break;
            case MotionEvent.ACTION_UP:
                fingers = 0;
                break;
            case MotionEvent.ACTION_DOWN:
                fingers = 1;
                break;
        }
        switch (ev.getActionMasked()) {
            case 0:
                this.mOnTouchesListener.onTouchStarted(ev);
                break;
            case 1:
            case 3:
                this.mOnTouchesListener.onTouchEnded(ev);
                break;
        }
        if (fingers > 1) {
            disableScrolling();
        } else if (fingers < 1) {
            enableScrolling();
        }
        if (fingers > 1) {
            return scaleGestureDetector.onTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
        }else{
            return false ;
        }
    }

    private void enableScrolling() {
        if (googleMap != null && !googleMap.getUiSettings().isScrollGesturesEnabled()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    googleMap.getUiSettings().setAllGesturesEnabled(true);
                }
            }, 50);
        }
    }

    private void disableScrolling() {
        handler.removeCallbacksAndMessages(null);
        if (googleMap != null && googleMap.getUiSettings().isScrollGesturesEnabled()) {
            googleMap.getUiSettings().setAllGesturesEnabled(false);
        }
    }
}
