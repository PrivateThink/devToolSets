package com.zeros.devtool.controller.format;

import cn.hutool.core.util.XmlUtil;
import com.zeros.devtool.enums.CodeTypeEnum;
import com.zeros.devtool.utils.CodeLightUtil;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.format.XMLFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.w3c.dom.Document;


import java.net.URL;
import java.util.ResourceBundle;

public class XMLFormatController extends XMLFormatView {


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

    }

    private void initEvent() {

        //内容变化监听
        rawXML.getCodeArea().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                formatXML(rawXML.getCodeArea(),formatXML.getCodeArea());
            }
        });



        //设置代码高亮
        rawXML.getCodeArea().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                CodeLightUtil.setCodeLight(newValue,rawXML.getCodeArea(), CodeTypeEnum.XML);
            }
        });

        formatXML.getCodeArea().textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                CodeLightUtil.setCodeLight(newValue,formatXML.getCodeArea(), CodeTypeEnum.XML);
            }
        });


        //粘贴
        pasteXml.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                rawXML.getCodeArea().replaceText(clipboard.getString());
            }
        });

        cleanXml.setOnAction(event -> {
            rawXML.getCodeArea().replaceText("");
        });

        clearFormatXML.setOnAction(event -> {
            formatXML.getCodeArea().replaceText("");
        });


        copyXml.setOnAction(event -> {
            copyEvent(copyXml);
        });

        copyFormatXML.setOnAction(event -> {
            copyEvent(copyFormatXML);
        });
    }


    /**
     * 格式化 xml
     * @param rawXml
     * @param formatXml
     */
    public void formatXML(CodeArea rawXml, CodeArea formatXml) {
        if (StringUtils.isNotBlank(rawXml.getText())){
            try {
                Document document = XmlUtil.parseXml(rawXml.getText());
                String format = XmlUtil.toStr(document,true);
                formatXml.replaceText(format);
            }catch (Exception e){
                formatXml.replaceText(e.getMessage());
            }

        }
    }

    private void copyEvent(Button copyXml){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(copyXml.getText());
        clipboard.setContent(clipboardContent);
        ToastUtil.toast("复制成功",2000);
    }
}
