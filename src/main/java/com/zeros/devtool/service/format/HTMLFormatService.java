package com.zeros.devtool.service.format;


import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.controller.format.HTMLFormatController;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.jsoup.Jsoup;


public class HTMLFormatService {



    public TreeItem<Node> getHtmlFormatTreeItem() {
        TreeItem<Node> xmlFormat = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.HTML_FORMAT.getType(), Constants.HTML_FORMAT));
        xmlFormat.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    HTMLFormatController htmlFormatController = ControllerMangerUtil.getHTMLFormatController();
                    IndexController indexController = ControllerMangerUtil.getIndexController();
                    indexController.getIndexPane().setCenter(htmlFormatController.getHtmlAnchorPane());
                }
            }
        });
        return xmlFormat;
    }


    public void formatHtml(CodeArea rawHtml, CodeArea formatHtml) {
        if (StringUtils.isNotBlank(rawHtml.getText())){
            try {
                formatHtml.replaceText(Jsoup.parse(rawHtml.getText()).toString());
            }catch (Exception e){
                formatHtml.replaceText(e.getMessage());
            }

        }
    }

}
