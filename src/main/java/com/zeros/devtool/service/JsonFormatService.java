package com.zeros.devtool.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.controller.format.JsonFormatController;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.controller.network.SwitchHostController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.apache.commons.lang3.StringUtils;

public class JsonFormatService {


    public TreeItem<Node> getJsonFormatTreeItem(){
        TreeItem<Node> jsonFormat = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.JSON_FORMAT.getType(), Constants.JSON_FORMAT));
        jsonFormat.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    JsonFormatController jsonFormatController = ControllerMangerUtil.getJsonFormatController();
                    IndexController indexController = ControllerMangerUtil.getIndexController();
                    indexController.getIndexPane().setCenter(jsonFormatController.getTabPaneMain());
                }
            }
        });
        return jsonFormat;
    }

    public void setJsonTextEvent(String text, TextArea jsonText,TextArea formatText){
        if (StringUtils.isNotEmpty(text)){
            try {
                JSONObject object = JSONObject.parseObject(text);
                String result = JSON.toJSONString(object, true);
                formatText.setText(result);
                jsonText.setText(result);
            }catch (Exception e){
                formatText.setText(e.getMessage());
            }
        }else {
            formatText.setText("");
        }

    }

    public void addTabEvent(Tab addTab, Tab newTab, TextArea jsonText, TextArea formatText, TabPane tabPane){
        if(newTab == addTab) {
            HBox hBox = new HBox();
            TextArea leftArea = new TextArea();
            TextArea rightArea = new TextArea();
            leftArea.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                   setJsonTextEvent(newValue,jsonText,formatText);
                }
            });

            HBox.setHgrow(leftArea, Priority.ALWAYS);
            HBox.setHgrow(rightArea,Priority.ALWAYS);
            hBox.getChildren().addAll(leftArea, rightArea);
            Tab tab = new Tab("选项卡");
            tab.setContent(hBox);
            tabPane.getTabs().add(tabPane.getTabs().size() - 1, tab);
            tabPane.getSelectionModel().select(tabPane.getTabs().size() - 2);
        }
    }
}
