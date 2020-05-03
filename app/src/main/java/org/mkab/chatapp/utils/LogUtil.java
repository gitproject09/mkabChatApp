package org.mkab.chatapp.utils;

import android.util.Log;

public class LogUtil {

    public static void printLogMessage(String className, String tag, String message) {
        Log.d("MedicalHelpDesk", className + "   ===>   " + tag + "   ===>   " + message);
    }

    public static void printInfoMessage(String className, String tag, String message) {
        Log.i("MedicalHelpDesk", className + "   ===>   " + tag + "   ===>   " + message);
    }

    public static void printWarningMessage(String className, String tag, String message) {
        Log.w("MedicalHelpDesk", className + "   ===>   " + tag + "   ===>   " + message);
    }

    public static void printErrorLogMessage(String className, String tag, String message) {
        Log.e("MedicalHelpDesk", className + "   ===>   " + tag + "   ===>   " + message);
    }

    public static void printErrorLog(String className, String message) {
        Log.e("MedicalHelpDesk::" + className, message);
    }
}