package com.zeros.devtool.view.format;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.Data;
import org.fxmisc.richtext.CodeArea;

@Data
public abstract class HTMLFormatView implements Initializable {


    @FXML
    protected AnchorPane htmlAnchorPane;

    @FXML
    protected HBox htmlHox;


    @FXML
    protected Button pasteHtml;


    @FXML
    protected Button copyHtml;


    @FXML
    protected Button cleanHtml;


    @FXML
    protected CodeArea rawHtml;

    @FXML
    protected Button clearFormatHtml;

    @FXML
    protected Button copyFormatHtml;

    @FXML
    protected CodeArea formatHtml;


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
