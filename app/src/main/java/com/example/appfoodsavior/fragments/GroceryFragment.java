package com.example.appfoodsavior.fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.example.appfoodsavior.activities.ComposeGroceryActivity;
import com.example.appfoodsavior.adapters.GroceryAdapter;
import com.example.appfoodsavior.parseitems.GroceryFood;
import com.example.appfoodsavior.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroceryFragment extends Fragment {
    public static final String TAG = "GroceryFragment";
    public static final int GROCERY_CREATE__ACTIVITY_REQUEST_CODE = 71;
    private RecyclerView rvGroceryFood;
    private List<GroceryFood> groceryFoods;
    private GroceryAdapter adapter;
    private GroceryFood deletedItem;
    private FloatingActionButton floatingActionButton;

    public GroceryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_grocery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvGroceryFood = view.findViewById(R.id.rvGroceryFood);
        groceryFoods = new ArrayList<>();
        //create adapter
        adapter = new GroceryAdapter(getContext(), groceryFoods);
        rvGroceryFood.setAdapter(adapter);
        rvGroceryFood.setLayoutManager(new LinearLayoutManager(getContext()));
        floatingActionButton = view.findViewById(R.id.fabAddGroceryFood);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate to ComposeGroceryActivity
                //Navigate to ComposeInventoryActivity; onActivity Result, manually insert
                Intent i = new Intent(getContext(), ComposeGroceryActivity.class);
                startActivityForResult(i, GROCERY_CREATE__ACTIVITY_REQUEST_CODE);
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(rvGroceryFood);

        queryGroceryFoods();
    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    deletedItem = groceryFoods.get(position);
                    groceryFoods.remove(position);
                    adapter.notifyItemRemoved(position);

                    Snackbar.make(rvGroceryFood, deletedItem.getName(), BaseTransientBottomBar.LENGTH_LONG).addCallback(new Snackbar.Callback(){
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            if (event ==Snackbar.Callback.DISMISS_EVENT_TIMEOUT){
                                deletedItem.deleteInBackground(new DeleteCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Toast.makeText(getContext(), "Deleted from Parse", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Have to create new object with same attributes
                            groceryFoods.add(position, deletedItem);
                            adapter.notifyItemInserted(position);
                            if (position == 0){
                                rvGroceryFood.smoothScrollToPosition(0);
                            }
                        }
                    }).show();
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.deleteRed))
                    .addSwipeLeftActionIcon(R.drawable.ic_delete_black_24dp)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private boolean copyOver(GroceryFood deletedItem, GroceryFood groceryFood) throws ParseException, JSONException {
        //Prety much iterrate through all elements and set equal
        if (deletedItem.getName()==null || deletedItem.getAmount()==null ||deletedItem.getDescription()==null ||
                deletedItem.getBrand()==null ||deletedItem.getImage()==null) {
            Log.i(TAG, "name "+ Boolean.toString(deletedItem.getName()==null));
            Log.i(TAG, "buyOn "+Boolean.toString(deletedItem.getBuyOn()==null));
            Log.i(TAG, "amount "+Boolean.toString(deletedItem.getAmount()==null));
            Log.i(TAG, "description "+Boolean.toString(deletedItem.getDescription()==null));
            Log.i(TAG, "brand "+Boolean.toString(deletedItem.getBrand()==null));
            Log.i(TAG, "image "+Boolean.toString(deletedItem.getImage()==null));
            return true;
        }

        groceryFood.setName(deletedItem.getName());
        //groceryFood.setBuyOn(deletedItem.getBuyOn());
        groceryFood.setAmount(deletedItem.getAmount());
        groceryFood.setDescription(deletedItem.getDescription());
        groceryFood.setBrand(deletedItem.getBrand());
        groceryFood.setUser(ParseUser.getCurrentUser());
        double real_price = Double.valueOf(deletedItem.getPrice());
        groceryFood.setPrice(real_price);
        groceryFood.setImage(new ParseFile(deletedItem.getImage().getFile()));
        return false;
    }

    private void queryGroceryFoods() {
        //TODO: make a query request
        ParseQuery<GroceryFood> query = ParseQuery.getQuery(GroceryFood.class);
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        query.include(GroceryFood.KEY_USER);
        query.setLimit(30);
        query.addDescendingOrder(GroceryFood.KEY_CREATED);

        query.findInBackground(new FindCallback<GroceryFood>() {
            @Override
            public void done(List<GroceryFood> mgroceryFoods, ParseException e) {
                //Do some proccessing like sending to adapter
                if (e!=null){
                    Log.e(TAG, "error with querying Inventory Food", e);
                    return;
                }
                //All posts valid here
                //groceryFoods.addAll(mgroceryFoods);
                //adapter.notifyDataSetChanged();
                adapter.addAll(mgroceryFoods);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GROCERY_CREATE__ACTIVITY_REQUEST_CODE){
            if (resultCode==RESULT_OK){
                //retrieve inventory item by Object ID
                Log.i(TAG, data.getStringExtra("groceryFoodID"));

                ParseQuery<GroceryFood> query = ParseQuery.getQuery(GroceryFood.class);
                query.getInBackground(data.getStringExtra("groceryFoodID"), new GetCallback<GroceryFood>() {
                    @Override
                    public void done(GroceryFood foodobject, ParseException e) {
                        groceryFoods.add(0, foodobject);
                        adapter.notifyItemInserted(0);
                        rvGroceryFood.smoothScrollToPosition(0);
                    }
                });

            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.grocery_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.grocery_search:
                final MenuItem searchItem = item;
                androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
                searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //Closes search on done
                        searchItem.collapseActionView();
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.getFilter().filter(newText);
                        return false;
                    }
                });
                break;

            case R.id.grocery_filter:
                openFilterDialog();
                Toast.makeText(getContext(), "Filter", Toast.LENGTH_SHORT).show();
                break;
            case R.id.grocery_settings:
                Toast.makeText(getContext(), "Settings", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void openFilterDialog() {
        //TODO: layout_filter_inventory; InvetoryFilterDialog implement
    }
}
