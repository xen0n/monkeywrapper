package name.xen0n.monkeywrapper.service;

import name.xen0n.monkeywrapper.events.A11yDebugEvent;
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

        getEventBus().post(new A11yDebugEvent(event));
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "onInterrupt");
    }

}
