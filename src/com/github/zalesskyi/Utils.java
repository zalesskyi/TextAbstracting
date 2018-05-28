package com.github.zalesskyi;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.github.zalesskyi.Utils.Constants.LOG_FILE_NAME;


public class Utils {
    public static class Constants {
        public static final String NEW_PARAGRAPH_MARKER_REGEX = "\\$_\\$_\\$";
        public static final String NEW_PARAGRAPH_MARKER = "$_$_$";

        public static final String LOG_FILE_NAME = "D://textAbstact_logs.txt";
    }

    private static FileOutputStream logStream;

    static {
        try {
            logStream = new FileOutputStream(LOG_FILE_NAME, false);
        } catch (FileNotFoundException exc) {
            exc.printStackTrace();
        }
    }

    public static final void log(String logMessage) {
        System.out.println(logMessage);
        logMessage += "\r\n";
        try {
            logStream.write(logMessage.getBytes());
        } catch (IOException exc) {
            exc.printStackTrace();
        }
    }

    public static final void errLog(String errMessage) {
        System.err.println(errMessage);
    }
}
