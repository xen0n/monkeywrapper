package name.xen0n.monkeywrapper.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;


public abstract class MWBaseActivity extends ActionBarActivity {

    protected abstract int getContentViewId(final Bundle icicle);

    protected EventBus getEventBus() {
        return EventBus.getDefault();
    }

    @Override
    protected void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        final int contentViewId = getContentViewId(icicle);
        setContentView(contentViewId);
        ButterKnife.inject(this);

        getEventBus().register(this);
    }

    @Override
    protected void onDestroy() {
        getEventBus().unregister(this);

        super.onDestroy();
    }
}
