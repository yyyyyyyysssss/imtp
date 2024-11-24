package org.imtp.desktop.component;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

/**
 * @Description
 * @Author ys
 * @Date 2024/6/21 13:31
 */
public class AutoResizeTextFiled extends TextField {

    private double margin = -1;
    private double minWidth = -1;

    public AutoResizeTextFiled() {
        super();
        this.skinProperty().addListener((o, old, skin) -> {
            if (skin != null && margin < 0) {
                TextFieldSkin textFieldSkin = (TextFieldSkin) skin;
                Platform.runLater(() -> {
                    margin = textFieldSkin.getCharacterBounds(0).getMinX();
                    minWidth = this.prefWidth(0);
                    this.setText("");
                });
            }
        });
        this.textProperty().addListener((o, oldText, text) -> {
            double width;
            if (text.length() <= this.getPrefColumnCount()) {
                width = minWidth;
            } else {
                TextFieldSkin skin = (TextFieldSkin) this.getSkin();
                double left = skin.getCharacterBounds(0).getMinX();
                double right = skin.getCharacterBounds(
                        text.offsetByCodePoints(text.length(), -1)).getMaxX();
                width = right - left + margin * 2;
            }
            Platform.runLater(() -> this.setPrefWidth(width));
        });
    }

}
