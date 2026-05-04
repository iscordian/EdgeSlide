package com.iscordian.edgeslide;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GestureService extends AccessibilityService {
    private WindowManager windowManager;

    @Override
    public void onServiceConnected() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        // Create trigger zones for both sides
        addEdgeTrigger(Gravity.LEFT);
        addEdgeTrigger(Gravity.RIGHT);
    }

    private void addEdgeTrigger(final int side) {
        final int sizeInPx = (int) (40 * getResources().getDisplayMetrics().density);
        
        // Window parameters for the touch strip
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            30, // Narrow strip width
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = side;

        // Container and Arrow Icon
        final FrameLayout layout = new FrameLayout(this);
        final ImageView arrow = new ImageView(this);
        
        // Use your tapered arrow images
        arrow.setImageResource(side == Gravity.LEFT ? R.drawable.arrow_right : R.drawable.arrow_left);
        arrow.setAlpha(0.0f); // Hidden until swipe starts

        FrameLayout.LayoutParams arrowParams = new FrameLayout.LayoutParams(sizeInPx, sizeInPx, Gravity.CENTER_VERTICAL);
        layout.addView(arrow, arrowParams);

        layout.setOnTouchListener(new View.OnTouchListener() {
            private float startX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = Math.abs(event.getRawX() - startX);
                        // Show arrow more clearly as you pull further
                        arrow.setAlpha(Math.min(deltaX / 150f, 0.7f));
                        return true;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getRawX();
                        arrow.setAlpha(0.0f);
                        if (Math.abs(endX - startX) > 100) {
                            performGlobalAction(GLOBAL_ACTION_BACK);
                        }
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(layout, params);
    }

    @Override public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override public void onInterrupt() {}
  }
