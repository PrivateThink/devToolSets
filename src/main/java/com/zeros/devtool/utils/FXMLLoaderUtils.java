package com.zeros.devtool.utils;

import com.zeros.devtool.DevToolSetsMain;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.util.ResourceBundle;

public class FXMLLoaderUtils {

    public static FXMLLoader getFXMLLoader(String resource,String fxmlName) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(resource);
        URL url = DevToolSetsMain.class.getResource(fxmlName);
        FXMLLoader fXMLLoader = new FXMLLoader(url, resourceBundle);
        return fXMLLoader;
    }

    public static FXMLLoader getFXMLLoader(String fxmlName) {
        URL url = DevToolSetsMain.class.getResource(fxmlName);
        FXMLLoader fXMLLoader = new FXMLLoader(url);
        return fXMLLoader;
    }


}
