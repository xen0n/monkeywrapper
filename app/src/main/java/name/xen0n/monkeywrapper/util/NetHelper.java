package name.xen0n.monkeywrapper.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.os.Build;


public class NetHelper {

    public static InetAddress getLoopbackAddress() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return InetAddress.getLoopbackAddress();
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // wtf...
            return null;
        }
    }
}
