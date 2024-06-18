package org.imtp.client.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.entity.EmoteEntity;
import org.imtp.client.util.FXMLLoadUtils;

import java.io.IOException;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/12 10:44
 */
public class ChatEmoteDialog extends Dialog<String> {


    @FXML
    private TableView<EmoteEntity> chatEmoteTable;
    @FXML
    private TableColumn<EmoteEntity, String> e0;
    @FXML
    private TableColumn<EmoteEntity, String> e1;
    @FXML
    private TableColumn<EmoteEntity, String> e2;
    @FXML
    private TableColumn<EmoteEntity, String> e3;
    @FXML
    private TableColumn<EmoteEntity, String> e4;

    @FXML
    private void initialize(){
        chatEmoteTable.setFocusTraversable(false);
        chatEmoteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        chatEmoteTable.getSelectionModel().setCellSelectionEnabled(true);
        chatEmoteTable.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 1) {
                @SuppressWarnings("rawtypes")
                TablePosition pos = chatEmoteTable.getSelectionModel().getSelectedCells().getFirst();
                int row = pos.getRow();
                @SuppressWarnings("rawtypes")
                TableColumn column = pos.getTableColumn();
                String val = column.getCellData(row).toString();
                System.out.println("Selected Value, " + val);
            }
        });
        //设置单元格工厂
        e0.setCellValueFactory(cellData -> cellData.getValue().getEmoteAt(0));
        e1.setCellValueFactory(cellData -> cellData.getValue().getEmoteAt(1));
        e2.setCellValueFactory(cellData -> cellData.getValue().getEmoteAt(2));
        e3.setCellValueFactory(cellData -> cellData.getValue().getEmoteAt(3));
        e4.setCellValueFactory(cellData -> cellData.getValue().getEmoteAt(4));
    }

    public ChatEmoteDialog() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(FXMLLoadUtils.loadUrlByFxmlPath(FXMLResourceConstant.CHAT_EMOTE_DIALOG_FML));
        fxmlLoader.setController(this);
        try {
            DialogPane dialogPane = fxmlLoader.load();
            dialogPane.getButtonTypes().add(ButtonType.CLOSE);
            Node closeButton = dialogPane.lookupButton(ButtonType.CLOSE);
            closeButton.managedProperty().bind(closeButton.visibleProperty());
            closeButton.setVisible(false);
            setDialogPane(dialogPane);
            this.initModality(Modality.NONE);
            initEmoteData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initEmoteData() {
        ObservableList<EmoteEntity> data = FXCollections.observableArrayList();
        String[][] emojis = {
                {"\uD83D\uDE00", "\uD83D\uDE01", "\uD83D\uDE02", "\uD83E\uDD23", "\uD83D\uDE03"},
                {"\uD83D\uDE04", "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE09", "\uD83D\uDE0A"},
                {"\uD83D\uDE0B", "\uD83D\uDE0C", "\uD83D\uDE0D", "\uD83D\uDE0E", "\uD83D\uDE0F"},
                {"\uD83D\uDE10", "\uD83D\uDE11", "\uD83D\uDE12", "\uD83D\uDE13", "\uD83D\uDE14"},
                {"\uD83D\uDE15", "\uD83D\uDE16", "\uD83D\uDE17", "\uD83D\uDE18", "\uD83D\uDE19"},
                {"\uD83D\uDE1A", "\uD83D\uDE1B", "\uD83D\uDE1C", "\uD83D\uDE1D", "\uD83D\uDE1E"},
                {"\uD83D\uDE1F", "\uD83D\uDE20", "\uD83D\uDE21", "\uD83D\uDE22", "\uD83D\uDE23"},
                {"\uD83D\uDE24", "\uD83D\uDE25", "\uD83D\uDE26", "\uD83D\uDE27", "\uD83D\uDE28"},
                {"\uD83D\uDE29", "\uD83D\uDE2A", "\uD83D\uDE2B", "\uD83D\uDE2C", "\uD83D\uDE2D"},
                {"\uD83D\uDE2E", "\uD83D\uDE2F", "\uD83D\uDE30", "\uD83D\uDE31", "\uD83D\uDE32"}
        };
        for (int i = 0; i < emojis.length; i++) {
            String[] e = emojis[i];
            EmoteEntity emoteEntity = new EmoteEntity(e);
            data.add(emoteEntity);
        }
        chatEmoteTable.setItems(data);
    }

}
