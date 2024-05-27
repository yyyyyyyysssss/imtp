package org.imtp.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.Client;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.handler.LoginHandler;
import org.imtp.client.model.MessageModel;
import org.imtp.common.packet.LoginRequest;
import org.imtp.common.packet.LoginResponse;
import org.imtp.common.packet.body.LoginInfo;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/6 16:46
 */
@Slf4j
public class LoginController extends AbstractController{

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private TextField errorMsg;

    private Client client;

    @Override
    protected void init0() {
        client = new Client((LoginHandler) messageModel);
        //启动netty
        new Thread(client).start();
    }

    public void login(ActionEvent actionEvent){
        String u = username.getText();
        String p = password.getText();
        log.info("u:{} p:{}",u,p);
        if(u.isEmpty() || p.isEmpty()){
            errorMsg.setText("用户名或密码为空");
            errorMsg.setVisible(true);
            return;
        }
        LoginInfo loginInfo = new LoginInfo(u,p);
        send(new LoginRequest(loginInfo));
    }

    @Override
    public void update(Object object) {
        LoginResponse loginResponse = (LoginResponse) object;
        if(loginResponse.loginSuccess()){
            errorMsg.setVisible(false);
            log.info("登录成功");
            MessageModel nextModel = messageModel.getNextModel();
            //跳转主页
            skipScene(FXMLResourceConstant.HOME_FXML,"聊天页",nextModel);
        }else{
            errorMsg.setText("用户名或密码错误!");
            errorMsg.setVisible(true);
            log.info("登录失败");
        }
    }
}
