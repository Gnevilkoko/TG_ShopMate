package me.gnevilkoko.Utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class LocaleFile {

    public static String getString(String language, String key) {
        Gson gson = new Gson();

        String filePath = System.getProperty("user.dir")+ File.separator+"locales"+File.separator + language + ".json";
        try (FileReader fileReader = new FileReader(filePath)) {
            JsonElement jsonElement = JsonParser.parseReader(fileReader);
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            JsonElement valueElement = jsonObject.get(key);
            if (valueElement != null) {
                return valueElement.getAsString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static ArrayList<String> getLocales(){
        File file = new File(System.getProperty("user.dir")+ File.separator+"locales");
        String[] list = file.list();

        if(list == null) return new ArrayList<>();

        ArrayList<String> res = new ArrayList<>();
        for(String lang : list){
            res.add(lang.substring(0, lang.length()-5));
        }
        return res;
    }
}
