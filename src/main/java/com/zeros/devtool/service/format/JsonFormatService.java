package com.zeros.devtool.service.format;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.controller.format.JsonFormatController;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.controller.network.SwitchHostController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;


public class JsonFormatService {


    // 开始搜索的位置
    private int startIndex = 0;
    // textarea中光标的位置
    private int position = 0;



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
                    jsonFormatController.getJsonHBox().setPadding(new Insets(5,10,10,10));
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

    public void addTabEvent(Tab addTab, Tab newTab, TabPane tabPane){

        if(newTab == addTab) {
            HBox hBox = new HBox();
            TextArea leftArea = new TextArea();
            hBox.setSpacing(5);
            hBox.setPadding(new Insets(5,10,10,10));
            TextArea rightArea = new TextArea();
            leftArea.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                   setJsonTextEvent(newValue,leftArea,rightArea);
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

    public void findText(TextArea formatText){
        HBox searchWindow = new HBox();
        searchWindow.setSpacing(10);
        searchWindow.setPadding(new Insets(10));
        Label label = new Label("查找目标:");
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        TextField textField = new TextField();
        Button button = new Button("查找");
        searchWindow.getChildren().addAll(label, textField, button);
        Stage stage = new Stage();
        stage.setTitle("查找");
        stage.setScene(new Scene(searchWindow));
        stage.setResizable(false);
        stage.show();
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String text = formatText.getText();
                String targetText = textField.getText();
                if (StringUtils.isBlank(targetText)) {
                    ToastUtil.toast("查找的内容不能为空", 2000);
                    return;
                }
                if (StringUtils.isNotEmpty(text)) {
                    startIndex = text.indexOf(targetText, startIndex);
                    if (startIndex >= 0 && startIndex < text.length()) {
                        formatText.selectRange(startIndex, startIndex + targetText.length());
                        startIndex += targetText.length();
                    }
                }
            }
        });
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
}
