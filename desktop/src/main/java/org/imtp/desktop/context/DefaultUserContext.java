package org.imtp.desktop.context;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

/**
 * @Description
 * @Author ys
 * @Date 2025/12/25 16:50
 */
public class DefaultUserContext implements UserContext{

    private Long userId;
    private String username;
    private String nickname;
    private String avatarUrl;  // 存储头像的 URL
    private ObjectProperty<Image> avatarImage;

    public DefaultUserContext(Long userId, String username, String nickname, String avatarUrl) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.avatarImage = new SimpleObjectProperty<>();
        setAvatarImage(this.avatarUrl);
    }

    @Override
    public Long userId() {
        return userId;
    }

    @Override
    public String username() {
        return username;
    }

    @Override
    public String nickname() {
        return nickname;
    }

    @Override
    public String avatarUrl() {
        return avatarUrl;
    }

    @Override
    public void setAvatarUrl(String avatarUrl) {
        if (!avatarUrl.equals(this.avatarUrl)) {
            this.avatarUrl = avatarUrl;
            setAvatarImage(avatarUrl);  // 更新头像
        }
    }

    @Override
    public ObjectProperty<Image> avatarImageProperty() {

        return avatarImage;
    }

    public void setAvatarImage(String url) {
        if(avatarImage.get() == null || !url.equals(avatarImage.get().getUrl())){
            this.avatarImage.set(new Image(url,true));
        }
    }
}
