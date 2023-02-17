package elounittest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EloPackage {

    private String name;
    private final Map<String, String> folders;

    EloPackage(JSONObject obj) throws JSONException {
        name = "";
        folders = new HashMap<>();

        try {
            name = obj.getString("name");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        String[] jarrayfolders = JsonUtils.getStringArray(obj, "folders");
        for(String folder: jarrayfolders){
            folders.put(folder, folder);
        }
    }
    EloPackage() {
        name = "";
        folders = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getFolders() {
        return folders;
    }

}
