package com.zeros.devtool.controller.system;

import cn.hutool.core.collection.CollectionUtil;
import com.zeros.devtool.model.system.PortCheckInfo;
import com.zeros.devtool.service.PortCheckService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.system.PortCheckView;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class PortCheckController extends PortCheckView {


    private static final PortCheckService portService = new PortCheckService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initService();
        initView();
        initEvent();
    }

    private void initView() {

        //初始化端口列表
        setTableView();


    }

    private void initService() {
        ControllerMangerUtil.setController(PortCheckController.this.getClass().getName(), this);
    }

    private void initEvent() {
        //设置查询
        setSearchEvent();
    }


    public void setTableView() {

        portTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // 设置表格可编辑
        portTableView.setEditable(false);

        processName.setCellValueFactory(new PropertyValueFactory<>("processName"));
        pid.setCellValueFactory(new PropertyValueFactory<>("pid"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        port.setCellValueFactory(new PropertyValueFactory<>("port"));

        List<PortCheckInfo> portCheckInfos = portService.getPortCheckInfos();
        portTableView.setItems(FXCollections.observableArrayList(portCheckInfos));

        operator.setCellValueFactory(portCheckInfoCell -> {
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
                    String result = portService.kill(info.getPid());
                    ToastUtil.toast(result,3000);
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


    private void refresh(TableView<PortCheckInfo> portTableView) {
        ObservableList<PortCheckInfo> items = portTableView.getItems();
        items.removeAll(items);
        List<PortCheckInfo> infos = portService.getPortCheckInfos();
        items.addAll(infos);
        portTableView.refresh();
    }


    public void setSearchEvent() {
        this.setSearchEnterEvent();
        this.setButtonSearchEvent();
    }

    private void setSearchEnterEvent() {
        portTextFile.setOnKeyPressed(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                searchPort(portTextFile, portTableView);
            }
        });
    }

    private void setButtonSearchEvent() {
        findButton.setOnAction(actionEvent -> {
            searchPort(portTextFile, portTableView);
        });
    }

    public void searchPort(TextField portTextFile, TableView<PortCheckInfo> portTableView) {
        String text = portTextFile.getText();
        if (StringUtils.isBlank(text)) {
            refresh(portTableView);
            return;
        }

        List<PortCheckInfo> infos = portService.getPortCheckInfos();
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

}
