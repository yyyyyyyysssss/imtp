package org.imtp.client.controller;

import com.gluonhq.emoji.Emoji;
import com.gluonhq.emoji.EmojiData;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Modality;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.util.FXMLLoadUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/12 10:44
 */
public class ChatEmojiDialog extends Dialog<String> {

    @FXML
    private DialogPane emojiDialogPane;
    @FXML
    private TableView<List<Emoji>> chatEmoteTable;

    private int col;

    private static final Collection<Emoji> EMOJIS_ALL;

    static {
        Map<String,Integer> orderMap = new HashMap<>();
        orderMap.put("Smileys & Emotion",0);
        orderMap.put("People & Body",1);
        orderMap.put("Animals & Nature",2);
        orderMap.put("Symbols",3);
        orderMap.put("Objects",4);
        orderMap.put("Travel & Places",5);
        orderMap.put("Flags",6);
        orderMap.put("Component",7);
        orderMap.put("Food & Drink",8);
        orderMap.put("Activities",9);
        Collection<Emoji> emojiCollection = EmojiData.getEmojiCollection();
        EMOJIS_ALL = emojiCollection.stream().sorted(Comparator.comparingInt(o -> orderMap.get(o.getCategory()))).toList();
    }

    @FXML
    private void initialize(){
        chatEmoteTable.setFocusTraversable(false);
        chatEmoteTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public ChatEmojiDialog() {
        this(10);
    }

    public ChatEmojiDialog(int col) {
        this.col = col;
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
        for (int i = 0; i < col; i++) {
            TableColumn<List<Emoji>, Emoji> tableColumn= new TableColumn<>();
            tableColumn.setCellFactory(cellFactory -> new ChatEmojiTableCell(emojiDialogPane));
            final int ii = i;
            tableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(ii)));
            chatEmoteTable.getColumns().add(tableColumn);
        }

        ObservableList<List<Emoji>> data = FXCollections.observableArrayList();
        List<Emoji> list = new ArrayList<>(EMOJIS_ALL);
        List<List<Emoji>> lists = splitList(list, col);
        data.addAll(lists);
        chatEmoteTable.setItems(data);
    }

    //分隔列表，不够则填充null
    public <T> List<List<T>> splitList(List<T> list, int size) {
        return IntStream.range(0, (list.size() + size - 1) / size)
                .mapToObj(i -> list.subList(i * size, Math.min(size * (i + 1), list.size())))
                .map(subList -> {
                    List<T> paddedList = new ArrayList<>(subList);
                    while (paddedList.size() < size) {
                        paddedList.add(null);
                    }
                    return paddedList;
                })
                .collect(Collectors.toList());
    }


    public void showEmojiPane(Node node){
        if (this.isShowing()){
            this.close();
        }
        Point2D point2D = node.localToScene(0.0, 0.0);
        double x = point2D.getX()  + node.getScene().getX() + node.getScene().getWindow().getX() - 200;
        double y = point2D.getY() + node.getScene().getY() + node.getScene().getWindow().getY() - 490;
        this.setX(x);
        this.setY(y);
        this.show();
    }

}
