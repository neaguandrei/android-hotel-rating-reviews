package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import ratingapp.ddey.com.testratingapp.R;

//pentru a face Viewul de informatie al locatiei custom
public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;
    private final View window;

    public CustomInfoWindowAdapter(Context context) {
        this.context = context;
        this.window = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    private void initializeWindowInformation(Marker marker, View view) {
        String title = marker.getTitle();
        TextView tvTitle = view.findViewById(R.id.custom_tv_title);

        if (!title.equals(""))
            tvTitle.setText(title);

        String snippet = marker.getSnippet();
        TextView tvSnippet = view.findViewById(R.id.custom_tv_snippet);

        if (!snippet.equals(""))
            tvSnippet.setText(snippet);
    }
    @Override
    public View getInfoWindow(Marker marker) {
        initializeWindowInformation(marker, window);
        return window;
    }

    @Override
    public View getInfoContents(Marker marker) {
        initializeWindowInformation(marker, window);
        return window;
    }
}
