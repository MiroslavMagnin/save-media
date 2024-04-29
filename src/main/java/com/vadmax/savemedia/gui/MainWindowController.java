package com.vadmax.savemedia.gui;

import com.vadmax.savemedia.cmd.Cmd;
import com.vadmax.savemedia.data.Config;
import com.vadmax.savemedia.downloadsettings.RowHistoryTable;
import com.vadmax.savemedia.downloadsettings.VideoQuality;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainWindowController {
    @FXML
    private VBox mainVBox;
    @FXML
    public TextField videoLink;
    @FXML
    public ComboBox videoFormat;
    @FXML
    private TextField downloadPath;
    @FXML
    public ProgressBar downloadProgress;
    @FXML
    private TableView historyTable;
    @FXML
    public ListView logList;


    public void initialize() {
        // Инициализируем TextArea для класса, который будет в этот TextArea выводить строки выполнения библиотеки yt-dlp
        Cmd.logList = logList;
        logList.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setWrapText(true);
                }
            }
        });

        Cmd.downloadProgress = downloadProgress;
        downloadProgress.setProgress(0.0);


        // Создаем таблицу с историей скачанных видео
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn videoNameColumn = new TableColumn("Video name");
        videoNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn locationColumn = new TableColumn("Location");
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("path"));

        TableColumn actionColumn = new TableColumn<>("Action");
        Callback<TableColumn<RowHistoryTable, String>, TableCell<RowHistoryTable, String>> cellFactory =
                new Callback<TableColumn<RowHistoryTable, String>, TableCell<RowHistoryTable, String>>() {
                    @Override
                    public TableCell call(final TableColumn<RowHistoryTable, String> param) {
                        final TableCell<RowHistoryTable, String> cell = new TableCell<RowHistoryTable, String>() {
                            final Button btn = new Button("Open in Explorer");

                            {
                                btn.setOnAction((ActionEvent event) -> {
                                    RowHistoryTable rht = getTableView().getItems().get(getIndex());
                                    Desktop desktop = Desktop.getDesktop();
                                    File file = new File(rht.getPath());
                                    try {
                                        desktop.open(file);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                });
                            }

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        actionColumn.setCellFactory(cellFactory);
        historyTable.getColumns().addAll(videoNameColumn, locationColumn, actionColumn);
    }

    @FXML
    public void browser() {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) mainVBox.getScene().getWindow();
        File file = directoryChooser.showDialog(stage);

        if (file != null) {
            downloadPath.setText(file.getAbsolutePath());
        }
    }

    @FXML
    public void download() {
        new Thread(() -> {
            ArrayList<String> arr = new ArrayList<>(Arrays.asList("--print", "title", "--windows-filenames")); // Команды с получением всех данных о видео
            Cmd gd = new Cmd.Builder(videoLink.getText())
                    .anyCommand(arr)
                    .build();
            ArrayList<String> videoTitle = gd.runCmdWithOutput();
            Config.fileName = videoTitle.get(1);
            System.out.println(videoTitle);

            Cmd dl = new Cmd.Builder(videoLink.getText())
                    .quality(VideoQuality.FormatToVideoQuality.toVideoQuality(videoFormat.getValue().toString()))
                    .path(downloadPath.getText())
                    .build();
            dl.runCmd();

            Platform.runLater(() -> {
                historyTable.getItems().add(new RowHistoryTable(Config.fileName, downloadPath.getText()));
            });
        }).start();
    }

    public void clearText() {
        videoLink.clear();
    }
}