package com.zeros.devtool.controller.system;

import com.zeros.devtool.service.PortCheckService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.view.system.PortCheckView;


import java.net.URL;
import java.util.ResourceBundle;

public class PortCheckController extends PortCheckView {


    private static final PortCheckService portService = new PortCheckService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initService();
        initView();
        initEvent();
    }

    private void initView() {

        //初始化端口列表
        portService.setTableView();

        //设置查询
        portService.setSearchEvent();
    }

    private void initService() {
        ControllerMangerUtil.setController(PortCheckController.this.getClass().getName(), this);
    }

    private void initEvent() {

    }

}
