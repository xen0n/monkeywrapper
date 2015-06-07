package name.xen0n.monkeywrapper.app;

import java.util.Set;

import android.content.Intent;


public interface MWServiceAspect {

    void initAspect(final MWBaseService ctx);

    Set<Integer> queryCapableRequests();

    void handleStartCommand(
            final MWBaseService ctx,
            final Intent intent,
            final int flags,
            final int startId);

    // void handleServiceRequest(Context ctx, Message msg);
    Object handleQuery(
            final MWBaseService ctx,
            final int request,
            final Object args);

    void destroyAspect(final MWBaseService ctx);
}
