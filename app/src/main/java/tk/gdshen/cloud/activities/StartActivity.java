package tk.gdshen.cloud.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

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

        //作为启动界面跳转到主界面
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run() {
                Intent mainIntent = new Intent(StartActivity.this,AlbumActivity.class);
                StartActivity.this.startActivity(mainIntent);
                StartActivity.this.finish();
            }

        }, SPLASH_DISPLAY_LENGTH);
    }
}