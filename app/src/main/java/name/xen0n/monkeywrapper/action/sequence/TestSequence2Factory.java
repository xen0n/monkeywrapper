package name.xen0n.monkeywrapper.action.sequence;

import name.xen0n.monkeywrapper.action.sequence.MWActionSequence.Builder;


public class TestSequence2Factory extends MWSequenceFactory {

    @Override
    protected void buildSequence(final Builder builder) {
        builder.add(new Press("back"));
        builder.add(new Quit());
    }
}
