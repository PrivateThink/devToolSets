package com.zeros.devtool.service;

import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.constants.FileConstants;
import com.zeros.devtool.constants.FxmlConstant;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.controller.network.SwitchHostController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.FXMLLoaderUtils;
import com.zeros.devtool.utils.SystemUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;
import org.fxmisc.richtext.CodeArea;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;


public class MenuService {



    private final AtomicInteger fileNameIndex = new AtomicInteger(1);

    private final List<String> hostTypes = new ArrayList<>();



    public AtomicInteger getFileNameIndex() {
        return fileNameIndex;
    }


    public TreeItem<Label> getNetWorkRootTreeItem() {
        //添加元素
        TreeItem<Label> network = new TreeItem<>(this.getLabel(MenuTypeEnum.NETWORK.getType(), Constants.NETWORK));
        network.setExpanded(true);
        TreeItem<Label> switchHost = new TreeItem<>(this.getLabel(MenuTypeEnum.SWITCH_HOST.getType(), Constants.SWITCH_HOST));
        TreeItem<Label> currentHostItem = new TreeItem<>(this.getLabel(MenuTypeEnum.CURRENT_HOST.getType(), Constants.CURRENT_HOST));
        network.getChildren().add(switchHost);
        switchHost.getChildren().add(currentHostItem);

        this.addHostType(MenuTypeEnum.CURRENT_HOST.getType());

        currentHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                //加载系统当前的host
                if (event.getButton() == MouseButton.PRIMARY) {
                    String hostFile="";
                    if (SystemUtils.isWindows()) {
                        hostFile = FileConstants.WIN_HOST;
                    }else {
                        hostFile = FileConstants.MAC_HOST;
                    }


                    SwitchHostController switchHostController = MenuService.this.getSwitchHostController();

                    IndexController indexController= MenuService.this.getIndexController();
                    indexController.getIndexPane().setCenter(switchHostController.getTabPaneMain());


                    //添加host tab
                    CodeArea codeArea = switchHostController.addHostTab(Constants.CURRENT_HOST);
                    //读取host
                    switchHostController.loadSystemHost(codeArea,hostFile);
                    //选择系统当前的host
                    switchHostController.switchHostTab(Constants.CURRENT_HOST);
                }
            }
        });

        switchHost.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (event.getButton() == MouseButton.PRIMARY) {

                    List<String> hostFiles = new ArrayList<>();

                    SwitchHostController switchHostController = MenuService.this.getSwitchHostController();

                    IndexController indexController= MenuService.this.getIndexController();
                    indexController.getIndexPane().setCenter(switchHostController.getTabPaneMain());

                    //添加host tab
                    CodeArea codeArea = switchHostController.addHostTab(Constants.CURRENT_HOST);
                    //读取host
                    switchHostController.loadSystemHost(codeArea,FileConstants.WIN_HOST);

                    //选择系统当前的host
                    switchHostController.switchHostTab(Constants.CURRENT_HOST);


                } else {
                    ContextMenu hostMenu = new ContextMenu();
                    MenuItem addMenuItem = new MenuItem("添加");
                    hostMenu.getItems().add(addMenuItem);
                    switchHost.getValue().setContextMenu(hostMenu);
                    addMenuItem.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            SwitchHostController switchHostController = MenuService.this.getSwitchHostController();
                            int index = fileNameIndex.getAndIncrement();
                            String name = "新文件" + index;
                            switchHostController.addHostTab(name);
                            String hostType = MenuTypeEnum.SWITCH_HOST.getType() + "_" + index;
                            MenuService.this.addHostType(hostType);
                            TreeItem<Label> newHostItem = new TreeItem<>(MenuService.this.getLabel(hostType, name));
                            switchHost.getChildren().add(newHostItem);
                            newHostItem.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    if (event.getButton() == MouseButton.PRIMARY) {
                                        //点击切换host tab
                                        switchHostController.switchHostTab(name);

                                    }else if (event.getButton() == MouseButton.SECONDARY){
                                        ContextMenu newHostMenu = new ContextMenu();
                                        MenuItem addMenuItem = new MenuItem("修改名字");
                                        newHostMenu.getItems().add(addMenuItem);
                                        newHostItem.getValue().setContextMenu(newHostMenu);

                                        TextInputDialog dialog = new TextInputDialog("");
                                        dialog.setTitle("");
                                        dialog.setHeaderText("");
                                        dialog.setContentText("");
                                        dialog.setGraphic(null);

                                        Optional<String> result = dialog.showAndWait();
                                    }
                                }
                            });
                        }
                    });
                }

            }
        });

        return network;
    }

    public TreeItem<Label> getTextRootTreeItem() {
        TreeItem<Label> text = new TreeItem<>(this.getLabel(MenuTypeEnum.TEXT.getType(), Constants.TEXT));
        text.setExpanded(true);
        TreeItem<Label> clipboardHistory = new TreeItem<>(this.getLabel(MenuTypeEnum.CLIPBOARD_HISTORY.getType(), Constants.CLIPBOARD_HISTORY));
        text.getChildren().add(clipboardHistory);
        return text;
    }

    public TreeItem<Label> getRootTreeItem() {
        TreeItem<Label> text = new TreeItem<>(this.getLabel(MenuTypeEnum.ALL.getType(), Constants.ALL));
        return text;
    }


    public Label getLabel(String id, String text) {
        Label label = new Label();
        label.setTextAlignment(TextAlignment.CENTER);
        label.setId(id);
        label.setText(text);
        return label;
    }

    public SwitchHostController getSwitchHostController() {
        SwitchHostController controller = (SwitchHostController) ControllerMangerUtil.getController(SwitchHostController.class.getName());
        if (controller == null) {
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.SWITCH_HOST);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller = fxmlLoader.getController();
            ControllerMangerUtil.setController(SwitchHostController.class.getName(), controller);
        }
        return controller;
    }


    public IndexController getIndexController() {
        IndexController controller = (IndexController) ControllerMangerUtil.getController(IndexController.class.getName());
        return controller;
    }

    public List<String> getHostTypes() {
        return hostTypes;
    }

    public synchronized void addHostType(String name) {
        if (hostTypes.contains(name)) {
            return;
        }
        hostTypes.add(name);
    }
}
