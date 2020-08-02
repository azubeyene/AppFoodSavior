package com.example.appfoodsavior;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeDetailsActivity extends AppCompatActivity {
    private ImageView ivRecipeDetailsPhoto;
    private TextView tvRecipeDetailsName;
    private TextView tvRecipeDetailsIngredients;
    private TextView tvRecipeDetailsInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        ivRecipeDetailsPhoto = findViewById(R.id.ivRecipeDetailsPhoto);
        tvRecipeDetailsName = findViewById(R.id.tvRecipeDetailsName);
        tvRecipeDetailsIngredients = findViewById(R.id.tvRecipeDetailsIngredients);
        tvRecipeDetailsInstructions = findViewById(R.id.tvRecipeDetailsInstructions);
        //query recipe object ID:
    }
}
