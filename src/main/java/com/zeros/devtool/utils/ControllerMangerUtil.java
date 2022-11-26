package com.zeros.devtool.utils;

import com.zeros.devtool.constants.FxmlConstant;
import com.zeros.devtool.controller.format.JsonFormatController;
import com.zeros.devtool.controller.format.SQLFormatController;
import com.zeros.devtool.controller.format.XMLFormatController;
import com.zeros.devtool.controller.index.IndexController;
import com.zeros.devtool.controller.network.SwitchHostController;
import com.zeros.devtool.controller.system.PortCheckController;
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


    public static SwitchHostController getSwitchHostController() {
        SwitchHostController controller = (SwitchHostController) ControllerMangerUtil.getController(SwitchHostController.class.getName());
        if (controller == null) {
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.SWITCH_HOST);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller = fxmlLoader.getController();
            ControllerMangerUtil.setController(SwitchHostController.class.getName(), controller);
        }
        return controller;
    }


    public static IndexController getIndexController() {
        IndexController controller = (IndexController) ControllerMangerUtil.getController(IndexController.class.getName());
        return controller;
    }

    public static JsonFormatController getJsonFormatController() {
        JsonFormatController controller = (JsonFormatController) ControllerMangerUtil.getController(JsonFormatController.class.getName());
        if (controller == null) {
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.JSON_FORMAT);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller = fxmlLoader.getController();
            ControllerMangerUtil.setController(JsonFormatController.class.getName(), controller);
        }
        return controller;
    }

    public static PortCheckController getPortCheckController() {
        PortCheckController controller = (PortCheckController) ControllerMangerUtil.getController(PortCheckController.class.getName());
        if (controller == null) {
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.PORT_CHECK);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller = fxmlLoader.getController();
            ControllerMangerUtil.setController(PortCheckController.class.getName(), controller);
        }
        return controller;
    }

    public static SQLFormatController getSQLFormatController() {
        SQLFormatController controller = (SQLFormatController) ControllerMangerUtil.getController(SQLFormatController.class.getName());
        if (controller == null) {
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.SQL_FORMAT);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller = fxmlLoader.getController();
            ControllerMangerUtil.setController(SQLFormatController.class.getName(), controller);
        }
        return controller;
    }

    public static XMLFormatController getXMLFormatController() {
        XMLFormatController controller = (XMLFormatController) ControllerMangerUtil.getController(XMLFormatController.class.getName());
        if (controller == null) {
            FXMLLoader fxmlLoader = FXMLLoaderUtils.getFXMLLoader(FxmlConstant.XML_FORMAT);
            try {
                fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            controller = fxmlLoader.getController();
            ControllerMangerUtil.setController(XMLFormatController.class.getName(), controller);
        }
        return controller;
    }
}
