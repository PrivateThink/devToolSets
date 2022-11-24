package com.zeros.devtool.view.format;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.Data;

@Data
public abstract class XMLFormatView implements Initializable {

    @FXML
    protected AnchorPane xmlAnchorPane;

    @FXML
    protected HBox xmlHox;

    @FXML
    protected Button pasteXml;

    @FXML
    protected Button copyXml;

    @FXML
    protected Button cleanXml;

    @FXML
    protected TextArea rawXML;

    @FXML
    protected Button clearFormatXML;

    @FXML
    protected Button copyXML;

    @FXML
    protected TextArea formatXML;
}
