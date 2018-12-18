package com.jyj.mylinkmusickeysend;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ShellCmd {

    private static final String INPUT_KEY_EVENT = "input keyevent ";

    private static ShellCmd cmd;

    public static ShellCmd getInstance() {
        if (cmd == null) {
            cmd = new ShellCmd();
        }
        return cmd;
    }

    public void sendInputKeyEvent(final int keyCode) {
        Log.d(ShellCmd.class.getSimpleName(), "Start exec input event " + keyCode);
        new Thread(new Runnable() {
            @Override
            public void run() {
                OutputStream out = null;
                InputStream in = null;
                try {
//                    Process child = Runtime.getRuntime().exec(INPUT_KEY_EVENT + keyCode);
//                    out = child.getOutputStream();
//                    in = child.getInputStream();
//                    InputStreamReader reader = new InputStreamReader(in);
//                    BufferedReader bufferedReader = new BufferedReader(reader);
//                    int numRead;
//                    char[] buffer = new char[5000];
//                    StringBuffer commandOutput = new StringBuffer();
//                    while ((numRead = bufferedReader.read(buffer)) > 0) {
//                        commandOutput.append(buffer, 0, numRead);
//                    }

                    Process child = Runtime.getRuntime().exec("sh");
                    Log.d("ShellCmd", "Process created.");
                    out = child.getOutputStream();
                    out.write((INPUT_KEY_EVENT + keyCode).getBytes());
                    out.write("\n".getBytes());
                    out.flush();
                    in = child.getInputStream();
                    InputStreamReader reader = new InputStreamReader(in);
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    int numRead;
                    char[] buffer = new char[5000];
                    StringBuffer commandOutput = new StringBuffer();
                    while ((numRead = bufferedReader.read(buffer)) > 0) {
                        commandOutput.append(buffer, 0, numRead);
                    }

                    child.waitFor();
                    Log.d(ShellCmd.class.getSimpleName(), "Command Result" + commandOutput.toString());
                } catch (IOException e) {
                    Log.e(ShellCmd.class.getSimpleName(), e.getMessage());
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("ShellCmd", e.getMessage());
                }
                finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (out != null) {
                        try {
                            out.flush();
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();

    }

}
