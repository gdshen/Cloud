package tk.gdshen.cloud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.vdisk.android.VDiskAuthSession;
import com.vdisk.android.VDiskDialogListener;
import com.vdisk.net.VDiskAPI;
import com.vdisk.net.exception.VDiskDialogError;
import com.vdisk.net.exception.VDiskException;
import com.vdisk.net.session.AccessToken;
import com.vdisk.net.session.AppKeyPair;
import com.vdisk.net.session.Session;

import java.util.ArrayList;
import java.util.List;

import tk.gdshen.cloud.R;
import tk.gdshen.cloud.helpers.Constants;

public class VdiskActivity extends ActionBarActivity implements VDiskDialogListener {

    VDiskAuthSession session;
    AppKeyPair appKeyPair;

    VDiskAPI.Account account;
    VDiskAPI<VDiskAuthSession> mApi;

    private static final int SUCCEED = 0;
    private static final int FAILED = -1;
    private static final int SHOW_THUMBNAIL = 2;

    ArrayList<String> list;

    Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case SUCCEED:
                    showToast(msg.getData().getString("msg"));
                    break;
                case SHOW_THUMBNAIL:
//                    dialog.dismiss();
//                    if (mDrawable != null)
//                        showThumbnailDialog();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vdisk);


        appKeyPair = new AppKeyPair(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        session = VDiskAuthSession.getInstance(this, appKeyPair, Session.AccessType.APP_FOLDER);
        session.setRedirectUrl(Constants.REDIRECT_URL);
        if(!session.isLinked()) {
            session.authorize(VdiskActivity.this, VdiskActivity.this);
        }

        mApi = new VDiskAPI<>(session);
//        getAccountInfo(); 获取用户信息
        // todo 获取文件夹的信息
        getMetaData("/",0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vdisk, menu);
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

    private void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void getAccountInfo() {

        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    VDiskAPI.Account account = mApi.accountInfo();
                    msg.what = SUCCEED;
                    data.putString("msg", ":"
                            + account.quota + "\n"
                            + ":"
                            + account.consumed
                            + "\n" + ":"
                            + account.screen_name + "\n"
                            + ":"
                            + account.location + "\n"
                            + ":" + account.gender
                            + "\n"
                            + ":" + account.profile_image_url + "\n"
                            + ":"
                            + account.avatar_large);
                } catch (VDiskException e) {
                    e.printStackTrace();
                    msg.what = FAILED;
                    data.putSerializable("error", e);
                }
                msg.setData(data);
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 获取文件/文件夹原始信息
     *
     * Get information of file or directory
     *
     * @param path
     *            文件/文件夹路径,格式为 "/test/1.jpg",第一个"/"表示微盘根目录. Path of file or
     *            directory, format as "/test/1.jpg", and the first "/"
     *            represents the root directory of VDisk.
     * @param type
     *            0 表示获取该文件夹下所有文件列表信息;1 表示获取该文件/文件夹的原始信息. 0 represents to get
     *            information of the all file list in the directory; 1
     *            represents to get original information of the file or
     *            directory.
     */
    private void getMetaData(final String path, final int type) {

        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    VDiskAPI.Entry metadata = mApi.metadata(path, null, true, false);
                    List<VDiskAPI.Entry> contents = metadata.contents;

                    list = new ArrayList<String>();

                    if (contents != null && type == 0) {
                        for (VDiskAPI.Entry entry : contents) {
                            if (entry.isDir) {
//                                list.add(entry.fileName() + "("
//                                        + getString(R.string.adir) + ")");
                            } else {
                                list.add(entry.fileName());
                                Log.d(Constants.TAG, list.get(0));
                            }
                        }
                        startResultActivity(list);
                    } else {
                        // 不使用下载文件的信息,也就是type不使用1,只使用0
//                        list.add(getString(R.string.file_name) + ": "
//                                + metadata.fileName() + "\n"
//                                + getString(R.string.file_size) + ": "
//                                + metadata.size + "\n"
//                                + getString(R.string.edit_time) + ": "
//                                + metadata.modified + "\n"
//                                + getString(R.string.file_path) + ": "
//                                + metadata.path);
                    }
                } catch (VDiskException e) {
                    e.printStackTrace();
                    msg.what = FAILED;
                    data.putSerializable("error", e);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void startResultActivity(ArrayList<String> list) {
        Intent intent = new Intent();
        intent.putExtra("result", list);
        intent.setClass(this, VdiskGalleryActivity.class);
        startActivity(intent);
    }

}
