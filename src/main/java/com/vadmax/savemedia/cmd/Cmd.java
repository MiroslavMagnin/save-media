package com.vadmax.savemedia.cmd;

import com.vadmax.savemedia.data.Config;
import com.vadmax.savemedia.downloadsettings.VideoQuality;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Cmd {
    @FXML
    public static TextArea logArea;

    public Cmd(String link, VideoQuality quality, String path, String filename) {
        runCmd(Commands.saveVideo(link, quality, path, filename));
    }

    public void runCmd(ArrayList<String> cmdArr) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder(cmdArr);
                    processBuilder.redirectErrorStream(true); // объединить stdout и stderr

                    Process process = processBuilder.start();

                    StringBuilder textLog = new StringBuilder();

                    // Вывод в консоль строк из cmd в процессе работы
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Обновление процента загрузки (удаляем прошлую строку с процентом и ставим новую)
                        if (line.startsWith("[download]")) {
                            // Удаляем предыдущее вхождение [download]
                            int lastIndex = textLog.lastIndexOf("[download]");
                            if (lastIndex != -1) {
                                textLog.replace(lastIndex, textLog.length(), line);
                            } else {
                                textLog.append(line);
                            }
                        } else {
                            textLog.append(line).append("\n");
                        }

                        // Выводим textLog в TextArea
                        final String finalTextLog = textLog.toString();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                logArea.setText(finalTextLog);
                                logArea.setScrollTop(Double.MAX_VALUE);
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static class Commands {
        public static ArrayList<String> saveVideo(String link, VideoQuality quality, String path, String filename) {
            ArrayList<String> cmdArr = new ArrayList<>();
            cmdArr.add(Config.YtDlpLocation); // Путь к exe-файлу библиотеки yt-dlp

            // Video quality
            cmdArr.add("-f");
            cmdArr.add(quality.getQuality());

            // Output path and filename
            cmdArr.add("-P");
            cmdArr.add(path);
            cmdArr.add("-o");
            cmdArr.add(filename + ".%(ext)s");

            // Video url
            cmdArr.add(link);

            System.out.println(cmdArr);
            return cmdArr;
        }
    }


}
