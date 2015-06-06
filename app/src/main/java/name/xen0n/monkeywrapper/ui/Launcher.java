package name.xen0n.monkeywrapper.ui;

import name.xen0n.monkeywrapper.R;
import name.xen0n.monkeywrapper.app.MWBaseActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import butterknife.InjectView;


public class Launcher extends MWBaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected int getContentViewId(final Bundle icicle) {
        return R.layout.ui_launcher;
    }

    @Override
    protected void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setupToolbar(toolbar);
    }

    public void onEvent(int dummy) {
    }

    private void setupToolbar(final Toolbar toolbar) {
        toolbar.setTitle(R.string.app_name);
    }
}
