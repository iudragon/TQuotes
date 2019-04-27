package com.lewokapps.tquotes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lewokapps.tquotes.model.WishlistModel;

import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {

    private List<WishlistModel> wishlistModelList;

    private Boolean wishlist;

    private int lastPosition = -1;

    private FirebaseUser currentUser;

    public WishlistAdapter(List<WishlistModel> wishlistModelList, Boolean wishlist) {
        this.wishlistModelList = wishlistModelList;
        this.wishlist = wishlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wishlist_item_layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        String productId = wishlistModelList.get(position).getProductId();
        String resource = wishlistModelList.get(position).getProductImage();
        String title = wishlistModelList.get(position).getProductTitle();
        String productPrice = wishlistModelList.get(position).getProductPrice();

        viewHolder.setData(productId, resource, title, productPrice, position);

        if (lastPosition < position) {

            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }

    @Override
    public int getItemCount() {
        return wishlistModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TextView productTitle;
        private TextView productPrice;
        private ImageButton deleteBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
            productPrice = itemView.findViewById(R.id.product_price);
            deleteBtn = itemView.findViewById(R.id.delete_button);

        }

        private void setData(final String productId, String resource, String title, String price, final int index) {

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholdericonmini)).into(productImage);

            productTitle.setText(title);

            productPrice.setTextColor(itemView.getContext().getResources().getColor(R.color.colorBlack));
            productPrice.setText("Rs. " + price + "/-");

            if (wishlist) {
                if (currentUser == null) {
                    deleteBtn.setVisibility(View.GONE);
                } else if (currentUser != null) {
                    deleteBtn.setVisibility(View.VISIBLE);
                }

            } else {
                deleteBtn.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                String productId;
                @Override
                public void onClick(View v) {

                    if (!ProductDetailsActivity.running_wishlist_query) {

                        ProductDetailsActivity.running_wishlist_query = true;
                        if (DBqueries.wishlistModelList.size() > 0) {
                            productId = DBqueries.wishlistModelList.get(index).getProductId();
                            DBqueries.removeFromWishlist(productId, itemView.getContext(), true);
                        } else {

                        }

                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent productDetailsIntent = new Intent(itemView.getContext(), ProductDetailsActivity.class);

                    productDetailsIntent.putExtra("PRODUCT_ID", productId);

                    itemView.getContext().startActivity(productDetailsIntent);
                }
            });
        }
    }
}