package name.xen0n.monkeywrapper.action.sequence;

public class Done implements MWActionSequenceElement {

    @Override
    public String toMonkeyCommand() {
        return "done";
    }
}
