package com.zeros.devtool.view.format;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import lombok.Data;

@Data
public abstract class JsonFormatView implements Initializable {

    @FXML
    protected TabPane tabPaneMain;

    @FXML
    protected TextArea jsonText;

    @FXML
    protected TextArea formatText;

}
