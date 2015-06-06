package name.xen0n.monkeywrapper.action.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import name.xen0n.monkeywrapper.action.MWRequestActionEvent;
import name.xen0n.monkeywrapper.util.NetHelper;
import android.util.Log;
import de.greenrobot.event.EventBus;


public class MWTCPTestTransport implements MWTransport {

    private static final String TAG = "MWTCPTestTransport";
    private static final int TEST_PORT = 12450;

    private TransportThread thrd;

    @Override
    public CharSequence getName() {
        return "test tcp";
    }

    @Override
    public void startTransport(final EventBus bus) {
        Log.i(TAG, "startTransport: bus=" + bus);

        thrd = new TransportThread(bus, TEST_PORT);
        thrd.start();
    }

    @Override
    public void stopTransport(final EventBus bus) {
        Log.i(TAG, "stopTransport");

        if (thrd != null) {
            for (int i = 0; i < 10; i++) {
                try {
                    thrd.join();
                    thrd = null;
                    break;
                } catch (final InterruptedException e) {
                    Log.e(TAG, "stopTransport: interrupted", e);
                }
            }
        }
    }

    class TransportThread extends Thread {

        private final EventBus bus;
        private final int port;

        public TransportThread(final EventBus bus, final int port) {
            this.bus = bus;
            this.port = port;
        }

        @Override
        public void run() {
            final ServerSocket sk;
            try {
                sk = new ServerSocket(
                        port,
                        1024,
                        NetHelper.getLoopbackAddress());
            } catch (final IOException e) {
                Log.e(TAG, "error during server socket creation", e);
                return;
            }

            try {
                while (!isInterrupted()) {
                    try {
                        final Socket client = sk.accept();
                        final ClientHandlerThread thrd = new ClientHandlerThread(
                                bus,
                                client);
                        thrd.start();
                    } catch (final IOException e) {
                        Log.e(TAG, "error during accept", e);
                    }
                }
            } finally {
                try {
                    sk.close();
                } catch (final IOException e) {
                    Log.w(TAG, "error during cleanup", e);
                }
            }
        }
    }

    class ClientHandlerThread extends Thread {

        private final EventBus bus;
        private final Socket sk;

        public ClientHandlerThread(final EventBus bus, final Socket sk) {
            this.bus = bus;
            this.sk = sk;
        }

        @Override
        public void run() {
            final BufferedReader input;
            try {
                input = new BufferedReader(new InputStreamReader(
                        sk.getInputStream()));
            } catch (final IOException e) {
                Log.e(TAG, "error creating input stream reader", e);
                return;
            }

            while (!isInterrupted()) {
                String line = null;
                try {
                    line = input.readLine().trim();
                } catch (final IOException e) {
                    Log.e(TAG, "error reading input", e);
                }

                int actionId = 0;
                try {
                    actionId = Integer.parseInt(line);
                } catch (final NumberFormatException ignored) {
                }

                if (actionId == 0) {
                    // end the connection
                    break;
                }

                // emit an action request
                bus.post(new MWRequestActionEvent(actionId));
            }

            try {
                input.close();
                sk.close();
            } catch (final IOException e) {
                Log.e(TAG, "error during client thread cleanup", e);
            }
        }
    }
}
