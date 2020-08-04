package com.example.appfoodsavior.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appfoodsavior.R;
import com.example.appfoodsavior.activities.RecipeDetailsActivity;
import com.example.appfoodsavior.parseitems.Recipe;

import java.util.List;

public class RecipesChildAdapter extends RecyclerView.Adapter<RecipesChildAdapter.ViewHolder> {
    //private List<Recipe> recipeList;
    private List<Recipe> recipeList;
    private Context context;

    public RecipesChildAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recipe_child, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe);

    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivChildRecipePhoto;
        private TextView tvChildRecipeName;
        private TextView tvRecipeCookTime;
        private TextView tvRecipeServes;
        private TextView tvRecipeCaloriesServing;
        private TextView tvRecipeDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivChildRecipePhoto = itemView.findViewById(R.id.ivChildRecipePhoto);
            tvChildRecipeName = itemView.findViewById(R.id.tvChildRecipeName);

            tvRecipeCookTime = itemView.findViewById(R.id.tvRecipeCookTime);
            tvRecipeServes = itemView.findViewById(R.id.tvRecipeServes);
            tvRecipeCaloriesServing = itemView.findViewById(R.id.tvRecipeCaloriesServing);
            tvRecipeDescription = itemView.findViewById(R.id.tvRecipeDescription);
            itemView.setOnClickListener(this);
        }

        public void bind(Recipe recipe) {
            //Bind the Photo and name; Set on Click Listener on item view...
            tvChildRecipeName.setText(recipe.getName());
            Glide.with(context).load(recipe.getImage().getUrl()).into(ivChildRecipePhoto);

            int newCookTime = (int) recipe.getCookTime();
            tvRecipeCookTime.setText(Integer.toString(newCookTime) + " min");
            tvRecipeDescription.setText(recipe.getDescription());

        }

        @Override
        public void onClick(View view) {
            Recipe recipe = recipeList.get(getAdapterPosition());

            Intent i = new Intent(context, RecipeDetailsActivity.class);
            i.putExtra("recipeItemID", recipe.getObjectId());
            context.startActivity(i);
        }
    }
}
