package org.imtp.client.controller;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.entity.SessionEntity;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.base.Packet;

@Slf4j
public class ChatController extends AbstractController{

    @FXML
    private ListView<?> chatListView;

    @FXML
    private TextArea inputText;

    @FXML
    private Button sendButton;

    private SessionEntity sessionEntity;

    @Override
    protected void init0() {
        inputText.setWrapText(true);
        inputText.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()){
                case ENTER :
                    keyEvent.consume();
                    if(keyEvent.isShiftDown()){
                        inputText.appendText(System.lineSeparator());
                    }else {
                        sendMessage();
                    }
                    break;
            }
        });

        sendButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                sendMessage();
            }
        });
    }

    @Override
    public void initData(Object object) {
        this.sessionEntity = (SessionEntity) object;
    }

    private void sendMessage(){
        String text = inputText.getText();
        if (text.isEmpty() || text.trim().replaceAll("\n","").isEmpty()){
            inputText.clear();
            return;
        }
        System.out.println(text);
        inputText.clear();
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        switch (packet.getHeader().getCmd()){
            case TEXT_MESSAGE:
                TextMessage textMessage = (TextMessage) packet;
                if(textMessage.isGroup()){
                    System.out.println("*用户["+ textMessage.getSender() + "]:" + textMessage.getMessage());
                }else {
                    System.out.println("用户["+ textMessage.getSender() + "]:" + textMessage.getMessage());
                }
                break;
        }
    }

}
