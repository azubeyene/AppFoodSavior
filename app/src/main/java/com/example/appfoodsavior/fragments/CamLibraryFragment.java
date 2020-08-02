package com.example.appfoodsavior.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appfoodsavior.CamLibraryAdapter;
import com.example.appfoodsavior.GroceryFood;
import com.example.appfoodsavior.InventoryFood;
import com.example.appfoodsavior.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CamLibraryFragment extends Fragment {
    public static final String TAG = "CamLibrarFragment";
    private RecyclerView rvLibraryPhotos;
    private List<String> photoUrlList;
    private CamLibraryAdapter adapter;

    public CamLibraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cam_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //here we define a gridview recylcer view
        rvLibraryPhotos = view.findViewById(R.id.rvLibraryPhotos);
        photoUrlList = new ArrayList<>();
        adapter = new CamLibraryAdapter(getContext(), photoUrlList);
        rvLibraryPhotos.setAdapter(adapter);
        rvLibraryPhotos.setLayoutManager(new GridLayoutManager(getContext(), 3));
        queryPhotosInventory();

    }

    private void queryPhotosInventory(){
        ParseQuery<InventoryFood> query = ParseQuery.getQuery(InventoryFood.class);
        query.whereEqualTo(InventoryFood.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(40);
        query.addDescendingOrder(InventoryFood.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<InventoryFood>() {
            @Override
            public void done(List<InventoryFood> inventoryFoodList, ParseException e) {
                if (e!=null){
                    Log.e(TAG, "error in getting inventory foods for photos", e);
                }
                for (InventoryFood inventoryFood: inventoryFoodList){
                    if (!inventoryFood.getPrevPhoto()){
                        photoUrlList.add(inventoryFood.getImage().getUrl());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
