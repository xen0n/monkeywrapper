package name.xen0n.monkeywrapper.action.transport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import name.xen0n.monkeywrapper.action.MWRequestActionEvent;
import name.xen0n.monkeywrapper.util.BluetoothConnector;
import name.xen0n.monkeywrapper.util.BluetoothConnector.BluetoothSocketWrapper;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import de.greenrobot.event.EventBus;


public class BluetoothTransport implements MWTransport {

    private static final String TAG = "BluetoothTransport";

    private final BluetoothAdapter adapter;

    private TransportThread thrd;

    public EventBus getEventBus() {
        return EventBus.getDefault();
    }

    public BluetoothTransport() {
        adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean isSupported() {
        return adapter != null;
    }

    @Override
    public CharSequence getName() {
        return "Bluetooth";
    }

    @Override
    public void startTransport(final Context ctx, final EventBus bus) {
        if (!isSupported()) {
            Log.w(TAG, "startTransport: not starting because of no support");
            return;
        }

        Log.i(TAG, "startTransport: bus=" + bus);

        if (thrd != null) {
            Log.w(TAG, "ignoring restart of transport");
            return;
        }

        getEventBus().register(this);
        thrd = new TransportThread(ctx, bus, adapter);
        thrd.start();
    }

    @Override
    public void stopTransport(final Context ctx, final EventBus bus) {
        Log.i(TAG, "stopTransport");

        doStopTransport();
    }

    private void doStopTransport() {
        if (thrd != null) {
            thrd.interrupt();

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

        getEventBus().unregister(this);
    }

    public void onEvent(final TransportFailureEvent evt) {
        doStopTransport();
    }

    class TransportThread extends Thread {

        private final Context ctx;
        private final EventBus bus;
        private final BluetoothAdapter adapter;

        public TransportThread(
                final Context ctx,
                final EventBus bus,
                final BluetoothAdapter adapter) {
            this.ctx = ctx;
            this.bus = bus;
            this.adapter = adapter;
        }

        @Override
        public void run() {
            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);

                // We're launched from a service context, thus no
                // startActivityForResult. This, together with the
                // timing of transport initialization (together with
                // service init, which is likely at system boot), may
                // contribute to negative UX.
                // TODO: optimize the initialization flow
                ctx.startActivity(enableBtIntent);

                // because startActivity immediately returns, we have
                // no choice but stop.
                // TODO: use broadcast receiver to start BT transport
                // whenever BT is enabled
                bus.post(new TransportFailureEvent());
                return;
            }

            while (!interrupted()) {
                final Set<BluetoothDevice> pairedDevices = adapter
                        .getBondedDevices();
                BluetoothDevice targetDevice = null;
                if (pairedDevices.size() > 0) {
                    for (final BluetoothDevice device : pairedDevices) {
                        final String mac = device.getAddress();
                        Log
                            .i(TAG, "paired: " + device.getName() + " "
                                    + mac);

                        // test
                        // if (mac.equalsIgnoreCase("5C:31:3E:FE:20:68")) {
                        // if (mac.equalsIgnoreCase("00:14:04:01:29:95")) {
                        if (mac.equalsIgnoreCase("B4:99:4C:71:72:94")) {
                            targetDevice = device;
                            // break;
                        }
                    }
                }

                if (targetDevice == null) {
                    Log.w(TAG, "target device not found");
                    bus.post(new TransportFailureEvent());
                    return;
                }

                Log.i(TAG, "connecting target device");
                /*
                 * BluetoothSocket sk; try { sk =
                 * targetDevice.createRfcommSocketToServiceRecord(UUID
                 * .fromString("00001101-0000-1000-8000-00805F9B34FB")); }
                 * catch (final IOException e) { Log.e(TAG,
                 * "error creating rfcomm socket", e); bus.post(new
                 * TransportFailureEvent()); return; }
                 * 
                 * adapter.cancelDiscovery(); try { sk.connect(); } catch
                 * (IOException e) { Log.e(TAG,
                 * "error connecting rfcomm socket", e); bus.post(new
                 * TransportFailureEvent()); return; }
                 */

                final BluetoothConnector connector = new BluetoothConnector(
                        targetDevice,
                        false,
                        adapter,
                        null);

                BluetoothSocketWrapper skWrapper;
                try {
                    skWrapper = connector.connect();
                } catch (final IOException e) {
                    Log.e(TAG, "error creating rfcomm socket", e);
                    bus.post(new TransportFailureEvent());
                    return;
                }

                // pass established socket to client handler
                final ClientHandlerThread thrd = new ClientHandlerThread(
                        bus,
                        skWrapper);
                thrd.start();
            }
        }
    }

    class ClientHandlerThread extends Thread {

        private final EventBus bus;
        private final BluetoothSocketWrapper sk;

        public ClientHandlerThread(
                final EventBus bus,
                final BluetoothSocketWrapper sk) {
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

            try {
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
            } finally {
                try {
                    input.close();
                    sk.close();
                } catch (final IOException e) {
                    Log.e(TAG, "error during client thread cleanup", e);
                }
            }
        }
    }

    static class TransportFailureEvent {
    }
}
