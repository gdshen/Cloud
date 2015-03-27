package tk.gdshen.cloud.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import java.util.Random;

import tk.gdshen.cloud.R;
import tk.gdshen.cloud.helpers.Constants;
import tk.gdshen.cloud.helpers.LargeFileUpload;
import tk.gdshen.cloud.helpers.TransformHelper;

public class TransformAndUploadActivity extends ActionBarActivity implements VDiskDialogListener {

    VDiskAuthSession session;
    AppKeyPair appKeyPair;

    VDiskAPI.Account account;
    VDiskAPI<VDiskAuthSession> mApi;
    boolean tranformState = true;

    public final int REQUEST_CODE_IMAGE_SECRET = 1;

    public final int REQUEST_CODE_IMAGE_COVER = 2;

    ImageView secretImage;
    ImageView coverImage;
    TextView secretTextView;

    String secretPath;
    String coverPath;
    String tranformedFilePath = Constants.cloud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transform_and_upload);
        coverImage = (ImageView) findViewById(R.id.coverImage);
        secretImage = (ImageView) findViewById(R.id.secretImage);
        secretTextView = (TextView) findViewById(R.id.textView2);
        appKeyPair = new AppKeyPair(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        session = VDiskAuthSession.getInstance(this, appKeyPair, Session.AccessType.APP_FOLDER);
        session.setRedirectUrl(Constants.REDIRECT_URL);
        if (!session.isLinked()) {
            session.authorize(TransformAndUploadActivity.this, TransformAndUploadActivity.this);
        }

        mApi = new VDiskAPI<>(session);
        final Button button = (Button) findViewById(R.id.upload_button);

        final Random random = new Random(System.currentTimeMillis());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String srcPath = Constants.fileList.get(0);
//                Log.d(Constants.TAG, srcPath);
//                String desPath = "/picture";
//                uploadLargeFile(srcPath, desPath);
                if (tranformState) {
                    tranformState = !tranformState;
                    tranformedFilePath += "/" + random.nextInt(10000)+".jpg";
                    Log.d("transformFIlePath", tranformedFilePath);
                    TransformHelper.encryptJPEG(secretPath, coverPath, tranformedFilePath, "1");
                    // 执行transform变换函数
                    Picasso.with(getApplicationContext()).load(new File(tranformedFilePath)).centerCrop().fit().into(secretImage);
                    secretTextView.setText("变换后的图片");

                    button.setText(R.string.button_upload);
                } else {
                    tranformState = !tranformState;
                    String desPath = "/";
                    uploadLargeFile(tranformedFilePath,desPath);
                }
            }
        });

        Picasso.with(this).load(R.mipmap.ic_launcher).into(secretImage);
        secretImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "点击了选择秘密图片", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_IMAGE_SECRET);
            }
        });

        Picasso.with(this).load(R.mipmap.ic_launcher).into(coverImage);
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "点击了选择掩体图片", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_IMAGE_COVER);
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
     * <p/>
     * Upload a large file
     *
     * @param srcPath 本地文件的路径 Source path of local file
     * @param desPath 云端目标文件的文件夹路径 Target directory path of file in the cloud
     */
    private void uploadLargeFile(String srcPath, String desPath) {
        File file = new File(srcPath);
        LargeFileUpload upload = new LargeFileUpload(this, mApi, desPath, file);
        upload.execute();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                Log.d("get path", path);
                switch (requestCode){
                    case REQUEST_CODE_IMAGE_SECRET: {
                        secretPath = path;
                        Picasso.with(this).load(new File(path)).centerCrop().fit().into(secretImage);
                        break;
                    }
                    case REQUEST_CODE_IMAGE_COVER: {
                        coverPath = path;
                        Picasso.with(this).load(new File(path)).centerCrop().fit().into(coverImage);
                        break;
                    }
                }
            }
        }
    }
}
