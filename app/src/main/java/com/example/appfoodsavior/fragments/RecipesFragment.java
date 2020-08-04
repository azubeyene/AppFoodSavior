package com.example.appfoodsavior.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appfoodsavior.R;
import com.example.appfoodsavior.adapters.RecipesParentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecipesFragment extends Fragment {
    //This is going to be the parent rv
    private RecyclerView rvRecipeParent;
    private List<String> recipeCategoriesList;
    private RecipesParentAdapter adapter;

    public RecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recipeCategoriesList = new ArrayList<>();
        rvRecipeParent = view.findViewById(R.id.rvRecipeParent);

        adapter = new RecipesParentAdapter(getContext(), recipeCategoriesList);
        rvRecipeParent.setAdapter(adapter);
        rvRecipeParent.setLayoutManager(new LinearLayoutManager(getContext()));

        queryFoodCategories();

    }

    private void queryFoodCategories() {
        //TODO: get popular categories/tags from recipe API

        //For now, load fake data in
        recipeCategoriesList.add("My Recipes");
        recipeCategoriesList.add("vegan");
        recipeCategoriesList.add("italian");
        //we will always add myRecipes manually, even when we have the API. Make sure to check if myRecipes is already in list just to be sure

        //notify dataset change on adapter
        adapter.notifyDataSetChanged();


    }
}
