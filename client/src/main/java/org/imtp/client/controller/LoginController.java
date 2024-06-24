package org.imtp.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.Client;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.constant.SendMessageListener;
import org.imtp.client.handler.LoginHandler;
import org.imtp.client.model.MessageModel;
import org.imtp.client.util.ResourceUtils;
import org.imtp.common.packet.LoginRequest;
import org.imtp.common.packet.LoginResponse;
import org.imtp.common.packet.body.LoginInfo;

import java.net.URL;
import java.util.List;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/6 16:46
 */
@Slf4j
public class LoginController extends AbstractController{

    @FXML
    private BorderPane loginBorderPane;

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Separator uSeparator;

    @FXML
    private Separator pSeparator;

    @FXML
    private Circle pic;

    @FXML
    private VBox loginVBox;

    @FXML
    private Button loginButton;

    @FXML
    private Text errorMsg;

    private ImageView loadingImage;

    private Client client;

    private final Tooltip errorTip = new Tooltip();

    private ObservableList<Node> defaultChildren;


    @FXML
    public void initialize(){
        defaultChildren = FXCollections.observableArrayList(loginVBox.getChildren());

        loginBorderPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                errorTip.hide();
            }
        });

        URL passwordImageUrl = ResourceUtils.classPathResource("/img/tmp.jpg");
        Image passwordImageIcon = new Image(passwordImageUrl.toExternalForm());
        pic.setFill(new ImagePattern(passwordImageIcon));

        URL loadingUrl = ResourceUtils.classPathResource("/img/loading.gif");
        Image image = new Image(loadingUrl.toExternalForm());
        loadingImage = new ImageView(image);
        loadingImage.setFitHeight(50);
        loadingImage.setFitHeight(50);

        username.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                login();
            }
        });

        username.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.isEmpty()){
                uSeparator.getStyleClass().removeAll("separator_change");
                uSeparator.getStyleClass().add("separator_default");
                errorTip.hide();
            }
        });

        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER){
                login();
            }
        });
        password.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.isEmpty()){
                pSeparator.getStyleClass().removeAll("separator_change");
                pSeparator.getStyleClass().add("separator_default");
                errorTip.hide();
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
        if (u.isEmpty()){
            username.requestFocus();
            uSeparator.getStyleClass().removeAll("separator_default");
            uSeparator.getStyleClass().add("separator_change");
            showTooltip(username,"请输入账号后再登录");
            return;
        }
        if(p.isEmpty()){
            password.requestFocus();
            pSeparator.getStyleClass().removeAll("separator_default");
            pSeparator.getStyleClass().add("separator_change");
            showTooltip(password,"请输入密码后再登录");
            return;
        }
        if (client == null){
            client = new Client(u,p,(LoginHandler) messageModel);
            //启动netty
            new Thread(client).start();
        }

        loggingIn();

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
            //触发静态代码块执行，提前加载表情包
            ChatEmojiDialog.trigger();
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
        loggingFail();
    }


    private void showTooltip(Node node,String msg){
        errorTip.setText(msg);
        Point2D point2D = node.localToScene(0.0, 0.0);
        errorTip.show(node,point2D.getX() + node.getScene().getX() + node.getScene().getWindow().getX(),
                point2D.getY() + node.getScene().getY() + node.getScene().getWindow().getY() + 10);
    }

    private void loggingIn(){
        Platform.runLater(() -> {
            ObservableList<Node> children = loginVBox.getChildren();
            children.removeAll(loginVBox.getChildren());
            children.add(loadingImage);
        });

    }

    private void loggingFail(){
        Platform.runLater(() -> {
            ObservableList<Node> children = loginVBox.getChildren();
            children.removeAll(loginVBox.getChildren());
            children.addAll(defaultChildren);
        });

    }

}
