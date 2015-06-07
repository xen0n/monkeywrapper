package name.xen0n.monkeywrapper.service;

import java.util.HashSet;
import java.util.Set;

import name.xen0n.monkeywrapper.action.bridge.MonkeyWrapper;
import name.xen0n.monkeywrapper.action.bridge.MonkeyWrapper.MonkeyStoppedEvent;
import name.xen0n.monkeywrapper.app.MWBaseService;
import name.xen0n.monkeywrapper.app.MWServiceAspect;
import android.content.Intent;
import de.greenrobot.event.EventBus;


public class MWMonkeyWrapperAspect implements MWServiceAspect {

    private MonkeyWrapper wrapper;

    @Override
    public void initAspect(final MWBaseService ctx) {
        wrapper = new MonkeyWrapper();

        EventBus.getDefault().register(this);
    }

    @Override
    public Set<Integer> queryCapableRequests() {
        final Set<Integer> commands = new HashSet<Integer>();
        commands.add(MWServiceRequests.REQ_MONKEY_AVAILABILITY);
        commands.add(MWServiceRequests.REQ_MONKEY_STARTED);
        commands.add(MWServiceRequests.REQ_MONKEY_WRAPPER);

        return commands;
    }

    @Override
    public void handleStartCommand(
            final MWBaseService ctx,
            final Intent intent,
            final int flags,
            final int startId) {
        if (!wrapper.isStarted()) {
            wrapper.startMonkey();
        }
    }

    @Override
    public Object handleQuery(
            final MWBaseService ctx,
            final int request,
            final Object args) {
        switch (request) {
            case MWServiceRequests.REQ_MONKEY_AVAILABILITY:
                return wrapper.isAvailable();

            case MWServiceRequests.REQ_MONKEY_STARTED:
                return wrapper.isStarted();

            case MWServiceRequests.REQ_MONKEY_WRAPPER:
                return wrapper;
        }

        return null;
    }

    @Override
    public void destroyAspect(final MWBaseService ctx) {
        wrapper.stopMonkey();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(final MonkeyStoppedEvent evt) {
        // due to monkey bug p/android/issues/detail?id=77961,
        // we immediately restart monkey here
        // let's hope that actions won't come up so frequently
        if (wrapper.isAvailable()) {
            wrapper.startMonkey();
        }
    }
}
