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

import com.example.appfoodsavior.parseitems.InventoryFood;
import com.example.appfoodsavior.R;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ComposeInventoryActivity extends AppCompatActivity {
    public static final String TAG = "ComposeInventory";
    public static final int PHOTO_IMAGE_ACTIVITY_REQUEST_CODE = 88;
    private EditText etInventoryComposeName2;
    private EditText etInvComposExp;
    private EditText etInvComposAmountNum;
    private EditText etInvComposAmountUnits;
    private EditText etInvComposDescript;
    private EditText etInvComposBrand;
    private ImageView ivComposeFoodPic;
    private Button btnAddInvItem;
    private File photoFile;
    private File photoFile2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_inventory);

        etInventoryComposeName2 = findViewById(R.id.etInventoryComposeName2);
        etInvComposExp = findViewById(R.id.etInvComposExp);
        etInvComposAmountNum = findViewById(R.id.etInvComposAmountNum);
        etInvComposAmountUnits = findViewById(R.id.etInvComposAmountUnits);
        etInvComposDescript = findViewById(R.id.etInvComposDescript);
        etInvComposBrand = findViewById(R.id.etInvComposBrand);

        ivComposeFoodPic = findViewById(R.id.ivComposeFoodPic);
        btnAddInvItem = findViewById(R.id.btnAddInvItem);

        btnAddInvItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if all requred fields are filled
                String name = etInventoryComposeName2.getText().toString();
                String expiration = etInvComposExp.getText().toString();
                String amount_num = etInvComposAmountNum.getText().toString();
                String amount_units = etInvComposAmountUnits.getText().toString();
                String description = etInvComposDescript.getText().toString();
                String brand = etInvComposBrand.getText().toString();
                Date date_exp = null;
                ArrayList<String> arrayAmount = new ArrayList<>();

                if(notValidItem(name, expiration, amount_num, amount_units, description, brand)){ return;}
                arrayAmount.add(amount_num);
                arrayAmount.add(amount_units);

                //Now convert date to Date object
                try {
                    date_exp = new SimpleDateFormat("MM/dd/yyyy").parse(expiration);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                createInventoryItem(name, date_exp, arrayAmount, description, brand);
                //Navigate to Inventory Fragment();
            }
        });

        ivComposeFoodPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ComposeInventoryActivity.this, CameraActivity.class);
                startActivityForResult(i, PHOTO_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private void createInventoryItem(String name, Date date_exp, ArrayList<String> arrayAmount, String description, String brand) {
        final InventoryFood inventoryFood = new InventoryFood();
        inventoryFood.setName(name);
        inventoryFood.setExpiration(date_exp);
        inventoryFood.setAmount(arrayAmount);
        inventoryFood.setDescription(description);
        inventoryFood.setBrand(brand);
        inventoryFood.setUser(ParseUser.getCurrentUser());
        //Check if we take library photo or camera photo
        if (photoFile==null){
            inventoryFood.setImage(new ParseFile(photoFile2));
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
                Intent i = getIntent();
                i.putExtra("inventoryFoodID", inventoryFood.getObjectId());
                (ComposeInventoryActivity.this).setResult(RESULT_OK, i);
                etInventoryComposeName2.setText("");
                etInvComposExp.setText("");
                etInvComposAmountNum.setText("");
                etInvComposAmountUnits.setText("");
                etInvComposDescript.setText("");
                etInvComposBrand.setText("");
                ivComposeFoodPic.setImageResource(R.drawable.ic_camera_icon_filler);

                (ComposeInventoryActivity.this).finish();
            }
        });

    }

    private boolean notValidItem(String name, String expiration, String amount_num, String amount_units, String description, String brand) {
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
        if (brand.isEmpty()){
            Toast.makeText(this, "Brand required for food", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (photoFile==null && photoFile2==null ||  ivComposeFoodPic.getDrawable()==null){
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
                    ivComposeFoodPic.setImageBitmap(takenImage);
                    photoFile = null;

                } else {
                    photoFile = (File) data.getExtras().get("photoFile");
                    Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    ivComposeFoodPic.setImageBitmap(takenImage);
                }
            }
        }
    }
}
