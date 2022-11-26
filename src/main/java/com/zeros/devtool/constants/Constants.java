package com.zeros.devtool.constants;

import java.util.regex.Pattern;

public class Constants {

    public static final String CONFIG_RESOURCE="locale.config";



    public static final String KEYWORD_PATTERN =
            "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\b";

    public static final String COMMENT_PATTERN = "#[^\n]*";

    public static final Pattern PATTERN = Pattern
            .compile("(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");





    public static final String  ALL ="全部";

    //===========================network===================================
    public static final String  NETWORK ="网络";
    public static final String  SWITCH_HOST ="HOST";
    public static final String  CURRENT_HOST ="系统当前的HOST";
    public static final String  ADD_HOST ="+";


    //========================== text =============================
    public static final String  TEXT ="文本";

    public static final String  CLIPBOARD_HISTORY ="剪切板记录";

    public static final String  MEMORANDUM ="备忘录";

    //=======================格式化================================

    public static final String  FORMAT ="格式化";

    public static final String  JSON_FORMAT ="JSON格式化";
    public static final String  SQL_FORMAT ="SQL格式化";
    public static final String  XML_FORMAT ="XML格式化";
    public static final String  HTML_FORMAT ="HTML格式化";



    //=======================系统================================

    public static final String  SYSTEM ="系统";
    public static final String  PORT_CHECK ="端口列表";

}
