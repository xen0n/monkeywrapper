package name.xen0n.monkeywrapper.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import name.xen0n.monkeywrapper.action.transport.MWTCPTestTransport;
import name.xen0n.monkeywrapper.action.transport.MWTransport;
import name.xen0n.monkeywrapper.app.MWBaseService;
import name.xen0n.monkeywrapper.app.MWServiceAspect;
import android.content.Intent;
import android.util.Log;
import de.greenrobot.event.EventBus;


public class MWTransportDriverAspect implements MWServiceAspect {

    private static final String TAG = "MWTransportDriverAspect";

    private List<MWTransport> xports;

    @Override
    public void initAspect(final MWBaseService ctx) {
        xports = new ArrayList<MWTransport>();
        xports.add(new MWTCPTestTransport());
    }

    @Override
    public Set<Integer> queryCapableRequests() {
        return null;
    }

    @Override
    public void handleStartCommand(
            final MWBaseService ctx,
            final Intent intent,
            final int flags,
            final int startId) {
        final EventBus bus = EventBus.getDefault();

        Log.i(TAG, "starting transports");
        for (final MWTransport xport : xports) {
            xport.startTransport(ctx, bus);
        }
    }

    @Override
    public Object handleQuery(
            final MWBaseService ctx,
            final int request,
            final Object args) {
        return null;
    }

    @Override
    public void destroyAspect(final MWBaseService ctx) {
        final EventBus bus = EventBus.getDefault();

        Log.i(TAG, "stopping transports");
        for (final MWTransport xport : xports) {
            xport.stopTransport(ctx, bus);
        }
    }
}
