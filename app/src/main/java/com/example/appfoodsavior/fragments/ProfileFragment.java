package com.example.appfoodsavior.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.appfoodsavior.LoginActivity;
import com.example.appfoodsavior.MainActivity;
import com.example.appfoodsavior.R;
import com.example.appfoodsavior.parseitems.InventoryFood;
import com.example.appfoodsavior.parseitems.PreviousInventory;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.zxing.integration.android.IntentIntegrator;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private ImageView ivProfileheadshot;
    private TextView tvProfileNameHeadshot;
    private TextView tvProfileMostFrequentInvDescript;
    private TextView tvProfileMostFrequentPrevInvDescription;
    private PieChart chartInv;
    private PieChart chartPrev;

    private List<String> inventoryFoodNamesList = new ArrayList<>();
    private HashMap<String, Integer> inventoryHashMap = new HashMap<>();

    List<String> previousInventoryNamesList = new ArrayList<>();
    HashMap<String, Integer> previousInventoryHashMap = new HashMap<>();

    private Button btnLogOut;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfileheadshot = view.findViewById(R.id.ivProfileheadshot);
        tvProfileNameHeadshot = view.findViewById(R.id.tvProfileNameHeadshot);
        tvProfileMostFrequentInvDescript = view.findViewById(R.id.tvProfileMostFrequentInvDescript);
        tvProfileMostFrequentPrevInvDescription = view.findViewById(R.id.tvProfileMostFrequentPrevInvDescription);
        chartInv = view.findViewById(R.id.pieChartInv);
        chartPrev = view.findViewById(R.id.pieChartPrev);

        btnLogOut = view.findViewById(R.id.btnLogOut);

        //Fill in the Header info
        if (ParseUser.getCurrentUser().getParseFile("profilePhoto")!=null){
            Glide.with(getContext()).load(ParseUser.getCurrentUser().getParseFile("profilePhoto").getUrl()).into(ivProfileheadshot);
        }
        if (ParseUser.getCurrentUser().get("firstName")!=null &&ParseUser.getCurrentUser().get("lastName")!=null){
            String firstName = (String) ParseUser.getCurrentUser().get("firstName");
            String lastName = (String) ParseUser.getCurrentUser().get("lastName");
            tvProfileNameHeadshot.setText(firstName+lastName);
        }

        fillInventory(view);
        fillPreviousInventory(view);

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e!=null){
                            //navigate to log in screen
                            return;
                        } else {
                            Log.e("ProfileFragment", "Error occurred in logout", e);
                        }
                    }
                });
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }

    private void fillInventory(View view) {
        ParseQuery<InventoryFood> queryInv = ParseQuery.getQuery(InventoryFood.class);
        queryInv.whereEqualTo("user", ParseUser.getCurrentUser());
        queryInv.findInBackground(new FindCallback<InventoryFood>() {
            @Override
            public void done(List<InventoryFood> minventoryFoods, ParseException e) {
                //Add all words to namesList
                List<String> allinventoryFoodNamesList = new ArrayList<>();
                for (InventoryFood minventoryFood: minventoryFoods){
                    //format the string
                    String reformatedName = minventoryFood.getName().toLowerCase().trim();
                    allinventoryFoodNamesList.add(reformatedName);
                }
                //Build up frequency hashmap
                for (String name: allinventoryFoodNamesList){
                    if (inventoryHashMap.containsKey(name)){
                        inventoryHashMap.put(name, inventoryHashMap.get(name) + 1);
                    } else {
                        inventoryHashMap.put(name, 1);
                        inventoryFoodNamesList.add(name);
                    }
                }
                //Set up Pie chart
                List<PieEntry> pieEntriesInv = new ArrayList<>();
                for (int i = 0; i<inventoryFoodNamesList.size(); i++){
                    pieEntriesInv.add(new PieEntry(inventoryHashMap.get(inventoryFoodNamesList.get(i)),
                            inventoryFoodNamesList.get(i).substring(0,1).toUpperCase()+inventoryFoodNamesList.get(i).substring(1)));
                }
                PieDataSet dataSetInv = new PieDataSet(pieEntriesInv,  "Inventory Foods");
                dataSetInv.setColors(ColorTemplate.JOYFUL_COLORS);
                PieData  dataInv = new PieData(dataSetInv);

                //Getting Chart
                chartInv.setData(dataInv);
                chartInv.animateXY(1000, 1000);
                chartInv.invalidate();

                String[] top5Inventory = top5(inventoryFoodNamesList, inventoryHashMap);
                //Display on Text view
                String top5InventoryText = "";
                for (int i = 0; i<5; i++){
                    top5InventoryText = top5InventoryText + "(" + Integer.toString(i + 1) + ") " +top5Inventory[i].substring(0,1).toUpperCase()+top5Inventory[i].substring(1)+"\n\n";
                }
                tvProfileMostFrequentInvDescript.setText(top5InventoryText);
                //Do stuff for
            }
        });
    }

    private void fillPreviousInventory(View view) {
        ParseQuery<PreviousInventory> queryInv = ParseQuery.getQuery(PreviousInventory.class);
        queryInv.whereEqualTo("user", ParseUser.getCurrentUser());
        queryInv.findInBackground(new FindCallback<PreviousInventory>() {
            @Override
            public void done(List<PreviousInventory> mpreviousInventoryFoods, ParseException e) {
                //Add all words to namesList
                List<String> allprevInventoryFoodNamesList = new ArrayList<>();
                for (PreviousInventory mprevInventoryFood: mpreviousInventoryFoods){
                    //format the string
                    String reformatedName = mprevInventoryFood.getName().toLowerCase().trim();
                    allprevInventoryFoodNamesList.add(reformatedName);
                }
                //allprevInventoryFoodNamesList.addAll(inventoryFoodNamesList);
                //Build up frequency hashmap
                for (String name: allprevInventoryFoodNamesList){
                    if (previousInventoryHashMap.containsKey(name)){
                        previousInventoryHashMap.put(name, previousInventoryHashMap.get(name) + 1);
                    } else {
                        previousInventoryHashMap.put(name, 1);
                        previousInventoryNamesList.add(name);
                    }
                }
                //Set up Pie chart
                List<PieEntry> pieEntriesPrev = new ArrayList<>();
                for (int i = 0; i<previousInventoryNamesList.size(); i++){
                    pieEntriesPrev.add(new PieEntry(previousInventoryHashMap.get(previousInventoryNamesList.get(i)),
                            previousInventoryNamesList.get(i).substring(0,1).toUpperCase()+previousInventoryNamesList.get(i).substring(1)));
                }
                PieDataSet dataSetPrev = new PieDataSet(pieEntriesPrev,  "Prev Inventory Foods");
                dataSetPrev.setColors(ColorTemplate.JOYFUL_COLORS);
                PieData  dataPrev = new PieData(dataSetPrev);

                //Getting Chart
                chartPrev.setData(dataPrev);
                chartPrev.animateY(1000);
                chartPrev.invalidate();

                String[] top5PrevInv = top5(previousInventoryNamesList, previousInventoryHashMap);
                //Display on Text view
                String top5InventoryText = "";
                for (int i = 0; i<5; i++){
                    top5InventoryText = top5InventoryText + "(" + Integer.toString(i + 1) + ") " +top5PrevInv[i].substring(0,1).toUpperCase()+top5PrevInv[i].substring(1)+"\n\n";
                }
                tvProfileMostFrequentPrevInvDescription.setText(top5InventoryText);
                //Do stuff for
            }
        });

    }

    private String[] top5(List<String> namesList, HashMap<String, Integer> frequencyHashMap) {
        //Return the top 5 food items; Loop over names list
        int[] min_array = new int[2]; //of form [index of smallest item, frequency]
        String[] top5Names = new String[5];

        min_array[0] = 0;
        min_array[1] = frequencyHashMap.get(namesList.get(0));
        int num_to_down = 5;
        //Initialize top 5 from namesList
        for (int i = 0; i<5; i++){
            //keep track of min
            top5Names[i] = namesList.get(i);
            if (frequencyHashMap.get(namesList.get(i))< min_array[1]){
                min_array[0] = i;
                min_array[1] = frequencyHashMap.get(namesList.get(i));
            }

        }
        //looping to get top 5
        for (String name: namesList){
            if (frequencyHashMap.get(name)>min_array[1]){
                //we have some thing bigger, so we replace
                top5Names[min_array[0]] = name;

                //now we search for min index
                int min_value = frequencyHashMap.get(name);
                for (int i =0; i<top5Names.length; i++){
                    if (frequencyHashMap.get(top5Names[i])<min_value){
                        min_value = frequencyHashMap.get(top5Names[i]);
                        min_array[0] = i;
                        min_array[1] = min_value;
                    }
                }
                //if min_value stll == inventoryHashMap.get(name), we leave unchanged
            }
        }
        return top5Names;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile_export:
                Toast.makeText(getContext(), "Export Profile", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile_settings:
                Toast.makeText(getContext(), "Settings", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


}
