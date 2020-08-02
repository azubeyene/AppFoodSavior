package com.example.appfoodsavior;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.appfoodsavior.fragments.CamPhotoFragment;

import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CamLibraryAdapter extends RecyclerView.Adapter<CamLibraryAdapter.ViewHolder> {
    private Context context;
    private List<String> photoUrlList;

    public CamLibraryAdapter(Context context, List<String> photoUrlList) {
        this.context = context;
        this.photoUrlList = photoUrlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_foodgrid_pic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String photoUrl = photoUrlList.get(position);
        holder.bind(photoUrl);
    }

    @Override
    public int getItemCount() {
        return photoUrlList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivFoodGridPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoodGridPic = itemView.findViewById(R.id.ivFoodGridPic);

        }

        public void bind(final String photoUrl) {
            Glide.with(context).load(photoUrl).into(ivFoodGridPic);
            ivFoodGridPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //navigate back to main avitivty
                    Intent intent = ((CameraActivity) context).getIntent();
                    //now pass the file photo back to main activity
                    intent.putExtra("photoUrl", photoUrl);
                    ((CameraActivity) context).setResult(RESULT_OK, intent);
                    ((CameraActivity) context).finish();

                }
            });
        }
    }
}
