package name.xen0n.monkeywrapper.service;

import java.lang.ref.WeakReference;
import java.util.Set;

import name.xen0n.monkeywrapper.action.MWActionDecl;
import name.xen0n.monkeywrapper.action.MWActionRegistry;
import name.xen0n.monkeywrapper.action.MWPerformActionEvent;
import name.xen0n.monkeywrapper.action.MWRequestActionEvent;
import name.xen0n.monkeywrapper.action.bridge.MonkeyBridge;
import name.xen0n.monkeywrapper.app.MWBaseService;
import name.xen0n.monkeywrapper.app.MWServiceAspect;
import android.content.Intent;
import android.util.Log;
import de.greenrobot.event.EventBus;


public class MWActionResolutionAspect implements MWServiceAspect {

    private static final String TAG = "MWActionResolutionAspect";

    private WeakReference<MWBaseService> srv;
    private MWActionRegistry registry;

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    @Override
    public void initAspect(final MWBaseService ctx) {
        srv = new WeakReference<MWBaseService>(ctx);
        registry = new MWActionRegistry();

        getEventBus().register(this);
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
        getEventBus().unregister(this);
    }

    public void onEvent(final MWRequestActionEvent evt) {
        resolveAction(evt.getActionId());
    }

    public void onEvent(final MWPerformActionEvent evt) {
        final MWActionDecl decl = evt.getActionDecl();
        Log.i(TAG, "going to perform action " + decl);

        MonkeyBridge bridge = new MonkeyBridge();
        decl.sendToBridge(bridge);
    }

    private void resolveAction(final int actionId) {
        final String packageName = (String) srv.get().queryAspectFor(
                MWServiceRequests.REQ_TOP_PACKAGE_NAME,
                null);
        final String className = (String) srv.get().queryAspectFor(
                MWServiceRequests.REQ_TOP_CLASS_NAME,
                null);

        Log.d(TAG, "resolveAction: id=" + actionId + " package="
                + packageName + " class=" + className);
        final MWActionDecl resolvedDecl = registry.resolveAction(
                actionId,
                packageName,
                className);

        if (resolvedDecl == null) {
            Log.i(TAG, "resolveAction: no corresponding action");
            return;
        }

        Log.i(TAG, "resolveAction: resolvedDecl=" + resolvedDecl);
        getEventBus().post(new MWPerformActionEvent(resolvedDecl));
    }
}
