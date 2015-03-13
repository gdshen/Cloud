package tk.gdshen.cloud.helpers;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gdshen on 15-3-13.
 */
public class Constants {

    public final static String TAG = "SGD_Debuging";

    public static ArrayList<String> fileList;

    public static ArrayList<String> getFileList() {
        ArrayList<String> stringArrayList = new ArrayList<>();
        String imageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM";
        File fileList = new File(imageDirectory);
        for (File file : fileList.listFiles()) {
            if (file.getName().endsWith("jpg") && !file.isDirectory()) {
                stringArrayList.add(file.getAbsolutePath());
            }
        }
        return stringArrayList;
    }
}
