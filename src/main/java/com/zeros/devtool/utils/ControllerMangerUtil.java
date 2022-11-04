package com.zeros.devtool.utils;

import com.zeros.devtool.constants.FxmlConstant;
import com.zeros.devtool.controller.network.SwitchHostController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerMangerUtil {

    private static final Map<String, Initializable> controller= new ConcurrentHashMap<>();

    public static Initializable getController(String key){
        return controller.get(key);
    }

    public static void setController(String key,Initializable initializable){
         controller.put(key,initializable);
    }
}
