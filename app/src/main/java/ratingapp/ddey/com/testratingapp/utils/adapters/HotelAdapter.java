package ratingapp.ddey.com.testratingapp.utils.adapters;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import ratingapp.ddey.com.testratingapp.R;
import ratingapp.ddey.com.testratingapp.ui.activities.HotelSearchInformationActivity;
import ratingapp.ddey.com.testratingapp.models.Hotel;
import ratingapp.ddey.com.testratingapp.utils.others.Constants;
import ratingapp.ddey.com.testratingapp.utils.others.Session;
import ratingapp.ddey.com.testratingapp.utils.remote.ConnectionStatus;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {
    private List<Hotel> mList;
    private Context mContext;
    private Activity mActivity;
    private SparseBooleanArray expandState;

    public HotelAdapter(Context context, List<Hotel> list, Activity activity, SparseBooleanArray expandState) {
        mList = list;
        mContext = context;
        mActivity = activity;
        this.expandState = expandState;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivHotel;
        private TextView tvHotelName;
        private TextView tvDistanceFromCenter;
        private TextView tvPrice;
        private TextView tvRating;
        private RatingBar rbStars;
        private Button btnViewReviews;

        private View btnExpand;
        private ExpandableLinearLayout expandableLinearLayout;

        private ViewHolder(View itemView) {
            super(itemView);
            ivHotel = itemView.findViewById(R.id.search_layout_iv_hotel);
            tvHotelName = itemView.findViewById(R.id.search_layout_tv_name_hotel);
            tvDistanceFromCenter = itemView.findViewById(R.id.search_layout_tv_distance);
            tvRating = itemView.findViewById(R.id.search_layout_tv_rating_value);
            tvPrice = itemView.findViewById(R.id.search_layout_tv_price_per_night);
            rbStars = itemView.findViewById(R.id.search_layout_rb_stars);
            btnViewReviews = itemView.findViewById(R.id.expandable_btn_view_reviews);
            btnExpand = itemView.findViewById(R.id.btn_expand);
            expandableLinearLayout = itemView.findViewById(R.id.expandableLayout);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview_hotels_expandable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        final Hotel model = mList.get(position);

        viewHolder.setIsRecyclable(false);
        Picasso.with(mActivity)
                .load(model.getImageUrl())
                .placeholder(R.drawable.ic_image_black_24dp)
                .error(R.drawable.ic_error_black_24dp)
                .into(viewHolder.ivHotel);
        viewHolder.tvHotelName.setText(model.getHotelName());
        viewHolder.tvDistanceFromCenter.setText("Distance from city center " + String.valueOf(model.getDistanceFromCenter()) + " km");

        viewHolder.expandableLinearLayout.setInRecyclerView(true);
        viewHolder.expandableLinearLayout.setExpanded(expandState.get(position));
        viewHolder.expandableLinearLayout.setListener(new ExpandableLayoutListenerAdapter() {
            @Override
            public void onPreOpen() {
                changeRotate(viewHolder.btnExpand, 0f, 180f).start();
                expandState.put(position, true);
            }

            @Override
            public void onPreClose() {
                changeRotate(viewHolder.btnExpand, 180f, 0f).start();
                expandState.put(position, false);
            }
        });

        viewHolder.btnExpand.setRotation(expandState.get(position) ? 180f : 0f);
        viewHolder.btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.expandableLinearLayout.toggle();
            }
        });
        viewHolder.tvPrice.setText("Price per night starts from " + String.valueOf(model.getPrice()) + " RON");

        if (model.getRating() == 0f)
            viewHolder.tvRating.setText("No ratings for this hotel yet");
        else
            viewHolder.tvRating.setText(String.valueOf(model.getRating()));


        viewHolder.rbStars.setRating(model.getStars());

        viewHolder.btnViewReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyConnection()) {
                    Intent intent = new Intent(mActivity, HotelSearchInformationActivity.class);
                    intent.putExtra(Constants.VIEW_REVIEWS_RATINGS_KEY, mList.get(position));
                    mActivity.startActivity(intent);
                } else {
                    displayNoInternetDialog();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    private ObjectAnimator changeRotate(View btn, float from, float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(btn, "rotation", from, to);
        animator.setDuration(300);
        animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
        return animator;
    }

    public boolean verifyConnection() {
        ConnectionStatus connection = ConnectionStatus.getInstance(mActivity);
        return connection.isOnline();
    }

    private void displayNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                .setTitle("No internet connection found")
                .setMessage("You need to have mobile data or WiFi connection to access this. Press OK to return or go to Wi-Fi settings")
                .setPositiveButton("Internet settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        mActivity.finish();
                    }
                })
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mActivity.finish();
                    }
                });
        builder.create().show();
    }
}

