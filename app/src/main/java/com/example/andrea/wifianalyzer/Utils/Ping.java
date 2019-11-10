package com.example.andrea.wifianalyzer.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class Ping {

    private static final String TAG = "Network.java";

    public static String pingError = null;

    public static int pingHost(String host) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ping -c 1 " + host);
        proc.waitFor();
        int exit = proc.exitValue();
        return exit;
    }

    public static String ping(String host) throws IOException, InterruptedException {
        StringBuffer echo = new StringBuffer();
        Runtime runtime = Runtime.getRuntime();
        Process proc = runtime.exec("ping -c 1 " + host);
        proc.waitFor();
        int exit = proc.exitValue();
        if (exit == 0) {
            InputStreamReader reader = new InputStreamReader(proc.getInputStream());
            BufferedReader buffer = new BufferedReader(reader);
            String line = "";
            while ((line = buffer.readLine()) != null) {
                echo.append(line + "\n");
            }
            String s = getPingStats(echo.toString());
            Double d = Double.parseDouble(s);
            DecimalFormat df = new DecimalFormat("##");
            return Integer.valueOf(df.format(d)) + "";
        } else if (exit == 1) {
            pingError = "failed, exit = 1";
            return null;
        } else {
            pingError = "error, exit = 2";
            return null;
        }
    }

    public static String getPingStats(String s) {
        if (s.contains("0% packet loss")) {
            int start = s.indexOf("/mdev = ");
            int end = s.indexOf(" ms\n", start);
            s = s.substring(start + 8, end);
            String stats[] = s.split("/");
            return stats[2];
        } else if (s.contains("100% packet loss")) {
            pingError = "100% packet loss";
            return null;
        } else if (s.contains("% packet loss")) {
            pingError = "partial packet loss";
            return null;
        } else if (s.contains("unknown host")) {
            pingError = "unknown host";
            return null;
        } else {
            pingError = "unknown error in getPingStats";
            return null;
        }
    }
}