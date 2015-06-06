package name.xen0n.monkeywrapper.action;

public class MWRequestActionEvent {

    private final int actionId;
    private final CharSequence targetPackage;
    private final CharSequence targetClass;

    public MWRequestActionEvent(
            final int actionId,
            final CharSequence targetPackage,
            final CharSequence targetClass) {
        this.actionId = actionId;
        this.targetPackage = targetPackage;
        this.targetClass = targetClass;
    }

    public int getActionId() {
        return actionId;
    }

    public CharSequence getTargetPackage() {
        return targetPackage;
    }

    public CharSequence getTargetClass() {
        return targetClass;
    }
}
