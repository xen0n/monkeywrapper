package name.xen0n.monkeywrapper.events;

import android.view.accessibility.AccessibilityEvent;


public class A11yDebugEvent {

    private final AccessibilityEvent evt;

    public A11yDebugEvent(final AccessibilityEvent evt) {
        this.evt = evt;
    }

    public AccessibilityEvent getEvent() {
        return evt;
    }
}
