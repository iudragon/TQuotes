package com.lewokapps.tquotes;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.lewokapps.tquotes.model.ProductSpecificationModel;
import com.lewokapps.tquotes.model.SpeciallistModel;
import com.lewokapps.tquotes.model.WishlistModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lewokapps.tquotes.RegisterActivity.setSignUpFragment;

public class ProductDetailsActivity extends AppCompatActivity {

    public static boolean running_wishlist_query = false;
    public static boolean running_speciallist_query = false;
    public static boolean running_rating_query = false;
    //    public static boolean running_cart_query = false;
    public static Activity productDetailsActivity;

    private TextView productTitle;
    private TextView averageRatingMiniView;
    private TextView totalRatingMiniView;
    private TextView productPrice;

    public static boolean WISHLIST_BTN_CLICKED = false;
    public static boolean SPECIALIST_BTN_CLICKED = false;

    private NotificationManagerCompat notificationManager;


//    private ImageView codIndicator;
//    private TextView tvCodIndicator;


    private ViewPager productImagesViewPager;
    private TabLayout viewPagerIndicator;


    public static boolean ALREADY_ADDED_TO_WISHLIST = false;
    public static boolean ALREADY_ADDED_TO_SPECIALLIST = false;
    public static FloatingActionButton addToWishlistBtn;
    public static FloatingActionButton addToSpeciallistBtn;

    ///// product description

    private ConstraintLayout productDetailsOnlyContainer;
    private ConstraintLayout productDetailsTabsContainer;
    private ViewPager productDetailsViewPager;
    private TabLayout productDetailsTabLayout;

    private TextView productOnlyDescriptionBody;
    private ConstraintLayout productImagesConstraintLayoutContainer;

    private String productDescription;
    private String productOtherDetails;

    private List<ProductSpecificationModel> productSpecificationModelList = new ArrayList<>();


    ///// product description

    private LinearLayout couponRedemptionLayout;

    private Button couponRedeemBtn;

    private TextView rewardTitle;
    private TextView rewardBody;

//    public static boolean ALREADY_ADDED_TO_CART = false;


    ///// ratings layout

    public static int initialRating;
    public static LinearLayout rateNowContainer;
    private TextView totalRatings;
    private LinearLayout ratingsNoContainer;
    private TextView totalRatingsFigure;
    private LinearLayout ratingsProgressBarContainer;

    private TextView averageRating;

    ///// ratings layout

    private Button buyNowBtn;

//    private LinearLayout addToCartBtn;

    public static MenuItem cartItem;

    private FirebaseFirestore firebaseFirestore;

    ///// coupon dialog

    public static TextView couponTitle;
    public static TextView couponExpiryDate;
    public static TextView couponBody;
    private static RecyclerView couponsRecyclerView;
    private static LinearLayout selectedCoupon;

    ///// coupon dialog

    private Dialog loadingDialog;

    private Dialog signInDialog;

    private FirebaseUser currentUser;

    public static String productID;

    private TextView badgeCount;

    private DocumentSnapshot documentSnapshot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SPECIALIST_BTN_CLICKED = false;
        WISHLIST_BTN_CLICKED = false;

        notificationManager = NotificationManagerCompat.from(this);

        productImagesViewPager = findViewById(R.id.product_images_viewpager);
        viewPagerIndicator = findViewById(R.id.viewpager_indicator);
        addToWishlistBtn = findViewById(R.id.add_to_wishlist_btn);
        addToSpeciallistBtn = findViewById(R.id.add_to_speciallist_btn);

        productDetailsViewPager = findViewById(R.id.product_details_viewpager);
        productDetailsTabLayout = findViewById(R.id.product_details_tablayout);

        productTitle = findViewById(R.id.product_title);

        productPrice = findViewById(R.id.product_price);

        rewardTitle = findViewById(R.id.reward_title);
        rewardBody = findViewById(R.id.reward_body);

        productDetailsTabsContainer = findViewById(R.id.product_details_tab_container);


        loadingDialog = new Dialog(ProductDetailsActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);

        loadingDialog.setCancelable(false);

        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        loadingDialog.show();

        productDetailsTabsContainer.setVisibility(View.VISIBLE);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        final List<String> productImages = new ArrayList<>();

        productID = getIntent().getStringExtra("PRODUCT_ID");

