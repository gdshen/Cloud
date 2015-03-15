package tk.gdshen.cloud.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import tk.gdshen.cloud.R;
import tk.gdshen.cloud.adapters.AlbumAdapter;
import tk.gdshen.cloud.helpers.Constants;

public class AlbumActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        AlbumAdapter albumAdapter = new AlbumAdapter(Constants.fileList, getApplicationContext());
        gridView.setAdapter(albumAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String filePath = (String) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(AlbumActivity.this, LocalPhotoDetailActivity.class);
                intent.putExtra("filePath", filePath);
                startActivity(intent);
            }
        });

        FloatingActionButton connectVdiskButton = (FloatingActionButton) findViewById(R.id.connectVdiskButton);
        FloatingActionButton transformButton = (FloatingActionButton) findViewById(R.id.transformButton);

        connectVdiskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"点击了连接到我的微云", Toast.LENGTH_SHORT).show();
            }
        });

        transformButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"点击了进行变换", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album, menu);
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
