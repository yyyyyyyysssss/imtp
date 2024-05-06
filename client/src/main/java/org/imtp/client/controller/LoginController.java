package org.imtp.client.controller;

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
public class LoginController implements Observer{

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    private Client client;

    private MessageModel messageModel;

    public void login(ActionEvent actionEvent){
        String u = username.getText();
        String p = password.getText();
        log.info("u:{} p:{}",u,p);
        if(client == null){
            messageModel = new LoginHandler();
            client = new Client(u,p,(LoginHandler) messageModel);
            messageModel.registerObserver(this);
            new Thread(client).start();
        }else {
            new Thread(client).start();
        }
    }

    @Override
    public void update() {
        Packet packet = messageModel.getMessage();
        if(packet instanceof LoginResponse loginResponse){
            if(loginResponse.getLoginState().equals(LoginState.SUCCESS)){
                log.info("登录成功");
            }else {
                log.info("登录失败");
            }
        }else {
            throw new UnknownError("未知错误");
        }
    }
}
