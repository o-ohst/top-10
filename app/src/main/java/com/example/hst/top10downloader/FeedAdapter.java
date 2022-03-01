package com.example.hst.top10downloader;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by hst on 12/11/2017.
 */

public class FeedAdapter extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<FeedEntry> applications;

    public FeedAdapter(@NonNull Context context, int resource, List<FeedEntry> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @NonNull
    @Override
    //use convertView instead of new View to save memory
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView == null){
            Log.d(TAG, "getView: called with null convertView");
            convertView = layoutInflater.inflate(layoutResource, parent, false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            Log.d(TAG, "getView: called with provided convertView");
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //view that holds 3 textViews
        ///// View view = layoutInflater.inflate(layoutResource, parent, false);
        //finding within the container view(constraintLayout)
//        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
//        TextView tvArtist = (TextView) convertView.findViewById(R.id.tvArtist);
//        TextView tvGenre = (TextView) convertView.findViewById(R.id.tvGenre);

        FeedEntry currentApp = applications.get(position);

        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvGenre.setText(currentApp.getGenre());

        return convertView;
    }

    //class to hold the view to save using findviewbyid each time
    private class ViewHolder{
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvGenre;

        ViewHolder(View v){
            this.tvName = (TextView) v.findViewById(R.id.tvName);
            this.tvArtist = (TextView) v.findViewById(R.id.tvArtist);
            this.tvGenre = (TextView) v.findViewById(R.id.tvGenre);
        }
    }
}
