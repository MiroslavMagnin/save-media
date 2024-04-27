package com.vadmax.savemedia.test;

import com.vadmax.savemedia.cmd.Cmd;
import com.vadmax.savemedia.data.Config;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import org.apache.commons.io.IOUtils;

public class TestCmd{
    private ArrayList<String> cmdArr = new ArrayList<>();

    public TestCmd() {
        cmdArr.add("cmd.exe");
//        cmdArr.add("chcp");
//        cmdArr.add("65001");
//        cmdArr.add("&&");
//        cmdArr.add("/" + Cmd.getDisk(Config.YtDlpLocation));
//        cmdArr.add("dir");
//        cmdArr.add(Config.YtDlpLocation);
//        cmdArr.add("&&");
//        cmdArr.add("yt-dlp");
//        cmdArr.add("yt-dlp");
    }

    public void executeCommand(ArrayList<String> commands) {
        try {
            cmdArr.addAll(commands);
            System.out.println(cmdArr);

            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/" + Config.homeDrive, "chcp", "65001", "&&", "yt-dlp", "--print", "filename", "https://www.youtube.com/watch?v=1rzwbjRxfkQ");
//            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            logOutput(process.getInputStream(), "");
            logOutput(process.getErrorStream(), "Error: ");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logOutput(InputStream inputStream, String prefix) {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(prefix + line);
                }

            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }).start();
    }
    private static SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss:SSS");
    private synchronized void log(String message) {
        System.out.println(format.format(new Date()) + ": " + message);
    }
}
