package name.xen0n.monkeywrapper.action.transport;

import de.greenrobot.event.EventBus;

public interface MWTransport {

    CharSequence getName();

    void startTransport(final EventBus bus);

    void stopTransport(final EventBus bus);
}
