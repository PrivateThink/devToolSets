package com.zeros.devtool.controller.format;


import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.format.HTMLFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.jsoup.Jsoup;

import java.net.URL;
import java.util.ResourceBundle;

public class HTMLFormatController extends HTMLFormatView {


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


    }



    private void initService() {
        ControllerMangerUtil.setController(HTMLFormatController.this.getClass().getName(), this);
    }



    private void initEvent() {

        //内容变化监听
        rawHtml.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                formatHtml(rawHtml,formatHtml);
            }
        });

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
            copyEvent(copyHtml);
        });

        copyFormatHtml.setOnAction(event -> {
            copyEvent(copyFormatHtml);
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

    /**
     *  格式化 html
     * @param rawHtml
     * @param formatHtml
     */
    public void formatHtml(CodeArea rawHtml, CodeArea formatHtml) {
        if (StringUtils.isNotBlank(rawHtml.getText())){
            try {
                formatHtml.replaceText(Jsoup.parse(rawHtml.getText()).toString());
            }catch (Exception e){
                formatHtml.replaceText(e.getMessage());
            }

        }
    }


    private void copyEvent(Button copyHtml){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(copyHtml.getText());
        clipboard.setContent(clipboardContent);
        ToastUtil.toast("复制成功",2000);
    }

}
