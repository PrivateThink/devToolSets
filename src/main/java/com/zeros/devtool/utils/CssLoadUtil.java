package com.zeros.devtool.utils;

import com.zeros.devtool.DevToolSetsMain;

import java.net.URL;

public class CssLoadUtil {

    public static String getResourceUrl(String resourcePath) {
        URL resource = DevToolSetsMain.class.getResource(resourcePath);
        return resource == null ? null : resource.toExternalForm();
    }
}
