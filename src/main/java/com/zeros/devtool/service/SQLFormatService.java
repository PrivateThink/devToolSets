package com.zeros.devtool.service;

import com.alibaba.druid.DbType;
import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.controller.format.JsonFormatController;
import com.zeros.devtool.controller.format.SQLFormatController;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.enums.MenuTypeEnum;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.SQLFormatUtils;
import com.zeros.devtool.utils.view.ViewUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class SQLFormatService {


    private final List<String> dbTypes = List.of(
            DbType.mysql.toString(),
            DbType.oracle.toString(),
            DbType.sqlserver.toString(),
            DbType.sqlite.toString(),
            DbType.postgresql.toString(),
            DbType.h2.toString()

    );




    public TreeItem<Node> getSqlFormatTreeItem(){
        TreeItem<Node> jsonFormat = new TreeItem<>(ViewUtil.getTreeItem(MenuTypeEnum.SQL_FORMAT.getType(), Constants.SQL_FORMAT));
        jsonFormat.getValue().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                    //设置为tabPane
                    SQLFormatController jsonFormatController = ControllerMangerUtil.getSQLFormatController();
                    IndexController indexController = ControllerMangerUtil.getIndexController();
                    indexController.getIndexPane().setCenter(jsonFormatController.getSqlAnchorPane());
                }
            }
        });
        return jsonFormat;
    }

    public void setDBType(ChoiceBox<String> sqlBox){
        ObservableList<String> dbType = FXCollections.observableArrayList(dbTypes);
        sqlBox.setItems(dbType);
        sqlBox.getSelectionModel().select(0);
    }

    public void formatSql(TextArea rawSql,TextArea formatSql,ChoiceBox<String> sqlBox){
        if (StringUtils.isNotEmpty(rawSql.getText())){
            String dbType = sqlBox.getSelectionModel().getSelectedItem();
            try {
                String sql = SQLFormatUtils.format(rawSql.getText(), DbType.valueOf(dbType));
                formatSql.setText(sql);
                rawSql.setText(sql);
            }catch (Exception e){
                log.error("sql format error:",e);
                formatSql.setText(e.getMessage());
            }
        }
    }
}
