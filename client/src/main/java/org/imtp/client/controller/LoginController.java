package org.imtp.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.Client;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.constant.SendMessageListener;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.client.handler.LoginHandler;
import org.imtp.client.model.MessageModel;
import org.imtp.client.util.ResourceUtils;
import org.imtp.common.packet.LoginRequest;
import org.imtp.common.packet.LoginResponse;
import org.imtp.common.packet.body.LoginInfo;

import java.net.URL;
import java.security.Key;

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
    private Text errorMsg;

    @FXML
    private Circle pic;

    private Client client;

    @FXML
    public void initialize(){
        URL passwordImageUrl = ResourceUtils.classPathResource("/img/tmp.jpg");
        Image passwordImageIcon = new Image(passwordImageUrl.toExternalForm());
        pic.setFill(new ImagePattern(passwordImageIcon));

        username.setTooltip(new Tooltip("请输入账号"));
        username.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                login();
            }
        });

        password.setTooltip(new Tooltip("请输入密码"));
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                login();
            }
        });

    }

    @Override
    protected void init0() {

    }

    public void login(){
        String u = username.getText();
        String p = password.getText();
        log.info("u:{} p:{}",u,p);
        if(u.isEmpty() || p.isEmpty()){
            showErrMsg("用户名或密码为空");
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
                showErrMsg("登录失败，与服务连接异常");
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
            showErrMsg("用户名或密码错误!");
            log.info("登录失败");
        }
    }

    private void showErrMsg(String msg){
        double layoutX = errorMsg.getLayoutX();
        double layoutY = errorMsg.getLayoutY();
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
        errorMsg.setLayoutX(layoutX);
        errorMsg.setLayoutY(layoutY);
    }

}
