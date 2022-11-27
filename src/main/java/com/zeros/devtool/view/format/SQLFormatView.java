package com.zeros.devtool.view.format;

import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import lombok.Data;
import org.fxmisc.richtext.CodeArea;


@Data
public abstract class SQLFormatView implements Initializable {

    @FXML
    protected AnchorPane sqlAnchorPane;

    @FXML
    protected HBox sqlHBox;

    @FXML
    protected ChoiceBox<String> sqlBox;

    @FXML
    protected Label mysqlType;

    @FXML
    protected Button cleanSql;


    @FXML
    protected Button clearFormatSql;

    @FXML
    protected Button pasteSql;

    @FXML
    protected CodeArea rawSql;

    @FXML
    protected Button copySql;

    @FXML
    protected CodeArea formatSql;


    @FXML
    protected BorderPane sqlLeftBorderPane;

    @FXML
    protected BorderPane sqlRightBorderPane;
}
