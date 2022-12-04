package com.zeros.devtool.controller.format;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeros.devtool.control.SearchCodeArea;
import com.zeros.devtool.utils.ControllerMangerUtil;

import com.zeros.devtool.utils.view.ViewUtil;
import com.zeros.devtool.view.format.JsonFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;



import java.net.URL;
import java.util.ResourceBundle;

public class JsonFormatController extends JsonFormatView {


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initService();
        initView();
        initEvent();
    }

    private void initView() {

    }

    private void initEvent() {


        //内容变化监听
        jsonText.getCodeArea().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                setJsonTextEvent(newValue, jsonText.getCodeArea(), formatText.getCodeArea());
            }
        });




        Tab addTab = new Tab("+");
        addTab.setClosable(false);
        addTab.setStyle("-fx-font-size: 14pt;");
        tabPaneMain.getTabs().add(tabPaneMain.getTabs().size(), addTab);

        tabPaneMain.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            addTabEvent(addTab, newTab, tabPaneMain);
            ViewUtil.setMenuItemVisible(tabPaneMain,1);
        });

        //tabPane 添加菜单
        handleTabPaneEvent(tabPaneMain);
    }

    public void setJsonTextEvent(String text, CodeArea jsonText, CodeArea formatText){
        if (StringUtils.isNotBlank(text)){
            try {
                JSONObject object = JSONObject.parseObject(text);
                String result = JSON.toJSONString(object, true);
                formatText.replaceText(result);
                jsonText.replaceText(result);
            }catch (Exception e){
                formatText.replaceText(e.getMessage());
            }
        }else {
            formatText.replaceText("");
        }

    }


    public void handleTabPaneEvent(TabPane tabPaneMain) {
        ContextMenu closeMenu = ViewUtil.getCloseMenu();

        Tab addTab = tabPaneMain.getTabs().get(tabPaneMain.getTabs().size() - 1);

        //关闭选中监听
        MenuItem closeMenuItem = closeMenu.getItems().get(0);
        closeMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tabPaneMain.getTabs().removeIf(tab -> tab.selectedProperty().getValue() && tab!=addTab);
            }
        });

        //关闭全部
        MenuItem closeAllMenuItem = closeMenu.getItems().get(1);
        closeAllMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tabPaneMain.getTabs().clear();
                tabPaneMain.getTabs().add(addTab);
            }
        });

        //关闭其他监听
        MenuItem closeOtherMenuItem = closeMenu.getItems().get(2);
        closeOtherMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tabPaneMain.getTabs().removeIf(tab -> !tab.selectedProperty().getValue() && tab!=addTab);
            }

        });

        tabPaneMain.setContextMenu(closeMenu);
    }


    public void addTabEvent(Tab addTab, Tab newTab, TabPane tabPane){

        if(newTab == addTab) {
            HBox hBox = new HBox();
            SearchCodeArea leftArea = new SearchCodeArea();
            hBox.setSpacing(5);
            hBox.setPadding(new Insets(5,10,10,10));
            SearchCodeArea rightArea = new SearchCodeArea();
            leftArea.getCodeArea().textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    setJsonTextEvent(newValue,leftArea.getCodeArea(),rightArea.getCodeArea());
                }
            });

            HBox.setHgrow(leftArea, Priority.ALWAYS);
            HBox.setHgrow(rightArea, Priority.ALWAYS);
            hBox.getChildren().addAll(leftArea, rightArea);
            Tab tab = new Tab("选项卡");
            tab.setContent(hBox);
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
            tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
        }
    }

    private void initService() {
        ControllerMangerUtil.setController(JsonFormatController.this.getClass().getName(), this);
    }


}
