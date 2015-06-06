package name.xen0n.monkeywrapper.action.sequence;

import java.util.ArrayList;
import java.util.List;

import name.xen0n.monkeywrapper.action.bridge.MonkeyBridge;


public class MWActionSequence {

    private final List<MWActionSequenceElement> actions;

    protected MWActionSequence(final List<MWActionSequenceElement> actions) {
        this.actions = actions;
    }

    public List<String> emitMonkeyCommands() {
        final List<String> commands = new ArrayList<String>();

        for (final MWActionSequenceElement action : actions) {
            commands.add(action.toMonkeyCommand());
        }

        return commands;
    }

    public int sendToBridge(final MonkeyBridge bridge) {
        return bridge.sendCommands(emitMonkeyCommands());
    }

    public static class Builder {
        private final List<MWActionSequenceElement> actions;

        public Builder() {
            actions = new ArrayList<MWActionSequenceElement>();
        }

        public MWActionSequence build() {
            return new MWActionSequence(actions);
        }

        public Builder add(final MWActionSequenceElement action) {
            actions.add(action);
            return this;
        }
    }
}
