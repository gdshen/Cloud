package tk.gdshen.cloud.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.util.Random;

import tk.gdshen.cloud.R;
import tk.gdshen.cloud.helpers.Constants;

public class StartActivity extends Activity {


    private static final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        //初始化一些全局变量
        Constants.fileList = Constants.getFileList();

        fileCreator();

        SharedPreferences sharedPreferences= getSharedPreferences("test",
                Activity.MODE_PRIVATE);
        String name =sharedPreferences.getString("key", null);
        if(name == null) {
            TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            String imei = telephonyManager.getDeviceId();
//        Log.d(Constants.TAG, imei);
            Random random = new Random(System.currentTimeMillis());
            for (int i = 0; i < 5; i++) {
                imei += random.nextInt(10);
            }
            Log.d(Constants.TAG, imei);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("key", imei);

            editor.apply();
            Constants.key = imei;

        } else {
            Constants.key = name;
        }


        //作为启动界面跳转到主界面
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent mainIntent = new Intent(StartActivity.this, AlbumActivity.class);
                StartActivity.this.startActivity(mainIntent);
                StartActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGTH);
    }

    private void fileCreator(){

        //在初始化的代码中加入对于应用程序相对应的文件夹是否存在的检测，如果不存在，就新建一个
        File cloudDirectory = new File(Constants.cloud);
        File thumbnail = new File(Constants.localThumbnail);
        File imageDetail = new File(Constants.localDetailImage);

        if( !cloudDirectory.exists()){
            cloudDirectory.mkdirs();
            Log.d(Constants.TAG, Constants.cloud + "已完成创建");
        }
        else{
            Log.d(Constants.TAG,Constants.cloud + "文件夹已经创建");
        }

        if( !thumbnail.exists()){
            thumbnail.mkdirs();
            Log.d(Constants.TAG, Constants.localThumbnail + "已完成创建");
        }
        else{
            Log.d(Constants.TAG,Constants.localThumbnail + "文件夹已经创建");
        }

        if( !imageDetail.exists()){
            imageDetail.mkdirs();
            Log.d(Constants.TAG, Constants.localDetailImage + "已完成创建");
        }
        else{
            Log.d(Constants.TAG,Constants.localDetailImage + "文件夹已经创建");
        }

    }
}