package com.zeros.devtool.view.control;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Data;
import org.fxmisc.richtext.CodeArea;

@Data
public abstract class SearchCodeAreaView extends VBox implements Initializable {



    @FXML
    protected CodeArea codeArea;

    @FXML
    protected HBox searchHBox;

    @FXML
    protected TextField keyWordSearch;

    @FXML
    protected Button downButton;

    @FXML
    protected Button upButton;

    @FXML
    protected Button searchClose;

    @FXML
    protected Label searchNum;


}
