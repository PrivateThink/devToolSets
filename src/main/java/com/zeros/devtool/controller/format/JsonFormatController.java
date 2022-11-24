package com.zeros.devtool.controller.format;

import com.zeros.devtool.service.JsonFormatService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import com.zeros.devtool.view.format.JsonFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import org.apache.commons.lang3.StringUtils;



import java.net.URL;
import java.util.ResourceBundle;

public class JsonFormatController extends JsonFormatView {

    JsonFormatService jsonFormatService = new JsonFormatService();


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
        jsonText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                jsonFormatService.setJsonTextEvent(newValue, jsonText, formatText);
            }
        });

        tabPaneMain.setOnKeyPressed(event -> {

            Tab selectedItem = tabPaneMain.getSelectionModel().getSelectedItem();
            HBox hBox = (HBox) selectedItem.getContent();
            TextArea formatArea = (TextArea)hBox.getChildren().get(1);
            //查找 ctrl + F
            if (new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN).match(event)
                    && StringUtils.isNotBlank(formatArea.getText())) {
                jsonFormatService.findText(formatArea);
            }
        });



        Tab addTab = new Tab("+");
        addTab.setClosable(false);
        addTab.setStyle("-fx-font-size: 14pt;");
        tabPaneMain.getTabs().add(tabPaneMain.getTabs().size(), addTab);

        tabPaneMain.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            jsonFormatService.addTabEvent(addTab, newTab, tabPaneMain);
            ViewUtil.setMenuItemVisible(tabPaneMain,1);
        });

        //tabPane 添加菜单
        jsonFormatService.handleTabPaneEvent(tabPaneMain);
    }

    private void initService() {
        ControllerMangerUtil.setController(JsonFormatController.this.getClass().getName(), this);
    }
}
