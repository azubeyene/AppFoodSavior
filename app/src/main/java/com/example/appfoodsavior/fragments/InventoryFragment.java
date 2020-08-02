package com.example.appfoodsavior.fragments;

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

import com.example.appfoodsavior.GroceryFood;
import com.example.appfoodsavior.InventoryAdapter;
import com.example.appfoodsavior.InventoryFood;
import com.example.appfoodsavior.MainActivity;
import com.example.appfoodsavior.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 */
public class InventoryFragment extends Fragment {
    public static final String TAG = "InventoryFragment";
    private RecyclerView rvInventoryFood;
    private List<InventoryFood> inventoryFoods;
    private InventoryAdapter adapter;
    private InventoryFood deletedItem;
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
                //We open a new activity with compose inside of it.
                //For now we just open/exhange to create fragment as it
                //Get fragment manager, the exchange to compose
                Fragment fragment = new ComposeFragment();
                ((MainActivity) getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.flContainer, fragment).addToBackStack(null).commit();
            }
        });

        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(rvInventoryFood);

        queryInventoryFood();

    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT| ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                    deletedItem = inventoryFoods.get(position);
                    inventoryFoods.remove(position);
                    adapter.notifyItemRemoved(position);
                    Snackbar.make(rvInventoryFood, deletedItem.getName(), BaseTransientBottomBar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            inventoryFoods.add(position, deletedItem);
                            adapter.notifyItemInserted(position);
                        }
                    }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    //Add Item to grocery List: TODO: edit when we make grocery list and/or inventory foods more descriptive
                    Toast.makeText(getContext(), "swipe right adds grocery item", Toast.LENGTH_SHORT).show();
                    //TODO: Implement Snackbar like above
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
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentOriginal))
                    .addSwipeRightActionIcon(R.drawable.ic_grocery_list_icon_new)
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

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
