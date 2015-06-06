package name.xen0n.monkeywrapper.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// taken from fqrouter, with minor modification
public class ShellUtils {

    private final static String[] BINARY_PLACES = {
            "/data/bin/",
            "/system/bin/",
            "/system/xbin/",
            "/sbin/",
            "/data/local/xbin/",
            "/data/local/bin/",
            "/system/sd/xbin/",
            "/system/bin/failsafe/",
            "/data/local/"
    };
    public static File DATA_DIR = new File(
            "/data/data/name.xen0n.monkeywrapper");
    public static File BUSYBOX_FILE = new File(DATA_DIR, "busybox");
    private static Boolean IS_ROOTED = null;

    public static String execute(String... command) throws Exception {
        Process process = executeNoWait(
                new HashMap<String, String>(),
                command);
        return waitFor(Arrays.toString(command), process);
    }

    public static String execute(Map<String, String> env, String... command)
            throws Exception {
        return waitFor(Arrays.toString(command), executeNoWait(env, command));
    }

    public static Process executeNoWait(
            Map<String, String> env,
            String... command) throws IOException {
        // LogUtils.i("command: " + Arrays.toString(command));
        List<String> envp = new ArrayList<String>();
        for (Map.Entry<String, String> entry : env.entrySet()) {
            envp.add(entry.getKey() + "=" + entry.getValue());
        }
        return Runtime.getRuntime().exec(
                command,
                envp.toArray(new String[envp.size()]));
    }

    public static String sudo(Map<String, String> env, String... command)
            throws Exception {
        if (Boolean.FALSE.equals(IS_ROOTED)) {
            if (BUSYBOX_FILE.exists()) {
                Process process = ShellUtils.executeNoWait(
                        env,
                        BUSYBOX_FILE.getCanonicalPath(),
                        "sh");
                OutputStreamWriter stdin = new OutputStreamWriter(
                        process.getOutputStream());
                try {
                    // LogUtils
                    // .i("write to stdin: " + Arrays.toString(command));
                    for (String c : command) {
                        stdin.write(c);
                        stdin.write(" ");
                    }
                    stdin.write("\nexit\n");
                } finally {
                    stdin.close();
                }
                return waitFor(Arrays.toString(command), process);
            } else {
                return waitFor(
                        Arrays.toString(command),
                        executeNoWait(env, command));
            }
        } else {
            Process process = sudoNoWait(env, command);
            return waitFor(Arrays.toString(command), process);
        }
    }

    public static String sudo(String... command) throws Exception {
        return sudo(new HashMap<String, String>(), command);
    }

    public static Process sudoNoWait(
            Map<String, String> env,
            String... command) throws Exception {
        if (Boolean.FALSE.equals(IS_ROOTED)) {
            return executeNoWait(env, command);
        }
        // LogUtils.i("sudo: " + Arrays.toString(command));
        ProcessBuilder processBuilder = new ProcessBuilder();
        Process process = processBuilder
            .command(findCommand("su"))
            .redirectErrorStream(true)
            .start();
        OutputStreamWriter stdin = new OutputStreamWriter(
                process.getOutputStream());
        try {
            for (Map.Entry<String, String> entry : env.entrySet()) {
                stdin.write(entry.getKey());
                stdin.write("=");
                stdin.write(entry.getValue());
                stdin.write(" ");
            }
            for (String c : command) {
                stdin.write(c);
                stdin.write(" ");
            }
            stdin.write("\nexit\n");
        } finally {
            stdin.close();
        }
        return process;
    }

    public static String findCommand(String command) {
        for (String binaryPlace : BINARY_PLACES) {
            String path = binaryPlace + command;
            if (new File(path).exists()) {
                return path;
            }
        }
        return command;
    }

    public static String waitFor(String command, Process process)
            throws Exception {
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                process.getInputStream()));
        StringBuilder output = new StringBuilder();
        try {
            String line;
            while (null != (line = stdout.readLine())) {
                output.append(line);
                output.append("\n");
            }
        } finally {
            stdout.close();
        }
        process.waitFor();
        int exitValue = process.exitValue();
        if (0 != exitValue) {
            throw new Exception("failed to execute: " + command
                    + ", exit value: " + exitValue + ", output: " + output);
        }
        return output.toString();
    }

    public static boolean checkRooted() {
        IS_ROOTED = null;
        try {
            IS_ROOTED = sudo("echo", "hello").contains("hello");
        } catch (Exception e) {
            IS_ROOTED = false;
        }
        return IS_ROOTED;
    }

    public static boolean isRooted() {
        return Boolean.TRUE.equals(IS_ROOTED);
    }
}
