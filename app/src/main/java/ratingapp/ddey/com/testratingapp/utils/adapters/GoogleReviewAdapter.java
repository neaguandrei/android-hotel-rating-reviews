package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseConstants;

public class GoogleReviewAdapter extends RecyclerView.Adapter<GoogleReviewAdapter.ReviewViewHolder> {
    private Context context;
    private Cursor cursor;

    public GoogleReviewAdapter(Context context, Cursor cursor)
    {
        this.context = context;
        this.cursor = cursor;
    }
    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView ivProfile;
        public TextView tvName;
        public TextView tvRating;
        public TextView tvTimeDescription;
        public TextView tvText;


        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.review_iv_profile);
            tvName = itemView.findViewById(R.id.review_author_name);
            tvRating = itemView.findViewById(R.id.review_author_rating);
            tvTimeDescription = itemView.findViewById(R.id.review_time_description);
            tvText = itemView.findViewById(R.id.review_text);
        }
    }
    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_google_reviews, viewGroup, false);
        return new ReviewViewHolder(view);
    }

    //data is displayed in item
    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder reviewViewHolder, int i) {
        if (!cursor.moveToPosition(i)) {
            return;
        }
        long id = cursor.getLong(cursor.getColumnIndex(DatabaseConstants.COLUMN_ID_REVIEW));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_NAME_AUTHOR));
        String rating = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_RATING));
        String time = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_TIME_DESCRIPTION));
        String text = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_TEXT_REVIEW));
        String img_url = cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_URL_REVIEW));


        reviewViewHolder.tvName.setText(name);
        reviewViewHolder.tvRating.setText("Rating: " + rating);
        reviewViewHolder.tvTimeDescription.setText("Posted: " + time);
        reviewViewHolder.tvText.setText(text);
        Picasso.with(context)
                .load(img_url)
                .placeholder(R.drawable.ic_profile)
                .into(reviewViewHolder.ivProfile);
        reviewViewHolder.itemView.setTag(id);
    }
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }


    public void swapCursor(Cursor newCursor)
    {
        if (cursor != null)
            cursor.close();
        cursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();;
        }
    }

    private String getPhotoOfPlace(String photoReference, int maxWidth) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth="+maxWidth);
        url.append("&photoreference="+photoReference);
        url.append("&key=AIzaSyB1IkhBtQhXItMjslFNp3Poy3_dPNdAFuE");

        return url.toString();
    }

}
