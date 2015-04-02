package tk.gdshen.cloud.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.vdisk.android.VDiskAuthSession;
import com.vdisk.net.VDiskAPI;
import com.vdisk.net.session.AppKeyPair;
import com.vdisk.net.session.Session;

import java.io.File;

import tk.gdshen.cloud.R;
import tk.gdshen.cloud.helpers.Constants;
import tk.gdshen.cloud.helpers.TransformHelper;

public class VdiskDetailActivity extends ActionBarActivity {

    VDiskAuthSession session;
    AppKeyPair appKeyPair;

    VDiskAPI.Account account;
    VDiskAPI<VDiskAuthSession> mApi;
    ImageView imageView;
    Button button;

    String filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vdisk_detail);
        getIntent().getStringExtra("filePath");

        appKeyPair = new AppKeyPair(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        session = VDiskAuthSession.getInstance(this, appKeyPair, Session.AccessType.APP_FOLDER);

        mApi = new VDiskAPI<>(session);
        mApi.createDownloadDirFile(Constants.localDetailImage);
        filePath = getIntent().getStringExtra("filePath");
        imageView = (ImageView) findViewById(R.id.vdiskImageView);
        Picasso.with(this).load(new File(Constants.localDetailImage + "/" +filePath)).centerCrop().fit().into(imageView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransformHelper.DecryptJPEG(Constants.localDetailImage + "/decrypt" + filePath,
                        Constants.localDetailImage + "/decrypt" + filePath, "1");
                Picasso.with(getApplicationContext()).
                        load(new File(Constants.localDetailImage + "/decrypt" + filePath)).centerCrop().fit().into(imageView);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vdisk_detail, menu);
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


}
