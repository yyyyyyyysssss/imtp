package org.imtp.desktop.context;

import javafx.beans.property.ObjectProperty;
import javafx.scene.image.Image;

public interface UserContext {

    Long userId();

    String username();

    String nickname();

    String avatarUrl();

    void setAvatarUrl(String avatarUrl);

    ObjectProperty<Image> avatarImageProperty();

}
