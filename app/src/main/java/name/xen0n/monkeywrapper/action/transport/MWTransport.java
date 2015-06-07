package name.xen0n.monkeywrapper.action.transport;

import android.content.Context;
import de.greenrobot.event.EventBus;


public interface MWTransport {

    CharSequence getName();

    void startTransport(final Context ctx, final EventBus bus);

    void stopTransport(final Context ctx, final EventBus bus);
}
