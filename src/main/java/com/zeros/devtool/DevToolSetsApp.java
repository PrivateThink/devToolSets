package com.zeros.devtool;


import com.zeros.devtool.constants.FxmlConstant;
import com.zeros.devtool.utils.FXMLLoaderUtils;
import com.zeros.devtool.utils.SystemUtils;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DevToolSetsApp  extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        Application.setUserAgentStylesheet(STYLESHEET_MODENA);
        FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.INDEX);
        Parent root = fxmlLoader.load();
        primaryStage.setResizable(true);
        double[] screenSize = SystemUtils.getScreenSize(0.7D, 0.8D);
        primaryStage.setScene(new Scene(root, screenSize[0], screenSize[1]));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }
}
