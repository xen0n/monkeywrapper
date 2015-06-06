package name.xen0n.monkeywrapper.service;

import java.util.HashSet;
import java.util.Set;

import name.xen0n.monkeywrapper.app.MWBaseService;
import name.xen0n.monkeywrapper.app.MWServiceAspect;
import name.xen0n.monkeywrapper.events.A11yTopWindowChangeEvent;
import android.content.Intent;
import de.greenrobot.event.EventBus;


public class MWTopWindowTrackingAspect implements MWServiceAspect {

    // top window tracking aspect
    private CharSequence topWindowPackageName;
    private CharSequence topWindowClassName;

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    @Override
    public void initAspect(final MWBaseService ctx) {
        topWindowPackageName = "";
        topWindowClassName = "";

        getEventBus().register(this);
    }

    @Override
    public Set<Integer> queryCapableRequests() {
        final Set<Integer> commands = new HashSet<Integer>();
        commands.add(MWServiceRequests.REQ_TOP_PACKAGE_NAME);
        commands.add(MWServiceRequests.REQ_TOP_CLASS_NAME);

        return commands;
    }

    @Override
    public void handleStartCommand(
            final MWBaseService ctx,
            final Intent intent,
            final int flags,
            final int startId) {
    }

    @Override
    public Object handleQuery(
            final MWBaseService ctx,
            final int request,
            final Object args) {
        switch (request) {
            case MWServiceRequests.REQ_TOP_PACKAGE_NAME:
                return topWindowPackageName;

            case MWServiceRequests.REQ_TOP_CLASS_NAME:
                return topWindowClassName;
        }

        return null;
    }

    @Override
    public void destroyAspect(final MWBaseService ctx) {
        getEventBus().unregister(this);
    }

    public void onEvent(final A11yTopWindowChangeEvent evt) {
        topWindowPackageName = evt.getPackageName();
        topWindowClassName = evt.getClassName();
    }
}
