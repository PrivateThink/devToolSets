package com.zeros.devtool.controller.format;

import cn.hutool.core.util.XmlUtil;
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
import org.fxmisc.richtext.LineNumberFactory;
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

        // 设置行号
        rawXML.setParagraphGraphicFactory(LineNumberFactory.get(rawXML));
        // 设置行号
        formatXML.setParagraphGraphicFactory(LineNumberFactory.get(formatXML));


    }

    private void initEvent() {

        //内容变化监听
        rawXML.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                formatXML(rawXML,formatXML);
            }
        });

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
