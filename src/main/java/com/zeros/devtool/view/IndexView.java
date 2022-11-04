package com.zeros.devtool.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import lombok.Data;

@Data
public  abstract class IndexView  implements Initializable {


    @FXML
    protected TreeView<Label> rootTree;


    @FXML
    protected TextField searchTex;


    @FXML
    protected BorderPane indexPane;



}
