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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.appfoodsavior.activities.ComposeInventoryActivity;
import com.example.appfoodsavior.parseitems.GroceryFood;
import com.example.appfoodsavior.adapters.InventoryAdapter;
import com.example.appfoodsavior.parseitems.InventoryFood;
import com.example.appfoodsavior.parseitems.PreviousInventory;
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
public class InventoryFragment extends Fragment {
    public static final String TAG = "InventoryFragment";
    public static final int INVENTORY_CREATE__ACTIVITY_REQUEST_CODE = 78;
    private RecyclerView rvInventoryFood;
    private List<InventoryFood> inventoryFoods;
    private InventoryAdapter adapter;
    private InventoryFood deletedItem;
    private InventoryFood movedItem;
    private FloatingActionButton floatingActionButton;


    public InventoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvInventoryFood = view.findViewById(R.id.rvInventoryFood);
        // we create an adapter, pass in these parameters
        inventoryFoods = new ArrayList<>();
        adapter = new InventoryAdapter(getContext(), inventoryFoods);
        rvInventoryFood.setAdapter(adapter);
        rvInventoryFood.setLayoutManager(new LinearLayoutManager(getContext()));
        floatingActionButton = view.findViewById(R.id.fabAddInventoryFood);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate to ComposeInventoryActivity; onActivity Result, manually insert
                Intent i = new Intent(getContext(), ComposeInventoryActivity.class);
                startActivityForResult(i, INVENTORY_CREATE__ACTIVITY_REQUEST_CODE);

                //Fragment fragment = new ComposeFragment();
                //((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(rvInventoryFood);

