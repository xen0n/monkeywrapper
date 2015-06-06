package name.xen0n.monkeywrapper.app;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.util.Log;


public abstract class MWBaseService extends Service
        implements MWAspectifiedService {

    private static final String TAG = "MWBaseService";

    private final Map<Integer, WeakReference<MWServiceAspect>> commandMap;
    private final List<MWServiceAspect> aspects;

    public MWBaseService() {
        commandMap = new HashMap<Integer, WeakReference<MWServiceAspect>>();
        aspects = new ArrayList<MWServiceAspect>();

        populateAspects();
        initCommandMap();
    }

    @Override
    public void addAspect(final MWServiceAspect aspect) {
        aspects.add(aspect);
    }

    private void initCommandMap() {
        for (final MWServiceAspect aspect : aspects) {
            final Set<Integer> capableCommands = aspect
                    .queryCapableRequests();
            for (final int command : capableCommands) {
                commandMap.put(command, new WeakReference<MWServiceAspect>(
                        aspect));
            }
        }
    }

    @Override
    public Object queryAspectFor(final int request, final Object args) {
        // delegate to aspects
        WeakReference<MWServiceAspect> aspect = commandMap.get(request);

        if (aspect != null) {
            return aspect.get().handleQuery(this, request, args);
        } else {
            Log
                .e(TAG, "unknown request: " + request + "(args=" + args
                        + ")");
            return null;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        for (MWServiceAspect aspect : aspects) {
            aspect.initAspect(this);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // notify aspects
        for (MWServiceAspect aspect : aspects) {
            // NOTE: intent could be null, if service crashed and is being
            // restarted by system. Aspects should handle this.
            aspect.handleStartCommand(this, intent, flags, startId);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // finalize aspects
        for (MWServiceAspect aspect : aspects) {
            aspect.destroyAspect(this);
        }

        super.onDestroy();
    }

}
