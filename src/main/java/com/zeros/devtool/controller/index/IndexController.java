package com.zeros.devtool.controller.index;

import com.zeros.devtool.constants.CssConstants;
import com.zeros.devtool.service.SwitchHostService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.CssLoadUtil;
import com.zeros.devtool.view.IndexView;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;



import java.net.URL;
import java.util.ResourceBundle;

public class IndexController extends IndexView {


    private final SwitchHostService switchHostService = new SwitchHostService();



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        switchHostService.loadMenu(rootTree);
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
