package com.lewokapps.tquotes;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.lewokapps.tquotes.RegisterActivity.setSignUpFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FrameLayout frameLayout;

    private NetworkInfo networkInfo;

    private ConnectivityManager connectivityManager;

    private ImageView headerProfileImage;

    private ImageView actionBarLogo;

    private static final int HOME_FRAGMENT = 0;
    public static final int WISHLIST_FRAGMENT = 3;
    public static final int REWARDS_FRAGMENT = 4;
    public static final int SPECIALLIST_FRAGMENT = 6;

    public static boolean resetMainActivity = false;

    private int currentFragment = -1;

    private NavigationView navigationView;

    private Window window;

    private int scrollFlags;

    private AppBarLayout.LayoutParams params;

    private Dialog signInDialog;

    private Toolbar toolbar;

    private FirebaseUser currentUser;

    public static DrawerLayout drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);

        actionBarLogo = findViewById(R.id.actionbar_logo);

        setSupportActionBar(toolbar);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();

        scrollFlags = params.getScrollFlags();

        drawer = findViewById(R.id.drawer_layout);


        navigationView = findViewById(R.id.nav_view);
        View headerview = navigationView.getHeaderView(0);
        headerProfileImage = headerview.findViewById(R.id.main_profile_image);
        headerProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser != null) {
                    Toast.makeText(MainActivity.this, "You are the owner", Toast.LENGTH_SHORT).show();
                } else {
                    signInDialog.show();
                }

            }
        });


        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        frameLayout = findViewById(R.id.main_framelayout);


            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            setFragment(new HomeFragment(), HOME_FRAGMENT);


        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);

        signInDialog.setCancelable(true);

        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        final Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);

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


    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {

            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(false);
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setVisible(false);

        } else {
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setEnabled(true);
            navigationView.getMenu().getItem(navigationView.getMenu().size() - 1).setVisible(true);

        }
        if (resetMainActivity) {
            resetMainActivity = false;
            actionBarLogo.setVisibility(View.GONE);
            setFragment(new HomeFragment(), HOME_FRAGMENT);
            navigationView.getMenu().getItem(0).setChecked(true);

        }

        invalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == HOME_FRAGMENT) {
                currentFragment = -1;
                super.onBackPressed();

            } else {

                    actionBarLogo.setVisibility(View.GONE);
                    invalidateOptionsMenu();
                    setFragment(new HomeFragment(), HOME_FRAGMENT);
                    navigationView.getMenu().getItem(0).setChecked(true);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if (currentFragment == HOME_FRAGMENT) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);


        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.main_search_icon) {

                gotoFragment(getString(R.string.available_to_eat), new MyWishlistFragment(), WISHLIST_FRAGMENT);
                navigationView.getMenu().getItem(1).setChecked(true);

                return true;
            } else if (id == R.id.main_notification_icon) {

                gotoFragment(getString(R.string.special_of_the_day), new MySpeciallistFragment(), SPECIALLIST_FRAGMENT);
                navigationView.getMenu().getItem(2).setChecked(true);

                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    private void gotoFragment(String title, Fragment fragment, int fragmentNo) {


        actionBarLogo.setVisibility(View.GONE); //// CHANGE FROM GONE TO VISIBLE
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(title);

        invalidateOptionsMenu();
        setFragment(fragment, fragmentNo);

            params.setScrollFlags(scrollFlags);

    }

    MenuItem menuItem;


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);

        menuItem = item;

        if (currentUser != null) {

            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    int id = menuItem.getItemId();

                    signInDialog.dismiss();

                    if (id == R.id.nav_my_mall) {


                        actionBarLogo.setVisibility(View.GONE);

                        invalidateOptionsMenu();

                        setFragment(new HomeFragment(), HOME_FRAGMENT);
                        navigationView.getMenu().getItem(0).setChecked(true);


                    } else if (id == R.id.nav_my_wishlist) {
                        gotoFragment(getString(R.string.available_to_eat), new MyWishlistFragment(), WISHLIST_FRAGMENT);
                        navigationView.getMenu().getItem(1).setChecked(true);


                    } else if (id == R.id.nav_my_speciallist) {
                        gotoFragment(getString(R.string.special_of_the_day), new MySpeciallistFragment(), SPECIALLIST_FRAGMENT);
                        navigationView.getMenu().getItem(2).setChecked(true);


                    } else if (id == R.id.nav_my_rewards) {

                        gotoFragment("About", new MyRewardsFragment(), REWARDS_FRAGMENT);
                        navigationView.getMenu().getItem(3).setChecked(true);

                    } else if (id == R.id.nav_sign_out) {
                        FirebaseAuth.getInstance().signOut();

                        DBqueries.clearData();

                        Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                        startActivity(registerIntent);
                        finish();
                    }

                }
            });


            return true;

        } else {

            drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerClosed(View drawerView) {

                    super.onDrawerClosed(drawerView);

                    int id = menuItem.getItemId();


                    if (id == R.id.nav_my_mall) {
                        invalidateOptionsMenu();
                        signInDialog.dismiss();
                        gotoFragment("Home", new HomeFragment(), HOME_FRAGMENT);
                        navigationView.getMenu().getItem(0).setChecked(true);

                    } else if (id == R.id.nav_my_wishlist) {
                        signInDialog.dismiss();
                        gotoFragment(getString(R.string.available_to_eat), new MyWishlistFragment(), WISHLIST_FRAGMENT);
                        navigationView.getMenu().getItem(1).setChecked(true);
                    } else if (id == R.id.nav_my_speciallist) {
                        signInDialog.dismiss();
                        gotoFragment(getString(R.string.special_of_the_day), new MySpeciallistFragment(), SPECIALLIST_FRAGMENT);
                        navigationView.getMenu().getItem(2).setChecked(true);
                    } else if (id == R.id.nav_my_rewards) {
                        signInDialog.dismiss();

                        gotoFragment("About", new MyRewardsFragment(), REWARDS_FRAGMENT);
                        navigationView.getMenu().getItem(3).setChecked(true);

                    } else {
                        signInDialog.dismiss();
                    }

                }
            });


            return false;


        }

    }

    private void setFragment(Fragment fragment, int fragmentNo) {

        if (fragmentNo != currentFragment) {
            if (fragmentNo == REWARDS_FRAGMENT) {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            } else {

                window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            }
            currentFragment = fragmentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();

        }
    }

}

