package io.github.lianjordaan.bytebuildersplotplugin.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class JsonDataManager {
    private static final Gson gson = new Gson();
    private static final File jsonFile = Paths.get(".", "data.json").toFile();
    private static JsonObject jsonObject;

    // Load JSON content from the file
    static {
        loadJson();
    }

    private static void loadJson() {
        try (FileReader reader = new FileReader(jsonFile)) {
            jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            jsonObject = new JsonObject(); // Initialize a new object if file not found or error occurred
        }
    }

    private static void saveJson() {
        try (FileWriter writer = new FileWriter(jsonFile)) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to traverse or create nested objects
    private static JsonObject getOrCreateNestedObject(JsonObject parent, String[] pathParts) {
        JsonObject current = parent;
        for (int i = 0; i < pathParts.length - 1; i++) {
            String part = pathParts[i];
            if (!current.has(part) || !current.get(part).isJsonObject()) {
                current.add(part, new JsonObject());
            }
            current = current.getAsJsonObject(part);
        }
        return current;
    }

    // Setters for various data types
    public static void set(String key, String value) {
        String[] pathParts = key.split("\\.");
        JsonObject parent = getOrCreateNestedObject(jsonObject, pathParts);
        parent.addProperty(pathParts[pathParts.length - 1], value);
        saveJson();
    }

    public static void set(String key, Number value) {
        String[] pathParts = key.split("\\.");
        JsonObject parent = getOrCreateNestedObject(jsonObject, pathParts);
        parent.addProperty(pathParts[pathParts.length - 1], value);
        saveJson();
    }

    public static void set(String key, Boolean value) {
        String[] pathParts = key.split("\\.");
        JsonObject parent = getOrCreateNestedObject(jsonObject, pathParts);
        parent.addProperty(pathParts[pathParts.length - 1], value);
        saveJson();
    }

    // Generic getter for a JSON element by key
    private static JsonElement getElement(String key) {
        String[] pathParts = key.split("\\.");
        JsonObject current = jsonObject;
        for (int i = 0; i < pathParts.length - 1; i++) {
            if (current.has(pathParts[i]) && current.get(pathParts[i]).isJsonObject()) {
                current = current.getAsJsonObject(pathParts[i]);
            } else {
                return null;
            }
        }
        return current.get(pathParts[pathParts.length - 1]);
    }

    // Getters for various data types
    public static String getString(String key) {
        JsonElement element = getElement(key);
        return (element != null && element.isJsonPrimitive()) ? element.getAsString() : null;
    }

    public static Integer getInt(String key) {
        JsonElement element = getElement(key);
        return (element != null && element.isJsonPrimitive()) ? element.getAsInt() : null;
    }

    public static Double getDouble(String key) {
        JsonElement element = getElement(key);
        return (element != null && element.isJsonPrimitive()) ? element.getAsDouble() : null;
    }

    public static Float getFloat(String key) {
        JsonElement element = getElement(key);
        return (element != null && element.isJsonPrimitive()) ? element.getAsFloat() : null;
    }

    public static Boolean getBoolean(String key) {
        JsonElement element = getElement(key);
        return (element != null && element.isJsonPrimitive()) && element.getAsBoolean();
    }

    /**
     * Removes a key-value pair from the JSON file.
     *
     * @param key the key to remove from the JSON
     */
    public static void remove(String key) {
        String[] pathParts = key.split("\\.");
        JsonObject current = jsonObject;
        for (int i = 0; i < pathParts.length - 1; i++) {
            if (current.has(pathParts[i]) && current.get(pathParts[i]).isJsonObject()) {
                current = current.getAsJsonObject(pathParts[i]);
            } else {
                return; // Key not found, nothing to remove
            }
        }
        current.remove(pathParts[pathParts.length - 1]);
        saveJson();
    }
}
