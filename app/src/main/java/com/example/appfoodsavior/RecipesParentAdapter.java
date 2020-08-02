package com.example.appfoodsavior;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RecipesParentAdapter extends RecyclerView.Adapter<RecipesParentAdapter.ViewHolder> {
    public static final String TAG = "RecipeParentAdapter";
    private Context context;
    private List<String> recipeCategoriesList;

    public RecipesParentAdapter(Context context, List<String> recipeCategoriesList) {
        this.context = context;
        this.recipeCategoriesList = recipeCategoriesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_recipe_parent, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String recipeCategory = recipeCategoriesList.get(position);
        holder.bind(recipeCategory);
    }

    @Override
    public int getItemCount() {
        return recipeCategoriesList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvParentRecipeCategory;
        private RecyclerView rvRecipesChild;
        private ArrayList<Recipe> recipeList;
        RecipesChildAdapter childAdapter;
        //rv adapter

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvParentRecipeCategory = itemView.findViewById(R.id.tvParentRecipeCategory);
            rvRecipesChild = itemView.findViewById(R.id.rvRecipesChild);
        }

        public void bind(String recipeCategory) {
            tvParentRecipeCategory.setText(recipeCategory);
            recipeList = new ArrayList<>();
            childAdapter = new RecipesChildAdapter(recipeList);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            rvRecipesChild.setLayoutManager(layoutManager);
            rvRecipesChild.setAdapter(childAdapter);

            queryCategoryRecipes(recipeCategory);
        }

        public void queryCategoryRecipes(final String recipeCategory){
            //TODO: make a query request
            //TODO: check if recipeCategory.equals(My Recipes ) and do logic, otherwise proceed as normal...
            ParseQuery<Recipe> query = ParseQuery.getQuery(Recipe.class);
            query.include(Recipe.KEY_USER);
            query.setLimit(10);
            query.addDescendingOrder(Recipe.KEY_CREATED);
            query.findInBackground(new FindCallback<Recipe>() {
                @Override
                public void done(List<Recipe> allRecipeList, ParseException e) {
                    if (e!=null){
                        Log.e(TAG, "error with querying Inventory Food", e);
                        return;
                    }
                    //we do a for loop
                    if (recipeCategory.equals("My Recipes")){
                        //we want to add italian stuff
                        for (int i = 0; i<allRecipeList.size(); i++){
                            try {
                                if (allRecipeList.get(i).getTags().contains("italian")){
                                    recipeList.add(allRecipeList.get(i));
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                    } else {
                        for (int i = 0; i<allRecipeList.size(); i++){
                            try {
                                if (allRecipeList.get(i).getTags().contains(recipeCategory)){
                                    recipeList.add(allRecipeList.get(i));
                                }
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                    childAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
