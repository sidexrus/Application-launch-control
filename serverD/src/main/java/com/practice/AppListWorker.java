package com.practice;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;

public class AppListWorker {
    static class Applications{
        String[] applications;
    }

    static String ToJson(ArrayList<String> apps){
        JsonObject sequence = new JsonObject();
        JsonArray apps_array = new JsonArray();
        for(String app:apps)
            apps_array.add(app);
        sequence.add("sequence", apps_array);
        return sequence.toString();
    }

    static ArrayList<String> ToArrayList(String file) {
        Gson gson = new Gson();
        Applications apps = gson.fromJson(file, Applications.class);
        return new ArrayList<String>(Arrays.asList(apps.applications));
    }
}
