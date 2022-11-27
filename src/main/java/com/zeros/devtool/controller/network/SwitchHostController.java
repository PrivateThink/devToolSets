package com.zeros.devtool.controller.network;

import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import com.zeros.devtool.view.network.SwitchHostView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.*;


public class SwitchHostController extends SwitchHostView {


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
        handleTabPaneEvent();

        tabPaneMain.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            ViewUtil.setMenuItemVisible(tabPaneMain, 0);
        });

    }


    public void handleTabPaneEvent() {
        ContextMenu closeMenu = ViewUtil.getCloseMenu();

        //关闭选中监听
        MenuItem closeMenuItem = closeMenu.getItems().get(0);
        closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleCloseMenuItemEvent(tabPaneMain);
            }
        });

        //关闭全部
        MenuItem closeAllMenuItem = closeMenu.getItems().get(1);
        closeAllMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleCloseAllMenuItemEvent(tabPaneMain);
            }
        });

        //关闭其他监听
        MenuItem closeOtherMenuItem = closeMenu.getItems().get(2);
        closeOtherMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                handleCloseOtherMenuItemEvent(tabPaneMain);
            }

        });

        tabPaneMain.setContextMenu(closeMenu);
    }


    private void handleCloseMenuItemEvent(TabPane tabPaneMain) {
        tabPaneMain.getTabs().removeIf(tab -> tab.selectedProperty().getValue());
    }

    private void handleCloseAllMenuItemEvent(TabPane tabPaneMain) {
        tabPaneMain.getTabs().clear();
    }

    private void handleCloseOtherMenuItemEvent(TabPane tabPaneMain) {
        tabPaneMain.getTabs().removeIf(tab -> !tab.selectedProperty().getValue());
    }

    private void initService() {
        ControllerMangerUtil.setController(SwitchHostController.this.getClass().getName(), this);
    }

}
