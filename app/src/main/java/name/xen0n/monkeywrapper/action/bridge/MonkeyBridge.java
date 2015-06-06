package name.xen0n.monkeywrapper.action.bridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import name.xen0n.monkeywrapper.util.ShellUtils;
import android.util.Log;
import de.greenrobot.event.EventBus;


public class MonkeyBridge {

    private static final String TAG = "MonkeyBridge";

    public static final int DEFAULT_MONKEY_PORT = 12970;
    public static final String DEFAULT_MONKEY_PATH = "monkey";

    private final String monkeyPath;
    private final int monkeyPort;

    private boolean isAvailable;
    private boolean isStarted;

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    public MonkeyBridge() {
        this(DEFAULT_MONKEY_PATH, DEFAULT_MONKEY_PORT);
    }

    public MonkeyBridge(final String monkeyPath) {
        this(monkeyPath, DEFAULT_MONKEY_PORT);
    }

    public MonkeyBridge(final String monkeyPath, final int monkeyPort) {
        this.monkeyPath = monkeyPath;
        this.monkeyPort = monkeyPort;

        isAvailable = false;
        isStarted = false;
    }

    public String getMonkeyPath() {
        return monkeyPath;
    }

    public int getMonkeyPort() {
        return monkeyPort;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void startMonkey() {
        getEventBus().register(this);
        maybeSpawnMonkey();
    }

    public void stopMonkey() {
        getEventBus().post(new StopMonkeyEvent());
    }

    private void maybeSpawnMonkey() {
        final MonkeyThread thrd = new MonkeyThread(
                monkeyPath,
                monkeyPort,
                getEventBus());
        thrd.start();
    }

    public void onEvent(final MonkeyStartedEvent evt) {
        isAvailable = true;
        isStarted = true;
    }

    public void onEvent(final MonkeyUnavailableEvent evt) {
        isAvailable = false;
        isStarted = false;
    }

    public void onEvent(final MonkeyStoppedEvent evt) {
        isStarted = false;

        getEventBus().unregister(this);
    }

    static class MonkeyThread extends Thread {

        private final String path;
        private final int port;
        private final EventBus bus;

        private Process process;
        private boolean shouldStop;

        public MonkeyThread(
                final String path,
                final int port,
                final EventBus bus) {
            super();
            this.path = path;
            this.port = port;
            this.bus = bus;
            shouldStop = false;

            bus.register(this);
        }

        @Override
        public void run() {
            do {
                try {
                    process = ShellUtils.sudoNoWait(
                            new HashMap<String, String>(),
                            path,
                            "--port",
                            Integer.toString(port));
                } catch (Exception e) {
                    Log.e(TAG, "spawning of monkey failed", e);
                    bus.post(new MonkeyUnavailableEvent());
                    break;
                }

                bus.post(new MonkeyStartedEvent());

                // capture stdout
                final BufferedReader stdout = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                while (!shouldStop) {
                    try {
                        final String line = stdout.readLine();
                        if (line == null) {
                            break;
                        }

                        Log.v(TAG, line);
                    } catch (final IOException e) {
                        Log.e(TAG, "error reading monkey stdout", e);
                        break;
                    } finally {
                        try {
                            stdout.close();
                        } catch (final IOException e) {
                            Log.e(TAG, "error closing monkey stdout", e);
                        }
                    }
                }

                try {
                    process.waitFor();
                } catch (final InterruptedException e) {
                    Log.w(TAG, "interrupted while waiting for join", e);
                }
            } while (false);

            bus.post(new MonkeyStoppedEvent());
            bus.unregister(this);
        }

        public void onEvent(final StopMonkeyEvent evt) {
            Log.d(TAG, "StopMonkeyEvent received");
            shouldStop = true;
            process.destroy();
        }
    }

    static class StopMonkeyEvent {
    }

    public static class MonkeyStartedEvent {
    }

    public static class MonkeyUnavailableEvent {
    }

    public static class MonkeyStoppedEvent {
    }
}
