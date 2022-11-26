package com.zeros.devtool.controller.format;

import com.zeros.devtool.service.format.SQLFormatService;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.format.SQLFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.extern.slf4j.Slf4j;


import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class SQLFormatController extends SQLFormatView {


    private SQLFormatService sqlFormatService = new SQLFormatService();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initService();
        initView();
        initEvent();
    }

    private void initService(){
        ControllerMangerUtil.setController(SQLFormatController.this.getClass().getName(), this);
    }

    private void initView(){
        //设置数据库类型
        sqlFormatService.setDBType(sqlBox);
    }

    private void initEvent(){
        //内容变化监听
        rawSql.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                sqlFormatService.formatSql(rawSql,formatSql,sqlBox);
            }
        });

        sqlBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            sqlFormatService.formatSql(rawSql,formatSql,sqlBox);
        });

        //粘贴
        pasteSql.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                rawSql.setText(clipboard.getString());
            }
        });

        cleanSql.setOnAction(event -> {
            rawSql.setText("");
        });

        clearFormatSql.setOnAction(event -> {
            formatSql.setText("");
        });

        copySql.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putString(formatSql.getText());
            clipboard.setContent(clipboardContent);
            ToastUtil.toast("复制成功",2000);
        });
    }

}
