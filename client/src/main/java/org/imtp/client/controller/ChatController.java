package org.imtp.client.controller;


import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.entity.SessionEntity;
import org.imtp.common.enums.DeliveryMethod;
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
                    int caretPosition = inputText.getCaretPosition();
                    keyEvent.consume();
                    if(keyEvent.isShiftDown()){
                        inputText.appendText(System.lineSeparator());
                    }else {
                        //去除末尾的换行符
                        String text = inputText.getText();
                        text =  text.substring(0,caretPosition-1);
                        inputText.setText(text);
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
        if (text.isEmpty()){
            inputText.clear();
            return;
        }
        Packet packet;
        if (sessionEntity.getDeliveryMethod().equals(DeliveryMethod.SINGLE)){
            packet = new TextMessage(text, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId());
        }else {
            packet = new TextMessage(text, ClientContextHolder.clientContext().id(), sessionEntity.getReceiverUserId(),true);
        }
        send(packet);
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
