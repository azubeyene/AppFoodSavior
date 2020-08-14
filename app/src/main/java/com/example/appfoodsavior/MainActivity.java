package com.example.appfoodsavior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.appfoodsavior.fragments.ComposeFragment;
import com.example.appfoodsavior.fragments.FakeSettingsFragment;
import com.example.appfoodsavior.fragments.GroceryFragment;
import com.example.appfoodsavior.fragments.InventoryFragment;
import com.example.appfoodsavior.fragments.ProfileFragment;
import com.example.appfoodsavior.fragments.RecipesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final int INVENTORY_DETAILS__ACTIVITY_REQUEST_CODE = 100;
    //codes for grocery and recipe fragments


    public DrawerLayout drawer;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout flContainer;
    public Toolbar toolbar;
    final FragmentManager fragmentManager = getSupportFragmentManager();
    public static Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar_home);
        toolbar.setTitle("Inventeory");
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        flContainer = findViewById(R.id.flContainer);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //TODO: When this is click, deselect anything in all drawer menus.
                switch (menuItem.getItemId()) {
                    case R.id.action_recipe:
                        fragment = new RecipesFragment();
                        //getSupportActionBar().setTitle("Recipes");
                        toolbar.setTitle("Recipes");

                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        getSupportActionBar().setTitle("Profile");
                        break;
                    case R.id.action_inventory:
                        fragment = new InventoryFragment();
                        toolbar.setTitle("Inventory");
                        //toolbar.inflateMenu(R.menu.inventory_fragment_menu);
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
