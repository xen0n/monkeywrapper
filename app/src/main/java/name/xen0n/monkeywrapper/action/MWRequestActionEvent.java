package name.xen0n.monkeywrapper.action;

public class MWRequestActionEvent {

    private final int actionId;

    public MWRequestActionEvent(final int actionId) {
        this.actionId = actionId;
    }

    public int getActionId() {
        return actionId;
    }
}
