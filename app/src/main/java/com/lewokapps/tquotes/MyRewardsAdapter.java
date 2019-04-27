package com.lewokapps.tquotes;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lewokapps.tquotes.model.RewardModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyRewardsAdapter extends RecyclerView.Adapter<MyRewardsAdapter.ViewHolder> {

    private List<RewardModel> rewardModelList;


    private Boolean useMiniLayout = false;

    public MyRewardsAdapter(List<RewardModel> rewardModelList, Boolean useMiniLayout) {
        this.rewardModelList = rewardModelList;
        this.useMiniLayout = useMiniLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;

        if (useMiniLayout) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mini_rewards_item_layout, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rewards_item_layout, viewGroup, false);
        }

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        String type = rewardModelList.get(position).getType();
        Date validity = rewardModelList.get(position).getTimestamp();
        String body = rewardModelList.get(position).getCouponBody();
        String couponTitleReward = rewardModelList.get(position).getCouponTitleReward();
        String lowerLimit = rewardModelList.get(position).getLowerLimit();
        String upperLimit = rewardModelList.get(position).getUpperLimit();
        String discORamt = rewardModelList.get(position).getDiscORamt();

        viewHolder.setData(type, validity, body, upperLimit, lowerLimit, discORamt, couponTitleReward);
    }

    @Override
    public int getItemCount() {
        return rewardModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView couponTitle;
        private TextView couponExpirydDate;
        private TextView couponBody;
        private ImageView couponIconViewRewards;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            couponTitle = itemView.findViewById(R.id.coupon_title);
            couponExpirydDate = itemView.findViewById(R.id.coupon_validity);
            couponBody = itemView.findViewById(R.id.coupon_body);
            couponIconViewRewards = itemView.findViewById(R.id.coupon_icon);

        }

        private void setData(final String type, final Date validity, final String body, String upperLimit, String lowerLimit, String discORamt, final String couponTitleReward) {


            couponTitle.setText(couponTitleReward);


            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MMM/YYYY");

            if (type.contains("time")) {
                couponExpirydDate.setText(lowerLimit + simpleDateFormat.format(validity) + upperLimit);

            } else {
                couponExpirydDate.setText(lowerLimit + type + upperLimit);
            }

            if (type.contains("Update")) {

                couponIconViewRewards.setImageResource(R.drawable.ic_touch_app);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.lewokapps.tquotes"));
                        itemView.getContext().startActivity(intent);
                    }
                });
            }

            if (type.contains("Location")) {

                couponIconViewRewards.setImageResource(R.drawable.ic_touch_app);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse("https://goo.gl/maps/scQzbUgu9PdkSyJ5A"));
                        itemView.getContext().startActivity(intent);
                    }
                });
            }

            if (type.contains("Feedback")) {

                couponIconViewRewards.setImageResource(R.drawable.ic_touch_app);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "canteengeca@gmail.com"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback T Quotes");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "T Quotes app");
//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

                        itemView.getContext().startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
                    }
                });
            }

            couponBody.setText(body);

            if (useMiniLayout) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProductDetailsActivity.couponTitle.setText(type);
                        ProductDetailsActivity.couponExpiryDate.setText(simpleDateFormat.format(validity));
                        ProductDetailsActivity.couponBody.setText(body);
                        ProductDetailsActivity.showDialogRecyclerView();
                    }
                });
            }

        }
    }
}






