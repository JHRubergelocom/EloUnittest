package elounittest;

import com.example.elounittest.EloUnittestApp;
import com.google.gson.Gson;
import de.elo.ix.client.DocMask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtils {
    public static JSONObject[] getArray (JSONObject jobj, String key) throws JSONException {
        JSONArray jarr = jobj.getJSONArray(key);
        JSONObject jobjs[] = new JSONObject[jarr.length()];
        for (int i = 0; i < jarr.length(); i++) {
            jobjs[i] = jarr.getJSONObject(i);
        }
        return jobjs;
    }
    public static String[] getStringArray (JSONObject jobj, String key) throws JSONException {
        JSONArray jarr = jobj.getJSONArray(key);
        String jstrings[] = new String[jarr.length()];
        for (int i = 0; i < jarr.length(); i++) {
            jstrings[i] = jarr.getString(i);
        }
        return jstrings;
    }

    static String getJsonString(DocMask dm) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(dm);
        return jsonString;
    }

    static DocMask getDocMask(String jsonString) {
        Gson gson = new Gson();
        DocMask dm = gson.fromJson(jsonString, DocMask.class);
        return dm;
    }

    static String formatJsonString(String jsonText) {
        try {
            JSONObject obj = new JSONObject (jsonText);
            return obj.toString(2);
        } catch (JSONException ex){
            EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
        }
        return jsonText;
    }
}
