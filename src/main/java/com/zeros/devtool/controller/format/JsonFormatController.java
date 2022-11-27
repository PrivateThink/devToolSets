package com.zeros.devtool.controller.format;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import com.zeros.devtool.view.format.JsonFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


import java.net.URL;
import java.util.ResourceBundle;

public class JsonFormatController extends JsonFormatView {


    // 开始搜索的位置
    private int startIndex = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initService();
        initView();
        initEvent();
    }

    private void initView() {
        // 设置行号
        jsonText.setParagraphGraphicFactory(LineNumberFactory.get(jsonText));
        // 设置行号
        formatText.setParagraphGraphicFactory(LineNumberFactory.get(formatText));
    }

    private void initEvent() {


        //内容变化监听
        jsonText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                setJsonTextEvent(newValue, jsonText, formatText);
            }
        });

        tabPaneMain.setOnKeyPressed(event -> {

            Tab selectedItem = tabPaneMain.getSelectionModel().getSelectedItem();
            HBox hBox = (HBox) selectedItem.getContent();
            CodeArea formatArea = (CodeArea)hBox.getChildren().get(1);
            //查找 ctrl + F
            if (new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN).match(event)
                    && StringUtils.isNotBlank(formatArea.getText())) {
                 findText(formatArea);
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


    public void findText(CodeArea formatText){
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


    public void addTabEvent(Tab addTab, Tab newTab, TabPane tabPane){

        if(newTab == addTab) {
            HBox hBox = new HBox();
            CodeArea leftArea = new CodeArea();
            hBox.setSpacing(5);
            hBox.setPadding(new Insets(5,10,10,10));
            CodeArea rightArea = new CodeArea();
            leftArea.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    setJsonTextEvent(newValue,leftArea,rightArea);
                }
            });

            // 设置行号
            leftArea.setParagraphGraphicFactory(LineNumberFactory.get(leftArea));
            // 设置行号
            rightArea.setParagraphGraphicFactory(LineNumberFactory.get(rightArea));
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
