package name.xen0n.monkeywrapper.service;

import name.xen0n.monkeywrapper.events.A11yTopWindowChangeEvent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import de.greenrobot.event.EventBus;


public class MWDaemonService extends Service {

    private static final String TAG = "MWDaemonService";

    private CharSequence topWindowPackageName;
    private CharSequence topWindowClassName;

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    public static void start(final Context ctx) {
        final Intent intent = new Intent(ctx, MWDaemonService.class);
        ctx.startService(intent);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(
            final Intent intent,
            final int flags,
            final int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getEventBus().register(this);
    }

    @Override
    public void onDestroy() {
        getEventBus().unregister(this);
        super.onDestroy();
    }

    public void onEvent(final A11yTopWindowChangeEvent evt) {
        topWindowPackageName = evt.getPackageName();
        topWindowClassName = evt.getClassName();
    }
}
