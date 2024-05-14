package org.imtp.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import lombok.extern.slf4j.Slf4j;
import org.imtp.client.entity.SessionEntity;

/**
 * @Description
 * @Author ys
 * @Date 2024/5/14 16:27
 */
@Slf4j
public class HomeController extends AbstractController{

    @FXML
    private ListView<SessionEntity> homeListView;

    @Override
    protected void init0() {

    }

    @Override
    public void update(Object object) {

    }
}
