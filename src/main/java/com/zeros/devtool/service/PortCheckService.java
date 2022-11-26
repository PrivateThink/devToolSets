package com.zeros.devtool.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RuntimeUtil;
import com.zeros.devtool.constants.CmdConstants;
import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.controller.system.PortCheckController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.model.system.PortCheckInfo;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.utils.view.ViewUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PortCheckService {


    public TreeItem<Node> getPortTreeItem() {
        TreeItem<Node> port = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.PORT_CHECK.getType(), Constants.PORT_CHECK));

        port.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    PortCheckController portCheckController = ControllerMangerUtil.getPortCheckController();
                    IndexController indexController = ControllerMangerUtil.getIndexController();
                    indexController.getIndexPane().setCenter(portCheckController.getPortPane());
                    searchPort(portCheckController.getPortTextFile(),portCheckController.getPortTableView());
                }
            }
        });

        return port;
    }


    public void setTableView() {

        PortCheckController portCheckController = ControllerMangerUtil.getPortCheckController();
        // 去掉空白多于列
        TableView<PortCheckInfo> portTableView = portCheckController.getPortTableView();
        portTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // 设置表格可编辑
        portTableView.setEditable(false);

        portCheckController.getProcessName().setCellValueFactory(new PropertyValueFactory<>("processName"));
        portCheckController.getPid().setCellValueFactory(new PropertyValueFactory<>("pid"));
        portCheckController.getStatus().setCellValueFactory(new PropertyValueFactory<>("status"));
        portCheckController.getPort().setCellValueFactory(new PropertyValueFactory<>("port"));

        List<PortCheckInfo> portCheckInfos = this.getPortCheckInfos();
        portTableView.setItems(FXCollections.observableArrayList(portCheckInfos));

        portCheckController.getOperator().setCellValueFactory(portCheckInfoCell -> {
            PortCheckInfo info = portCheckInfoCell.getValue();
            Button kill = new Button("终止");
            kill.setStyle("-jfx-button-type: FLAT;" +
                    "-fx-background-color: white;" +
                    "-fx-text-fill: black;");
            kill.setOnAction(actionEvent -> {
                String message = "确定终止[" + info.getProcessName() + "]吗？";
                Alert alert = new Alert(Alert.AlertType.NONE, message, ButtonType.OK, ButtonType.CANCEL);
                alert.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> buttonType = alert.showAndWait();
                if (buttonType.get() == ButtonType.CANCEL) {
                    alert.close();
                } else if (buttonType.get() == ButtonType.OK) {
                    //终止
                    kill(info.getPid());
                    //刷新
                    refresh(portTableView);
                }
            });

            kill.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    kill.setStyle("-jfx-button-type: FLAT;" +
                            "-fx-background-color: #A9D0F5;" +
                            "-fx-text-fill:black ;");
                }
            });

            kill.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    kill.setStyle("-jfx-button-type: FLAT;" +
                            "-fx-background-color: white;" +
                            "-fx-text-fill: black;");
                }
            });

            return new ReadOnlyObjectWrapper<Button>(kill);
        });
    }

    public void setSearchEvent() {
        this.setSearchEnterEvent();
        this.setButtonSearchEvent();
    }

    private void setSearchEnterEvent() {
        PortCheckController portCheckController = ControllerMangerUtil.getPortCheckController();
        TextField portTextFile = portCheckController.getPortTextFile();
        TableView<PortCheckInfo> portTableView = portCheckController.getPortTableView();
        portTextFile.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                searchPort(portTextFile, portTableView);
            }
        });
    }

    private void setButtonSearchEvent() {
        PortCheckController portCheckController = ControllerMangerUtil.getPortCheckController();
        Button findButton = portCheckController.getFindButton();
        TextField portTextFile = portCheckController.getPortTextFile();
        TableView<PortCheckInfo> portTableView = portCheckController.getPortTableView();
        findButton.setOnAction(actionEvent -> {
            searchPort(portTextFile, portTableView);
        });
    }

    private void searchPort(TextField portTextFile, TableView<PortCheckInfo> portTableView) {
        String text = portTextFile.getText();
        if (StringUtils.isBlank(text)) {
            refresh(portTableView);
            return;
        }

        List<PortCheckInfo> infos = this.getPortCheckInfos();
        if (CollectionUtil.isEmpty(infos)) {
            portTableView.getItems().clear();
        }
        List<PortCheckInfo> targetInfo = new ArrayList<>();
        for (PortCheckInfo info : infos) {
            if (info.getPort().equals(text.trim())) {
                targetInfo.add(info);
            }
        }

        portTableView.setItems(FXCollections.observableArrayList(targetInfo));
    }

    private void refresh(TableView<PortCheckInfo> portTableView) {
        ObservableList<PortCheckInfo> items = portTableView.getItems();
        items.removeAll(items);
        List<PortCheckInfo> infos = this.getPortCheckInfos();
        items.addAll(infos);
        portTableView.refresh();
    }


    public List<PortCheckInfo> getPortCheckInfos() {

        List<PortCheckInfo> infos = new ArrayList<>();
        List<String> portInfos = RuntimeUtil.execForLines(CmdConstants.PORT_LIST);
        if (CollectionUtils.isEmpty(portInfos)) {
            return infos;
        }
        Map<String, String> process = getProcess();
        for (String info : portInfos) {
            info = info.trim();
            if (info.toLowerCase().startsWith("tcp") || info.startsWith("udp")) {
                String[] infoSplit = info.split("\\s+");
                PortCheckInfo portCheckInfo = new PortCheckInfo();
                portCheckInfo.setProtocol(infoSplit[0]);
                portCheckInfo.setPort(infoSplit[1].split(":")[1]);
                portCheckInfo.setStatus(infoSplit[3]);
                portCheckInfo.setPid(infoSplit[4]);
                portCheckInfo.setProcessName(process.getOrDefault(portCheckInfo.getPid(), "-"));
                infos.add(portCheckInfo);
            }
        }
        infos = infos.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(PortCheckInfo::getPid))), ArrayList::new));
        return infos;
    }

    private Map<String, String> getProcess() {
        Map<String, String> processMap = new HashMap<>();
        List<String> taskList = RuntimeUtil.execForLines(CmdConstants.TASK_LIST);
        if (CollectionUtils.isEmpty(taskList)) {
            return processMap;
        }
        for (String task : taskList) {
            if (StringUtils.isBlank(task)) {
                continue;
            }
            String[] taskSpilt = task.split("\\s+");
            if (taskList == null || taskList.size() < 2) {
                continue;
            }

            processMap.put(taskSpilt[1], taskSpilt[0]);

        }
        return processMap;
    }

    public void kill(String pid) {
        RuntimeUtil.execForStr(CmdConstants.KILL_OPERATOR + pid);
    }

}
