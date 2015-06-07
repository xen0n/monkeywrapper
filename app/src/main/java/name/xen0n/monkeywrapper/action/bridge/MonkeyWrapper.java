package name.xen0n.monkeywrapper.action.bridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import name.xen0n.monkeywrapper.util.ShellUtils;
import android.util.Log;
import de.greenrobot.event.EventBus;


public class MonkeyWrapper {

    private static final String TAG = "MonkeyWrapper";

    public static final String DEFAULT_MONKEY_PATH = "monkey";

    private final String monkeyPath;
    private final int monkeyPort;

    private boolean isRegistered;
    private boolean isAvailable;
    private boolean isStarted;

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    public MonkeyWrapper() {
        this(DEFAULT_MONKEY_PATH, MonkeyBridge.DEFAULT_MONKEY_PORT);
    }

    public MonkeyWrapper(final String monkeyPath) {
        this(monkeyPath, MonkeyBridge.DEFAULT_MONKEY_PORT);
    }

    public MonkeyWrapper(final String monkeyPath, final int monkeyPort) {
        this.monkeyPath = monkeyPath;
        this.monkeyPort = monkeyPort;

        isRegistered = false;
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
        if (!isRegistered) {
            getEventBus().register(this);
            isRegistered = true;
        }

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

        if (isRegistered) {
            getEventBus().unregister(this);
            isRegistered = false;
        }
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
            String psOutput;
            try {
                psOutput = ShellUtils.execute("ps");
            } catch (Exception e) {
                Log.w(TAG, "error executing ps, bailing for safety");
                return;
            }

            if (psOutput.contains("com.android.commands.monkey")) {
                Log.d(TAG, "there is already a monkey, bailing");
                return;
            }

            try {
                do {
                    Log.i(TAG, "spawning monkey: path=" + path + " port="
                            + port);

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

                    Log.i(TAG, "monkey started: " + process);
                    bus.post(new MonkeyStartedEvent());

                    // capture stdout
                    final BufferedReader stdout = new BufferedReader(
                            new InputStreamReader(process.getInputStream()));

                    try {
                        while (!shouldStop) {
                            try {
                                final String line = stdout.readLine();
                                if (line == null) {
                                    break;
                                }

                                Log.v(TAG, line);

                                if (line
                                        .equals("Error binding to network socket.")) {
                                    // maybe another monkey is running?
                                    // kill it!
                                    // XXX: only works with busybox...
                                    try {
                                        ShellUtils
                                        .sudo(
                                                "killall",
                                                "com.android.commands.monkey");
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            } catch (final IOException e) {
                                Log.e(TAG, "error reading monkey stdout", e);
                                break;
                            }
                        }
                    } finally {
                        try {
                            stdout.close();
                        } catch (final IOException e) {
                            Log.e(TAG, "error closing monkey stdout", e);
                        }
                    }

                    try {
                        process.waitFor();
                    } catch (final InterruptedException e) {
                        Log.w(TAG, "interrupted while waiting for join", e);
                    }
                } while (false);
            } finally {
                bus.post(new MonkeyStoppedEvent());
                bus.unregister(this);
            }
        }

        public void onEvent(final StopMonkeyEvent evt) {
            Log.d(TAG, "StopMonkeyEvent received");
            shouldStop = true;
            if (process != null) {
                process.destroy();
                process = null;
            }
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
