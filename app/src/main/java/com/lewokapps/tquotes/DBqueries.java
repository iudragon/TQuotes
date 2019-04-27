package com.lewokapps.tquotes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lewokapps.tquotes.model.HomePageModel;
import com.lewokapps.tquotes.model.HorizontalProductScrollModel;
import com.lewokapps.tquotes.model.RewardModel;
import com.lewokapps.tquotes.model.SliderModel;
import com.lewokapps.tquotes.model.SpeciallistModel;
import com.lewokapps.tquotes.model.WishlistModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lewokapps.tquotes.MySpeciallistFragment.speciallistAdapter;
import static com.lewokapps.tquotes.MyWishlistFragment.wishlistAdapter;


public class DBqueries {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public static List<List<HomePageModel>> lists = new ArrayList<>();

    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> wishlist = new ArrayList<>();
    public static List<String> speciallist = new ArrayList<>();

    public static List<WishlistModel> wishlistModelList = new ArrayList<>();
    public static List<SpeciallistModel> speciallistModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();

    public static List<Long> myRating = new ArrayList<>();

    public static List<RewardModel> rewardModelList = new ArrayList<>();

    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName) {

        firebaseFirestore.collection("CATEGORIES").document(categoryName.toUpperCase()).collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if ((long) documentSnapshot.get("view_type") == 0) {
                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long no_of_banners = (long) documentSnapshot.get("no_of_banners");

                                    for (long x = 1; x < no_of_banners + 1; x++) {

                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(), documentSnapshot.get("banner_" + x + "_background").toString()));
                                    }

                                    lists.get(index).add(new HomePageModel(0, sliderModelList));

                                } else if ((long) documentSnapshot.get("view_type") == 1) {
                                    lists.get(index).add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString(), documentSnapshot.get("background").toString()));

                                } else if ((long) documentSnapshot.get("view_type") == 2) {

                                    List<WishlistModel> viewAllProductlist = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();

                                    long no_of_products = (long) documentSnapshot.get("no_of_products");

                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString(), documentSnapshot.get("product_image_" + x).toString(), documentSnapshot.get("product_title_" + x).toString(), documentSnapshot.get("product_subtitle_" + x).toString(), documentSnapshot.get("product_price_" + x).toString()));

                                        viewAllProductlist.add(new WishlistModel(documentSnapshot.get("product_ID_" + x).toString(), documentSnapshot.get("product_image_" + x).toString(), documentSnapshot.get("product_full_title_" + x).toString(), documentSnapshot.get("product_price_" + x).toString()));
                                    }

                                    lists.get(index).add(new HomePageModel(2, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductlist));


                                } else if ((long) documentSnapshot.get("view_type") == 6) {

                                    List<WishlistModel> viewAllProductlist = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();


                                    long no_of_products = (long) documentSnapshot.get("no_of_products");

                                    for (long x = 1; x < no_of_products + 1; x++) {
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString(), documentSnapshot.get("product_image_" + x).toString(), documentSnapshot.get("product_title_" + x).toString(), documentSnapshot.get("product_subtitle_" + x).toString(), documentSnapshot.get("product_price_" + x).toString()));


                                        viewAllProductlist.add(new WishlistModel(documentSnapshot.get("product_ID_" + x).toString(), documentSnapshot.get("product_image_" + x).toString(), documentSnapshot.get("product_full_title_" + x).toString(), documentSnapshot.get("product_price_" + x).toString()));

                                    }

                                    lists.get(index).add(new HomePageModel(6, documentSnapshot.get("layout_title").toString(), documentSnapshot.get("layout_background").toString(), horizontalProductScrollModelList, viewAllProductlist));


                                }
                            }

                            HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));

                            homePageRecyclerView.setAdapter(homePageAdapter);

                            homePageAdapter.notifyDataSetChanged();
