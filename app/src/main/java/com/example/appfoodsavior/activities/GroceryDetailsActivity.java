package com.example.appfoodsavior.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfoodsavior.MainActivity;
import com.example.appfoodsavior.R;
import com.example.appfoodsavior.fragments.GroceryFragment;
import com.example.appfoodsavior.parseitems.GroceryFood;
import com.example.appfoodsavior.parseitems.InventoryFood;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GroceryDetailsActivity extends AppCompatActivity {
    Context context = GroceryDetailsActivity.this;

    private Toolbar toolbar;
    private ImageView ivGroceryDetails;
    private EditText etGrocDetailsName;
    private EditText etGroceryDetailsBrand;
    private EditText etGrocDetailsAmountPrice;
    private EditText etGrocDetailsAmountNum;
    private EditText etGrocDetailsAmountUnits;
    private EditText etGrocDetailsDescription;
    private EditText etGrocDetailsBuyBy;
    private Button btnGroceryDetailsDelete;

    private GroceryFood groceryFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grocery_details);

        toolbar = findViewById(R.id.toolbar_grocery_details);
        toolbar.setTitle("Grocery Details");
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.grocery_fragment_menu);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivGroceryDetails = findViewById(R.id.ivGroceryDetails);
        etGrocDetailsName = findViewById(R.id.etGrocDetailsName);
        etGroceryDetailsBrand = findViewById(R.id.etGroceryDetailsBrand);
        etGrocDetailsAmountPrice = findViewById(R.id.etGrocDetailsAmountPrice);
        etGrocDetailsAmountNum = findViewById(R.id.etGrocDetailsAmountNum);
        etGrocDetailsAmountUnits = findViewById(R.id.etGrocDetailsAmountUnits);
        etGrocDetailsDescription = findViewById(R.id.etGrocDetailsDescription);
        etGrocDetailsBuyBy = findViewById(R.id.etGrocDetailsBuyBy);
        btnGroceryDetailsDelete = findViewById(R.id.btnGroceryDetailsDelete);

        ParseQuery<GroceryFood> query = ParseQuery.getQuery(GroceryFood.class);
        query.getInBackground(getIntent().getStringExtra("groceryFoodID"), new GetCallback<GroceryFood>() {
            @Override
            public void done(GroceryFood mgroceryFood, ParseException e) {
                //Initial set up
                groceryFood = mgroceryFood;
                etGrocDetailsName.setText(groceryFood.getName());
                toolbar.setTitle(groceryFood.getName());

                //Prefill details
                etGrocDetailsAmountPrice.setText(groceryPriceReformat(groceryFood));
                etGrocDetailsBuyBy.setText(groceryBuyReformat(groceryFood));

                if (groceryFood.getBrand()!=null){
                    etGroceryDetailsBrand.setText(groceryFood.getBrand());
                }
                if (groceryFood.getDescription()!=null){
                    etGrocDetailsDescription.setText(groceryFood.getDescription());
                }
                if (groceryFood.getImage()!=null){
                    //Glide.with(context).load(groceryFood.getImage().getUrl()).into(ivGroceryDetails);
                    Glide.with(GroceryDetailsActivity.this).load(groceryFood.getImage().getUrl()).into(ivGroceryDetails);
                }
                try {
                    if (groceryFood.getAmount() !=null ){
                        Log.i("grocery details", "This has time object");
                        etGrocDetailsAmountNum.setText(groceryFood.getAmount().get(0));
                        etGrocDetailsAmountUnits.setText(groceryFood.getAmount().get(1));
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        });

        btnGroceryDetailsDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //delete the inventory item
                groceryFood.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        finish();
                    }
                });
            }
        });
    }

    private String groceryPriceReformat(GroceryFood mgroceryFood) {
        //We assume default value is zero
        String priceAsString = String.valueOf(mgroceryFood.getPrice());
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
    private String groceryBuyReformat(GroceryFood mgroceryFood) {

        if (mgroceryFood.getBuyOn()!=null){
            //TODO: html insert a bolded Exp: upfront
            String input = mgroceryFood.getBuyOn().toString();
            String new_input = input.substring(4, 10);
            //String sourceString = "Buy by " + new_input;
            String sourceString = "<b>" + "Buy" + "</b> " + new_input;
            String good_exp = Html.fromHtml(sourceString).toString();
            return good_exp;
        } else{
            return "01/01/2000";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.grocery_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.grocery_details_amazon:
                //open amazon link; search up grocery name
                String[] groceryNameList = groceryFood.getName().split(" ");
                String urlName = "";
                for (String name: groceryNameList){
                    urlName = urlName+"+"+name;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.amazon.com/s?k="+urlName));
                startActivity(browserIntent);
                break;
            case R.id.grocery_details_done:
                //We want to save state and return to activity
                ArrayList<String> arrayAmount = new ArrayList<>();
                arrayAmount.add(etGrocDetailsAmountNum.getText().toString());
                arrayAmount.add(etGrocDetailsAmountUnits.getText().toString());
                Date date_exp = null;
                //Change this so its date selection
                try {
                    date_exp = new SimpleDateFormat("MM/dd/yyyy").parse(etGrocDetailsBuyBy.getText().toString());
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }

                groceryFood.setName(etGrocDetailsName.getText().toString());
                groceryFood.setDescription(etGrocDetailsDescription.getText().toString());
                groceryFood.setBrand(etGroceryDetailsBrand.getText().toString());
                groceryFood.setAmount(arrayAmount);
                if (date_exp!=null){
                    groceryFood.setBuyOn(date_exp);
                }
                groceryFood.saveInBackground();

                //Manually insert to grocery fragment
                finish();
                break;
        }
        return true;
    }

}
