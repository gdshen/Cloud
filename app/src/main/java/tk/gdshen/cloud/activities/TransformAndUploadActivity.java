package tk.gdshen.cloud.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vdisk.android.VDiskAuthSession;
import com.vdisk.android.VDiskDialogListener;
import com.vdisk.net.VDiskAPI;
import com.vdisk.net.exception.VDiskDialogError;
import com.vdisk.net.exception.VDiskException;
import com.vdisk.net.session.AccessToken;
import com.vdisk.net.session.AppKeyPair;
import com.vdisk.net.session.Session;

import java.io.File;

import tk.gdshen.cloud.R;
import tk.gdshen.cloud.helpers.Constants;
import tk.gdshen.cloud.helpers.LargeFileUpload;

public class TransformAndUploadActivity extends ActionBarActivity  implements VDiskDialogListener{

    VDiskAuthSession session;
    AppKeyPair appKeyPair;

    VDiskAPI.Account account;
    VDiskAPI<VDiskAuthSession> mApi;
    boolean tranformState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_and_upload);

        appKeyPair = new AppKeyPair(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        session = VDiskAuthSession.getInstance(this,appKeyPair, Session.AccessType.APP_FOLDER);
        session.setRedirectUrl(Constants.REDIRECT_URL);
        if(!session.isLinked()) {
            session.authorize(TransformAndUploadActivity.this, TransformAndUploadActivity.this);
        }

        mApi = new VDiskAPI<>(session);
        final Button button = (Button) findViewById(R.id.upload_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String srcPath = Constants.fileList.get(0);
//                Log.d(Constants.TAG, srcPath);
//                String desPath = "/picture";
//                uploadLargeFile(srcPath, desPath);
                if(tranformState){
                    tranformState = !tranformState;
                    button.setText(R.string.button_upload);
                }
                else {
                    tranformState = !tranformState;
//                    button.setText(R.string.button_tranform);
                }
            }
        });
        ImageView secretImage = (ImageView) findViewById(R.id.secretImage);
        Picasso.with(this).load(R.mipmap.ic_launcher).into(secretImage);
        secretImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"点击了选择秘密图片",Toast.LENGTH_SHORT).show();
            }
        });
        ImageView coverImage = (ImageView) findViewById(R.id.coverImage);
        Picasso.with(this).load(R.mipmap.ic_launcher).into(coverImage);
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"点击了选择掩体图片",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transform_and_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onComplete(Bundle values) {
        if (values != null) {
            AccessToken mToken = (AccessToken) values
                    .getSerializable(VDiskAuthSession.OAUTH2_TOKEN);
            session.finishAuthorize(mToken);
        }
    }

    @Override
    public void onError(VDiskDialogError error) {

    }

    @Override
    public void onVDiskException(VDiskException exception) {

    }

    @Override
    public void onCancel() {

    }

    /**
     * 分段上传大文件
     *
     * Upload a large file
     *
     * @param srcPath
     *            本地文件的路径 Source path of local file
     * @param desPath
     *            云端目标文件的文件夹路径 Target directory path of file in the cloud
     */
    private void uploadLargeFile(String srcPath, String desPath) {
        File file = new File(srcPath);
        LargeFileUpload upload = new LargeFileUpload(this, mApi, desPath, file);
        upload.execute();
    }
}
