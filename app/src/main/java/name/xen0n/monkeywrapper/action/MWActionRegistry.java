package name.xen0n.monkeywrapper.action;

import java.util.ArrayList;
import java.util.List;


public class MWActionRegistry {

    private List<MWActionDecl> decls;

    public MWActionRegistry() {
        decls = new ArrayList<MWActionDecl>();

        decls.add(new TestActionDecl1());
        decls.add(new TestActionDecl2());
    }

    public MWActionDecl resolveAction(
            final int actionId,
            final CharSequence packageName,
            final CharSequence className) {
        for (final MWActionDecl decl : decls) {
            if (decl.matchTarget(actionId, packageName, className)) {
                return decl;
            }
        }

        return null;
    }
}
