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
import com.example.appfoodsavior.activities.InventoryDetailsActivity;
import com.example.appfoodsavior.parseitems.InventoryFood;

import org.json.JSONException;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {
    public static final String TAG = "InventoryAdapter";
    private Context context;
    private List<InventoryFood> inventoryFoods;

    public InventoryAdapter(Context context, List<InventoryFood> inventoryFoods) {
        this.context = context;
        this.inventoryFoods = inventoryFoods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food_inventory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryFood inventoryFood = inventoryFoods.get(position);
        try {
            holder.bind(inventoryFood);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return inventoryFoods.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView ivInventoryFoodPic;
        private TextView tvInventoryFoodName;
        private TextView tvInventoryFoodExp;
        private TextView tvInventoryAmount;
        private TextView tvInventoryDescript;
        private TextView tvInventoryCalories;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivInventoryFoodPic = itemView.findViewById(R.id.ivInventoryFoodPic);
            tvInventoryFoodName = itemView.findViewById(R.id.tvInventoryFoodName);
            tvInventoryFoodExp = itemView.findViewById(R.id.tvInventoryFoodExp);

            tvInventoryAmount = itemView.findViewById(R.id.tvInventoryAmount);
            tvInventoryDescript = itemView.findViewById(R.id.tvInventoryDescript);
            tvInventoryCalories = itemView.findViewById(R.id.tvInventoryCalories);

            itemView.setOnClickListener(this);
        }

        public void bind(final InventoryFood inventoryFood) throws JSONException {
            //bind inventoryFood to view holder
            Log.i(TAG, "binding view");
            tvInventoryFoodName.setText(inventoryFood.getName());
            tvInventoryFoodExp.setText(inventoryExpReformat(inventoryFood));
            tvInventoryDescript.setText(invetoryDescriptReformat(inventoryFood));
            tvInventoryCalories.setText("Cal. " + Integer.toString((int) inventoryFood.getCalories()));

            if (inventoryFood.getImage()!=null){
                Glide.with(context).load(inventoryFood.getImage().getUrl()).into(ivInventoryFoodPic);
            }
            //Displaying food amount
            if (inventoryFood.getAmount() !=null ){
                Log.i(TAG, "This has time object");
                String full_amount = inventoryFood.getAmount().get(0) + " " + inventoryFood.getAmount().get(1);
                tvInventoryAmount.setText(full_amount);
            }
        }

        private String inventoryPriceReformat(InventoryFood inventoryFood) {
            if (inventoryFood.getPrice()>-1){
                //tvInventoryPrice.setVisibility(View.VISIBLE);
                return "$"+Double.toString(inventoryFood.getPrice());
            } else{
                //Make it gone
                //tvInventoryPrice.setVisibility(View.GONE);
                return "";
            }
        }

        private String invetoryDescriptReformat(InventoryFood inventoryFood) {
            if (inventoryFood.getDescription()!=null){
                //tvInventoryDescript.setVisibility(View.VISIBLE);
                return inventoryFood.getDescription();
            } else {
                //Make it gone
                //tvInventoryDescript.setVisibility(View.GONE);
                return "";
            }

        }

        private String inventoryExpReformat(InventoryFood inventoryFood) {
            if (inventoryFood.getExpiration()!=null){
                //TODO: html insert a bolded Exp: upfront
                String input = inventoryFood.getExpiration().toString();
                String new_input = input.substring(4, 10) + input.substring(23);
                String sourceString = "<b>" + "Exp:" + "</b> " + new_input;
                String good_exp = Html.fromHtml(sourceString).toString();
                return good_exp;
            } else{
                return "No Expiration";
            }
        }

        @Override
        public void onClick(View view) {
            InventoryFood inventoryFood = inventoryFoods.get(getAdapterPosition());

            Intent i = new Intent(context, InventoryDetailsActivity.class);
            i.putExtra("inventoryFoodID", inventoryFood.getObjectId());
            //pass inventoryFood as parcelable
            //or pass inventoryFood object ID and then query
            context.startActivity(i);

        }
    }
}
