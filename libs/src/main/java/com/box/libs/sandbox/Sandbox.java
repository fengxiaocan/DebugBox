package com.box.libs.sandbox;

import android.annotation.TargetApi;
import android.os.Build;

import com.box.libs.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by linjiang on 04/06/2018.
 */

public class Sandbox {

    private static String ROOT_PATH = Utils.getApplication().getApplicationInfo().dataDir;

    public static List<File> getRootFiles() {
        return getFiles(new File(ROOT_PATH));
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static List<File> getDPMFiles() {
        return getFiles(new File(Utils.getApplication().getApplicationInfo().deviceProtectedDataDir));
    }

    public static List<File> getFiles(File curFile) {
        List<File> descriptors = new ArrayList<>();
        if (curFile.isDirectory() && curFile.exists()) {
            descriptors.addAll(Arrays.asList(curFile.listFiles()));
        }
        return descriptors;
    }
}
