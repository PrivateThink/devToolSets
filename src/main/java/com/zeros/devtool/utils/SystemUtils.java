package com.zeros.devtool.utils;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.constants.FileConstants;
import javafx.stage.Screen;

import javafx.geometry.Rectangle2D;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;


public class SystemUtils {

    private final static Logger logger = LoggerFactory.getLogger(SystemUtils.class);


    public static double[] getScreenSize(double width, double height) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double screenWidth = (double) screenSize.width * width;
        double screenHeight = (double) screenSize.height * height;
        Rectangle2D rectangle = Screen.getPrimary().getVisualBounds();
        if (screenWidth > rectangle.getWidth() || screenHeight > rectangle.getHeight()) {
            screenWidth = rectangle.getWidth();
            screenHeight = rectangle.getHeight();
        }

        return new double[]{screenWidth, screenHeight};
    }

    public static String getUserHome(){
        return FileConstants.USER_HOME;
    }


    public static String getHostContent(String fileName){
        String hostContent="";
        try {
            hostContent = FileUtils.readFileToString(new File(fileName), Charset.defaultCharset());
        } catch (IOException e) {
            logger.error("获取host失败:{}", e);
        }
        return hostContent;
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toUpperCase().contains("WINDOWS");
    }

}
