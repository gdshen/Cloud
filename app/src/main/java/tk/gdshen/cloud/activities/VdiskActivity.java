package tk.gdshen.cloud.activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.vdisk.android.VDiskAuthSession;
import com.vdisk.android.VDiskDialogListener;
import com.vdisk.net.VDiskAPI;
import com.vdisk.net.exception.VDiskDialogError;
import com.vdisk.net.exception.VDiskException;
import com.vdisk.net.session.AccessToken;
import com.vdisk.net.session.AppKeyPair;
import com.vdisk.net.session.Session;

import tk.gdshen.cloud.R;
import tk.gdshen.cloud.helpers.Constants;

public class VdiskActivity extends ActionBarActivity implements VDiskDialogListener {

    VDiskAuthSession session;
    AppKeyPair appKeyPair;

    VDiskAPI.Account account;
    VDiskAPI<VDiskAuthSession> mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vdisk);

        //todo 进行微盘认证,并测试上传
        appKeyPair = new AppKeyPair(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        session = VDiskAuthSession.getInstance(this,appKeyPair,Session.AccessType.APP_FOLDER);
        session.setRedirectUrl(Constants.REDIRECT_URL);
        session.authorize(VdiskActivity.this, VdiskActivity.this);

        mApi = new VDiskAPI<>(session);
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
}
