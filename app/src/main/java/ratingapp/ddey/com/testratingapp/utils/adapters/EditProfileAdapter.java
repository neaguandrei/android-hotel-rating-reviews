package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ratingapp.ddey.com.testratingapp.R;

public class EditProfileAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Integer> listId;
    private ArrayList<String> listTitles;


    public EditProfileAdapter(Context context, ArrayList<Integer> listId, ArrayList<String> listTitles) {
        this.context = context;
        this.listId = listId;
        this.listTitles = listTitles;
    }

    @Override
    public int getCount() {
        return listTitles.size();
    }

    @Override
    public Object getItem(int position) {
        return listTitles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null)
        {
            convertView = View.inflate(context, R.layout.listview_profile, null);
        }

        ImageView images = convertView.findViewById(R.id.imageViewList);
        TextView texts = convertView.findViewById(R.id.textViewList);

        images.setImageResource(listId.get(position));
        texts.setText(listTitles.get(position));

        return convertView;
    }
}
