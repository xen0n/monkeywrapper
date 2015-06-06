package name.xen0n.monkeywrapper.action.sequence;

public abstract class MWSequenceFactory {

    protected abstract void buildSequence(MWActionSequence.Builder builder);

    public MWActionSequence getSequence() {
        MWActionSequence.Builder builder = new MWActionSequence.Builder();
        buildSequence(builder);
        return builder.build();
    }
}
