package com.example.appfoodsavior;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("GroceryFood")
public class GroceryFood extends ParseObject {
    public static final String KEY_NAME = "name";
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_STORE = "store";
    public static final String KEY_CREATED = "createdAt";
    public static final String KEY_DESCRIPTION = "description";

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
}
