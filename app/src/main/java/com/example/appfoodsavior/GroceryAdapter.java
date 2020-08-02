package com.example.appfoodsavior;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {
    private Context context;
    private List<GroceryFood> groceryFoods;

    public GroceryAdapter(Context context, List<GroceryFood> groceryFoods) {
        this.context = context;
        this.groceryFoods = groceryFoods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_grocery, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroceryFood groceryFood = groceryFoods.get(position);
        holder.bind(groceryFood);
    }

    @Override
    public int getItemCount() {
        return groceryFoods.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivGroceryFoodPic;
        private TextView tvGroceryFoodName;
        private TextView tvGroceryFoodStore;
        private TextView tvGroceryDescript;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGroceryFoodPic = itemView.findViewById(R.id.ivGroceryFoodPic);
            tvGroceryFoodName = itemView.findViewById(R.id.tvGroceryFoodName);
            tvGroceryFoodStore = itemView.findViewById(R.id.tvGroceryFoodStore);
            tvGroceryDescript = itemView.findViewById(R.id.tvGroceryDescript);
        }

        public void bind(GroceryFood groceryFood) {
            tvGroceryFoodName.setText(groceryFood.getName());
            if (groceryFood.getStore()!=null){
                tvGroceryFoodStore.setText(groceryFood.getStore());
            }
            if (groceryFood.getImage()!=null){
                Glide.with(context).load(groceryFood.getImage().getUrl()).into(ivGroceryFoodPic);
            }
            tvGroceryDescript.setText(groceryFood.getDescription());

        }
    }
}
