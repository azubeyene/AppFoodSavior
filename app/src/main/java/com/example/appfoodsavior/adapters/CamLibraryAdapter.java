package com.example.appfoodsavior.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appfoodsavior.R;
import com.example.appfoodsavior.activities.CameraActivity;
import com.example.appfoodsavior.parseitems.InventoryFood;
import com.parse.ParseException;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CamLibraryAdapter extends RecyclerView.Adapter<CamLibraryAdapter.ViewHolder> {
    private Context context;
    private List<String> photoUrlList;
    private List<InventoryFood> inventoryFoodList;
    /*
    public CamLibraryAdapter(Context context, List<String> photoUrlList) {
        this.context = context;
        this.photoUrlList = photoUrlList;
    }

     */

    public CamLibraryAdapter(Context context, List<InventoryFood> inventoryFoodList) {
        this.context = context;
        this.inventoryFoodList = inventoryFoodList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_foodgrid_pic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        InventoryFood inventoryFood = inventoryFoodList.get(position);
        holder.bind(inventoryFood);
    }

    @Override
    public int getItemCount() {
        return inventoryFoodList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivFoodGridPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodGridPic = itemView.findViewById(R.id.ivFoodGridPic);

        }

        public void bind(final InventoryFood inventoryFood) {
            Glide.with(context).load(inventoryFood.getImage().getUrl()).into(ivFoodGridPic);
            ivFoodGridPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //navigate back to main avitivty
                    Intent intent = ((CameraActivity) context).getIntent();
                    //now pass the file photo back to main activity
                    //intent.putExtra("photoUrl", photoUrl);
                    try {
                        intent.putExtra("selectedPhotoFile", inventoryFood.getImage().getFile());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ((CameraActivity) context).setResult(RESULT_OK, intent);
                    ((CameraActivity) context).finish();

                }
            });
        }
    }
}
