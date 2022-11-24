package com.zeros.devtool.controller.index;

import com.zeros.devtool.constants.CssConstants;
import com.zeros.devtool.service.*;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.CssLoadUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import com.zeros.devtool.view.IndexView;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;



import java.net.URL;
import java.util.ResourceBundle;

public class IndexController extends IndexView {


    private final SwitchHostService switchHostService = new SwitchHostService();

    private final JsonFormatService jsonFormatService = new JsonFormatService();

    private final PortCheckService portCheckService = new PortCheckService();


    private final SQLFormatService sqlFormatService = new SQLFormatService();

    private final XMLFormatService xmlFormatService = new XMLFormatService();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
//        switchHostService.loadMenu(rootTree);
        loadMenu();
    }

    private void loadMenu(){
        TreeItem<Node> all = ViewUtil.getRootTreeItem();
        //网络
        TreeItem<Node> networkTreeItem = getNetworkTreeItem();
        all.getChildren().add(networkTreeItem);

        //格式化
        TreeItem<Node> formatTreeItem = getFormatTreeItem();
        all.getChildren().add(formatTreeItem);

        //系统
        TreeItem<Node> systemTreeItem = getSystemTreeItem();

        all.getChildren().add(systemTreeItem);
        all.setExpanded(true);
        rootTree.setRoot(all);
    }

    private TreeItem<Node> getNetworkTreeItem(){
        TreeItem<Node> network = ViewUtil.getNetworkTreeItem();
        TreeItem<Node> hostTreeItem = switchHostService.getHostRootTreeItem();
        network.getChildren().add(hostTreeItem);
        return network;
    }

    private TreeItem<Node> getFormatTreeItem(){
        TreeItem<Node> format = ViewUtil.getFormatTreeItem();
        TreeItem<Node> jsonFormatTreeItem = jsonFormatService.getJsonFormatTreeItem();
        TreeItem<Node> sqlFormatTreeItem = sqlFormatService.getSqlFormatTreeItem();
        TreeItem<Node> xmlFormatTreeItem = xmlFormatService.getSqlFormatTreeItem();
        format.getChildren().add(jsonFormatTreeItem);
        format.getChildren().add(sqlFormatTreeItem);
        format.getChildren().add(xmlFormatTreeItem);
        return format;
    }

    private TreeItem<Node> getSystemTreeItem(){
        TreeItem<Node> system = ViewUtil.getSystemTreeItem();
        TreeItem<Node> portTreeItem = portCheckService.getPortTreeItem();
        system.getChildren().add(portTreeItem);
        return system;
    }


    private void initEvent() {

       rootTree.getStylesheets().add(CssLoadUtil.getResourceUrl(CssConstants.ROOT_TREE_CSS));

        rootTree.setOnMouseClicked(event -> {
            TreeItem<Node> item = rootTree.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }
        });

    }

    private void initService() {
        ControllerMangerUtil.setController(IndexController.this.getClass().getName(), this);
    }

}
