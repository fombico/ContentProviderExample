package com.example.providers;

import android.os.Looper;
import android.util.Log;

public class Logger {

    public static void d(String message) {
        String thread = Looper.getMainLooper().equals(Looper.myLooper()) ? "UI: " : "BG: ";
        Log.d("FRED", thread + message);
    }

}
