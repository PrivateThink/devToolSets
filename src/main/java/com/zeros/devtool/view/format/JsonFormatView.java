package com.zeros.devtool.view.format;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import lombok.Data;
import org.fxmisc.richtext.CodeArea;

@Data
public abstract class JsonFormatView implements Initializable {

    @FXML
    protected TabPane tabPaneMain;

    @FXML
    protected CodeArea jsonText;

    @FXML
    protected CodeArea formatText;

    @FXML
    public HBox jsonHBox;

}
