package name.xen0n.monkeywrapper.service;

import name.xen0n.monkeywrapper.events.A11yTopWindowChangeEvent;
import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import de.greenrobot.event.EventBus;


public class MWA11yHelperService extends AccessibilityService {

    private static final String TAG = "MWA11yHelperService";

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        Log.d(TAG, "onA11yEvent: " + event);

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                getEventBus().post(processWindowStateChangeEvent(event));
                return;

            default:
                Log.wtf(TAG, "should never happen");
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

    private A11yTopWindowChangeEvent processWindowStateChangeEvent(
            final AccessibilityEvent evt) {
        final CharSequence packageName = evt.getPackageName();
        final CharSequence className = evt.getClassName();

        return new A11yTopWindowChangeEvent(packageName, className);
    }
}
