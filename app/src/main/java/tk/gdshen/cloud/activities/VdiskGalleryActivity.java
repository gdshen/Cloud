package tk.gdshen.cloud.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.vdisk.android.VDiskAuthSession;
import com.vdisk.net.VDiskAPI;
import com.vdisk.net.exception.VDiskException;
import com.vdisk.net.session.AppKeyPair;
import com.vdisk.net.session.Session;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tk.gdshen.cloud.R;
import tk.gdshen.cloud.adapters.VdiskAlbumAdapter;
import tk.gdshen.cloud.helpers.Constants;
import tk.gdshen.cloud.helpers.DownloadFile;

public class VdiskGalleryActivity extends ActionBarActivity {

    List<String> list; //微云上所有图片文件夹的路径
    ArrayList<String> directoryDetailList = new ArrayList<>();

    VDiskAuthSession session;
    AppKeyPair appKeyPair;

    VDiskAPI.Account account;
    VDiskAPI<VDiskAuthSession> mApi;

    VdiskAlbumAdapter vdiskAlbumAdapter;
    GridView gridView;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            vdiskAlbumAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vdisk_gallery);

        appKeyPair = new AppKeyPair(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        session = VDiskAuthSession.getInstance(this, appKeyPair, Session.AccessType.APP_FOLDER);

        mApi = new VDiskAPI<>(session);


        gridView = (GridView) findViewById(R.id.vdiskGridView);
        vdiskAlbumAdapter =
                new VdiskAlbumAdapter(directoryDetailList, getApplicationContext());
        gridView.setAdapter(vdiskAlbumAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String file = new File((String) adapterView.getItemAtPosition(i)).getName();
                file = "/" +file;
                File fileDetail = new File(Constants.localDetailImage + file);
                if(!fileDetail.exists()) {
                    DownloadFile downloadFile = new DownloadFile(VdiskGalleryActivity.this, mApi, file, Constants.localDetailImage + "/" + file);
                    downloadFile.execute();
                }else {
                    Intent intent = new Intent(getApplicationContext(), VdiskDetailActivity.class);
                    intent.putExtra("filePath", file);
                    startActivity(intent);
                }
            }
        });

        list = getIntent().getStringArrayListExtra("result"); //这里list是在微云上所有文件的名字
        for (int i = 0; i < list.size(); i++) {
            directoryDetailList.add(Constants.localThumbnail+"/" + list.get(i));
            downloadThumbnail(list.get(i));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vdisk_gallery, menu);
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

    private void downloadThumbnail(final String path) {

        new Thread() {
            @Override
            public void run() {
                FileOutputStream mFos = null;
                try {
                    String cachePath = Constants.localThumbnail + "/"
                            + path;
                    File file = new File(cachePath);
                    file.createNewFile();

                    mFos = new FileOutputStream(cachePath, false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    mApi.getThumbnail(Constants.CLOUDPICTURE + path, mFos, VDiskAPI.ThumbSize.ICON_100x100, null);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    msg.setData(data);
                    handler.sendMessage(msg);
                } catch (VDiskException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();
    }


}
