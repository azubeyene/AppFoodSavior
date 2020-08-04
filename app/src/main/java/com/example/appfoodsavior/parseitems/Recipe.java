package com.example.appfoodsavior.parseitems;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Recipe")
public class Recipe extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_COOK_TIME = "cookTime";
    public static final String KEY_CREATED = "createdAt";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_INGREDIENTS = "ingredients";
    public static final String KEY_INSTRUCTIONS = "instructions";
    public static final String KEY_URL = "url";
    public static final String KEY_TAGS = "tags";

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

    public String getUrl(){
        return getString(KEY_URL);
    }
    public void setUrl(String url){
        put(KEY_URL, url);
    }

    public double getCookTime(){
        return getDouble(KEY_COOK_TIME);
    }
    public void seCookTime(double cookTime){
        put(KEY_COOK_TIME, cookTime);
    }

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public ArrayList<String> getIngredients() throws JSONException {
        JSONArray jsonArray = getJSONArray(KEY_INGREDIENTS);
        List<String> returnArray = new ArrayList<String>();

        for (int i = 0; i<jsonArray.length(); i++){
            returnArray.add(jsonArray.getString(i));
        }
        return (ArrayList<String>) returnArray;
    }
    public void setIngredients(ArrayList<String> amountList){
        put(KEY_INGREDIENTS, amountList);
    }

    public ArrayList<String> getInstructions() throws JSONException {
        JSONArray jsonArray = getJSONArray(KEY_INSTRUCTIONS);
        List<String> returnArray = new ArrayList<String>();

        for (int i = 0; i<jsonArray.length(); i++){
            returnArray.add(jsonArray.getString(i));
        }
        return (ArrayList<String>) returnArray;
    }
    public void setInstructions(ArrayList<String> amountList){
        put(KEY_INSTRUCTIONS, amountList);
    }

    public ArrayList<String> getTags() throws JSONException {
        JSONArray jsonArray = getJSONArray(KEY_TAGS);
        List<String> returnArray = new ArrayList<String>();

        for (int i = 0; i<jsonArray.length(); i++){
            returnArray.add(jsonArray.getString(i));
        }
        return (ArrayList<String>) returnArray;
    }
    public void setTags(ArrayList<String> amountList){
        put(KEY_TAGS, amountList);
    }


}
