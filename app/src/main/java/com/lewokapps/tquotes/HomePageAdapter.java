package com.lewokapps.tquotes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lewokapps.tquotes.model.HomePageModel;
import com.lewokapps.tquotes.model.HorizontalProductScrollModel;
import com.lewokapps.tquotes.model.SliderModel;
import com.lewokapps.tquotes.model.WishlistModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomePageAdapter extends RecyclerView.Adapter {

    private List<HomePageModel> homePageModelList;

    private RecyclerView.RecycledViewPool recycledViewPool;

    private int lastPosition = -1;

    public HomePageAdapter(List<HomePageModel> homePageModelList) {
        this.homePageModelList = homePageModelList;
        recycledViewPool = new RecyclerView.RecycledViewPool();
    }


    @Override
    public int getItemViewType(int position) {

        switch (homePageModelList.get(position).getType()) {

            case 0:

                return HomePageModel.BANNER_SLIDER;

            case 1:

                return HomePageModel.STRIP_AD_BANNER;

            case 2:

                return HomePageModel.HORIZONTAL_PRODUCT_VIEW;

            case 6:

                return HomePageModel.DUPLICATE_HORIZ_PRO_VIEW;

            default:
                return -1;

        }

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        switch (viewType) {

            case HomePageModel.BANNER_SLIDER:

                View bannerSliderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sliding_ad_layout, viewGroup, false);

                return new BannerSliderViewHolder(bannerSliderView);

            case HomePageModel.STRIP_AD_BANNER:

                View stripAdView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.strip_ad_layout, viewGroup, false);

                return new StripAdBannerViewHoler(stripAdView);


            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:

                View horizontalProductView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.horizontal_scroll_layout, viewGroup, false);

                return new HorizontalProductViewHolder(horizontalProductView);

            case HomePageModel.DUPLICATE_HORIZ_PRO_VIEW:

                View duplicateHorizProView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.duplicate_horiz_scroll_layout, viewGroup, false);

                return new HorizontalProductViewHolderDuplicate(duplicateHorizProView);




            default:
                return null;


        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        switch (homePageModelList.get(position).getType()) {


            case HomePageModel.BANNER_SLIDER:

                List<SliderModel> sliderModelList = homePageModelList.get(position).getSliderModelList();

                ((BannerSliderViewHolder) viewHolder).setBannerSliderViewPager(sliderModelList);

                break;

            case HomePageModel.STRIP_AD_BANNER:

                String resource = homePageModelList.get(position).getResource();
                String color = homePageModelList.get(position).getBackgroundColor();

                ((StripAdBannerViewHoler) viewHolder).setStripAd(resource, color);
                break;

            case HomePageModel.HORIZONTAL_PRODUCT_VIEW:

                String layoutColor = homePageModelList.get(position).getBackgroundColor();
                String horizontalLayoutTitle = homePageModelList.get(position).getTitle();

                List<WishlistModel> viewAllProductList = homePageModelList.get(position).getViewAllProductList();

                List<HorizontalProductScrollModel> horizontalProductScrollModelList = homePageModelList.get(position).getHorizontalProductScrollModelList();

                ((HorizontalProductViewHolder) viewHolder).setHorizontalProductLayout(horizontalProductScrollModelList, horizontalLayoutTitle, layoutColor, viewAllProductList);


                break;


            /////////////////////

            case HomePageModel.DUPLICATE_HORIZ_PRO_VIEW:

                String layoutColorDuplicate = homePageModelList.get(position).getBackgroundColor();
                String horizontalLayoutTitleDuplicate = homePageModelList.get(position).getTitle();

                List<WishlistModel> viewAllProductListDuplicate = homePageModelList.get(position).getViewAllProductList();

                List<HorizontalProductScrollModel> horizontalProductScrollModelListDuplicate = homePageModelList.get(position).getHorizontalProductScrollModelList();

                ((HorizontalProductViewHolderDuplicate) viewHolder).setHorizontalProductLayoutDuplicate(horizontalProductScrollModelListDuplicate, horizontalLayoutTitleDuplicate, layoutColorDuplicate, viewAllProductListDuplicate);


                break;

            /////////////////////



            default:
                return;

        }

        if (lastPosition < position) {

            Animation animation = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(), R.anim.fade_in);
            viewHolder.itemView.setAnimation(animation);
            lastPosition = position;
        }

    }


    @Override
    public int getItemCount() {
        return homePageModelList.size();
    }

    public class BannerSliderViewHolder extends RecyclerView.ViewHolder {


        private ViewPager bannerSliderViewPager;
        private int currentPage;

        private Timer timer;

        final private long DELAY_TIME = 3000;
        final private long PERIOD_TIME = 3000;

        private List<SliderModel> arrangedList;


        public BannerSliderViewHolder(@NonNull View itemView) {
            super(itemView);


            bannerSliderViewPager = itemView.findViewById(R.id.banner_slider_view_pager);


        }

        private void setBannerSliderViewPager(final List<SliderModel> sliderModelList) {

            currentPage = 2;

            if (timer != null) {

                timer.cancel();
            }

            arrangedList = new ArrayList<>();
            for (int x = 0; x < sliderModelList.size(); x++) {

                arrangedList.add(x, sliderModelList.get(x));
            }

            arrangedList.add(0, sliderModelList.get(sliderModelList.size() - 2));
            arrangedList.add(1, sliderModelList.get(sliderModelList.size() - 1));
            arrangedList.add(sliderModelList.get(0));
            arrangedList.add(sliderModelList.get(1));

            SliderAdapter sliderAdapter = new SliderAdapter(arrangedList);

            bannerSliderViewPager.setAdapter(sliderAdapter);

            bannerSliderViewPager.setClipToPadding(false);
            bannerSliderViewPager.setPageMargin(20);

            bannerSliderViewPager.setCurrentItem(currentPage);

            ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int i, float v, int i1) {

                }

                @Override
                public void onPageSelected(int i) {
                    currentPage = i;
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                    if (i == ViewPager.SCROLL_STATE_IDLE) {

                        pageLooper(arrangedList);
                    }

                }
            };


            bannerSliderViewPager.addOnPageChangeListener(onPageChangeListener);

            startBannerSlideShow(arrangedList);

            bannerSliderViewPager.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    pageLooper(arrangedList);
                    stopBannerSlideShow();

                    if (event.getAction() == MotionEvent.ACTION_UP) {

                        startBannerSlideShow(arrangedList);

                    }

                    return false;
                }
            });
        }

        private void pageLooper(List<SliderModel> sliderModelList) {

            if (currentPage == sliderModelList.size() - 2) {

                currentPage = 2;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }

            if (currentPage == 1) {

                currentPage = sliderModelList.size() - 3;
                bannerSliderViewPager.setCurrentItem(currentPage, false);
            }

        }

        private void startBannerSlideShow(final List<SliderModel> sliderModelList) {

            final Handler handler = new Handler();
            final Runnable update = new Runnable() {
                @Override
                public void run() {

                    if (currentPage >= sliderModelList.size()) {

                        currentPage = 1;

                    }

                    bannerSliderViewPager.setCurrentItem(currentPage++, true);

                }
            };
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAY_TIME, PERIOD_TIME);


        }

        private void stopBannerSlideShow() {

            timer.cancel();

        }

    }


    public class StripAdBannerViewHoler extends RecyclerView.ViewHolder {

        private ImageView stripAdImage;
        private ConstraintLayout stripAdContainer;

        public StripAdBannerViewHoler(@NonNull View itemView) {
            super(itemView);


            stripAdImage = itemView.findViewById(R.id.strip_ad_image);
            stripAdContainer = itemView.findViewById(R.id.strip_ad_container);

        }


        private void setStripAd(String resource, String color) {

            Glide.with(itemView.getContext()).load(resource).apply(new RequestOptions().placeholder(R.drawable.placeholdericon)).into(stripAdImage);

            stripAdContainer.setBackgroundColor(Color.parseColor(color));

        }


    }

    public class HorizontalProductViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout container;
        private Button horizontalLayoutViewAllBtn;
        private TextView horizontalLayoutTitle;
        private RecyclerView horizontalRecyclerView;

        public HorizontalProductViewHolder(@NonNull View itemView) {
            super(itemView);

            container = itemView.findViewById(R.id.container);
            horizontalLayoutTitle = itemView.findViewById(R.id.horizontal_scroll_layout_title);
            horizontalLayoutViewAllBtn = itemView.findViewById(R.id.horizontal_scroll_view_all_button);
            horizontalRecyclerView = itemView.findViewById(R.id.horizontal_product_scroll_layout_recyclerview);
            horizontalRecyclerView.setRecycledViewPool(recycledViewPool);

        }


        private void setHorizontalProductLayout(List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title, String color, final List<WishlistModel> viewAllProductList) {

            container.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            horizontalLayoutTitle.setText(title);


            if (horizontalProductScrollModelList.size() > 8) {

                horizontalLayoutViewAllBtn.setVisibility(View.VISIBLE);
                horizontalLayoutViewAllBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ViewAllActivity.wishlistModelList = viewAllProductList;

                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code", 0);
                        viewAllIntent.putExtra("title", title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });

            } else {

                horizontalLayoutViewAllBtn.setVisibility(View.INVISIBLE);


            }

            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerView.setLayoutManager(linearLayoutManager);

            horizontalRecyclerView.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();

        }

    }




    //////////////////////////////


    public class HorizontalProductViewHolderDuplicate extends RecyclerView.ViewHolder {

        private ConstraintLayout containerDuplicate;
        private Button horizontalLayoutViewAllBtnDuplicate;
        private TextView horizontalLayoutTitleDuplicate;
        private RecyclerView horizontalRecyclerViewDuplicate;

        public HorizontalProductViewHolderDuplicate(@NonNull View itemView) {
            super(itemView);

            containerDuplicate = itemView.findViewById(R.id.container_duplicate);
            horizontalLayoutTitleDuplicate = itemView.findViewById(R.id.horizontal_scroll_layout_title_duplicate);
            horizontalLayoutViewAllBtnDuplicate = itemView.findViewById(R.id.horizontal_scroll_view_all_button_duplicate);
            horizontalRecyclerViewDuplicate = itemView.findViewById(R.id.horizontal_product_scroll_layout_recyclerview_duplicate);
            horizontalRecyclerViewDuplicate.setRecycledViewPool(recycledViewPool);

        }


        private void setHorizontalProductLayoutDuplicate(List<HorizontalProductScrollModel> horizontalProductScrollModelList, final String title, String color, final List<WishlistModel> viewAllProductList) {

            containerDuplicate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
            horizontalLayoutTitleDuplicate.setText(title);


            if (horizontalProductScrollModelList.size() > 8) {

                horizontalLayoutViewAllBtnDuplicate.setVisibility(View.VISIBLE);
                horizontalLayoutViewAllBtnDuplicate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ViewAllActivity.wishlistModelList = viewAllProductList;

                        Intent viewAllIntent = new Intent(itemView.getContext(), ViewAllActivity.class);
                        viewAllIntent.putExtra("layout_code", 2);
                        viewAllIntent.putExtra("title", title);
                        itemView.getContext().startActivity(viewAllIntent);
                    }
                });

            } else {

                horizontalLayoutViewAllBtnDuplicate.setVisibility(View.INVISIBLE);


            }

            HorizontalProductScrollAdapter horizontalProductScrollAdapter = new HorizontalProductScrollAdapter(horizontalProductScrollModelList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(itemView.getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            horizontalRecyclerViewDuplicate.setLayoutManager(linearLayoutManager);

            horizontalRecyclerViewDuplicate.setAdapter(horizontalProductScrollAdapter);
            horizontalProductScrollAdapter.notifyDataSetChanged();

        }

    }



    //////////////////////////////

}
