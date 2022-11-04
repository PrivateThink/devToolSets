package com.zeros.devtool.controller.index;

import com.zeros.devtool.service.MenuService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.view.IndexView;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;



import java.net.URL;
import java.util.ResourceBundle;

public class IndexController extends IndexView {


    public MenuService getMenuService() {
        return menuService;
    }

    private final MenuService menuService = new MenuService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        loadMenu();

    }


    private void initEvent() {
        rootTree.setOnMouseClicked(event -> {
            TreeItem<Label> item = rootTree.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }


        });

    }

    private void initService() {
        ControllerMangerUtil.setController(IndexController.this.getClass().getName(), this);
    }


    private void loadMenu() {
        TreeItem<Label> network = menuService.getNetWorkRootTreeItem();
        TreeItem<Label> text = menuService.getTextRootTreeItem();
        TreeItem<Label> all = menuService.getRootTreeItem();
        all.getChildren().add(network);
        all.getChildren().add(text);
        all.setExpanded(true);
        rootTree.setRoot(all);
    }




}