        firebaseFirestore.collection("PRODUCTS").document(productID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    documentSnapshot = task.getResult();

                    firebaseFirestore.collection("PRODUCTS").document(productID).collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {

                                for (long x = 1; x < (long) documentSnapshot.get("no_of_product_images") + 1; x++) {
                                    productImages.add(documentSnapshot.get("product_image_" + x).toString());

                                }

                                ProductImagesAdapter productImagesAdapter = new ProductImagesAdapter(productImages);
                                productImagesViewPager.setAdapter(productImagesAdapter);

                                productTitle.setText(documentSnapshot.get("product_title").toString());
//                                averageRatingMiniView.setText(documentSnapshot.get("average_rating").toString());
//
//                                totalRatingMiniView.setText("(" + documentSnapshot.get("total_ratings") + ")ratings");

                                productPrice.setText("Rs. " + documentSnapshot.get("product_price").toString() + "/-");
//
//                                rewardTitle.setText((long) documentSnapshot.get("free_coupons") + documentSnapshot.get("free_coupon_title").toString());
//
//
//                                rewardBody.setText(documentSnapshot.get("free_coupon_body").toString());

                                if ((boolean) documentSnapshot.get("use_tab_layout")) {
                                    productDetailsTabsContainer.setVisibility(View.VISIBLE);
//                                    productDetailsOnlyContainer.setVisibility(View.GONE);

                                    productDescription = documentSnapshot.get("product_description").toString();

                                    productOtherDetails = documentSnapshot.get("product_other_details").toString();

                                    for (long x = 1; x < (long) documentSnapshot.get("total_spec_titles") + 1; x++) {

                                        productSpecificationModelList.add(new ProductSpecificationModel(0, documentSnapshot.get("spec_title_" + x).toString()));

                                        for (long y = 1; y < (long) documentSnapshot.get("spec_title_" + x + "_total_fields") + 1; y++) {
                                            productSpecificationModelList.add(new ProductSpecificationModel(1, documentSnapshot.get("spec_title_" + x + "_field_" + y + "_name").toString(),
                                                    documentSnapshot.get("spec_title_" + x + "_field_" + y + "_value").toString()));

                                        }
                                    }

                                } else {
                                    productDetailsTabsContainer.setVisibility(View.GONE);
//                                    productDetailsOnlyContainer.setVisibility(View.VISIBLE);
                                    productOnlyDescriptionBody.setText(documentSnapshot.get("product_description").toString());
                                }


                                productDetailsViewPager.setAdapter(new ProductDetailsAdapter(getSupportFragmentManager(), productDetailsTabLayout.getTabCount(), productDescription, productOtherDetails, productSpecificationModelList));


                                if (currentUser != null) {


                                    if (DBqueries.wishlist.size() == 0) {

                                        DBqueries.loadWishlist(ProductDetailsActivity.this, loadingDialog, true);

                                    } else {
                                        //  loadingDialog.dismiss();

                                    }

                                    if (DBqueries.speciallist.size() == 0) {

                                        DBqueries.loadSpeciallist(ProductDetailsActivity.this, loadingDialog, true);

                                    } else {
                                        //   loadingDialog.dismiss();
                                    }


                                } else {
                                    //  loadingDialog.dismiss();

                                }

                                if (DBqueries.wishlist.contains(productID)) {

                                    ALREADY_ADDED_TO_WISHLIST = true;
                                    addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorAvailable));
                                } else {
                                    addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreyNoItems)));
                                    ALREADY_ADDED_TO_WISHLIST = false;
                                }

                                if (DBqueries.speciallist.contains(productID)) {

                                    ALREADY_ADDED_TO_SPECIALLIST = true;
                                    addToSpeciallistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorSpecial));
                                } else {
                                    addToSpeciallistBtn.setSupportImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreyNoItems)));
                                    ALREADY_ADDED_TO_SPECIALLIST = false;
                                }


                            } else {
                                String error = task.getException().getMessage();
                                Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    // loadingDialog.dismiss();
                    String error = task.getException().getMessage();
                    Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();

                }

            }

        });
        loadingDialog.dismiss();

        viewPagerIndicator.setupWithViewPager(productImagesViewPager, true);


        addToWishlistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WISHLIST_BTN_CLICKED = true;
                SPECIALIST_BTN_CLICKED = false;
                if (currentUser == null) {

                    Toast.makeText(ProductDetailsActivity.this, "Icon: Available to eat", Toast.LENGTH_SHORT).show();

//                    signInDialog.show();

                } else {

                    if (!running_wishlist_query) {

                        running_wishlist_query = true;

                        if (DBqueries.wishlist.contains(productID)) {
                            int index = DBqueries.wishlist.indexOf(productID);

                            DBqueries.removeFromWishlist(productID, ProductDetailsActivity.this, true);
                            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreyNoItems)));

                        } else {
                            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAvailable)));
                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID" +
                                    "_" + String.valueOf(DBqueries.wishlist.size()), productID);
                            addProduct.put("list_size", (long) (DBqueries.wishlist.size() + 1));
                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_WISHLIST")

                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {



                                        if (DBqueries.wishlistModelList.size() != 0) {

                                            DBqueries.wishlistModelList.add(new WishlistModel(productID, documentSnapshot.get("product_image_1").toString(), documentSnapshot.get("product_title").toString(), documentSnapshot.get("product_price").toString()));

                                        }

                                        ALREADY_ADDED_TO_WISHLIST = true;

                                        addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorAvailable));

                                        if (!DBqueries.wishlist.contains(productID)) {
                                            DBqueries.wishlist.add(productID);
                                        }
                                        Toast.makeText(ProductDetailsActivity.this, "Added to Available to eat", Toast.LENGTH_SHORT).show();

                                    } else {
                                        addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorGreyNoItems));
                                        String error = task.getException().getMessage();
                                    }
                                    running_wishlist_query = false;
                                }
                            });

                        }
                    }

                }

            }
        });



        //// SPECIAL

        addToSpeciallistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                WISHLIST_BTN_CLICKED = false;
                SPECIALIST_BTN_CLICKED = true;

                if (currentUser == null) {

                    Toast.makeText(ProductDetailsActivity.this, "Icon: Special of the day", Toast.LENGTH_SHORT).show();

//                    signInDialog.show();
                } else {

                    if (!running_speciallist_query) {

                        running_speciallist_query = true;

                        if (DBqueries.speciallist.contains(productID)) {

                            int index = DBqueries.speciallist.indexOf(productID);

                            DBqueries.removeFromSpeciallist(productID, ProductDetailsActivity.this, true);

                            addToSpeciallistBtn.setSupportImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreyNoItems)));

                        } else {
                            addToSpeciallistBtn.setSupportImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorSpecial)));

                            Map<String, Object> addProduct = new HashMap<>();
                            addProduct.put("product_ID_" + String.valueOf(DBqueries.speciallist.size()), productID);
                            addProduct.put("list_size", (long) (DBqueries.speciallist.size() + 1));
                            firebaseFirestore.collection("USERS").document(currentUser.getUid()).collection("USER_DATA").document("MY_SPECIALLIST")

                                    .update(addProduct).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {



                                        if (DBqueries.speciallistModelList.size() != 0) {

                                            DBqueries.speciallistModelList.add(new SpeciallistModel(productID, documentSnapshot.get("product_image_1").toString(), documentSnapshot.get("product_title").toString(), documentSnapshot.get("product_price").toString()));

                                        }

                                        ALREADY_ADDED_TO_SPECIALLIST = true;

                                        addToSpeciallistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorSpecial));

                                        if (!DBqueries.speciallist.contains(productID)) {

                                            DBqueries.speciallist.add(productID);
                                        }
                                        Toast.makeText(ProductDetailsActivity.this, "Added to Special of the day", Toast.LENGTH_SHORT).show();

                                    } else {
                                        addToSpeciallistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorGreyNoItems));
                                        String error = task.getException().getMessage();
                                        Toast.makeText(ProductDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
                                    }
                                    running_speciallist_query = false;
                                }
                            });

                        }
                    }

                }

            }
        });

        //// SPECIAL

        WISHLIST_BTN_CLICKED = false;
        SPECIALIST_BTN_CLICKED = false;

        productDetailsViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(productDetailsTabLayout));
        productDetailsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                productDetailsViewPager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(ProductDetailsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ///// sign in dialog


        signInDialog = new Dialog(ProductDetailsActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);

        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
//        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(ProductDetailsActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignInFragment.disableCloseBtn = true;
                SignUpFragment.disableCloseBtn = true;

                signInDialog.dismiss();
                setSignUpFragment = false;
                startActivity(registerIntent);
            }
        });

        ///// sign in dialog

    }

    @Override
    protected void onStart() {
        super.onStart();


        productDetailsTabsContainer.setVisibility(View.GONE);


        if (DBqueries.wishlistModelList.size() == 0) {


            DBqueries.wishlist.clear();

            DBqueries.loadWishlist(ProductDetailsActivity.this, loadingDialog, true);

        } else {


            DBqueries.loadWishlist(ProductDetailsActivity.this, loadingDialog, true);

        }

        if (DBqueries.speciallistModelList.size() == 0) {


            DBqueries.speciallist.clear();

            DBqueries.loadSpeciallist(ProductDetailsActivity.this, loadingDialog, true);


        } else {


            DBqueries.loadSpeciallist(ProductDetailsActivity.this, loadingDialog, true);

        }


        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        if (currentUser != null) {

            if (DBqueries.wishlist.size() == 0) {


            } else {
                loadingDialog.dismiss();
            }

            //// SPECIAL

            if (DBqueries.speciallist.size() == 0) {

            } else {
                loadingDialog.dismiss();
            }

            //// SPECIAL

        } else {
            loadingDialog.dismiss();
        }


        if (DBqueries.wishlist.contains(productID)) {

            ALREADY_ADDED_TO_WISHLIST = true;
            addToWishlistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorAvailable));
        } else {

            addToWishlistBtn.setSupportImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreyNoItems)));

            ALREADY_ADDED_TO_WISHLIST = false;
        }

        //// SPECIAL

        if (DBqueries.speciallist.contains(productID)) {

            ALREADY_ADDED_TO_SPECIALLIST = true;
            addToSpeciallistBtn.setSupportImageTintList(getResources().getColorStateList(R.color.colorAvailable));
        } else {

            addToSpeciallistBtn.setSupportImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorGreyNoItems)));

            ALREADY_ADDED_TO_SPECIALLIST = false;
        }

        //// SPECIAL


        invalidateOptionsMenu();

    }

    public static void showDialogRecyclerView() {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            productDetailsActivity = null;
            finish();
            return true;
        } else if (id == R.id.main_search_icon) {
            return true;
//
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        productDetailsActivity = null;
        super.onBackPressed();

    }



}



