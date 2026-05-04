package com.iscordian.edgeslide;

import android.accessibilityservice.AccessibilityService;
import android.graphics.PixelFormat;
import android.os.Handler;
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
        addEdgeTrigger(Gravity.LEFT);
        addEdgeTrigger(Gravity.RIGHT);
    }

    private void addEdgeTrigger(final int side) {
        final int sizeInPx = (int) (40 * getResources().getDisplayMetrics().density);
        
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            40, // Slightly wider for easier detection
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = side;

        final FrameLayout layout = new FrameLayout(this);
        final ImageView arrow = new ImageView(this);
        arrow.setImageResource(side == Gravity.LEFT ? R.drawable.arrow_right : R.drawable.arrow_left);
        arrow.setAlpha(0.0f);

        layout.addView(arrow, new FrameLayout.LayoutParams(sizeInPx, sizeInPx, Gravity.CENTER_VERTICAL));

        layout.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private final Handler handler = new Handler();
            // Runnable to trigger power menu
            private final Runnable longPressRunnable = new Runnable() {
                @Override
                public void run() {
                    performGlobalAction(GLOBAL_ACTION_POWER_DIALOG);
                }
            };

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        handler.postDelayed(longPressRunnable, 5000); // 5 second timer
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = Math.abs(event.getRawX() - startX);
                        arrow.setAlpha(Math.min(deltaX / 150f, 0.7f));
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(longPressRunnable); // Stop timer if finger lifted
                        arrow.setAlpha(0.0f);
                        if (Math.abs(event.getRawX() - startX) > 100) {
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
