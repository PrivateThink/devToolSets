package com.zeros.devtool.controller.format;

import com.zeros.devtool.service.format.XMLFormatService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.format.XMLFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;


import java.net.URL;
import java.util.ResourceBundle;

public class XMLFormatController extends XMLFormatView {



    private final XMLFormatService xmlFormatService = new XMLFormatService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initService();
        initView();
        initEvent();
    }


    private void initService() {
        ControllerMangerUtil.setController(XMLFormatController.this.getClass().getName(), this);
    }

    private void initView() {
        //内容变化监听
        rawXML.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                xmlFormatService.formatXML(rawXML,formatXML);
            }
        });
    }

    private void initEvent() {
        //粘贴
        pasteXml.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                rawXML.setText(clipboard.getString());
            }
        });

        cleanXml.setOnAction(event -> {
            rawXML.setText("");
        });

        clearFormatXML.setOnAction(event -> {
            formatXML.setText("");
        });

        copyFormatXML.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(formatXML.getText());
            clipboard.setContent(clipboardContent);
            ToastUtil.toast("复制成功",2000);
        });
    }
}
