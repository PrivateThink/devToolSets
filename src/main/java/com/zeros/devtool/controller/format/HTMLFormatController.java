package com.zeros.devtool.controller.format;

import com.zeros.devtool.enums.CodeTypeEnum;
import com.zeros.devtool.utils.CodeLightUtil;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.format.HTMLFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
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

    }



    private void initService() {
        ControllerMangerUtil.setController(HTMLFormatController.this.getClass().getName(), this);
    }



    private void initEvent() {

        //内容变化监听
        rawHtmArea.getCodeArea().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                formatHtml(rawHtmArea.getCodeArea(),formatHtmlArea.getCodeArea());
            }
        });

        //设置代码高亮
        rawHtmArea.getCodeArea().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                CodeLightUtil.setCodeLight(newValue,rawHtmArea.getCodeArea(), CodeTypeEnum.HTML);
            }
        });

        formatHtmlArea.getCodeArea().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                CodeLightUtil.setCodeLight(newValue,formatHtmlArea.getCodeArea(), CodeTypeEnum.HTML);
            }
        });

        //粘贴
        pasteHtml.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                rawHtmArea.getCodeArea().replaceText(clipboard.getString());
            }
        });

        cleanHtml.setOnAction(event -> {
            rawHtmArea.getCodeArea().replaceText("");
        });

        clearFormatHtml.setOnAction(event -> {
            formatHtmlArea.getCodeArea().replaceText("");
        });

        copyHtml.setOnAction(event -> {
            copyEvent(copyHtml);
        });

        copyFormatHtml.setOnAction(event -> {
            copyEvent(copyFormatHtml);
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
