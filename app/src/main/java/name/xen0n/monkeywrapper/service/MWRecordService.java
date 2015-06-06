package name.xen0n.monkeywrapper.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;


public class MWRecordService extends Service {

    private static final String TAG = "MWRecordService";

    private WindowManager wm;
    private View overlayView;

    public static void start(final Context ctx) {
        final Intent intent = new Intent(ctx, MWRecordService.class);
        ctx.startService(intent);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        appendOverlayView();
    }

    @Override
    public void onDestroy() {
        if (overlayView != null) {
            wm.removeView(overlayView);
        }

        super.onDestroy();
    }

    private void appendOverlayView() {
        final View result = new View(this);
        result.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                Log.v(TAG, "onTouch: " + event);
                return false;
            }
        });
        result.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(
                    final View v,
                    final int keyCode,
                    final KeyEvent event) {
                Log.v(TAG, "onKey: keyCode=" + keyCode + " " + event);
                return false;
            }
        });

        final LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT);
        lp.gravity = Gravity.TOP | Gravity.LEFT;
        lp.x = 0;
        lp.y = 0;

        wm.addView(result, lp);

        overlayView = result;
    }
}
