package com.jyj.mylinkmusickeysend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 崩溃日志抓取
 * <p>
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告
 * <p>
 * @author liuhe688
 * @一些改动 waka
 */
public class CrashCatchHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    private static final CrashCatchHandler INSTANCE = new CrashCatchHandler();// 单例模式
    private Context context;
    private UncaughtExceptionHandler defaultHandler;// 系统默认的UncaughtException处理类
    private Map<String, String> infosMap = new HashMap<String, String>(); // 用来存储设备信息和异常信息

    /**
     * 私有构造方法，保证只有一个CrashHandler实例
     */
    private CrashCatchHandler() {

    }

    /**
     * 获取CrashHandler，单例模式
     *
     * @return
     */
    public static CrashCatchHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置当前CrashHandler为程序的默认处理器
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && defaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            defaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "exception : ", e);
                e.printStackTrace();
            }
            // 杀死进程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return 如果处理了该异常信息, 返回true;否则返回false.
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        // 使用Toast显示异常信息
        new Thread() {
            public void run() {
                Looper.prepare();
                String stackTrace = CrashCatchHandler.this.getStackTrace(ex);
                Toast.makeText(context, "程序出现未捕获的异常，即将退出！" + ex.getMessage() + " " + stackTrace, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

//        collectDeviceInfo(context);// 收集设备参数信息
        saveCrashInfoToFile(ex);// 保存日志文件

        return true;
    }

    private String getStackTrace(Throwable ex) {
        Writer writer = new StringWriter();// 这个writer下面还会用到，所以需要它的实例
        PrintWriter printWriter = new PrintWriter(writer);// 输出错误栈信息需要用到PrintWriter
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {// 循环，把所有的cause都输出到printWriter中
            cause.printStackTrace(printWriter);
            cause = ex.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 收集设备信息
     *
     * @param context
     */
    public void collectDeviceInfo(Context context) {
        // 使用包管理器获取信息
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                // TODO 在这里得到包的信息
                String versionName = pi.versionName == null ? "" : pi.versionName;// 版本名;若versionName==null，则="null"；否则=versionName
                String versionCode = pi.versionCode + "";// 版本号
                infosMap.put("versionName", versionName);
                infosMap.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an NameNotFoundException occured when collect package info");
            e.printStackTrace();
        }

        // 使用反射获取获取系统的硬件信息
        Field[] fields = Build.class.getDeclaredFields();// 获得某个类的所有申明的字段，即包括public、private和proteced，
        for (Field field : fields) {
            field.setAccessible(true);// 暴力反射 ,获取私有的信息;类中的成员变量为private,故必须进行此操作
            try {
                infosMap.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "an IllegalArgumentException occured when collect reflect field info", e);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                Log.e(TAG, "an IllegalAccessException occured when collect reflect field info", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称
     */
    @SuppressLint("CommitPrefEdits")
    private String saveCrashInfoToFile(Throwable ex) {
        // 字符串流
        final StringBuffer stringBuffer = new StringBuffer();

        // 获得设备信息
        for (Map.Entry<String, String> entry : infosMap.entrySet()) {// 遍历map中的值
            String key = entry.getKey();
            String value = entry.getValue();
            stringBuffer.append(key + "=" + value + "\n");
        }

        String result = getStackTrace(ex);
        stringBuffer.append(result);

        // 写入文件
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/key_play";
        String crashFileName = rootPath + "/crash_" + simpleDateFormat.format(new Date()) + ".log";


        //因为是sd卡根目录，所以就需要创建父文件夹了
        File file = new File(rootPath);
        if (!file.exists()) {
            file.mkdirs();// 如果不存在，则创建所有的父文件夹
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(crashFileName);
            fos.write(stringBuffer.toString().getBytes());

            return crashFileName;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "an FileNotFoundException occured when write crashfile to sdcard", e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "an IOException occured when write crashfile to sdcard", e);
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
