package name.xen0n.monkeywrapper.ui;

import name.xen0n.monkeywrapper.R;
import name.xen0n.monkeywrapper.app.MWBaseActivity;
import name.xen0n.monkeywrapper.service.MWDaemonService;
import name.xen0n.monkeywrapper.util.ShellUtils;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import butterknife.InjectView;


public class Launcher extends MWBaseActivity {

    private static final String TAG = "Launcher";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.textRootStatus)
    TextView textRootStatus;

    @Override
    protected int getContentViewId(final Bundle icicle) {
        return R.layout.ui_launcher;
    }

    @Override
    protected void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        MWDaemonService.start(this);

        setupToolbar(toolbar);
        checkRootStatus();
        checkMonkeyStatus();
    }

    public void onEvent(int dummy) {
    }

    private void setupToolbar(final Toolbar toolbar) {
        toolbar.setTitle(R.string.app_name);
    }

    private void checkRootStatus() {
        final boolean isRooted = ShellUtils.checkRooted();
        textRootStatus.setText(isRooted ? "rooted" : "not rooted");
    }

    private void checkMonkeyStatus() {
    }
}
