package com.zeros.devtool.constants;

import java.io.File;

public class FileConstants {

    public static final String USER_HOME = System.getProperty("user.home");

    public static final String DEV_TOOL_SETS = "devToolSets";

    public static final String WIN_HOST = "C://WINDOWS//system32//drivers//etc//hosts";

    public static final String MAC_HOST = "/etc/hosts";

    public static final String NETWORK = "network";

    public static final String HOST = "host";

    public static final String NETWORK_PATH = USER_HOME + File.separator + DEV_TOOL_SETS + File.separator + NETWORK;

    public static final String HOST_PATH = NETWORK_PATH + File.separator + HOST;
}
