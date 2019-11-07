package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.ui.activities.ViewPublicProfileActivity;
import ratingapp.ddey.com.testratingapp.models.Review;
import ratingapp.ddey.com.testratingapp.models.User;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.database.FirebaseHelper;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {
    private List<Review> mList;
    private Context mContext;
    private Activity mActivity;
    private FirebaseHelper mFirebaseHelper;

    public ReviewAdapter(Context context, List<Review> list, Activity activity) {
        mList = list;
        mContext = context;
        mActivity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivIsPositive;
        private TextView tvIsPositive;
        private TextView tvReviewTitle;
        private TextView tvDate;
        private TextView tvUserName;
        private TextView tvDescription;
        private ImageView ivViewProfile;

        private ViewHolder(View itemView) {
            super(itemView);
            ivIsPositive = itemView.findViewById(R.id.hotelreviews_iv_isPositive);
            tvIsPositive = itemView.findViewById(R.id.hotelreviews_tv_isPositive);
            tvReviewTitle = itemView.findViewById(R.id.hotelreviews_tv_title);
            tvDate = itemView.findViewById(R.id.hotelreviews_tv_date);
            tvUserName = itemView.findViewById(R.id.hotelreviews_tv_userName);
            tvDescription = itemView.findViewById(R.id.hotelreviews_tv_description);
            ivViewProfile = itemView.findViewById(R.id.hotelreviews_iv_viewprofile);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_hotels_reviews, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        final Review model = mList.get(position);

        if (model != null) {
            if (model.isPositive()) {
                viewHolder.ivIsPositive.setImageResource(R.drawable.ic_thumb_up);
                viewHolder.tvIsPositive.setText("Positive review");
            } else {
                viewHolder.ivIsPositive.setImageResource(R.drawable.ic_thumb_down);
                viewHolder.tvIsPositive.setText("Negative review");
            }

            if (model.getTitle() != null && !model.getTitle().trim().isEmpty()) {
                viewHolder.tvReviewTitle.setText(model.getTitle());
            }
            if (model.getDate() != null) {
                viewHolder.tvDate.setText(Constants.simpleDateFormat.format(model.getDate()));
            }
            if (model.getUserToken() != null) {
                mFirebaseHelper = FirebaseHelper.getInstance();
                mFirebaseHelper.openConnection();
                mFirebaseHelper.getUsersReference().child(model.getUserToken()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null && user.getName() != null) {
                            viewHolder.tvUserName.setText(user.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            if (model.getDescription() != null) {
                viewHolder.tvDescription.setText(model.getDescription());
            }

            viewHolder.ivViewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadUserFromFirebase(model.getUserToken());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void loadUserFromFirebase(String userToken) {
        mFirebaseHelper.openConnection();
        mFirebaseHelper.getUsersReference().child(userToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.isProfilePublic()) {
                        Intent intent = new Intent(mActivity, ViewPublicProfileActivity.class);
                        intent.putExtra(Constants.VIEW_PROFILE_KEY, user);
                        mActivity.startActivity(intent);
                    } else {
                        Toast.makeText(mActivity, "That account is private!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(mActivity, "User doesn't exist! Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
