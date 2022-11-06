package com.zeros.devtool.controller.network;

import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.service.SwitchHostService;
import com.zeros.devtool.utils.ControllerMangerUtil;

import com.zeros.devtool.view.network.SwitchHostView;
import javafx.scene.control.Tab;

import java.net.URL;

import java.util.*;


public class SwitchHostController extends SwitchHostView {

    private final SwitchHostService switchHostService = new SwitchHostService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initService();
        initEvent();
    }

    private void initView() {
    }


    private void initEvent() {
        Tab tab = new Tab(Constants.ADD_HOST);
        tab.setClosable(false);
        tab.setStyle("-fx-font-size: 16pt;");
        tabPaneMain.getTabs().add(tab);
        tabPaneMain.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if(newTab == tab) {
                switchHostService.handleAddMenuItemEvent(tabPaneMain);
            }

            switchHostService.setMenuItemVisible(tabPaneMain);
        });

        switchHostService.handleTabPaneEvent(tabPaneMain);
    }

    private void initService() {
        ControllerMangerUtil.setController(SwitchHostController.this.getClass().getName(), this);
    }

}
