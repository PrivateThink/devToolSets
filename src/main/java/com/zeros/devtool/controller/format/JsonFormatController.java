package com.zeros.devtool.controller.format;

import com.zeros.devtool.service.JsonFormatService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.view.format.JsonFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;



import java.net.URL;
import java.util.ResourceBundle;

public class JsonFormatController extends JsonFormatView {


    JsonFormatService jsonFormatService = new JsonFormatService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
    }

    private void initEvent() {


        jsonText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                jsonFormatService.setJsonTextEvent(newValue, jsonText, formatText);
            }
        });

        Tab addTab = new Tab("+");
        addTab.setClosable(false);

        tabPaneMain.getTabs().add(tabPaneMain.getTabs().size(), addTab);
        tabPaneMain.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            jsonFormatService.addTabEvent(addTab, newTab, jsonText, formatText, tabPaneMain);
        });
    }

    private void initService() {
        ControllerMangerUtil.setController(JsonFormatController.this.getClass().getName(), this);
    }
}
