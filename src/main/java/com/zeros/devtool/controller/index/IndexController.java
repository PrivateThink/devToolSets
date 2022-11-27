package com.zeros.devtool.controller.index;

import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.constants.CssConstants;
import com.zeros.devtool.constants.FileConstants;
import com.zeros.devtool.controller.format.HTMLFormatController;
import com.zeros.devtool.controller.format.JsonFormatController;
import com.zeros.devtool.controller.format.SQLFormatController;
import com.zeros.devtool.controller.format.XMLFormatController;
import com.zeros.devtool.controller.network.SwitchHostController;
import com.zeros.devtool.controller.system.PortCheckController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.service.*;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.CssLoadUtil;
import com.zeros.devtool.utils.SystemUtils;
import com.zeros.devtool.utils.view.ViewUtil;
import com.zeros.devtool.view.IndexView;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.fxmisc.richtext.CodeArea;


import java.net.URL;
import java.util.ResourceBundle;

public class IndexController extends IndexView {

    private final SwitchHostService switchHostService = new SwitchHostService();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
        initEvent();
        initService();
    }

    private void initView() {
        loadMenu();
    }

    private void loadMenu() {
        TreeItem<Node> all = ViewUtil.getRootTreeItem();
        //网络
        TreeItem<Node> networkTreeItem = getNetworkTreeItem();
        all.getChildren().add(networkTreeItem);

        //格式化
        TreeItem<Node> formatTreeItem = getFormatTreeItem();
        all.getChildren().add(formatTreeItem);

        //系统
        TreeItem<Node> systemTreeItem = getSystemTreeItem();

        all.getChildren().add(systemTreeItem);
        all.setExpanded(true);
        rootTree.setRoot(all);
    }

    private TreeItem<Node> getNetworkTreeItem() {
        TreeItem<Node> network = ViewUtil.getNetworkTreeItem();
        TreeItem<Node> hostTreeItem = this.getHostRootTreeItem();
        network.getChildren().add(hostTreeItem);
        return network;
    }


    //获取host的树形菜单
    public TreeItem<Node> getHostRootTreeItem() {

        //切换host
        TreeItem<Node> switchHost = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.SWITCH_HOST.getType(), Constants.SWITCH_HOST));
        //系统当前的host
        TreeItem<Node> currentHostItem = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.CURRENT_HOST.getType(), Constants.CURRENT_HOST));


        currentHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                //加载系统当前的host
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    String hostFile = "";
                    if (SystemUtils.isWindows()) {
                        hostFile = FileConstants.WIN_HOST;
                    } else {
                        hostFile = FileConstants.MAC_HOST;
                    }
                    //设置为tabPane
                    SwitchHostController switchHostController = ControllerMangerUtil.getSwitchHostController();
                    indexPane.setCenter(switchHostController.getTabPaneMain());


                    //添加host tab
                    CodeArea codeArea = switchHostService.addHostTab(Constants.CURRENT_HOST, switchHostController.getTabPaneMain());
                    //读取host
                    switchHostService.loadSystemHost(codeArea, hostFile);
                    //选择系统当前的host
                    switchHostService.switchHostTab(Constants.CURRENT_HOST);
                }
            }
        });

        switchHost.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    ObservableList<TreeItem<Node>> treeItems = switchHost.getChildren();
                    if (!treeItems.contains(currentHostItem)) {
                        switchHost.getChildren().add(currentHostItem);
                    }
                    switchHostService.loadHost(switchHost);
                }
            }
        });


        return switchHost;
    }

    private TreeItem<Node> getFormatTreeItem() {
        TreeItem<Node> format = ViewUtil.getFormatTreeItem();
        TreeItem<Node> jsonFormatTreeItem = this.getJsonFormatTreeItem();
        TreeItem<Node> sqlFormatTreeItem = this.getSqlFormatTreeItem();
        TreeItem<Node> xmlFormatTreeItem = this.getXmlFormatTreeItem();
        TreeItem<Node> htmlFormatTreeItem = this.getHtmlFormatTreeItem();
        format.getChildren().add(jsonFormatTreeItem);
        format.getChildren().add(sqlFormatTreeItem);
        format.getChildren().add(xmlFormatTreeItem);
        format.getChildren().add(htmlFormatTreeItem);
        return format;
    }


    private TreeItem<Node> getHtmlFormatTreeItem() {
        TreeItem<Node> htmlFormat = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.HTML_FORMAT.getType(), Constants.HTML_FORMAT));
        htmlFormat.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    HTMLFormatController htmlFormatController = ControllerMangerUtil.getHTMLFormatController();
                    indexPane.setCenter(htmlFormatController.getHtmlAnchorPane());
                }
            }
        });
        return htmlFormat;
    }


    public TreeItem<Node> getXmlFormatTreeItem() {
        TreeItem<Node> xmlFormat = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.XML_FORMAT.getType(), Constants.XML_FORMAT));
        xmlFormat.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    XMLFormatController xmlFormatController = ControllerMangerUtil.getXMLFormatController();
                    indexPane.setCenter(xmlFormatController.getXmlAnchorPane());
                }
            }
        });
        return xmlFormat;
    }

    public TreeItem<Node> getSqlFormatTreeItem() {
        TreeItem<Node> sqlFormat = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.SQL_FORMAT.getType(), Constants.SQL_FORMAT));
        sqlFormat.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    SQLFormatController sqlFormatController = ControllerMangerUtil.getSQLFormatController();
                    indexPane.setCenter(sqlFormatController.getSqlAnchorPane());
                }
            }
        });
        return sqlFormat;
    }


    public TreeItem<Node> getJsonFormatTreeItem() {
        TreeItem<Node> jsonFormat = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.JSON_FORMAT.getType(), Constants.JSON_FORMAT));
        jsonFormat.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    JsonFormatController jsonFormatController = ControllerMangerUtil.getJsonFormatController();
                    indexPane.setCenter(jsonFormatController.getTabPaneMain());
                    jsonFormatController.getJsonHBox().setPadding(new Insets(5, 10, 10, 10));
                }
            }
        });
        return jsonFormat;
    }

    private TreeItem<Node> getSystemTreeItem() {
        TreeItem<Node> system = ViewUtil.getSystemTreeItem();
        TreeItem<Node> portTreeItem = getPortTreeItem();
        system.getChildren().add(portTreeItem);
        return system;
    }

    public TreeItem<Node> getPortTreeItem() {
        TreeItem<Node> port = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.PORT_CHECK.getType(), Constants.PORT_CHECK));

        port.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    PortCheckController portCheckController = ControllerMangerUtil.getPortCheckController();
                    indexPane.setCenter(portCheckController.getPortPane());
                    portCheckController.searchPort(portCheckController.getPortTextFile(),portCheckController.getPortTableView());
                }
            }
        });

        return port;
    }


    private void initEvent() {

        rootTree.getStylesheets().add(CssLoadUtil.getResourceUrl(CssConstants.ROOT_TREE_CSS));

        rootTree.setOnMouseClicked(event -> {
            TreeItem<Node> item = rootTree.getSelectionModel().getSelectedItem();
            if (item == null) {
                return;
            }
        });

    }

    private void initService() {
        ControllerMangerUtil.setController(IndexController.this.getClass().getName(), this);
    }

}
