package com.zeros.devtool.controller.format;

import com.alibaba.druid.DbType;
import com.zeros.devtool.utils.ControllerMangerUtil;
import com.zeros.devtool.utils.SQLFormatUtils;
import com.zeros.devtool.utils.ToastUtil;
import com.zeros.devtool.view.format.SQLFormatView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
public class SQLFormatController extends SQLFormatView {


    private final List<String> dbTypes = List.of(
            DbType.mysql.toString(),
            DbType.oracle.toString(),
            DbType.sqlserver.toString(),
            DbType.sqlite.toString(),
            DbType.postgresql.toString(),
            DbType.h2.toString()

    );


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
        setDBType(sqlBox);

        // 设置行号
        rawSql.setParagraphGraphicFactory(LineNumberFactory.get(rawSql));
        // 设置行号
        formatSql.setParagraphGraphicFactory(LineNumberFactory.get(formatSql));

    }

    private void initEvent(){
        //内容变化监听
        rawSql.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                formatSql(rawSql,formatSql,sqlBox);
            }
        });

        sqlBox.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
                formatSql(rawSql,formatSql,sqlBox);
        });

        //粘贴
        pasteSql.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            if (clipboard.hasString()) {
                rawSql.replaceText(clipboard.getString());
            }
        });

        cleanSql.setOnAction(event -> {
            rawSql.replaceText("");
        });

        clearFormatSql.setOnAction(event -> {
            formatSql.replaceText("");
        });

        copySql.setOnAction(event -> {
            copyEvent(copySql);
        });
    }


    /**
     * 格式化 SQL
     * @param rawSql
     * @param formatSql
     * @param sqlBox
     */
    public void formatSql(CodeArea rawSql, CodeArea formatSql, ChoiceBox<String> sqlBox){
        if (StringUtils.isNotBlank(rawSql.getText())){
            String dbType = sqlBox.getSelectionModel().getSelectedItem();
            try {
                String sql = SQLFormatUtils.format(rawSql.getText(), DbType.valueOf(dbType));
                formatSql.replaceText(sql);
                rawSql.replaceText(sql);
            }catch (Exception e){
                log.error("sql format error:",e);
                formatSql.replaceText(e.getMessage());
            }
        }
    }

    private void copyEvent(Button copy){
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(copy.getText());
        clipboard.setContent(clipboardContent);
        ToastUtil.toast("复制成功",2000);
    }

    public void setDBType(ChoiceBox<String> sqlBox){
        ObservableList<String> dbType = FXCollections.observableArrayList(dbTypes);
        sqlBox.setItems(dbType);
        sqlBox.getSelectionModel().select(0);
    }
}
