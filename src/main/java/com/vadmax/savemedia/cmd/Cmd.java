package com.vadmax.savemedia.cmd;

import com.vadmax.savemedia.data.Config;
import com.vadmax.savemedia.downloadsettings.VideoQuality;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

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

    public static class Builder {
        private Cmd newCmd;

        public Builder(String link) {
            newCmd = new Cmd();
            newCmd.cmdArr.add(Config.YtDlpLocation);
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

        public Builder fileName() {
            newCmd.getFileName();
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

    public void getFileName() {
        Thread getFileNameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] fileName = new String[1];
                try {
                    // Кириллица не работает в процессе YtDlpLocation почему-то
                    ArrayList<String> cmdArr = new ArrayList<>();
                    cmdArr.add(Config.YtDlpLocation);
                    cmdArr.add("--print");
                    cmdArr.add("filename");
                    cmdArr.add(link);
                    ProcessBuilder processBuilder = new ProcessBuilder(cmdArr);
                    processBuilder.redirectErrorStream(true); // объединить stdout и stderr

                    Process process = processBuilder.start();

                    // Вывод в консоль строк из cmd в процессе работы
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                        if (!line.contains("WARNING") && !line.contains("ERROR")) {
                            fileName[0] = line.substring(0, line.lastIndexOf("."));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (fileName[0] != null) {
                    Config.fileName = fileName[0];
                }
                else {
                    throw new RuntimeException();
                }
            }
        });

        getFileNameThread.start();
        try {
            getFileNameThread.join();
        } catch(InterruptedException e) {
            System.out.println("Тут должна быть обработка неправильно введеной ссылки или неподдерживаемого сайта");
        }
    }

}
