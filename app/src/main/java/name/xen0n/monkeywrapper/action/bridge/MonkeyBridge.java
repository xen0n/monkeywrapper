package name.xen0n.monkeywrapper.action.bridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import android.util.Log;


public class MonkeyBridge {

    private static final String TAG = "MonkeyBridge";

    public static final int DEFAULT_MONKEY_PORT = 12970;

    private final int monkeyPort;

    private Socket sk;
    private BufferedReader rx;
    private OutputStreamWriter tx;

    public MonkeyBridge() {
        this(DEFAULT_MONKEY_PORT);
    }

    public MonkeyBridge(final int monkeyPort) {
        this.monkeyPort = monkeyPort;
    }

    public int getMonkeyPort() {
        return monkeyPort;
    }

    public boolean connect() {
        final Socket tmp;
        try {
            tmp = new Socket("127.0.0.1", monkeyPort);
        } catch (UnknownHostException e) {
            // not possible
            Log.wtf(TAG, "UnknownHostException with 127.0.0.1", e);
            return false;
        } catch (IOException e) {
            Log.e(TAG, "error connecting to monkey", e);
            return false;
        }

        sk = tmp;
        try {
            tx = new OutputStreamWriter(sk.getOutputStream());
            rx = new BufferedReader(new InputStreamReader(
                    sk.getInputStream()));
        } catch (final IOException e) {
            Log.e(TAG, "error creating streams", e);

            try {
                if (tx != null) {
                    tx.close();
                }
                if (rx != null) {
                    rx.close();
                }

                sk.close();
            } catch (final IOException e1) {
                Log.e(TAG, "error during failed connection cleanup", e1);
            }

            return false;
        }

        return true;
    }

    public void close() {
        try {
            tx.close();
            rx.close();
            sk.close();
        } catch (IOException e) {
            Log.w(TAG, "error closing monkey socket", e);
        }

        tx = null;
        rx = null;
        sk = null;
    }

    public int sendCommands(final List<String> cmds) {
        int commandsSent = 0;

        try {
            for (final String cmd : cmds) {
                tx.write(cmd);
                tx.write("\n");
                tx.flush();

                final String response = rx.readLine();
                Log.v(TAG, "cmd: " + cmd + " -> " + response);
            }
        } catch (final IOException e) {
            Log.e(TAG, "error during commanding", e);
        }

        Log.d(TAG, "sent " + commandsSent + " command(s)");

        return commandsSent;
    }
}
