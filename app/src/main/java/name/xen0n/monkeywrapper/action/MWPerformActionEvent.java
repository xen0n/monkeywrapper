package name.xen0n.monkeywrapper.action;

public class MWPerformActionEvent {

    private final MWActionDecl actionDecl;

    public MWPerformActionEvent(final MWActionDecl actionDecl) {
        super();
        this.actionDecl = actionDecl;
    }

    public MWActionDecl getActionDecl() {
        return actionDecl;
    }
}
