package com.zeros.devtool.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.VBox;
import lombok.Data;

@Data
public  abstract class IndexView  implements Initializable {


    @FXML
    protected TreeView<Node> rootTree;


    @FXML
    protected TextField searchTex;


    @FXML
    protected BorderPane indexPane;

    @FXML
    protected SplitPane indexSplitPane;


    @FXML
    protected VBox indexVBox;

}
