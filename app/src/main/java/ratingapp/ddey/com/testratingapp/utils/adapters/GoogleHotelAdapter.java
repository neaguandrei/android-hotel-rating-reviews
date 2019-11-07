package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.ui.activities.ReviewsActivity;
import ratingapp.ddey.com.testratingapp.utils.database.DatabaseConstants;

public class GoogleHotelAdapter extends RecyclerView.Adapter<GoogleHotelAdapter.HotelViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public long id;

    public GoogleHotelAdapter(Context mContext, Cursor mCursor)
    {
        this.mContext = mContext;
        this.mCursor = mCursor;
    }
    public class HotelViewHolder extends RecyclerView.ViewHolder
    {   public TextView nameText;
        public TextView addressText;
        public TextView phoneText;
        public ImageView imageView;
        public TextView tvGoogleRating;
        public Button btnReview;


        public HotelViewHolder(@NonNull final View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.rated_hotel_title);
            addressText = itemView.findViewById(R.id.rated_hotel_address);
            phoneText = itemView.findViewById(R.id.rated_hotel_phone);
            imageView = itemView.findViewById(R.id.image_hotel);
            btnReview = itemView.findViewById(R.id.rated_btn_reviews);
            tvGoogleRating = itemView.findViewById(R.id.rated_hotel_rating);

        }
    }
    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.recyclerview_google_hotels, viewGroup, false);

        return new HotelViewHolder(view);
    }

    //data is displayed in item
    @Override
    public void onBindViewHolder(@NonNull final HotelViewHolder hotelViewHolder, int i) {
        if (!mCursor.moveToPosition(i)) {
            return;
        }
        id = (long) mCursor.getInt(mCursor.getColumnIndex(DatabaseConstants.COLUMN_ID_GOOGLE_HOTEL));
        String name = mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.COLUMN_NAME_HOTEL));
        String address = mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.COLUMN_ADDRESS));
        String phone = mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.COLUMN_PHONE));
        String imgUrl = mCursor.getString(mCursor.getColumnIndex(DatabaseConstants.COLUMN_URL_HOTEL));
        float rating = mCursor.getFloat(mCursor.getColumnIndex(DatabaseConstants.COLUMN_GOOGLE_RATING));

        hotelViewHolder.nameText.setText(name);
        hotelViewHolder.addressText.setText(address);
        hotelViewHolder.phoneText.setText(phone);
        hotelViewHolder.tvGoogleRating.setText("Google rating " + String.valueOf(rating));

        if (imgUrl.startsWith("https://"))
            Picasso.with(mContext).load(imgUrl).into(hotelViewHolder.imageView);
        else
            Picasso.with(mContext).load(getPhotoOfPlace(imgUrl, 1000))
                    .error(R.drawable.ic_error_black_24dp)
                    .into(hotelViewHolder.imageView);

        hotelViewHolder.btnReview.setTag(i);
        hotelViewHolder.btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ReviewsActivity.class);
                //preiau tagul viewului pe care dau click (adica hotelID)
                intent.putExtra("hotelToken", (long)hotelViewHolder.itemView.getTag());
                mContext.startActivity(intent);
            }
        });
        //salvez hotelid ca tag pt itemview
        hotelViewHolder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


    public void swapCursor(Cursor newCursor)
    {
        if (mCursor != null)
            mCursor.close();
        mCursor = newCursor;

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
