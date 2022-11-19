package com.zeros.devtool.controller.index;

import com.zeros.devtool.constants.CssConstants;
import com.zeros.devtool.service.JsonFormatService;
import com.zeros.devtool.service.SwitchHostService;
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
        TreeItem<Node> network = ViewUtil.getNetworkTreeItem();
        TreeItem<Node> hostTreeItem = switchHostService.getHostRootTreeItem();
        network.getChildren().add(hostTreeItem);
        all.getChildren().add(network);
        TreeItem<Node> format = ViewUtil.getFormatTreeItem();
        TreeItem<Node> jsonFormatTreeItem = jsonFormatService.getJsonFormatTreeItem();
        format.getChildren().add(jsonFormatTreeItem);
        all.getChildren().add(format);
        all.setExpanded(true);
        rootTree.setRoot(all);
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
