package com.example.appfoodsavior.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appfoodsavior.parseitems.GroceryFood;
import com.example.appfoodsavior.R;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ComposeGroceryActivity extends AppCompatActivity {
    public static final String TAG = "ComposeGrocery";
    public static final int PHOTO_IMAGE_ACTIVITY_REQUEST_CODE = 86;
    private EditText etGrocComposeName;
    private EditText etGrocComposBuyBy;
    private EditText etGrocComposAmountNum;
    private EditText etGrocComposAmountUnits;
    private EditText etGrocComposDescript;
    private EditText etGrocComposePrice;
    private EditText etGrocComposeBrand;
    private ImageView ivGrocComposeFoodPic;
    private Button btnAddGrocItem;
    private File photoFile;
    private File photoFile2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_grocery);
        //getActionBar().setTitle("Compose Grocery Item");
        getSupportActionBar().setTitle("Compose Grocery Item");

        etGrocComposeName = findViewById(R.id.etGrocComposeName);
        etGrocComposBuyBy = findViewById(R.id.etGrocComposBuyBy);
        etGrocComposAmountNum = findViewById(R.id.etGrocComposAmountNum);
        etGrocComposAmountUnits = findViewById(R.id.etGrocComposAmountUnits);
        etGrocComposDescript = findViewById(R.id.etGrocComposDescript);
        etGrocComposePrice = findViewById(R.id.etGrocComposePrice);
        etGrocComposeBrand = findViewById(R.id.etGrocComposeBrand);

        ivGrocComposeFoodPic = findViewById(R.id.ivGrocComposeFoodPic);
        btnAddGrocItem = findViewById(R.id.btnAddGrocItem);
        btnAddGrocItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if all requred fields are filled
                String name = etGrocComposeName.getText().toString();
                String buy_by = etGrocComposBuyBy.getText().toString();
                String amount_num = etGrocComposAmountNum.getText().toString();
                String amount_units = etGrocComposAmountUnits.getText().toString();
                String description = etGrocComposDescript.getText().toString();
                String price = etGrocComposePrice.getText().toString();
                String brand = etGrocComposeBrand.getText().toString();

                Date date_buy = null;
                ArrayList<String> arrayAmount = new ArrayList<>();

                if(notValidItem(name, buy_by, amount_num, amount_units, description, price, brand)){ return;}
                arrayAmount.add(amount_num);
                arrayAmount.add(amount_units);

                //Now convert date to Date object
                try {
                    date_buy = new SimpleDateFormat("MM/dd/yyyy").parse(buy_by);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                createGroceryItem(name, date_buy, arrayAmount, description, price, brand);
                //Navigate to Inventory Fragment();
            }
        });

        ivGrocComposeFoodPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ComposeGroceryActivity.this, CameraActivity.class);
                startActivityForResult(i, PHOTO_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }
    private void createGroceryItem(String name, Date date_buy, ArrayList<String> arrayAmount, String description, String price, String brand) {
        final GroceryFood groceryFood = new GroceryFood();
        groceryFood.setName(name);
        groceryFood.setBuyOn(date_buy);
        groceryFood.setAmount(arrayAmount);
        groceryFood.setDescription(description);
        groceryFood.setBrand(brand);
        groceryFood.setUser(ParseUser.getCurrentUser());
        double real_price = Double.valueOf(price);
        groceryFood.setPrice(real_price);
        //Check if we take library photo or camera photo
        if (photoFile==null){
            groceryFood.setImage(new ParseFile(photoFile2));
        } else{
            groceryFood.setImage(new ParseFile(photoFile));
        }

        groceryFood.saveInBackground(new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e!=null){
                    Log.e(TAG, "Error occured while creating new inventory item", e);
                    return;
                }
                //Successful
                Log.i(TAG, "Success on saving inventory food item");
                Intent i = getIntent();
                i.putExtra("groceryFoodID", groceryFood.getObjectId());
                (ComposeGroceryActivity.this).setResult(RESULT_OK, i);
                etGrocComposeName.setText("");
                etGrocComposBuyBy.setText("");
                etGrocComposAmountNum.setText("");
                etGrocComposAmountUnits.setText("");
                etGrocComposDescript.setText("");
                etGrocComposePrice.setText("");
                etGrocComposeBrand.setText("");
                ivGrocComposeFoodPic.setImageResource(R.drawable.ic_camera_icon_filler);

                (ComposeGroceryActivity.this).finish();
            }
        });

    }

    private boolean notValidItem(String name, String expiration, String amount_num, String amount_units, String description, String price, String brand) {
        if (name.isEmpty()){
            Toast.makeText(this, "Name required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (expiration.isEmpty()){
            Toast.makeText(this, "Expiration required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (amount_num.isEmpty()){
            Toast.makeText(this, "Amount required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (amount_units.isEmpty()){
            Toast.makeText(this, "Amount units required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (description.isEmpty()){
            Toast.makeText(this, "Description required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (price.isEmpty()){
            Toast.makeText(this, "Price required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (brand.isEmpty()){
            Toast.makeText(this, "Brand required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (photoFile==null && photoFile2==null ||  ivGrocComposeFoodPic.getDrawable()==null){
            Toast.makeText(this, "Photo required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PHOTO_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode==RESULT_OK){//Retrive Photo then add to image view

                if (data.getExtras().get("selectedPhotoFile")!=null){
                    photoFile2 = (File) data.getExtras().get("selectedPhotoFile");
                    Bitmap takenImage = BitmapFactory.decodeFile(photoFile2.getAbsolutePath());
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    ivGrocComposeFoodPic.setImageBitmap(takenImage);
                    photoFile = null;
                } else {
                    photoFile = (File) data.getExtras().get("photoFile");
                    Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    ivGrocComposeFoodPic.setImageBitmap(takenImage);
                }
            }
        }
    }
}
