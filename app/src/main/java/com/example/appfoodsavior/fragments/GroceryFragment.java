package com.example.appfoodsavior.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appfoodsavior.GroceryAdapter;
import com.example.appfoodsavior.GroceryFood;
import com.example.appfoodsavior.InventoryFood;
import com.example.appfoodsavior.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroceryFragment extends Fragment {
    public static final String TAG = "GroceryFragment";
    private RecyclerView rvGroceryFood;
    private List<GroceryFood> groceryFoods;
    private GroceryAdapter adapter;

    public GroceryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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

        queryGroceryFoods();
    }

    private void queryGroceryFoods() {
        //TODO: make a query request
        ParseQuery<GroceryFood> query = ParseQuery.getQuery(GroceryFood.class);
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
                groceryFoods.addAll(mgroceryFoods);
                adapter.notifyDataSetChanged();
            }
        });
    }
}
