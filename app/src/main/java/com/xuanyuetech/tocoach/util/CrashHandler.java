package com.xuanyuetech.tocoach.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.xuanyuetech.tocoach.activity.MainActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;

public class CrashHandler  implements Thread.UncaughtExceptionHandler{

    private Activity activity;
    private final static String KEY_JSON_ERROR_STACK = "error_stack";
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private CrashHandler() {}

    public static CrashHandler getInstance() {
        return new CrashHandler();
    }

    public void init() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public CrashHandler(Activity a) {
        activity = a;
    }

    @Override
    public void uncaughtException(@NotNull Thread thread, @NotNull Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        }else {

            Intent intent = new Intent(activity, MainActivity.class);

            intent.putExtra("crash", true);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    activity.getBaseContext(), 0, intent, intent.getFlags());

            //Following code will restart your application after 0.5 seconds
            AlarmManager mgr = (AlarmManager) activity.getBaseContext()
                    .getSystemService(Context.ALARM_SERVICE);
            assert mgr != null;
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500,
                    pendingIntent);

            //This will finish your activity manually
            activity.finish();

            //This will stop your application and take out from it.
            System.exit(2);
        }

    }

    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        saveCrashLog2File(ex);
        return false;
    }

    private void saveCrashLog2File(Throwable ex) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = sdFormatter.format((System.currentTimeMillis()));
        String filename = "crash_" + time + ".log";
        JSONObject messageRootObject = new JSONObject();
        try {
            messageRootObject.put("error", ex.toString());
            messageRootObject.put(KEY_JSON_ERROR_STACK, buildStackTraceFromException(ex));
        } catch (Exception e) {
            e.printStackTrace();
        }
        write(filename, messageRootObject.toString() + "\n");
    }

    private String buildStackTraceFromException(Throwable ex) {
        StringBuilder context = null;
        if (ex != null) {
            context = new StringBuilder(ex.toString() + "\n");
            StackTraceElement[] ste = ex.getStackTrace();
            for (StackTraceElement traceElement : ste) {
                context.append(" at ").append(traceElement.toString()).append("\n");
            }
            Throwable cex = ex.getCause();
            if (cex != null) {
                ste = cex.getStackTrace();
                context.append("Cased by: ").append(cex.toString()).append("\n");
                for (StackTraceElement stackTraceElement : ste) {
                    context.append(" at ").append(stackTraceElement.toString()).append("\n");
                }
            }
        }
        assert context != null;
        return context.toString();
    }
    private void write(String fileName, String content) {
        File file = new File(new FilePathHelper(activity).getAppCrushDirPath(), fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdir();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ignored) {
            }
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false)));
            out.write(content);
        } catch (Exception ignored) {


        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

}
