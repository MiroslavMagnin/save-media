package com.vadmax.savemedia.gui;

import com.vadmax.savemedia.cmd.Cmd;
import com.vadmax.savemedia.downloadsettings.RowHistoryTable;
import com.vadmax.savemedia.downloadsettings.VideoQuality;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

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
    private TableView historyTable;
    @FXML
    public TextArea logArea;

    public void initialize() {
        // Инициализируем TextArea для класса, который будет в этот TextArea выводить строки выполнения библиотеки yt-dlp
        Cmd.logArea = logArea;

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
        Cmd dl = new Cmd(videoLink.getText(),
                VideoQuality.FormatToVideoQuality.toVideoQuality(videoFormat.getValue().toString()),
                downloadPath.getText(), "%(title)s");
        historyTable.getItems().add(new RowHistoryTable("default", downloadPath.getText()));
    }

    public void clearText() {
        videoLink.clear();
    }
}