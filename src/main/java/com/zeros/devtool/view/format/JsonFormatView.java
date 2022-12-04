package com.zeros.devtool.view.format;

import com.zeros.devtool.control.SearchCodeArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import lombok.Data;


@Data
public abstract class JsonFormatView implements Initializable {

    @FXML
    protected TabPane tabPaneMain;

    @FXML
    protected SearchCodeArea jsonText;

    @FXML
    protected SearchCodeArea formatText;

    @FXML
    public HBox jsonHBox;

}
