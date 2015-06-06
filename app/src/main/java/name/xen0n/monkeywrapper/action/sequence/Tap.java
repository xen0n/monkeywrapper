package name.xen0n.monkeywrapper.action.sequence;

public class Tap implements MWActionSequenceElement {

    private final int x;
    private final int y;

    public Tap(final int x, final int y) {
        super();
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toMonkeyCommand() {
        return "tap " + x + " " + y;
    }

}
