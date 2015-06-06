package name.xen0n.monkeywrapper.service;

import name.xen0n.monkeywrapper.app.MWBaseService;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


public class MWDaemonService extends MWBaseService {

    private static final String TAG = "MWDaemonService";

    public static void start(final Context ctx) {
        final Intent intent = new Intent(ctx, MWDaemonService.class);
        ctx.startService(intent);
    }

    @Override
    public void populateAspects() {
        addAspect(new MWTopWindowTrackingAspect());
        addAspect(new MWActionResolutionAspect());
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }
}
