package name.xen0n.monkeywrapper.action;

import name.xen0n.monkeywrapper.action.sequence.TestSequence2Factory;


public class TestActionDecl2 extends MWActionDecl {

    public TestActionDecl2() {
        super(2, "com.tencent.mm", "", TestSequence2Factory.class);
    }
}
