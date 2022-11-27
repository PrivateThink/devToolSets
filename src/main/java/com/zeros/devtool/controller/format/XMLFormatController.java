package com.zeros.devtool.controller.format;

import com.zeros.devtool.service.format.XMLFormatService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.format.XMLFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.fxmisc.richtext.LineNumberFactory;


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

        // 设置行号
        rawXML.setParagraphGraphicFactory(LineNumberFactory.get(rawXML));
        // 设置行号
        formatXML.setParagraphGraphicFactory(LineNumberFactory.get(formatXML));

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
                rawXML.replaceText(clipboard.getString());
            }
        });

        cleanXml.setOnAction(event -> {
            rawXML.replaceText("");
        });

        clearFormatXML.setOnAction(event -> {
            formatXML.replaceText("");
        });


        copyXml.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(copyXml.getText());
            clipboard.setContent(clipboardContent);
            ToastUtil.toast("复制成功",2000);
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
