package com.example.appfoodsavior.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appfoodsavior.R;
import com.example.appfoodsavior.activities.GroceryDetailsActivity;
import com.example.appfoodsavior.parseitems.GroceryFood;

import org.json.JSONException;

import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> {
    public static final String TAG = "GroceryAdapter";
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
        try {
            holder.bind(groceryFood);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return groceryFoods.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivGroceryFoodPic;
        private TextView tvGroceryFoodName;
        private TextView tvGroceryFoodBrand;
        private TextView tvGroceryDescript;
        private TextView tvGroceryBuyOn;
        private TextView tvGroceryPrice;
        private TextView tvGroceryAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGroceryFoodPic = itemView.findViewById(R.id.ivGroceryFoodPic);

            tvGroceryFoodName = itemView.findViewById(R.id.tvGroceryFoodName);
            tvGroceryFoodBrand = itemView.findViewById(R.id.tvGroceryFoodBrand);
            tvGroceryDescript = itemView.findViewById(R.id.tvGroceryDescript);
            tvGroceryBuyOn = itemView.findViewById(R.id.tvGroceryBuyOn);
            tvGroceryPrice = itemView.findViewById(R.id.tvGroceryPrice);
            tvGroceryAmount = itemView.findViewById(R.id.tvGroceryAmount);
            itemView.setOnClickListener(this);
        }

        public void bind(GroceryFood groceryFood) throws JSONException {
            tvGroceryFoodName.setText(groceryFood.getName());
            tvGroceryDescript.setText(groceryFood.getDescription());
            tvGroceryBuyOn.setText(groceryBuyReformat(groceryFood));
            tvGroceryPrice.setText(groceryPriceReformat(groceryFood));

            if (groceryFood.getBrand()!=null){
                tvGroceryFoodBrand.setText(groceryFood.getBrand());
            }
            if (groceryFood.getImage()!=null){
                Glide.with(context).load(groceryFood.getImage().getUrl()).into(ivGroceryFoodPic);
            }

            //Displaying food amount
            if (groceryFood.getAmount() !=null ){
                Log.i(TAG, "This has time object");
                String full_amount = groceryFood.getAmount().get(0) + " " + groceryFood.getAmount().get(1);
                tvGroceryAmount.setText(full_amount);
            }

        }

        private String groceryPriceReformat(GroceryFood groceryFood) {
            //We assume default value is zero
            String priceAsString = String.valueOf(groceryFood.getPrice());
            int indexOfDecimal = priceAsString.indexOf(".");
            String decimalPrice = priceAsString.substring(indexOfDecimal+1);
            Log.i("grocery adapter", decimalPrice);
            if (decimalPrice.length()>2){
                decimalPrice = decimalPrice.substring(0, 2);
            } else if (decimalPrice.length()==1){
                decimalPrice = decimalPrice+"0";
            }

            priceAsString = "$" + priceAsString.substring(0, indexOfDecimal) + "." + decimalPrice;
            return priceAsString;
        }

        private String groceryBuyReformat(GroceryFood groceryFood) {
            if (groceryFood.getBuyOn()!=null){
                //TODO: html insert a bolded Exp: upfront
                String input = groceryFood.getBuyOn().toString();
                String new_input = input.substring(4, 10);
                //String sourceString = "Buy by " + new_input;
                String sourceString = "<b>" + "Buy" + "</b> " + new_input;
                String good_exp = Html.fromHtml(sourceString).toString();
                return good_exp;
            } else{
                return "Enter Buy By Date";
            }
        }

        @Override
        public void onClick(View view) {
            GroceryFood groceryFood = groceryFoods.get(getAdapterPosition());

            Intent i = new Intent(context, GroceryDetailsActivity.class);
            i.putExtra("inventoryFoodID", groceryFood.getObjectId());
            //pass inventoryFood object ID and then query
            context.startActivity(i);
        }
    }
}
