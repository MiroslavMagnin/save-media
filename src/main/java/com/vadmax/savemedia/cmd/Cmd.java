package com.vadmax.savemedia.cmd;

import com.vadmax.savemedia.data.Config;
import com.vadmax.savemedia.downloadsettings.VideoQuality;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.locks.Condition;

public class Cmd {
    /**
     * Список со всеми аргументами для команды CMD
     * Каждое новое слово в команде - новый элемент списка
     * (то есть каждый элемент списка должен быть без пробелов)
     */
    private ArrayList<String> cmdArr = new ArrayList<>();
    /**
     * Ссылка на видео из TextField fx:id="videoLink"
     * С помощью этой переменной получаем имя файла по умолчанию
     */
    private String link;
    @FXML
    public static TextArea logArea;

    private Cmd() {}

    public static class Builder {
        private Cmd newCmd;

        public Builder(String link) {
            newCmd = new Cmd();
            newCmd.cmdArr.add("cmd");
            newCmd.cmdArr.add("/" + Config.homeDrive);
            newCmd.cmdArr.add("chcp");
            newCmd.cmdArr.add("65001");
            newCmd.cmdArr.add("&&");
            newCmd.cmdArr.add("yt-dlp");
            newCmd.link = link;
        }

        public Builder quality(VideoQuality quality) {
            newCmd.cmdArr.add("-f");
            newCmd.cmdArr.add(quality.getQuality());
            return this;
        }

        public Builder path(String path) {
            newCmd.cmdArr.add("-P");
            newCmd.cmdArr.add(path);
            return this;
        }

        public Builder fileName(String fileName) {
            newCmd.cmdArr.add("-o");
            newCmd.cmdArr.add(fileName + ".%(ext)s");
            Config.fileName = fileName;
            return this;
        }

        public Builder anyCommand(String commandName, String commandValue) {
            newCmd.cmdArr.add(commandName);
            newCmd.cmdArr.add(commandValue);
            return this;
        }
        public Builder anyCommand(ArrayList<String> strings) {
            newCmd.cmdArr.addAll(strings);
            return this;
        }

        public Cmd build(){
            newCmd.cmdArr.add(newCmd.link); // в конце должна быть ссылка
            return newCmd;
        }
    }

    public void runCmd() {
        new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(cmdArr);
                System.out.println(cmdArr);
                processBuilder.redirectErrorStream(true); // объединить stdout и stderr
                Process process = processBuilder.start();

                logOutput(process.getInputStream(), "");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void logOutput(InputStream inputStream, String prefix) {
        new Thread(() -> {
            try {
                StringBuilder textLog = new StringBuilder(); // Весь текст, который будет отображатся в TextArea
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(prefix + line);

                    if (line.contains("Destination")) {
                        Config.fileName = line.substring(line.indexOf("\\"), line.lastIndexOf("."));
                    }

                    // Обновление процента загрузки (удаляем прошлую строку с процентом и ставим новую)
                    if (line.startsWith("[download]") && line.contains("% of")) {
                        // Удаляем предыдущее вхождение [download]
                        int lastDownloadLineIndex = textLog.lastIndexOf("[download]");

                        /**
                         * Исправить ошибку с тем, что строка
                         * [download] Destination: M:\Вариант Яндекс Учебника №6  КЕГЭ по информатике 2024 [Xm9j52mejOA].mp4
                         * не выводится в textLog
                         */

                        if (lastDownloadLineIndex != -1) {
                            textLog.delete(lastDownloadLineIndex, textLog.length());
                        }
                        textLog.append(line);
                    } else {
                        textLog.append(line).append("\n");
                    }

                    // Выводим textLog в TextArea
                    Platform.runLater(() -> {
                        logArea.setText(textLog.toString());
                        logArea.setScrollTop(Double.MAX_VALUE); // Автопрокрутка вниз, её надо реализовать получше
                    });
                }

            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }).start();
    }

    private ArrayList<String> getLogOutput(InputStream inputStream) {
        ArrayList<String> output = new ArrayList<>();
        Thread getLogOutputThread =  new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "windows-1251"));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                    System.out.println(line);
                }
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        });
        getLogOutputThread.start();

        try {
            getLogOutputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return output;
    }

    public ArrayList<String> runCmdWithOutput() {
        final ArrayList<String>[] output = new ArrayList[]{new ArrayList<>()};
        Thread runCmdWithOutputThread = new Thread(() -> {
            try {
                ProcessBuilder processBuilder = new ProcessBuilder(cmdArr);
                System.out.println(cmdArr);
                Process process = processBuilder.start();

                output[0] = getLogOutput(process.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        runCmdWithOutputThread.start();

        try {
            runCmdWithOutputThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return output[0];
    }
}
