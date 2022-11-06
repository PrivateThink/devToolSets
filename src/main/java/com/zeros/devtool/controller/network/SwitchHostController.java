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
        //tabPane 添加菜单
        switchHostService.handleTabPaneEvent(tabPaneMain);
    }

    private void initService() {
        ControllerMangerUtil.setController(SwitchHostController.this.getClass().getName(), this);
    }

}
