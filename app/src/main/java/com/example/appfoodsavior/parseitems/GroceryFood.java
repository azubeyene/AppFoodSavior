package com.example.appfoodsavior.parseitems;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ParseClassName("GroceryFood")
public class GroceryFood extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_STORE = "store";
    public static final String KEY_CREATED = "createdAt";
    public static final String KEY_DESCRIPTION = "description";

    public static final String KEY_AMOUNT = "amount"; //Array
    public static final String KEY_PRICE = "price"; //Number/double
    public static final String KEY_CALORIES = "calories"; //Number/double
    public static final String KEY_BRAND = "brand"; //String
    public static final String KEY_BUY_ON = "buyOn"; //Date

    //Getters and setters
    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser parseUser){
        put(KEY_USER, parseUser);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public String getName(){
        return getString(KEY_NAME);
    }
    public void setName(String name){
        put(KEY_NAME, name);
    }

    public String getStore(){
        return getString(KEY_STORE);
    }
    public void setStore(String store){
        put(KEY_STORE, store);
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public double getPrice(){
        return getDouble(KEY_PRICE);
    }
    public void setPrice(double price){
        put(KEY_PRICE, price);
    }

    public double getCalories(){
        return getDouble(KEY_CALORIES);
    }
    public void setCalories(double calories){
        put(KEY_CALORIES, calories);
    }

    public ArrayList<String> getAmount() throws JSONException {
        JSONArray jsonArray = getJSONArray(KEY_AMOUNT);
        List<String> returnArray = new ArrayList<String>();

        for (int i = 0; i<jsonArray.length(); i++){
            returnArray.add(jsonArray.getString(i));
        }
        return (ArrayList<String>) returnArray;
    }
    public void setAmount(ArrayList<String> amountList){
        put(KEY_AMOUNT, amountList);
    }

    public String getBrand(){
        return getString(KEY_BRAND);
    }
    public void setBrand(String brand){
        put(KEY_BRAND, brand);
    }

    public Date getBuyOn(){
        return getDate(KEY_BUY_ON);
    }
    public void setBuyOn(Date date){
        put(KEY_BUY_ON, date);
    }

    public void setCreated(Date date){
        put(KEY_CREATED, date);
    }
}
