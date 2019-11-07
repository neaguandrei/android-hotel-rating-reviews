package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.models.MyReviewsRatingsModel;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;

public class MyReviewsRatingsAdapter extends RecyclerView.Adapter<MyReviewsRatingsAdapter.ViewHolder> {
    private List<MyReviewsRatingsModel> mList;
    private Context mContext;
    private Activity mActivity;
    private FirebaseHelper mFirebaseHelper;

    public MyReviewsRatingsAdapter(Context context, List<MyReviewsRatingsModel> list, Activity activity) {
        mList = list;
        mContext = context;
        mActivity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvHotelName;
        private TextView tvCity;

        private View v1;
        private TextView tvReview;
        private TextView tvReviewTitle;
        private ImageView ivIsPositive;
        private TextView tvReviewDescription;

        private View v2;
        private TextView tvRating;
        private RatingBar rbRating;

        private ViewHolder(View itemView) {
            super(itemView);

            tvReview = itemView.findViewById(R.id.textView10);
            tvRating = itemView.findViewById(R.id.textView2);

            tvHotelName = itemView.findViewById(R.id.hotelreviews_tv_hotelname);
            tvCity = itemView.findViewById(R.id.hotelreviews_tv_city);

            v1 = itemView.findViewById(R.id.my_view1);
            tvReviewDescription = itemView.findViewById(R.id.my_review_description);
            ivIsPositive = itemView.findViewById(R.id.hotelreviews_iv_isPositive);
            tvReviewTitle = itemView.findViewById(R.id.hotelreviews_tv_title);

            v2 = itemView.findViewById(R.id.my_view2);
            rbRating = itemView.findViewById(R.id.my_rating);
        }
    }

    @NonNull
    @Override
    public MyReviewsRatingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_my_reviews_ratings, parent, false);
        return new MyReviewsRatingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        MyReviewsRatingsModel model = mList.get(position);

        if (model != null) {
            if (model.getHotelName() != null && !model.getHotelName().trim().isEmpty()) {
                viewHolder.tvHotelName.setText(model.getHotelName());
            }
            if (model.getCity() != null && !model.getCity().trim().isEmpty()) {
                viewHolder.tvCity.setText(model.getCity());
            }

            if (model.getReviewDescription()  == null || model.getReviewTitle() == null || model.getReviewTitle().trim().isEmpty() || model.getReviewDescription().trim().isEmpty()) {
                viewHolder.v1.setVisibility(View.GONE);
                viewHolder.ivIsPositive.setVisibility(View.GONE);
                viewHolder.tvReviewDescription.setVisibility(View.GONE);
                viewHolder.tvReviewTitle.setVisibility(View.GONE);
                viewHolder.tvReview.setVisibility(View.GONE);
            }
            else {
                if (model.isReviewPositive()) {
                    viewHolder.ivIsPositive.setImageResource(R.drawable.ic_thumb_up);
                } else {
                    viewHolder.ivIsPositive.setImageResource(R.drawable.ic_thumb_down);
                }
                if (model.getReviewTitle() != null && !model.getReviewTitle().trim().isEmpty()) {
                    viewHolder.tvReviewTitle.setText(model.getReviewTitle());
                }
                if (model.getReviewDescription() != null && !model.getReviewDescription().trim().isEmpty()) {
                    viewHolder.tvReviewDescription.setText(model.getReviewDescription());
                }
            }

            if (model.getRatingValue() == 0 || model.getRatingValue() == -1) {
                viewHolder.v2.setVisibility(View.GONE);
                viewHolder.rbRating.setVisibility(View.GONE);
                viewHolder.tvRating.setVisibility(View.GONE);
            } else {
                viewHolder.rbRating.setRating(model.getRatingValue());
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
