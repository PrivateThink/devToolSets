package com.zeros.devtool.controller.network;


import com.zeros.devtool.service.SwitchHostService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import com.zeros.devtool.view.network.SwitchHostView;
import java.net.URL;
import java.util.*;


public class SwitchHostController extends SwitchHostView {

    private final SwitchHostService switchHostService = new SwitchHostService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initService();
        initView();
        initEvent();
    }

    private void initView() {
    }


    private void initEvent() {
        //tabPane 添加菜单
        switchHostService.handleTabPaneEvent(tabPaneMain);

        tabPaneMain.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            ViewUtil.setMenuItemVisible(tabPaneMain,0);
        });

    }

    private void initService() {
        ControllerMangerUtil.setController(SwitchHostController.this.getClass().getName(), this);
    }

}
