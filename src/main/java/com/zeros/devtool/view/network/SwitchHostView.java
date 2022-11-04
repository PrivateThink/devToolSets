package com.zeros.devtool.view.network;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;


public abstract class SwitchHostView implements Initializable {

    @FXML
    protected TabPane tabPaneMain;

    public TabPane getTabPaneMain() {
        return tabPaneMain;
    }

    public void setTabPaneMain(TabPane tabPaneMain) {
        this.tabPaneMain = tabPaneMain;
    }

}
