package name.xen0n.monkeywrapper.action;

import name.xen0n.monkeywrapper.action.sequence.TestSequence1Factory;


public class TestActionDecl1 extends MWActionDecl {

    public TestActionDecl1() {
        super(1, "name.xen0n.monkeywrapper", "", TestSequence1Factory.class);
    }
}
