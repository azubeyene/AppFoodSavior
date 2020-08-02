 package com.example.appfoodsavior.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.appfoodsavior.CameraActivity;
import com.example.appfoodsavior.InventoryFood;
import com.example.appfoodsavior.R;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

 /**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends Fragment {
    public static final String TAG = "ComposeFragment";
    public static final int PHOTO_IMAGE_ACTIVITY_REQUEST_CODE = 90;
    private EditText etInventoryComposeName2;
    private EditText etInvComposExp;
    private EditText etInvComposAmountNum;
    private EditText etInvComposAmountUnits;
    private EditText etInvComposDescript;
    private ImageView ivComposeFoodPic;
    private Button btnAddInvItem;
    private File photoFile;
    private Bitmap photoBitMap;

    public ComposeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etInventoryComposeName2 = view.findViewById(R.id.etInventoryComposeName2);
        etInvComposExp = view.findViewById(R.id.etInvComposExp);
        etInvComposAmountNum = view.findViewById(R.id.etInvComposAmountNum);
        etInvComposAmountUnits = view.findViewById(R.id.etInvComposAmountUnits);
        etInvComposDescript = view.findViewById(R.id.etInvComposDescript);
        ivComposeFoodPic = view.findViewById(R.id.ivComposeFoodPic);
        btnAddInvItem = view.findViewById(R.id.btnAddInvItem);

        btnAddInvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if all requred fields are filled
                String name = etInventoryComposeName2.getText().toString();
                String expiration = etInvComposExp.getText().toString();
                String amount_num = etInvComposAmountNum.getText().toString();
                String amount_units = etInvComposAmountUnits.getText().toString();
                String description = etInvComposDescript.getText().toString();
                Date date_exp = null;
                ArrayList<String> arrayAmount = new ArrayList<>();

                if(notValidItem(name, expiration, amount_num, amount_units, description)){ return;}
                arrayAmount.add(amount_num);
                arrayAmount.add(amount_units);

                //Now convert date to Date object
                try {
                    date_exp = new SimpleDateFormat("MM/dd/yyyy").parse(expiration);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                createInventoryItem(name, date_exp, arrayAmount, description);
                //Navigate to Inventory Fragment();
            }
        });

        ivComposeFoodPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), CameraActivity.class);
                startActivityForResult(i, PHOTO_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

    }

     private void createInventoryItem(String name, Date date_exp, ArrayList<String> arrayAmount, String description) {
         InventoryFood inventoryFood = new InventoryFood();
         inventoryFood.setName(name);
         inventoryFood.setExpiration(date_exp);
         inventoryFood.setAmount(arrayAmount);
         inventoryFood.setDescription(description);
         inventoryFood.setUser(ParseUser.getCurrentUser());
         //Check if we take library photo or camera photo
         if (photoFile==null){
             ByteArrayOutputStream stream = new ByteArrayOutputStream();
             photoBitMap.compress(Bitmap.CompressFormat.PNG,100,stream);
             byte[] byteArray = stream.toByteArray();
             inventoryFood.setImage(new ParseFile("image.png",byteArray));
             inventoryFood.setPrevPhoto(true);
         } else{
             inventoryFood.setImage(new ParseFile(photoFile));
             inventoryFood.setPrevPhoto(false);
         }

         inventoryFood.saveInBackground(new SaveCallback() {
             @Override
             public void done(com.parse.ParseException e) {
                 if (e!=null){
                     Log.e(TAG, "Error occured while creating new inventory item", e);
                     return;
                 }
                 //Successful
                 Log.i(TAG, "Success on saving inventory food item");
                 etInventoryComposeName2.setText("");
                 etInvComposExp.setText("");
                 etInvComposAmountNum.setText("");
                 etInvComposAmountUnits.setText("");
                 etInvComposDescript.setText("");
                 ivComposeFoodPic.setImageResource(R.drawable.ic_camera_icon_filler);
             }
         });

     }

     private boolean notValidItem(String name, String expiration, String amount_num, String amount_units, String description) {
         if (name.isEmpty()){
             Toast.makeText(getContext(), "Name required for food", Toast.LENGTH_SHORT).show();
             return true;
         }
         if (expiration.isEmpty()){
             Toast.makeText(getContext(), "Expiration required for food", Toast.LENGTH_SHORT).show();
             return true;
         }
         if (amount_num.isEmpty()){
             Toast.makeText(getContext(), "Amount required for food", Toast.LENGTH_SHORT).show();
             return true;
         }
         if (amount_units.isEmpty()){
             Toast.makeText(getContext(), "Amount units required for food", Toast.LENGTH_SHORT).show();
             return true;
         }
         if (description.isEmpty()){
             Toast.makeText(getContext(), "Description required for food", Toast.LENGTH_SHORT).show();
             return true;
         }
         if (photoFile==null && photoBitMap==null ||  ivComposeFoodPic.getDrawable()==null){
             Toast.makeText(getContext(), "Photo required for food", Toast.LENGTH_SHORT).show();
             return true;
         }
         return false;
     }

     @Override
     public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode==PHOTO_IMAGE_ACTIVITY_REQUEST_CODE){
             if (resultCode==RESULT_OK){//Retrive Photo then add to image view
                 if (data.getExtras().get("photoUrl")!=null){
                     //Glide.with(getContext()).load(data.getExtras().get("photoUrl")).into(ivComposeFoodPic);
                     //Create bitmap then store it
                     Glide.with(getContext()).load(data.getExtras().get("photoUrl")).into(ivComposeFoodPic);
                     Glide.with(getContext()).asBitmap().load(data.getExtras().get("photoUrl")).into(new CustomTarget<Bitmap>() {
                         @Override
                         public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                             photoBitMap = resource;
                         }
                         @Override
                         public void onLoadCleared(@Nullable Drawable placeholder) {

                         }
                     });
                     //Next we delete top and add in like below
                     photoFile = null;
                 } else {
                     photoFile = (File) data.getExtras().get("photoFile");
                     Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                     // RESIZE BITMAP, see section below
                     // Load the taken image into a preview
                     ivComposeFoodPic.setImageBitmap(takenImage);
                     photoBitMap = null;
                 }
             }
         }
     }
 }