        queryInventoryFood();

    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    final PreviousInventory previousInventory = new PreviousInventory();
                    deletedItem = inventoryFoods.get(position);
                    deletedItem.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            inventoryFoods.remove(position);
                            adapter.notifyItemRemoved(position);
                            //Create previousInventory item
                            try {
                                copyOverPrevInv(deletedItem, previousInventory);
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            } catch (ParseException ex) {
                                ex.printStackTrace();
                            }
                            previousInventory.saveInBackground();
                        }
                    });

                    Snackbar.make(rvInventoryFood, "Deleted " + deletedItem.getName(), BaseTransientBottomBar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final InventoryFood newInventoryItem = new InventoryFood();
                            //Save Deleted Item
                            try {
                                copyOverInv(deletedItem, newInventoryItem);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            newInventoryItem.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    inventoryFoods.add(position, deletedItem);
                                    adapter.notifyItemInserted(position);
                                    if (position ==0){
                                        rvInventoryFood.smoothScrollToPosition(0);
                                    }
                                    //Delete Previous Item
                                    previousInventory.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Toast.makeText(getContext(), "we deleted inventory item", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    //TODO: Implement Snackbar like above
                    //We also need to delete it, then reinsert it
                    movedItem = inventoryFoods.get(position);
                    //Handle the animation
                    inventoryFoods.remove(position);
                    adapter.notifyItemRemoved(position);

                    //create Grocery item

                    final GroceryFood groceryFood = new GroceryFood();
                    copyOverGroc(movedItem, groceryFood);
                    groceryFood.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e!=null){
                                Log.e(TAG, "error in creating grocery item from inventory items", e);
                            }
                            inventoryFoods.add(position, movedItem);
                            adapter.notifyItemInserted(position);
                        }
                    });
                    Snackbar.make(rvInventoryFood, "Added " + movedItem.getName() + " to Grocery List", BaseTransientBottomBar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            groceryFood.deleteInBackground();
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
                    .addSwipeLeftLabel("Delete")
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentOriginal))
                    .addSwipeRightActionIcon(R.drawable.ic_grocery_list_icon)
                    .addSwipeRightLabel("Create Grocery Item")
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void copyOverGroc(InventoryFood movedItem, GroceryFood groceryFood) {
        groceryFood.setName(movedItem.getName());
        groceryFood.setBrand(movedItem.getBrand());
        groceryFood.setDescription(movedItem.getDescription());
        groceryFood.setPrice(movedItem.getPrice());
        try {
            groceryFood.setAmount(movedItem.getAmount());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        groceryFood.setUser(ParseUser.getCurrentUser());
        //Set Image: get image file -> new ParseFile
        try {
            groceryFood.setImage(new ParseFile(movedItem.getImage().getFile()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return;
    }

    private void copyOverInv(InventoryFood deletedItem, InventoryFood newInventoryItem) throws JSONException, ParseException {
        //Prety much iterrate through all elements and set equal
        if (deletedItem.getName()==null || deletedItem.getAmount()==null ||deletedItem.getDescription()==null ||
                deletedItem.getBrand()==null ||deletedItem.getImage()==null || deletedItem.getExpiration()==null) {
            Log.i(TAG, "name "+ Boolean.toString(deletedItem.getName()==null));
            Log.i(TAG, "expiration "+Boolean.toString(deletedItem.getExpiration()==null));
            Log.i(TAG, "amount "+Boolean.toString(deletedItem.getAmount()==null));
            Log.i(TAG, "description "+Boolean.toString(deletedItem.getDescription()==null));
            Log.i(TAG, "brand "+Boolean.toString(deletedItem.getBrand()==null));
            Log.i(TAG, "image "+Boolean.toString(deletedItem.getImage()==null));
            return;
        }
        newInventoryItem.setUser(ParseUser.getCurrentUser());
        newInventoryItem.setName(deletedItem.getName());
        newInventoryItem.setImage(new ParseFile(deletedItem.getImage().getFile()));
        newInventoryItem.setExpiration(deletedItem.getExpiration());
        newInventoryItem.setAmount(deletedItem.getAmount());
        double real_price = Double.valueOf(deletedItem.getPrice());
        newInventoryItem.setPrice(real_price);
        newInventoryItem.setDescription(deletedItem.getDescription());
        newInventoryItem.setBrand(deletedItem.getBrand());
        newInventoryItem.setCalories(deletedItem.getCalories());
        newInventoryItem.setPrevPhoto(deletedItem.getPrevPhoto());
        newInventoryItem.setImage(new ParseFile(deletedItem.getImage().getFile()));
        return;
    }

    private void copyOverPrevInv(InventoryFood deletedItem, PreviousInventory previousInventory) throws JSONException, ParseException {
        //Prety much iterrate through all elements and set equal
        if (deletedItem.getName()==null || deletedItem.getAmount()==null ||deletedItem.getDescription()==null ||
                deletedItem.getBrand()==null ||deletedItem.getImage()==null || deletedItem.getExpiration()==null) {
            Log.i(TAG, "name "+ Boolean.toString(deletedItem.getName()==null));
            Log.i(TAG, "expiration "+Boolean.toString(deletedItem.getExpiration()==null));
            Log.i(TAG, "amount "+Boolean.toString(deletedItem.getAmount()==null));
            Log.i(TAG, "description "+Boolean.toString(deletedItem.getDescription()==null));
            Log.i(TAG, "brand "+Boolean.toString(deletedItem.getBrand()==null));
            Log.i(TAG, "image "+Boolean.toString(deletedItem.getImage()==null));
            return;
        }
        previousInventory.setUser(ParseUser.getCurrentUser());
        previousInventory.setName(deletedItem.getName());
        previousInventory.setImage(new ParseFile(deletedItem.getImage().getFile()));
        previousInventory.setExpiration(deletedItem.getExpiration());
        previousInventory.setAmount(deletedItem.getAmount());
        double real_price = Double.valueOf(deletedItem.getPrice());
        previousInventory.setPrice(real_price);
        previousInventory.setDescription(deletedItem.getDescription());
        previousInventory.setBrand(deletedItem.getBrand());
        previousInventory.setCalories(deletedItem.getCalories());
        previousInventory.setPrevPhoto(deletedItem.getPrevPhoto());
        previousInventory.setImage(new ParseFile(deletedItem.getImage().getFile()));
        return;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==INVENTORY_CREATE__ACTIVITY_REQUEST_CODE){
            if (resultCode==RESULT_OK){
                //retrieve inventory item by Object ID
                if (data.getStringExtra("inventoryFoodID")!=null){
                    ParseQuery<InventoryFood> query = ParseQuery.getQuery(InventoryFood.class);
                    query.getInBackground(data.getStringExtra("inventoryFoodID"), new GetCallback<InventoryFood>() {
                        @Override
                        public void done(InventoryFood foodobject, ParseException e) {
                            //maually insert to inventoryFoods and notify adapter
                            inventoryFoods.add(0, foodobject);
                            adapter.notifyItemInserted(0);
                            rvInventoryFood.smoothScrollToPosition(0);
                        }
                    });
                }

            }
        }
    }

    private void queryInventoryFood() {
        //TODO: make a query request
        ParseQuery<InventoryFood> query = ParseQuery.getQuery(InventoryFood.class);
        query.include(InventoryFood.KEY_USER);
        query.setLimit(30);
        query.addDescendingOrder(InventoryFood.KEY_CREATED);

        query.findInBackground(new FindCallback<InventoryFood>() {
            @Override
            public void done(List<InventoryFood> minventoryFoods, ParseException e) {
                //Do some proccessing like sending to adapter
                if (e!=null){
                    Log.e(TAG, "error with querying Inventory Food", e);
                    return;
                }
                //All posts valid here
                inventoryFoods.addAll(minventoryFoods);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
