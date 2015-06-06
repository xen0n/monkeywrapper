package name.xen0n.monkeywrapper.app;

public interface MWAspectifiedService {

    void addAspect(final MWServiceAspect aspect);

    void populateAspects();

    Object queryAspectFor(final int request, final Object args);
}
