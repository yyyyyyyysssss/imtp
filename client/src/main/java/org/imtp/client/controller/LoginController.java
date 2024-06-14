package org.imtp.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.Client;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.constant.SendMessageListener;
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
        if (client == null){
            client = new Client(u,p,(LoginHandler) messageModel);
            //启动netty
            new Thread(client).start();
        }
        client.setAccount(u);
        client.setPassword(p);
        LoginInfo loginInfo = new LoginInfo(u,p);
        send(new LoginRequest(loginInfo), new SendMessageListener() {

            @Override
            public void isSuccess() {

            }
            @Override
            public void isFail() {
                errorMsg.setText("登录失败，与服务连接异常");
                errorMsg.setVisible(true);
            }
        });
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
            //将自身移除
            messageModel.removeObserver(this.getClass());
        }else{
            errorMsg.setText("用户名或密码错误!");
            errorMsg.setVisible(true);
            log.info("登录失败");
        }
    }
}
