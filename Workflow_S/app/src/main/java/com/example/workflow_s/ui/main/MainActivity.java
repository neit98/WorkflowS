package com.example.workflow_s.ui.main;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.example.workflow_s.R;
import com.example.workflow_s.ui.activity.ActivityFragment;
import com.example.workflow_s.ui.checklist.ChecklistFragment;
import com.example.workflow_s.ui.home.HomeFragment;
import com.example.workflow_s.ui.login.LoginActivity;
import com.example.workflow_s.ui.notification.NotificationFragment;
import com.example.workflow_s.ui.organization.OrganizationFragment;
import com.example.workflow_s.ui.setting.fragment.SettingFragment;
import com.example.workflow_s.ui.template.TemplateFragment;
import com.example.workflow_s.utils.SharedPreferenceUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private NavigationView nvDrawer;
    private String[] mNavigationDrawerItemTitles;

    // on Navigation Drawer
    private ImageView mUserProfileImageView;
    private TextView mUserDisplayName, mUserEmail;

    //google
    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setupToolbar();
        setupNavigationDrawer();
        onNewIntent(getIntent());
        //setupDefaultFragment();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        //super.onNewIntent(intent);
        setIntent(intent);
        if (null != intent.getExtras() ){
            for (String key : intent.getExtras().keySet()) {
                String value = intent.getExtras().getString(key);
                Log.d("Notification", "Key: " + key + " Value: " + value);
            }
            //String notify = intent.getStringExtra("flag_notify");
                //Log.i("intent", "go to this");
                NotificationFragment notificationFragment = new NotificationFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .add(R.id.flContent, notificationFragment)
                        .commit();

        } else {
            setupDefaultFragment();
        }
    }

    private void setupDefaultFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(R.id.flContent, homeFragment)
                .commit();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextAppearance(this, R.style.Toolbar);
    }

    private void setupNavigationDrawer() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.nav_home_view);

        // HEADER VIEW ON NAV_VIEW
        View headerView = nvDrawer.getHeaderView(0);
        mUserProfileImageView = headerView.findViewById(R.id.img_header_menu);
        mUserDisplayName = headerView.findViewById(R.id.tv_header_username);
        mUserEmail = headerView.findViewById(R.id.tv_header_email);



        String profileUrlString = SharedPreferenceUtils.retrieveData(this, getString(R.string.pref_avatar));
        String userDisplayName = SharedPreferenceUtils.retrieveData(this, getString(R.string.pref_username));
        String userEmail = SharedPreferenceUtils.retrieveData(this, getString(R.string.pref_email));

        if (profileUrlString == null) {
            mUserProfileImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_avatar));
        } else {
            Glide.with(this).load(profileUrlString).into(mUserProfileImageView);
        }
        mUserDisplayName.setText(userDisplayName);
        mUserEmail.setText(userEmail);


        mNavigationDrawerItemTitles = getResources().getStringArray(R.array.menu_items_title);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.mipmap.menu_icon, getTheme());
        mActionBarDrawerToggle.setHomeAsUpIndicator(drawable);
        mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });


        setupDrawerContent(nvDrawer);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }
        });
    }


    // TODO - SET FRAGMENT TO MAIN HERE
    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        Class fragmentClass;
        boolean flag = false;
        switch (menuItem.getItemId()) {
            case R.id.nav_organization_fragment:
                fragmentClass = OrganizationFragment.class;
                flag = true;
                break;
            case R.id.nav_home_fragment:
                fragmentClass = HomeFragment.class;
                flag = true;
                break;
            case R.id.nav_template_fragment:
                fragmentClass = TemplateFragment.class;
                flag = true;
                break;
            case R.id.nav_checklist_fragment:
                fragmentClass = ChecklistFragment.class;
                flag = true;
                break;
            case R.id.nav_activity_fragment:
                fragmentClass = ActivityFragment.class;
                flag = true;
                break;
            case R.id.nav_settings_fragment:
                fragmentClass = SettingFragment.class;
                flag = true;
                break;
            case R.id.nav_logout:
                fragmentClass = HomeFragment.class;
                revokeAccess();
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            Bundle args = new Bundle();
            args.putInt("status_checklist", 0);
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (flag) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(R.id.flContent, fragment)
                    .commit();
        }



        menuItem.setChecked(true); // Highlight the selected item has been done by NavigationView
        setTitle(menuItem.getTitle());
        mDrawerLayout.closeDrawers();
    }

    //log out
    private void revokeAccess() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        SharedPreferenceUtils.clearDataUser(getApplicationContext());
                        finish();
                    }

                });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        return true;
//    }
////
////    @Override
////    public boolean onOptionsItemSelected(MenuItem item) {
////        Fragment fragment = null;
////        Class fragmentClass;
////        switch (item.getItemId()) {
//////            case R.id.action_search:
//////                Intent intent = new Intent(this, SearchActivity.class);
//////                startActivity(intent);
//////                return true;
////            case R.id.action_notif:
////                //Toast.makeText(this, "NOFITICATION", Toast.LENGTH_SHORT).show();
//////                fragmentClass = NotificationFragment.class;
//////                try {
//////                    fragment = (Fragment) fragmentClass.newInstance();
//////                } catch (Exception e) {
//////                    e.printStackTrace();
//////                }
//////
//////                FragmentManager fragmentManager = getSupportFragmentManager();
//////                fragmentManager
//////                        .beginTransaction()
//////                        .replace(R.id.flContent, fragment)
//////                        .commit();
////                return true;
////            default:
////                return super.onOptionsItemSelected(item);
////        }
//
//
//    }
}
