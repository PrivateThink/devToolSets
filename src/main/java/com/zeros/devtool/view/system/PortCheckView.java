package com.zeros.devtool.view.system;

import com.zeros.devtool.model.system.PortCheckInfo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import lombok.Data;

@Data
public abstract class PortCheckView implements Initializable {

    @FXML
    protected AnchorPane portPane;

    @FXML
    protected BorderPane borderPane;

    @FXML
    protected TextField portTextFile;

    @FXML
    protected Button findButton;

    @FXML
    protected TableColumn<PortCheckInfo, String> processName;

    @FXML
    protected TableColumn<PortCheckInfo, String> pid;

    @FXML
    protected TableColumn<PortCheckInfo, String> port;

    @FXML
    public TableColumn<PortCheckInfo, String> status;

    @FXML
    protected TableColumn<PortCheckInfo, Button> operator;

    @FXML
    protected TableView<PortCheckInfo> portTableView;
}
