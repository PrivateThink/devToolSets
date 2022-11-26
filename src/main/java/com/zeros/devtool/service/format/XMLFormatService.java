package com.zeros.devtool.service.format;

import cn.hutool.core.util.XmlUtil;
import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.controller.format.XMLFormatController;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import javafx.event.EventHandler;

import javafx.scene.Node;

import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

public class XMLFormatService {

    public TreeItem<Node> getSqlFormatTreeItem() {
        TreeItem<Node> xmlFormat = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.XML_FORMAT.getType(), Constants.XML_FORMAT));
        xmlFormat.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    XMLFormatController xmlFormatController = ControllerMangerUtil.getXMLFormatController();
                    IndexController indexController = ControllerMangerUtil.getIndexController();
                    indexController.getIndexPane().setCenter(xmlFormatController.getXmlAnchorPane());
                }
            }
        });
        return xmlFormat;
    }


    public void formatXML(TextArea rawXml, TextArea formatXml) {
        if (StringUtils.isNotEmpty(rawXml.getText())){
            try {
                Document document = XmlUtil.parseXml(rawXml.getText());
                String format = XmlUtil.toStr(document,true);
                formatXml.setText(format);
            }catch (Exception e){
                formatXml.setText(e.getMessage());
            }

        }
    }
}
