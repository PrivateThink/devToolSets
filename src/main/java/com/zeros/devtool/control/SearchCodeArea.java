package com.zeros.devtool.control;

import com.zeros.devtool.constants.FxmlConstant;
import com.zeros.devtool.utils.FXMLLoaderUtils;
import com.zeros.devtool.view.control.SearchCodeAreaView;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.IndexRange;
import javafx.scene.input.KeyCode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.Selection;
import org.fxmisc.richtext.SelectionImpl;
import org.fxmisc.richtext.model.TwoDimensional;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class SearchCodeArea extends SearchCodeAreaView {


    private int startIndex = 0;

    private List<Integer> searchIndexList;

    private Selection<Collection<String>, String, Collection<String>> selection;

    public SearchCodeArea() {
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.SEARCH_CODE_AREA);
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initService();
        initView();
        initEvent();
    }


    private void initService() {
        selection = new SelectionImpl<>("key word selection", codeArea);
        codeArea.addSelection(selection);
    }

    private void initView() {
        // 设置行号
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setWrapText(false);
    }

    private void initEvent() {
        //打开搜索框
        this.setOnKeyPressed(event -> {
            if ((event.isControlDown() || event.isMetaDown()) && event.getCode().equals(KeyCode.F)) {
                searchHBox.setVisible(true);
                searchHBox.setManaged(true);
                keyWordSearch.requestFocus();
            }
        });

        //关闭搜索框
        searchClose.setOnAction(event -> {
            searchHBox.setVisible(false);
            searchHBox.setManaged(false);
            codeArea.requestFocus();
            keyWordSearch.clear();
        });

        //文本变化查询
        codeArea.textProperty().addListener((observable, oldValue, newValue) -> {
            searchIndexList = searchIndexList(codeArea.getText(), keyWordSearch.getText());
            startIndex = -1;
            searchNext();
        });

        // 关键字变化查询
        keyWordSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            searchIndexList = searchIndexList(codeArea.getText(), newValue);
            startIndex = -1;
            searchNext();
        });

        //回车搜索
        keyWordSearch.setOnKeyReleased(event -> {
            if (KeyCode.ENTER.equals(event.getCode())) {
                if (event.isControlDown()) {
                    searchLast();
                } else {
                    searchNext();
                }
            }
        });

        //向上查询
        upButton.setOnAction(event -> {
            searchLast();
        });

        //向下查询
        downButton.setOnAction(event -> {
            searchNext();
        });

    }

    public List<Integer> searchIndexList(String text, String key) {
        List<Integer> indexList = new ArrayList<>();
        if (StringUtils.isBlank(text) || StringUtils.isBlank(key)) {
            return indexList;
        }

        int startIndex = -1;
        while ((startIndex = text.indexOf(key, startIndex + 1)) > -1) {
            indexList.add(startIndex);
        }
        ;
        return indexList;
    }

    public void keyWordSelect(int index) {
        if (index < 0 || CollectionUtils.isEmpty(searchIndexList)) {
            return;
        }
        int start = searchIndexList.get(index);
        int end = start + keyWordSearch.getText().length();
        selection.selectRange(start, end);
        TwoDimensional.Position position = codeArea.offsetToPosition(start, TwoDimensional.Bias.Forward);
        if (position.getMinor() < keyWordSearch.getText().length()) {
            codeArea.moveTo(start);
        } else {
            codeArea.moveTo(end);
        }
        codeArea.requestFollowCaret();
        searchNum.setText(index + 1 + "/" + searchIndexList.size());
    }

    public void searchNext() {
        if (CollectionUtils.isEmpty(searchIndexList)) {
            searchNum.setText("0/0");
            selection.deselect();
            return;
        }

        if (startIndex < searchIndexList.size() - 1) {
            startIndex++;
        } else {
            startIndex = 0;
        }

        keyWordSelect(startIndex);
    }

    public void searchLast() {
        if (CollectionUtils.isEmpty(searchIndexList)) {
            searchNum.setText("0/0");
            selection.deselect();
            return;
        }
        if (startIndex > 0) {
            startIndex--;
        } else {
            startIndex = searchIndexList.size() - 1;
        }
        keyWordSelect(startIndex);
    }
}
