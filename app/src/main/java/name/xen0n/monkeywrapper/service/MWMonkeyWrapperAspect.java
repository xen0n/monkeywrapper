package name.xen0n.monkeywrapper.service;

import java.util.HashSet;
import java.util.Set;

import name.xen0n.monkeywrapper.action.bridge.MonkeyBridge;
import name.xen0n.monkeywrapper.app.MWBaseService;
import name.xen0n.monkeywrapper.app.MWServiceAspect;
import android.content.Intent;
import de.greenrobot.event.EventBus;


public class MWMonkeyWrapperAspect implements MWServiceAspect {

    private MonkeyBridge bridge;

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    @Override
    public void initAspect(final MWBaseService ctx) {
        bridge = new MonkeyBridge();

        getEventBus().register(this);
    }

    @Override
    public Set<Integer> queryCapableRequests() {
        final Set<Integer> commands = new HashSet<Integer>();
        commands.add(MWServiceRequests.REQ_MONKEY_AVAILABILITY);
        commands.add(MWServiceRequests.REQ_MONKEY_STARTED);

        return commands;
    }

    @Override
    public void handleStartCommand(
            final MWBaseService ctx,
            final Intent intent,
            final int flags,
            final int startId) {
        if (!bridge.isStarted()) {
            bridge.startMonkey();
        }
    }

    @Override
    public Object handleQuery(
            final MWBaseService ctx,
            final int request,
            final Object args) {
        switch (request) {
            case MWServiceRequests.REQ_MONKEY_AVAILABILITY:
                return bridge.isAvailable();

            case MWServiceRequests.REQ_MONKEY_STARTED:
                return bridge.isStarted();
        }

        return null;
    }

    @Override
    public void destroyAspect(final MWBaseService ctx) {
        bridge.stopMonkey();
        getEventBus().unregister(this);
    }
}
