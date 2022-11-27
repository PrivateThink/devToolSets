package com.zeros.devtool.controller.format;

import com.zeros.devtool.service.format.HTMLFormatService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.format.HTMLFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;

import org.fxmisc.richtext.LineNumberFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class HTMLFormatController extends HTMLFormatView {



    private HTMLFormatService htmlFormatService = new HTMLFormatService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initService();
        initView();
        initEvent();
    }

    private void initView() {

        // 设置行号
        rawHtml.setParagraphGraphicFactory(LineNumberFactory.get(rawHtml));
        // 设置行号
        formatHtml.setParagraphGraphicFactory(LineNumberFactory.get(rawHtml));

        //内容变化监听
        rawHtml.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                htmlFormatService.formatHtml(rawHtml,formatHtml);
            }
        });
    }



    private void initService() {
        ControllerMangerUtil.setController(HTMLFormatController.this.getClass().getName(), this);
    }



    private void initEvent() {

        //粘贴
        pasteHtml.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                rawHtml.replaceText(clipboard.getString());
            }
        });

        cleanHtml.setOnAction(event -> {
            rawHtml.replaceText("");
        });

        clearFormatHtml.setOnAction(event -> {
            formatHtml.replaceText("");
        });

        copyHtml.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(copyHtml.getText());
            clipboard.setContent(clipboardContent);
            ToastUtil.toast("复制成功",2000);
        });

        copyFormatHtml.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(formatHtml.getText());
            clipboard.setContent(clipboardContent);
            ToastUtil.toast("复制成功",2000);
        });

        htmlAnchorPane.setOnKeyPressed(event -> {
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode().equals(KeyCode.F)) {
                searchHBox.setVisible(true);
                searchHBox.setManaged(true);
            }
        });

        searchClose.setOnAction(event -> {
            searchHBox.setVisible(false);
            searchHBox.setManaged(false);
        });
    }


}
