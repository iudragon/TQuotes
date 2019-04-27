package com.lewokapps.tquotes;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.lewokapps.tquotes.model.HomePageModel;
import com.lewokapps.tquotes.model.HorizontalProductScrollModel;
import com.lewokapps.tquotes.model.SliderModel;
import com.lewokapps.tquotes.model.WishlistModel;
import java.util.ArrayList;
import java.util.List;
import static com.lewokapps.tquotes.DBqueries.lists;
import static com.lewokapps.tquotes.DBqueries.loadFragmentData;
import static com.lewokapps.tquotes.DBqueries.loadedCategoriesNames;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private ConnectivityManager connectivityManager;

    private NetworkInfo networkInfo;

    private RecyclerView homePageRecyclerView;

    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();

    private HomePageAdapter adapter;

    private ImageView noInternetConnection;

    private TextView noInternetTextConnection;

    public Button retryButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        noInternetConnection = view.findViewById(R.id.no_internet_connection);

        noInternetTextConnection = view.findViewById(R.id.no_internet_connection_text);

        homePageRecyclerView = view.findViewById(R.id.home_page_recyclerview);

        retryButton = view.findViewById(R.id.retry_button);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePageRecyclerView.setLayoutManager(testingLayoutManager);

        ///// home page fake list

        List<SliderModel> sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new SliderModel("null", "#defeed"));
        sliderModelFakeList.add(new SliderModel("null", "#defeed"));
        sliderModelFakeList.add(new SliderModel("null", "#defeed"));
        sliderModelFakeList.add(new SliderModel("null", "#defeed"));
        sliderModelFakeList.add(new SliderModel("null", "#defeed"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();

        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("", "", "", "", ""));

        homePageModelFakeList.add(new HomePageModel(0, sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1, "", "#defeed"));
        homePageModelFakeList.add(new HomePageModel(2, "", "#defeed", horizontalProductScrollModelFakeList, new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(6, "", "#defeed", horizontalProductScrollModelFakeList, new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(3, "", "#defeed", horizontalProductScrollModelFakeList));

        ///// home page fake list

        //////////////////////////////

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeListDuplicate = new ArrayList<>();

        horizontalProductScrollModelFakeListDuplicate.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeListDuplicate.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeListDuplicate.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeListDuplicate.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeListDuplicate.add(new HorizontalProductScrollModel("", "", "", "", ""));
        horizontalProductScrollModelFakeListDuplicate.add(new HorizontalProductScrollModel("", "", "", "", ""));

        ///// home page fake list

        //////////////////////////////

        adapter = new HomePageAdapter(homePageModelFakeList);

        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {

            MainActivity.drawer.setDrawerLockMode(0);

            noInternetConnection.setVisibility(View.GONE);
            noInternetTextConnection.setVisibility(View.GONE);
            retryButton.setVisibility(View.GONE);
            homePageRecyclerView.setVisibility(View.VISIBLE);

            if (lists.size() == 0) {
                loadedCategoriesNames.add("HOME");
                lists.add(new ArrayList<HomePageModel>());

                loadFragmentData(homePageRecyclerView, getContext(), 0, "Home");
            } else {
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }
            homePageRecyclerView.setAdapter(adapter);
        } else {

            MainActivity.drawer.setDrawerLockMode(1);

            homePageRecyclerView.setVisibility(View.GONE);

            Glide.with(this).load(R.drawable.ic_signal_wifi_off).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);

            noInternetTextConnection.setText("Please check your internet connection!");
            noInternetTextConnection.setVisibility(View.VISIBLE);

            retryButton.setVisibility(View.VISIBLE);
        }

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

        return view;
    }

    private void reloadPage() {

        networkInfo = connectivityManager.getActiveNetworkInfo();

        DBqueries.clearData();

        if (networkInfo != null && networkInfo.isConnected() == true) {

            MainActivity.drawer.setDrawerLockMode(0);

            noInternetConnection.setVisibility(View.GONE);
            noInternetTextConnection.setVisibility(View.GONE);

            retryButton.setVisibility(View.GONE);

            homePageRecyclerView.setVisibility(View.VISIBLE);

            adapter = new HomePageAdapter(homePageModelFakeList);

            homePageRecyclerView.setAdapter(adapter);

            loadedCategoriesNames.add("HOME");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(homePageRecyclerView, getContext(), 0, "Home");

        } else {

            MainActivity.drawer.setDrawerLockMode(1);
            Toast.makeText(getContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            homePageRecyclerView.setVisibility(View.GONE);
            Glide.with(getContext()).load(R.drawable.ic_signal_wifi_off).into(noInternetConnection);
            noInternetConnection.setVisibility(View.VISIBLE);
            noInternetTextConnection.setText("Please check your internet connection!");
            noInternetTextConnection.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);

        }

    }

}











