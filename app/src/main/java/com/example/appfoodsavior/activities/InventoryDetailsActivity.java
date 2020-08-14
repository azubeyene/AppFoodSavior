package com.example.appfoodsavior.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfoodsavior.DatePickerFragment;
import com.example.appfoodsavior.parseitems.GroceryFood;
import com.example.appfoodsavior.parseitems.InventoryFood;
import com.example.appfoodsavior.MainActivity;
import com.example.appfoodsavior.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class InventoryDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private Context context = InventoryDetailsActivity.this;
    private ImageView ivInventoryDetails;
    private EditText etInvDetailsName;
    private TextView tvInvDetExp;
    private EditText etInvDetAmountNum;
    private EditText etInvDetAmountUnits;
    private EditText etInvDetDescrip;
    private EditText etInvDetailsBrand;
    private EditText etInvDetPrice;
    private Date ExpirationDate;
    private Toolbar toolbar;

    private InventoryFood inventoryFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);
        toolbar = findViewById(R.id.toolbar_inventory_details);
        toolbar.setTitle("Inventeory Details");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.inventory_fragment_menu);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivInventoryDetails = findViewById(R.id.ivInventoryDetails);
        etInvDetailsName = findViewById(R.id.etInvDetailsName);
        tvInvDetExp = findViewById(R.id.tvInvDetExp);
        etInvDetAmountNum = findViewById(R.id.etInvDetAmountNum);
        etInvDetAmountUnits = findViewById(R.id.etInvDetAmountUnits);
        etInvDetDescrip = findViewById(R.id.etInvDetDescrip);
        etInvDetailsBrand = findViewById(R.id.etInvDetailsBrand);
        etInvDetPrice = findViewById(R.id.etInvDetPrice);

        tvInvDetExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                 datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });


        ParseQuery<InventoryFood> query = ParseQuery.getQuery(InventoryFood.class);
        query.getInBackground(getIntent().getStringExtra("inventoryFoodID"), new GetCallback<InventoryFood>() {
            @Override
            public void done(final InventoryFood minventoryFood, ParseException e) {
                //Preset the text to inventoryFood text
                inventoryFood = minventoryFood;
                etInvDetailsName.setText(inventoryFood.getName());
                toolbar.setTitle(inventoryFood.getName());

                //String input = inventoryFood.getExpiration().toString();
                //String new_input = input.substring(4, 10) + input.substring(23);
                if (inventoryFood.getExpiration()!=null){
                    ExpirationDate = inventoryFood.getExpiration();
                    String new_new_input = DateFormat.getDateInstance().format(inventoryFood.getExpiration().getTime());
                    tvInvDetExp.setText("Exp: " + new_new_input);
                }
                try {
                    etInvDetAmountNum.setText(inventoryFood.getAmount().get(0));
                    etInvDetAmountUnits.setText(inventoryFood.getAmount().get(1));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                if (inventoryFood.getBrand()!=null){
                    etInvDetailsBrand.setText(inventoryFood.getBrand());
                }
                etInvDetPrice.setText(inventoryPriceReformat(inventoryFood));
                etInvDetDescrip.setText(inventoryFood.getDescription());
                Glide.with(context).load(inventoryFood.getImage().getUrl()).into(ivInventoryDetails);
            }
        });
    }
    private String inventoryPriceReformat(InventoryFood minvetoryFood) {
        //We assume default value is zero
        String priceAsString = String.valueOf(minvetoryFood.getPrice());
        int indexOfDecimal = priceAsString.indexOf(".");
        String decimalPrice = priceAsString.substring(indexOfDecimal+1);
        Log.i("grocery details", decimalPrice);
        if (decimalPrice.length()>2){
            decimalPrice = decimalPrice.substring(0, 2);
        } else if (decimalPrice.length()==1){
            decimalPrice = decimalPrice+"0";
        }

        priceAsString = "$" + priceAsString.substring(0, indexOfDecimal) + "." + decimalPrice;
        return priceAsString;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //menu item is done
        switch (item.getItemId()){
            case R.id.inventory_details_done:
                //We want to save state and return to activity
                //array of amount, brand,
                ArrayList<String> arrayAmount = new ArrayList<>();
                arrayAmount.add(etInvDetAmountNum.getText().toString());
                arrayAmount.add(etInvDetAmountUnits.getText().toString());

                inventoryFood.setName(etInvDetailsName.getText().toString());
                inventoryFood.setDescription(etInvDetDescrip.getText().toString());
                inventoryFood.setAmount(arrayAmount);
                inventoryFood.setPrice(Double.parseDouble(etInvDetPrice.getText().toString().substring(1)));
                inventoryFood.setBrand(etInvDetailsBrand.getText().toString());
                inventoryFood.setExpiration(ExpirationDate);
                inventoryFood.saveInBackground();
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        //We get the chosen date
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        //Format to etInvComposExp
        ExpirationDate = c.getTime();
        String chosenDateString = DateFormat.getDateInstance().format(c.getTime());
        tvInvDetExp.setText(chosenDateString);
    }
}