//                            HomeFragment.swipeRefreshLayout.setRefreshing(false);

                        } else {

                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadWishlist(final Context context, final Dialog dialog, final boolean loadProductData) {


        wishlist.clear();
        final Dialog loadingDialog;
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);

        loadingDialog.setCancelable(false);

        loadingDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.slider_background));

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (!((Activity) context).isFinishing()) {
            loadingDialog.show();
        }


        firebaseFirestore.collection("USERS").document("pyFEsRmGjJVWejrOFagS0yEzJ1x1").collection("USER_DATA").document("MY_WISHLIST").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {


            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if ((long) task.getResult().get("list_size") == 0) {
                        if (!((Activity) context).isFinishing()) {
                            loadingDialog.show();
                        }
                        wishlistModelList.clear();
                        wishlistAdapter = new WishlistAdapter(DBqueries.wishlistModelList, false);

                        wishlistAdapter.notifyDataSetChanged();
                    }
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {

                        if (!wishlist.contains(task.getResult().get("product_ID_" + x).toString())) {
                            wishlist.add(task.getResult().get("product_ID_" + x).toString());
                        }

                        if (DBqueries.wishlist.contains(ProductDetailsActivity.productID)) {

                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;

                            if (ProductDetailsActivity.addToWishlistBtn != null) {

                                ProductDetailsActivity.addToWishlistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorAvailable));

                            }


                        } else {

                            if (ProductDetailsActivity.addToWishlistBtn != null) {


                                ProductDetailsActivity.addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorGreyNoItems)));

                            }

                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                        }

                        if (loadProductData) {


                            final Dialog loadingDialog;
                            loadingDialog = new Dialog(context);
                            loadingDialog.setContentView(R.layout.loading_progress_dialog);

                            loadingDialog.setCancelable(false);
                            loadingDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.slider_background));

                            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            if (!((Activity) context).isFinishing()) {
                                loadingDialog.show();
                            }
                            wishlistModelList.clear();

                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        final DocumentSnapshot documentSnapshot = task.getResult();

                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {

                                                    wishlistModelList.add(new WishlistModel(productId, documentSnapshot.get("product_image_1").toString(), documentSnapshot.get("product_title").toString(), documentSnapshot.get("product_price").toString()));

                                                    if (wishlistAdapter == null) {
                                                        wishlistAdapter = new WishlistAdapter(wishlistModelList, false);
                                                        wishlistAdapter.notifyDataSetChanged();
                                                        //do nothing
                                                    }

                                                    wishlistAdapter.notifyDataSetChanged();
                                                    loadingDialog.dismiss();
                                                } else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {

                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    }
                    //  loadingDialog.dismiss();

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    //   loadingDialog.dismiss();

                }

                loadingDialog.dismiss();
                dialog.dismiss();
            }


        });

    }

    //// SPECIAL


    public static void loadSpeciallist(final Context context, final Dialog dialog, final boolean loadProductData) {


        speciallist.clear();
        final Dialog loadingDialog;
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);

        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (!((Activity) context).isFinishing()) {
            loadingDialog.show();
        }


        firebaseFirestore.collection("USERS").document("pyFEsRmGjJVWejrOFagS0yEzJ1x1").collection("USER_DATA").document("MY_SPECIALLIST").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    if ((long) task.getResult().get("list_size") == 0) {

                        if (!((Activity) context).isFinishing()) {
                            loadingDialog.show();


                        }
                        speciallistModelList.clear();

                        speciallistAdapter = new SpeciallistAdapter(DBqueries.speciallistModelList, false);
                        speciallistAdapter.notifyDataSetChanged();
                    }
                    //     speciallistAdapter.notifyDataSetChanged();
                    for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                        if (!speciallist.contains(task.getResult().get("product_ID_" + x).toString())) {

                            speciallist.add(task.getResult().get("product_ID_" + x).toString());

                        }

                        if (DBqueries.speciallist.contains(ProductDetailsActivity.productID)) {

                            ProductDetailsActivity.ALREADY_ADDED_TO_SPECIALLIST = true;

                            if (ProductDetailsActivity.addToSpeciallistBtn != null) {

                                ProductDetailsActivity.addToSpeciallistBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorSpecial));

                            }


                        } else {

                            if (ProductDetailsActivity.addToSpeciallistBtn != null) {


                                ProductDetailsActivity.addToSpeciallistBtn.setSupportImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorGreyNoItems)));

                            }

                            ProductDetailsActivity.ALREADY_ADDED_TO_SPECIALLIST = false;
                        }

                        if (loadProductData) {
                            final Dialog loadingDialog;
                            loadingDialog = new Dialog(context);
                            loadingDialog.setContentView(R.layout.loading_progress_dialog);

                            loadingDialog.setCancelable(false);
                            loadingDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.slider_background));
                            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            if (!((Activity) context).isFinishing()) {
                                loadingDialog.show();
                            }

                            speciallistModelList.clear();

                            final String productId = task.getResult().get("product_ID_" + x).toString();


                            firebaseFirestore.collection("PRODUCTS").document(productId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        final DocumentSnapshot documentSnapshot = task.getResult();

                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {


                                                    speciallistModelList.add(new SpeciallistModel(productId, documentSnapshot.get("product_image_1").toString(), documentSnapshot.get("product_title").toString(), documentSnapshot.get("product_price").toString()));


                                                    if (speciallistAdapter == null) {
                                                        speciallistAdapter = new SpeciallistAdapter(speciallistModelList, false);
                                                        speciallistAdapter.notifyDataSetChanged();
                                                        //do nothing
                                                    }

                                                    speciallistAdapter.notifyDataSetChanged();
                                                    loadingDialog.dismiss();


                                                } else {
                                                    loadingDialog.dismiss();

                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                        // loadingDialog.dismiss();


                                    } else {
                                        // loadingDialog.dismiss();

                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }

                                    //  loadingDialog.dismiss();
                                }
                            });
                        }
                    }

                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();

                }
                loadingDialog.dismiss();
                dialog.dismiss();
            }
        });
    }

    //// SPECIAL

    public static void removeFromWishlist(final String removeProductId, final Context context, final boolean reload) {

        final Dialog loadingDialog;
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);

        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.slider_background));

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (!((Activity) context).isFinishing()) {
            loadingDialog.show();
        }

        //final String removeProductId = wishlist.get(index);
        final int index = wishlist.indexOf(removeProductId);
        Toast.makeText(context, "remove=" + index, Toast.LENGTH_SHORT).show();
        wishlist.remove(index);

        //   Toast.makeText(context, "remove index="+index, Toast.LENGTH_SHORT).show();
        Map<String, Object> updateWishlist = new HashMap<>();
        //   Toast.makeText(context, "new size="+wishlist.size(), Toast.LENGTH_SHORT).show();
        for (int x = 0; x < wishlist.size(); x++) {
            //  if(removeProductId!=wishlist.get(x))
            updateWishlist.put("product_ID_" + x, wishlist.get(x));
        }
        //  Toast.makeText(context, "update size="+wishlist.size(), Toast.LENGTH_SHORT).show();
        updateWishlist.put("list_size", (long) wishlist.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_WISHLIST").set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    if (wishlistModelList.size() > 0) {


                        wishlistModelList.remove(index);

                        if (reload == true) {

                            Toast.makeText(context, "reloading", Toast.LENGTH_SHORT).show();
                            loadWishlist(context, loadingDialog, true);
                            wishlistAdapter.notifyDataSetChanged();

                        }
                        wishlistAdapter.notifyDataSetChanged();

                    }

                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;

                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();

                } else {

                    if (ProductDetailsActivity.addToWishlistBtn != null) {

                        ProductDetailsActivity.addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAvailable)));
                    }

                    wishlist.add(index, removeProductId);

                    String error = task.getException().getMessage();
                    Toast.makeText(context, "err=" + error, Toast.LENGTH_SHORT).show();
                }

                ProductDetailsActivity.running_wishlist_query = false;
                loadingDialog.dismiss();
            }
        });


    }

    //// SPECIAL

    public static void removeFromSpeciallist(final String removeProductId, final Context context, final boolean reload) {

        final Dialog loadingDialog;
        loadingDialog = new Dialog(context);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);

        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(context.getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (!((Activity) context).isFinishing()) {
            loadingDialog.show();
        }

        final int index = speciallist.indexOf(removeProductId);
        //   Toast.makeText(context, "remove="+index, Toast.LENGTH_SHORT).show();
        speciallist.remove(index);

        Map<String, Object> updateSpeciallist = new HashMap<>();

        for (int x = 0; x < speciallist.size(); x++) {
            if (removeProductId != speciallist.get(x))

                updateSpeciallist.put("product_ID_" + x, speciallist.get(x));
        }

        updateSpeciallist.put("list_size", (long) speciallist.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA").document("MY_SPECIALLIST").set(updateSpeciallist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    if (speciallistModelList.size() > 0) {

                        speciallistModelList.remove(index);

                        if (reload == true) {

                            Toast.makeText(context, "reloading", Toast.LENGTH_SHORT).show();
                            loadSpeciallist(context, loadingDialog, true);
                            speciallistAdapter.notifyDataSetChanged();

                        }
                        speciallistAdapter.notifyDataSetChanged();
                    }

                    ProductDetailsActivity.ALREADY_ADDED_TO_SPECIALLIST = false;

                    Toast.makeText(context, "Removed successfully", Toast.LENGTH_SHORT).show();

                } else {

                    if (ProductDetailsActivity.addToSpeciallistBtn != null) {

                        ProductDetailsActivity.addToSpeciallistBtn.setSupportImageTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorAvailable)));
                    }

                    speciallist.add(index, removeProductId);

                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }


                ProductDetailsActivity.running_speciallist_query = false;
                loadingDialog.dismiss();

            }
        });
    }

    //// SPECIAL


    public static void loadRewards(final Context context, final Dialog loadingDialog, final Boolean onRewardFragment) {

        rewardModelList.clear();


        firebaseFirestore.collection("USERS").document("pyFEsRmGjJVWejrOFagS0yEzJ1x1").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final Date lastseenDate = task.getResult().getDate("Last seen");

                    firebaseFirestore.collection("USERS").document("pyFEsRmGjJVWejrOFagS0yEzJ1x1").collection("USER_REWARDS").orderBy("percentage", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {

                                    if (documentSnapshot.get("type").toString().contains("") && lastseenDate.before(documentSnapshot.getDate("validity"))) {
                                        rewardModelList.add(new RewardModel(documentSnapshot.getId(), documentSnapshot.get("type").toString(),
                                                documentSnapshot.get("lower_limit").toString(),
                                                documentSnapshot.get("upper_limit").toString(),
                                                documentSnapshot.get("percentage").toString(),
                                                documentSnapshot.get("body").toString(),
                                                documentSnapshot.get("coupon_title_reward").toString(),
                                                (Date) documentSnapshot.getTimestamp("validity").toDate(),
                                                (boolean) documentSnapshot.get("already_used")));
                                    }
                                }

                                if (onRewardFragment) {

                                    MyRewardsFragment.myRewardsAdapter.notifyDataSetChanged();
                                }
                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                            }

                            loadingDialog.dismiss();
                        }
                    });


                } else {
                    loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public static void clearData() {

        lists.clear();
        loadedCategoriesNames.clear();
        wishlist.clear();
        speciallist.clear();
        wishlistModelList.clear();
        speciallistModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        rewardModelList.clear();
    }

}