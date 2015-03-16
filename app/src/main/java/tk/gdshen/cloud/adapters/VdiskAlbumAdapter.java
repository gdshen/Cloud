package tk.gdshen.cloud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import tk.gdshen.cloud.R;

/**
 * Created by gdshen on 15-3-16.
 */
public class VdiskAlbumAdapter extends BaseAdapter {
    private ArrayList<String> stringArrayList;
    private Context mContext;

    public VdiskAlbumAdapter(ArrayList<String> stringArrayList, Context mContext) {
        this.stringArrayList = stringArrayList;
        this.mContext = mContext;
    }


    @Override

    public int getCount() {
        return stringArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return stringArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String filePath = stringArrayList.get(i);

        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_grid_item, viewGroup, false);
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Picasso.with(mContext).load(new File(filePath)).centerCrop().fit().into(imageView);
        return view;
    }

    private static class ViewHolder {
        String path;
    }
}
