package com.example.appfoodsavior;

import android.app.Application;

import com.example.appfoodsavior.parseitems.GroceryFood;
import com.example.appfoodsavior.parseitems.InventoryFood;
import com.example.appfoodsavior.parseitems.PreviousInventory;
import com.example.appfoodsavior.parseitems.Recipe;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ParseObject.registerSubclass(InventoryFood.class);
        ParseObject.registerSubclass(GroceryFood.class);
        ParseObject.registerSubclass(Recipe.class);
        ParseObject.registerSubclass(PreviousInventory.class);
        ParseObject.registerSubclass(ParseUser.class);


        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("app-foodsavior") // should correspond to APP_ID env variable
                .clientKey("myMasterKeyFoodSavior")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://app-foodsavior.herokuapp.com/parse").build());
    }
}
