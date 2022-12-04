package com.zeros.devtool.view.format;

import com.zeros.devtool.control.SearchCodeArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import lombok.Data;


@Data
public abstract class XMLFormatView implements Initializable {

    @FXML
    protected AnchorPane xmlAnchorPane;


    @FXML
    protected Button pasteXml;

    @FXML
    protected Button copyXml;

    @FXML
    protected Button cleanXml;

    @FXML
    protected SearchCodeArea rawXML;

    @FXML
    protected Button clearFormatXML;

    @FXML
    protected Button copyFormatXML;

    @FXML
    protected SearchCodeArea formatXML;
}
