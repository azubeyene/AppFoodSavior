package com.example.appfoodsavior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appfoodsavior.fragments.ComposeFragment;
import com.example.appfoodsavior.fragments.GroceryFragment;
import com.example.appfoodsavior.fragments.InventoryFragment;
import com.example.appfoodsavior.fragments.ProfileFragment;
import com.example.appfoodsavior.fragments.RecipesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    public static final String TAG = "MainActivity";
    private DrawerLayout drawer;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout flContainer;
    private Toolbar toolbar;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_example:
                        Toast.makeText(MainActivity.this, "Example clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.nav_Settings:
                        fragmentManager.beginTransaction().replace(R.id.flContainer, new FakeSettingsFragment()).commit();
                        break;

                }
                drawer.closeDrawer(GravityCompat.START );
                return true;
            }
        });
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        flContainer = findViewById(R.id.flContainer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
        R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //TODO: When this is click, deselect anything in all drawer menus.
                switch (menuItem.getItemId()) {
                    case R.id.action_recipe:
                        fragment = new RecipesFragment();
                        getSupportActionBar().setTitle("Recipes");
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        getSupportActionBar().setTitle("Profile");
                        break;
                    case R.id.action_compose:
                        fragment = new ComposeFragment();
                        getSupportActionBar().setTitle("Compose");
                        break;
                    case R.id.action_inventory:
                        fragment = new InventoryFragment();
                        getSupportActionBar().setTitle("Inventory");
                        break;
                    case R.id.action_grocery:
                        fragment = new GroceryFragment();
                        getSupportActionBar().setTitle("Grocery List");
                        break;
                    default:
                        fragment = new ComposeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_inventory);

        //queryInventoryFood();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
