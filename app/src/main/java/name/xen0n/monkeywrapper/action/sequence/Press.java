package name.xen0n.monkeywrapper.action.sequence;

public class Press implements MWActionSequenceElement {

    private final String keycode;

    public Press(final String keycode) {
        this.keycode = keycode;
    }

    public String getKeycode() {
        return keycode;
    }

    @Override
    public String toMonkeyCommand() {
        return "press " + keycode;
    }

}
