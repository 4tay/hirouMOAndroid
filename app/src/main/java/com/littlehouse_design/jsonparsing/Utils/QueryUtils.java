package com.littlehouse_design.jsonparsing.Utils;

import android.util.Log;

import com.littlehouse_design.jsonparsing.Utils.Cart.OrderItem;
import com.littlehouse_design.jsonparsing.Utils.Cart.OrderMod;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Catalog;
import com.littlehouse_design.jsonparsing.Utils.CatsAndItems.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by johnkonderla on 12/23/16.
 */

public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static Catalog fullCat;
    private static ArrayList<Item> itemArrayList;

    private QueryUtils() {

    }

    public static String getJSONFromUrl(String requestURL) {
        URL url = createUrl(requestURL);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error with input", e);
        }
        //MakeJson(jsonResponse);

        return jsonResponse;

    }

    private static URL createUrl(String strUrl) {
        URL url = null;
        try {
            url = new URL(strUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Couldn't Create URL: ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null)
            return jsonResponse;
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //If we connect and all things are good, we'll return 200

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error!! Response: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "probz with retrieving: ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Couldn't close my inputStream: ", e);
                }
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String singleLine = bufferedReader.readLine();
            while (singleLine != null) {
                stringBuilder.append(singleLine);
                singleLine = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
    }

    public static Catalog MakeJson(String json) {
        //catalogs = new ArrayList<>();
        fullCat = new Catalog();
        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            Catalog scheduleCats = new Catalog();
            Catalog categoryCats = new Catalog();
            Catalog subCatCats = new Catalog();
            ArrayList<Catalog> scheduleCatList = new ArrayList<>();
            ArrayList<Catalog> categoryCatList = new ArrayList<>();
            ArrayList<Catalog> subCatList = new ArrayList<>();
            boolean oneDeep = false;
            boolean twoDeep = false;
            boolean threeDeep = false;


            // build up a list of catalogs from catalog objects with the corresponding data.
            JSONObject fullOb = new JSONObject(json);

            //Feature node
            JSONObject topCat = fullOb.getJSONObject("data");


            //Properties node
            JSONObject singleCat = topCat.getJSONObject("Cat");
            JSONObject details = singleCat.getJSONObject("Details");

            //Get Code, Parent Code, and Name
            String code = details.getString("Code");
            String parentCode = details.getString("Parent");
            String name = details.getString("Name");
            int level = details.getInt("Level");
            fullCat.setParentCode(code);
            fullCat.setParentCode(parentCode);
            fullCat.setName(name);
            fullCat.setLevel(level);

            JSONArray cats = singleCat.optJSONArray("Cats");
            if (cats != null) {
                oneDeep = true;
                for (int i = 0; i < cats.length(); i++) {
                    JSONObject firstChild = cats.getJSONObject(i);
                    JSONObject firstCat = firstChild.getJSONObject("Cat");
                    JSONObject firstDetails = firstCat.getJSONObject("Details");
                    String firstCode = firstDetails.getString("Code");
                    String firstName = firstDetails.getString("Name");
                    String firstParentCode = firstDetails.getString("Parent");
                    int firstLevel = firstDetails.getInt("Level");

                    scheduleCats.setCode(firstCode);
                    scheduleCats.setParentCode(firstParentCode);
                    scheduleCats.setName(firstName);

                    scheduleCatList.add(new Catalog(firstCode, firstParentCode, firstName, firstLevel));
                    Log.d(LOG_TAG, "loop through schedules: " + i + " name: " + firstName);

                    JSONArray firstCats = firstCat.optJSONArray("Cats");
                    if (firstCats != null) {
                        twoDeep = true;
                        for (int j = 0; j < firstCats.length(); j++) {
                            JSONObject secondChild = firstCats.getJSONObject(j);
                            JSONObject secondCat = secondChild.getJSONObject("Cat");
                            JSONObject secondDetails = secondCat.getJSONObject("Details");
                            String secondCode = secondDetails.getString("Code");
                            String secondName = secondDetails.getString("Name");
                            String secondParentCode = secondDetails.getString("Parent");
                            int secondLevel = secondDetails.getInt("Level");

                            JSONArray secondCats = secondCat.optJSONArray("Cats");
                            categoryCats.setCode(secondCode);
                            categoryCats.setParentCode(secondParentCode);
                            categoryCats.setName(secondName);
                            categoryCatList.add(new Catalog(secondCode, secondParentCode, secondName, secondLevel));

                            Log.d(LOG_TAG, "loop through categories: " + j + " name: " + secondName);

                            if (secondCats.length() > 0) {
                                threeDeep = true;
                                for (int k = 0; k < secondCats.length(); k++) {
                                    JSONObject thirdChild = secondCats.getJSONObject(k);
                                    JSONObject thirdCat = thirdChild.getJSONObject("Cat");
                                    JSONObject thirdDetails = thirdCat.getJSONObject("Details");
                                    String thirdCode = thirdDetails.getString("Code");
                                    String thirdName = thirdDetails.getString("Name");
                                    String thirdParentCode = thirdDetails.getString("Parent");
                                    int thirdLevel = thirdDetails.getInt("Level");


                                    subCatCats.setCode(thirdCode);
                                    subCatCats.setParentCode(thirdParentCode);
                                    subCatCats.setName(thirdName);
                                    subCatList.add(new Catalog(thirdCode, parentCode, thirdName, thirdLevel));
                                    //categoryCats.addToArray(k, subCatCats);

                                    Log.d(LOG_TAG, "loop through subcats: " + k + " name: " + thirdName);

                                }
                            }
                            categoryCats.setChildren(subCatList);
                            //subCatList.clear();
                            //scheduleCats.addToArray(j, categoryCats);
                        }
                    }
                    scheduleCats.setChildren(categoryCatList);

                    Log.d(LOG_TAG, "Category tally: " + Integer.toString(scheduleCats.getChildren().size()));
                    //categoryCatList.clear();

                    //fullCat.addToArray(i, scheduleCats);
                }
            }
            fullCat.setChildren(scheduleCatList);
            //scheduleCatList.clear();
            Log.d(LOG_TAG, "How many schedules? " + Integer.toString(fullCat.getChildren().size()));


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of catalogs
        return fullCat;
    }

    public static Catalog SchedulesWithCats(String response, String catCode) {
        Catalog scheduleCats = new Catalog();
        Catalog categoryCats = new Catalog();
        ArrayList<String> categoryCatNames;
        ArrayList<String> categoryCatCodes;
        try {

            boolean oneDeep = false;
            boolean twoDeep = false;

            JSONObject fullOb = new JSONObject(response);

            //data node
            JSONObject topCat = fullOb.getJSONObject("data");


            //Properties node
            JSONObject singleCat = topCat.getJSONObject("Cat");


            JSONArray cats = singleCat.optJSONArray("Cats");
            if (cats != null) {
                oneDeep = true;
                for(int i = 0; i < cats.length(); i++) {
                    JSONObject firstChild = cats.getJSONObject(i);
                    JSONObject firstCat = firstChild.getJSONObject("Cat");
                    JSONObject firstDetails = firstCat.getJSONObject("Details");
                    String firstCode = firstDetails.getString("Code");
                    if (firstCode.equals(catCode)) {
                        String firstName = firstDetails.getString("Name");
                        String firstParentCode = firstDetails.getString("Parent");
                        int firstLevel = firstDetails.getInt("Level");

                        scheduleCats.setCode(firstCode);
                        scheduleCats.setParentCode(firstParentCode);
                        scheduleCats.setName(firstName);

                        Log.d(LOG_TAG, "grabbing my schedule: " + catCode + " name: " + firstName);

                        JSONArray firstCats = firstCat.optJSONArray("Cats");
                        if (firstCats != null) {
                            twoDeep = true;
                            categoryCatCodes = new ArrayList<>();
                            categoryCatNames= new ArrayList<>();
                            for (int j = 0; j < firstCats.length(); j++) {
                                JSONObject secondChild = firstCats.getJSONObject(j);
                                JSONObject secondCat = secondChild.getJSONObject("Cat");
                                JSONObject secondDetails = secondCat.getJSONObject("Details");
                                String secondCode = secondDetails.getString("Code");
                                String secondName = secondDetails.getString("Name");
                                String secondParentCode = secondDetails.getString("Parent");
                                int secondLevel = secondDetails.getInt("Level");
                                categoryCats.setCode(secondCode);
                                categoryCats.setParentCode(firstCode);
                                categoryCats.setLevel(secondLevel);
                                categoryCatCodes.add(secondCode);
                                categoryCatNames.add(secondName);
                                Log.d(LOG_TAG, "adding this cat: " + secondName);
                            }
                            Log.d(LOG_TAG, "Size of the name array: " + Integer.toString(categoryCatNames.size()));
                            categoryCats.setChildCodes(categoryCatCodes);
                            categoryCats.setChildNames(categoryCatNames);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        Log.d(LOG_TAG, "Right before return, catNames: " + Integer.toString(categoryCats.getChildCodes().size()));
        return categoryCats;

    }

    public static Catalog RootWithSchedules(String response) {
        Catalog scheduleCats;
        ArrayList<String> scheduleNames;
        ArrayList<String> scheduleCodes;
        ArrayList<String> schedulesImages;
        ArrayList<Boolean> skipCategory;
        scheduleCats = new Catalog();
        try {
            boolean oneDeep = false;
            boolean twoDeep = false;

            JSONObject fullOb = new JSONObject(response);

            //data node
            JSONObject topCat = fullOb.getJSONObject("data");


            //Properties node
            JSONObject singleCat = topCat.getJSONObject("Cat");


            JSONArray cats = singleCat.optJSONArray("Cats");
            if (cats != null) {
                scheduleCodes = new ArrayList<>();
                scheduleNames = new ArrayList<>();
                schedulesImages = new ArrayList<>();
                skipCategory = new ArrayList<>();
                oneDeep = true;
                for (int i = 0; i < cats.length(); i++) {
                    JSONObject firstChild = cats.getJSONObject(i);
                    JSONObject firstCat = firstChild.getJSONObject("Cat");
                    JSONObject firstDetails = firstCat.getJSONObject("Details");
                    String firstCode = firstDetails.getString("Code");
                    String firstName = firstDetails.getString("Name");
                    String firstParentCode = firstDetails.getString("Parent");
                    int firstLevel = firstDetails.getInt("Level");

                    /*scheduleCats.setCode(firstCode);
                    scheduleCats.setParentCode(firstParentCode);
                    scheduleCats.setName(firstName);*/

                    JSONArray firstCats = firstCat.optJSONArray("Cats");
                    if (firstCats != null) {
                        twoDeep = true;
                        //for (int j = 0; j < firstCats.length(); j++) {
                        JSONObject secondChild = firstCats.getJSONObject(0);
                        JSONObject secondCat = secondChild.getJSONObject("Cat");
                        JSONObject secondDetails = secondCat.getJSONObject("Details");
                        String imageCode = secondDetails.getString("Code");
                        schedulesImages.add(imageCode);

                        if(firstCats.length() < 2) {
                            skipCategory.add(true);
                        }
                        else {
                            skipCategory.add(false);
                        }
                        scheduleCodes.add(firstCode);
                        scheduleNames.add(firstName);

                    }
                }
                scheduleCats.setChildNames(scheduleNames);
                scheduleCats.setChildCodes(scheduleCodes);
                scheduleCats.setChildImages(schedulesImages);
                scheduleCats.setSkipCategory(skipCategory);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG,"Issue parsing my JSON: ", e);
        }

        return scheduleCats;
    }

    public static Catalog CatWithSubCats(String response, String parentCode, String catCode) {
//catalogs = new ArrayList<>();
        fullCat = new Catalog();
        Catalog categoryCats = new Catalog();
        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            Catalog scheduleCats = new Catalog();
            Catalog subCatCats = new Catalog();
            ArrayList<String> subCatNames = new ArrayList<>();
            ArrayList<String> subCatCodes = new ArrayList<>();
            ArrayList<Catalog> subCatList = new ArrayList<>();
            boolean oneDeep = false;
            boolean twoDeep = false;
            boolean threeDeep = false;


            // build up a list of catalogs from catalog objects with the corresponding data.
            JSONObject fullOb = new JSONObject(response);

            //Feature node
            JSONObject topCat = fullOb.getJSONObject("data");


            //Properties node
            JSONObject singleCat = topCat.getJSONObject("Cat");
            JSONObject details = singleCat.getJSONObject("Details");



            JSONArray cats = singleCat.optJSONArray("Cats");
            if (cats != null) {
                oneDeep = true;
                for (int i = 0; i < cats.length(); i++) {
                    JSONObject firstChild = cats.getJSONObject(i);
                    JSONObject firstCat = firstChild.getJSONObject("Cat");
                    JSONObject firstDetails = firstCat.getJSONObject("Details");
                    String firstCode = firstDetails.getString("Code");
                    String firstName = firstDetails.getString("Name");
                    String firstParentCode = firstDetails.getString("Parent");
                    Log.d(LOG_TAG, "I'm comparing against.... " + parentCode + " " + firstCode );
                    if (firstCode.equals(parentCode)) {
                        Log.d(LOG_TAG, "My parent matched!");

                        /*scheduleCats.setCode(firstCode);
                        scheduleCats.setParentCode(firstParentCode);


                        scheduleCatList.add(new Catalog(firstCode, firstParentCode, firstName));*/
                        scheduleCats.setName(firstName);
                        Log.d(LOG_TAG, "loop through schedules: " + i + " name: " + firstName);

                        JSONArray firstCats = firstCat.optJSONArray("Cats");
                        if (firstCats.length() > 0) {
                            twoDeep = true;
                            for (int j = 0; j < firstCats.length(); j++) {
                                JSONObject secondChild = firstCats.getJSONObject(j);
                                JSONObject secondCat = secondChild.getJSONObject("Cat");
                                JSONObject secondDetails = secondCat.getJSONObject("Details");
                                String secondCode = secondDetails.getString("Code");

                                if(secondCode.equals(catCode)) {
                                    String secondName = secondDetails.getString("Name");
                                    String secondParentCode = secondDetails.getString("Parent");
                                    int secondLevel = secondDetails.getInt("Level");

                                    JSONArray secondCats = secondCat.optJSONArray("Cats");
                                    categoryCats.setCode(secondCode);
                                    categoryCats.setParentCode(firstCode);
                                    categoryCats.setName(secondName);
                                    categoryCats.setLevel(secondLevel);
                                    //categoryCatList.add(new Catalog(secondCode, firstCode, secondName, secondLevel));

                                    Log.d(LOG_TAG, "loop through categories: " + j + " name: " + secondName);

                                    if (secondCats.length() > 0) {
                                        threeDeep = true;
                                        for (int k = 0; k < secondCats.length(); k++) {
                                            JSONObject thirdChild = secondCats.getJSONObject(k);
                                            JSONObject thirdCat = thirdChild.getJSONObject("Cat");
                                            JSONObject thirdDetails = thirdCat.getJSONObject("Details");
                                            String thirdCode = thirdDetails.getString("Code");
                                            String thirdName = thirdDetails.getString("Name");
                                            String thirdParentCode = thirdDetails.getString("Parent");
                                            int thirdLevel = thirdDetails.getInt("Level");


                                            subCatCats.setCode(thirdCode);
                                            subCatCats.setParentCode(secondParentCode);
                                            subCatCats.setName(thirdName);
                                            subCatList.add(new Catalog(thirdCode, secondParentCode, thirdName, thirdLevel));
                                            subCatCodes.add(thirdCode);
                                            subCatNames.add(thirdName);
                                            //categoryCats.addToArray(k, subCatCats);

                                            Log.d(LOG_TAG, "loop through subcats: " + k + " name: " + thirdName);

                                        }
                                        categoryCats.setChildCodes(subCatCodes);
                                        categoryCats.setChildNames(subCatNames);
                                    }
                                } else {
                                    Log.d(LOG_TAG, "My code didn't match: " + catCode);
                                }

                                categoryCats.setChildren(subCatList);
                                for(int m = 0; m < categoryCats.getChildren().size(); m++) {
                                    Log.d(LOG_TAG, "My cat name: " + categoryCats.getChild(m).getName());
                                }
                            }
                        }
                        //Log.d(LOG_TAG, "Category tally: " + Integer.toString(scheduleCats.getChildren().size()));
                    }
                    else {
                        Log.d(LOG_TAG, "My parent didn't match: " + parentCode);
                    }
                }
                //fullCat.setChildren(scheduleCatList);
                //scheduleCatList.clear();
                Log.d(LOG_TAG, "How many schedules? " + Integer.toString(fullCat.getChildren().size()));


            }
        }catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of catalogs
        Log.d(LOG_TAG, "returning cat..... " + categoryCats.getName());
        /*for(int i = 0; i < categoryCats.getChildren().size(); i++) {
            Log.d(LOG_TAG, categoryCats.getChild(i).getName());
        }*/
        return categoryCats;
    }

    public static ArrayList<Item> getItemsFromJSON(String JSON) {
        itemArrayList = new ArrayList<>();
        try {

            JSONObject fullOb = new JSONObject(JSON);

            //data node
            JSONObject data = fullOb.getJSONObject("data");


            //Properties node
            JSONArray allItems = data.optJSONArray("Items");
            for(int i = 0; i < allItems.length(); i++) {
                JSONObject singleOb = allItems.getJSONObject(i);
                JSONObject singleItem = singleOb.getJSONObject("Item");
                String itemNumber = singleItem.getString("ItemNumber");
                String itemDescriptor = singleItem.getString("Descriptor");
                JSONObject pricing = singleItem.getJSONObject("Pricing");
                float price = (float)pricing.getDouble("Price");


                Item addingItem = new Item(itemNumber,itemDescriptor,price);
                itemArrayList.add(addingItem);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON", e);
        }
        if(itemArrayList.size() < 1) {
            itemArrayList.add(new Item("-1","No Items found",-1));
        }
        Log.d(LOG_TAG, "returning this many! " + Integer.toString(itemArrayList.size()));
        return itemArrayList;
    }
    public static Item getSingleItem(String JSON) {
        Item item = new Item();
        try {

            JSONObject fullOb = new JSONObject(JSON);

            //data node
            JSONObject data = fullOb.getJSONObject("data");


            //Properties node
                JSONObject singleItem = data.getJSONObject("Item");
                String itemNumber = singleItem.getString("ItemNumber");
                String itemDescriptor = singleItem.getString("Descriptor");
                JSONObject pricing = singleItem.getJSONObject("Pricing");
                float price = (float)pricing.getDouble("Price");


                item = new Item(itemNumber,itemDescriptor,price);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON", e);
        }

        Log.d(LOG_TAG, "returning this item! " + item.getItemDescriptor());
        return item;

    }


    public static ArrayList<String> getScheduleImages(String requestURL) {
        ArrayList<String> categoryCodes = new ArrayList<>();
        String rootWithSchedules = getJSONFromUrl(requestURL);

        MakeJson(rootWithSchedules);
        if (fullCat.getChildren() != null) {
            for (int i = 0; i < fullCat.getChildren().size(); i++) {
                Catalog schedule = fullCat.getChild(i);
                if (schedule.getChildren() != null) {
                    String categoryCode = schedule.getChild(0).getCode();
                    categoryCodes.add(categoryCode);

                }
            }
        }
        return categoryCodes;
    }

    public static ArrayList<OrderMod> GetModifiers(String ob) {

        ArrayList<OrderMod> mods = new ArrayList<>();

        try {
            JSONObject fullOb = new JSONObject(ob);

            JSONArray modifiers = fullOb.optJSONArray("modifiers");

            for(int i = 0; i < modifiers.length(); i++) {
                JSONObject singleOb = modifiers.getJSONObject(i);

                JSONObject mod = singleOb.getJSONObject("mod");

                Log.d(LOG_TAG,"Begin the parse!");

                String modName = mod.getString("name");
                Log.d(LOG_TAG,"Got name: " + modName);

                float price = (float) mod.getDouble("price");

                Log.d(LOG_TAG,"Got price: " + Integer.toString((int)price));

                OrderMod singleMod = new OrderMod(modName,price);
                mods.add(singleMod);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG,"Error with Mod " + e.getMessage());
        }
        return mods;

    }

    /*public static ArrayList<OrderItem> GetSuggested(String ob) {

        ArrayList<OrderItem> mods = new ArrayList<>();

        try {
            JSONObject fullOb = new JSONObject(ob);

            JSONArray items = fullOb.optJSONArray("suggested");

            for(int i = 0; i < items.length(); i++) {
                JSONObject singleOb = items.getJSONObject(i);

                Item singleItem = new Item();

                singleItem.setItemPrice();

                mods.add(singleMod);
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG,"Error with Mod " + e.getMessage());
        }
        return mods;

    }*/




}
