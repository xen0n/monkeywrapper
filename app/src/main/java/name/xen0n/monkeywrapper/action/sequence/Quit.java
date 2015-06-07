package name.xen0n.monkeywrapper.action.sequence;

public class Quit implements MWActionSequenceElement {

    @Override
    public String toMonkeyCommand() {
        return "quit";
    }
}
