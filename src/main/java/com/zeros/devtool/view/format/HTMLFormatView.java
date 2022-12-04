package com.zeros.devtool.view.format;

import com.zeros.devtool.control.SearchCodeArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import lombok.Data;

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
    protected Button clearFormatHtml;

    @FXML
    protected Button copyFormatHtml;



    public SearchCodeArea formatHtmlArea;


    public SearchCodeArea rawHtmArea;
}
