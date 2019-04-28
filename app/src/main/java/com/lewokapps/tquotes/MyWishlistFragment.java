package com.lewokapps.tquotes;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyWishlistFragment extends Fragment {


    public MyWishlistFragment() {
        // Required empty public constructor

     //   wishlistRecyclerView = findViewById(R.id.my_wishlist_recycler_view);

    }

    public RecyclerView wishlistRecyclerView;

        private Dialog loadingDialog;

    public static WishlistAdapter wishlistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_wishlist, container, false);

        ///// loading dialog

        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);

        loadingDialog.setCancelable(false);

        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.slider_background));

        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//
        loadingDialog.show();

        ///// loading dialog

        wishlistRecyclerView = view.findViewById(R.id.my_wishlist_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wishlistRecyclerView.setLayoutManager(linearLayoutManager);

        if (DBqueries.wishlistModelList.size() == 0){

            DBqueries.wishlist.clear();

            DBqueries.loadWishlist(getContext(), loadingDialog, true);
         //   loadingDialog.dismiss();
         //   Toast.makeText(getContext(), "loaded if", Toast.LENGTH_SHORT).show();

        } else {
            DBqueries.loadWishlist(getContext(), loadingDialog, true);
       //     Toast.makeText(getContext(), "loaded else", Toast.LENGTH_SHORT).show();

            //  loadingDialog.dismiss();
        }



        wishlistAdapter = new WishlistAdapter(DBqueries.wishlistModelList, true);
        wishlistRecyclerView.setAdapter(wishlistAdapter);
        wishlistAdapter.notifyDataSetChanged();
        loadingDialog.dismiss();
        return view;
    }

}
