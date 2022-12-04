package com.zeros.devtool.view.format;


import com.zeros.devtool.control.SearchCodeArea;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import lombok.Data;



@Data
public abstract class SQLFormatView implements Initializable {

    @FXML
    protected AnchorPane sqlAnchorPane;


    @FXML
    protected ChoiceBox<String> sqlBox;


    @FXML
    protected Button cleanSql;


    @FXML
    protected Button clearFormatSql;

    @FXML
    protected Button pasteSql;

    @FXML
    protected SearchCodeArea rawSql;

    @FXML
    protected Button copySql;

    @FXML
    protected SearchCodeArea formatSql;


}
