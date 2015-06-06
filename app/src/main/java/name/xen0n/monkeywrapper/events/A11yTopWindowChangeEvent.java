package name.xen0n.monkeywrapper.events;

public class A11yTopWindowChangeEvent {

    private final CharSequence packageName;
    private final CharSequence className;

    public A11yTopWindowChangeEvent(
            final CharSequence packageName,
            final CharSequence className) {
        this.packageName = packageName;
        this.className = className;
    }

    public CharSequence getPackageName() {
        return packageName;
    }

    public CharSequence getClassName() {
        return className;
    }
}
