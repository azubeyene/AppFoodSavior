package com.example.appfoodsavior.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.appfoodsavior.parseitems.InventoryFood;
import com.example.appfoodsavior.MainActivity;
import com.example.appfoodsavior.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class InventoryDetailsActivity extends AppCompatActivity {
    private Context context = InventoryDetailsActivity.this;
    private ImageView ivInventoryDetails;
    private EditText etInvDetailsName;
    private EditText etInvDetExp;
    private EditText etInvDetAmount;
    private EditText etInvDetDescrip;
    private Button btnInvDetDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);

        ivInventoryDetails = findViewById(R.id.ivInventoryDetails);
        etInvDetailsName = findViewById(R.id.etInvDetailsName);
        etInvDetExp = findViewById(R.id.etInvDetExp);
        etInvDetAmount = findViewById(R.id.etInvDetAmount);
        etInvDetDescrip = findViewById(R.id.etInvDetDescrip);
        btnInvDetDone = findViewById(R.id.btnInvDetDone);

        ParseQuery<InventoryFood> query = ParseQuery.getQuery(InventoryFood.class);
        query.getInBackground(getIntent().getStringExtra("inventoryFoodID"), new GetCallback<InventoryFood>() {
            @Override
            public void done(final InventoryFood inventoryFood, ParseException e) {
                //Preset the text to inventoryFood text
                etInvDetailsName.setText(inventoryFood.getName());
                String input = inventoryFood.getExpiration().toString();
                String new_input = input.substring(4, 10) + input.substring(23);
                etInvDetExp.setText("Exp: " + new_input);
                etInvDetDescrip.setText(inventoryFood.getDescription());
                Glide.with(context).load(inventoryFood.getImage().getUrl()).into(ivInventoryDetails);

                btnInvDetDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //We want to save state and return to activity
                        inventoryFood.setName(etInvDetailsName.getText().toString());
                        inventoryFood.setDescription(etInvDetDescrip.getText().toString());
                        inventoryFood.saveInBackground();

                        Intent i = new Intent(context, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        });


    }
}
