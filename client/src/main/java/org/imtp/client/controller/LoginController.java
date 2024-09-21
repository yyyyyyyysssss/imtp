package org.imtp.client.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.ChannelHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.imtp.client.Client;
import org.imtp.client.Config;
import org.imtp.client.SceneManager;
import org.imtp.client.SceneManagerHolder;
import org.imtp.client.component.OKHttpClientHelper;
import org.imtp.client.constant.FXMLResourceConstant;
import org.imtp.client.constant.SendMessageListener;
import org.imtp.client.entity.TokenEntity;
import org.imtp.client.handler.AuthenticationHandler;
import org.imtp.client.util.EffectUtilities;
import org.imtp.client.util.ResourceUtils;
import org.imtp.common.packet.AuthenticationRequest;
import org.imtp.common.packet.AuthenticationResponse;
import org.imtp.common.response.Result;
import org.imtp.common.utils.JsonUtil;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/6 16:46
 */
@Slf4j
public class LoginController extends AbstractController {

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
    private Text errorMsg;

    @FXML
    private VBox headMinimizeVBxo;

    @FXML
    private VBox headCloseVBxo;

    private ImageView loadingImage;

    private Client client;

    private final Tooltip errorTip = new Tooltip();

    private ObservableList<Node> defaultChildren;

    private OKHttpClientHelper okHttpClientHelper;

    private Config config;

    @FXML
    public void initialize() {
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
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });

        username.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.isEmpty()) {
                uSeparator.getStyleClass().removeAll("separator_change");
                uSeparator.getStyleClass().add("separator_default");
                errorTip.hide();
            }
        });

        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                login();
            }
        });
        password.textProperty().addListener((observableValue, s, t1) -> {
            if (!t1.isEmpty()) {
                pSeparator.getStyleClass().removeAll("separator_change");
                pSeparator.getStyleClass().add("separator_default");
                errorTip.hide();
            }
        });

        SceneManager sceneManager = SceneManagerHolder.getSceneManager();
        Stage stage = sceneManager.getStage();
        EffectUtilities.makeDraggable(stage, loginBorderPane);

        headMinimizeVBxo.setOnMouseClicked(event -> {
            stage.setIconified(true);
        });
        headCloseVBxo.setOnMouseClicked(event -> {
            stage.close();
        });

        okHttpClientHelper = OKHttpClientHelper.getInstance();

        config = Config.getInstance();
    }


    @Override
    protected void init0() {

    }

    public void login() {
        String u = username.getText();
        String p = password.getText();
        log.info("u:{} p:{}", u, p);
        if (u.isEmpty()) {
            username.requestFocus();
            uSeparator.getStyleClass().removeAll("separator_default");
            uSeparator.getStyleClass().add("separator_change");
            showTooltip(username, "请输入账号后再登录");
            return;
        }
        if (p.isEmpty()) {
            password.requestFocus();
            pSeparator.getStyleClass().removeAll("separator_default");
            pSeparator.getStyleClass().add("separator_change");
            showTooltip(password, "请输入密码后再登录");
            return;
        }
        switchLogging();
        Map<String, String> loginRequest = new LinkedHashMap<>();
        loginRequest.put("username", u);
        loginRequest.put("credential", p);
        loginRequest.put("clientType", "APP");
        okHttpClientHelper.doPost(config.getApiHost() + "/login", loginRequest, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("login error: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                if (response.isSuccessful()) {
                    if (body != null) {
                        String str = body.string();
                        Result<TokenEntity> tokenResult = JsonUtil.parseObject(str, new TypeReference<Result<TokenEntity>>() {});
                        if(!tokenResult.isSucceed()){
                            showErrMsg("登录失败，用户名或密码错误");
                            return;
                        }
                        TokenEntity tokenEntity = tokenResult.getData();
                        if (client == null) {
                            String accessToken = tokenEntity.getAccessToken();
                            client = new Client((ChannelHandler) messageModel,accessToken);
                            client.addListener(() -> loggingIn(accessToken));
                            //启动netty
                            new Thread(client).start();
                        } else {

                        }

                    }
                } else {
                    if (response.code() == 401){
                        showErrMsg("登录失败，用户名或密码错误");
                        return;
                    }
                    String error = null;
                    if (body != null) {
                        error = body.string();
                    }
                    throw new RuntimeException("Request Failed Url: " + response.request().url().url().getPath() + "; response code : " + response.code() + "; error msg : " + error);
                }
            }
        });
    }

    private void loggingIn(String token) {
        send(new AuthenticationRequest(token), new SendMessageListener() {
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
        AuthenticationResponse authenticationResponse = (AuthenticationResponse) object;
        if (authenticationResponse.isAuthenticated()) {
            errorMsg.setVisible(false);
            log.info("登录成功");
            //触发静态代码块执行，提前加载表情包
            ChatEmojiDialog.trigger();
            //跳转主页
            skipScene(FXMLResourceConstant.HOME_FXML, "聊天页", ((AuthenticationHandler) messageModel).getClientCmdHandlerHandler());
            //将自身移除
            messageModel.removeObserver(this.getClass());
        } else {
            showErrMsg("用户名或密码错误!");
            log.info("登录失败");
        }
    }

    private void showErrMsg(String msg) {
        double layoutX = errorMsg.getLayoutX();
        double layoutY = errorMsg.getLayoutY();
        errorMsg.setText(msg);
        errorMsg.setVisible(true);
        errorMsg.setLayoutX(layoutX);
        errorMsg.setLayoutY(layoutY);
        loggingFail();
    }


    private void showTooltip(Node node, String msg) {
        errorTip.setText(msg);
        Point2D point2D = node.localToScene(0.0, 0.0);
        errorTip.show(node, point2D.getX() + node.getScene().getX() + node.getScene().getWindow().getX(),
                point2D.getY() + node.getScene().getY() + node.getScene().getWindow().getY() + 10);
    }

    private void switchLogging() {
        Platform.runLater(() -> {
            ObservableList<Node> children = loginVBox.getChildren();
            children.removeAll(loginVBox.getChildren());
            children.add(loadingImage);
        });
    }

    private void loggingFail() {
        Platform.runLater(() -> {
            ObservableList<Node> children = loginVBox.getChildren();
            children.removeAll(loginVBox.getChildren());
            children.addAll(defaultChildren);
        });

    }
}
