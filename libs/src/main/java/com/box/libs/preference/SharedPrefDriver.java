package com.box.libs.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.box.libs.preference.protocol.IProvider;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by linjiang on 04/06/2018.
 */

public class SharedPrefDriver {

    private static final String TAG = "SharedPrefDriver";
    private static final String SPECIFIC_SEGMENT = "/user_de/";

    private Context context;
    private Context DPStorageContext;

    public SharedPrefDriver(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DPStorageContext = context.createDeviceProtectedStorageContext();
        }
    }

    private static String removeSuffix(String name) {
        return name.substring(0, name.length() - IProvider.DEF_SUFFIX.length());
    }

    private static String convert2String(Object value) {
        if (value != null) {
            if (value instanceof Set) {
                JSONArray array = new JSONArray();
                for (String entry : (Set<String>) value) {
                    array.put(entry);
                }
                return array.toString();
            } else {
                return value.toString();
            }
        } else {
            return "";
        }
    }

    private static void fillEditor(SharedPreferences.Editor editor, String key, String newValue,
            Object existingValue)
    {
        if (existingValue instanceof Integer) {
            editor.putInt(key, Integer.parseInt(newValue));
        } else if (existingValue instanceof Long) {
            editor.putLong(key, Long.parseLong(newValue));
        } else if (existingValue instanceof Float) {
            editor.putFloat(key, Float.parseFloat(newValue));
        } else if (existingValue instanceof Boolean) {
            editor.putBoolean(key, Boolean.parseBoolean(newValue));
        } else if (existingValue instanceof String) {
            editor.putString(key, newValue);
        } else if (existingValue instanceof Set) {
            try {
                JSONArray obj = new JSONArray(newValue);
                int objN = obj.length();
                HashSet<String> set = new HashSet<>(objN);
                for (int i = 0; i < objN; i++) {
                    set.add(obj.getString(i));
                }
                editor.putStringSet(key, set);
            } catch (JSONException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            throw new IllegalArgumentException(
                    "Unsupported type: " + existingValue.getClass().getName());
        }
    }

    public List<File> getSharedPrefDescs(IProvider provider) {
        List<File> files = provider.getSharedPrefFiles(context);
        return files;
    }

    public Map<String, String> getSharedPrefContent(File file) {
        SharedPreferences prefs = getSharedPreferences(file);
        Map<String, String> transformed = new HashMap<>();
        for (Map.Entry<String, ?> entry : prefs.getAll().entrySet()) {
            transformed.put(entry.getKey(), convert2String(entry.getValue()));
        }
        return transformed;
    }

    public void updateSharedPref(File file, String key, String value) {
        SharedPreferences prefs = getSharedPreferences(file);
        Object oldValue = prefs.getAll().get(key);
        SharedPreferences.Editor editor = prefs.edit();
        fillEditor(editor, key, value, oldValue);
        editor.apply();
    }

    public void removeSharedPrefKey(File file, String key) {
        SharedPreferences prefs = getSharedPreferences(file);
        prefs.edit().remove(key).apply();
    }

    private SharedPreferences getSharedPreferences(File file) {
        String noSuffixName = removeSuffix(file.getName());
        SharedPreferences sp;
        if (file.getPath().contains(SPECIFIC_SEGMENT)) {// not perfect but simple
            sp = DPStorageContext.getSharedPreferences(noSuffixName, Context.MODE_PRIVATE);
        } else {
            sp = context.getSharedPreferences(noSuffixName, Context.MODE_PRIVATE);
        }
        return sp;
    }
}
