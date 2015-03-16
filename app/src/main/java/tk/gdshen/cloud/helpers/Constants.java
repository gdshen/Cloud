package tk.gdshen.cloud.helpers;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by gdshen on 15-3-13.
 */
public class Constants {

    public final static String TAG = "SGD_Debuging";

    public static ArrayList<String> fileList;

    //微盘登录认证的部分常量
    public static final String CONSUMER_KEY = "3114730934";

    public static final String CONSUMER_SECRET = "ba91bc887e824a81d5ce584a85bdc9e1";

    public static final String REDIRECT_URL = "http://www.ustc.edu.cn";

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
