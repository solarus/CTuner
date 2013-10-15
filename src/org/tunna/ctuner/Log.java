package org.tunna.ctuner;

public class Log {
    private static final String appName = "CTuner";

    public static void d (String message) {
        android.util.Log.d(appName, message);
    }

    public static void i (String message) {
        android.util.Log.i(appName, message);
    }

    public static void e (String message) {
        android.util.Log.e(appName, message);
    }
}
