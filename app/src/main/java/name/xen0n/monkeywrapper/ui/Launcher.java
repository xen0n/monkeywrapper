package name.xen0n.monkeywrapper.ui;

import name.xen0n.monkeywrapper.R;
import name.xen0n.monkeywrapper.app.MWBaseActivity;
import android.os.Bundle;


public class Launcher extends MWBaseActivity {

    @Override
    protected int getContentViewId(final Bundle icicle) {
        return R.layout.ui_launcher;
    }

    public void onEvent(int dummy) {
    }
}
