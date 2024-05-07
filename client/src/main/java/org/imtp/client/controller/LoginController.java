package org.imtp.client.controller;

import io.netty.channel.ChannelInboundHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.Client;
import org.imtp.client.handler.LoginHandler;
import org.imtp.client.model.MessageModel;
import org.imtp.client.model.Observer;
import org.imtp.common.enums.LoginState;
import org.imtp.common.packet.LoginResponse;
import org.imtp.common.packet.base.Packet;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/6 16:46
 */
@Slf4j
public class LoginController implements Observer,Controller{

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField errorMsg;

    private Client client;

    private MessageModel messageModel;

    private ChannelInboundHandler channelInboundHandler;

    public void login(ActionEvent actionEvent){
        String u = username.getText();
        String p = password.getText();
        log.info("u:{} p:{}",u,p);
        if(u.isEmpty() || p.isEmpty()){
            errorMsg.setText("用户名或密码为空");
            errorMsg.setVisible(true);
            return;
        }
        if(client == null){
            messageModel = new LoginHandler();
            client = new Client(u,p,(LoginHandler) messageModel);
            messageModel.registerObserver(this);
            new Thread(client).start();
        }else {
            client.setAccount(u);
            client.setPassword(p);
            new Thread(client).start();
        }
    }

    @Override
    public void update(Object object) {
        if(object instanceof LoginResponse){
            errorMsg.setText("用户名或密码错误!");
            errorMsg.setVisible(true);
            log.info("登录失败");
        }else if(object instanceof ChannelInboundHandler ch){
            errorMsg.setVisible(false);
            log.info("登录成功");
            this.channelInboundHandler = ch;
            //跳转聊天页面
        }else {
            throw new UnknownError("未知错误");
        }
    }

    @Override
    public void send(Packet packet) {
        messageModel.sendMessage(packet);
    }
}
