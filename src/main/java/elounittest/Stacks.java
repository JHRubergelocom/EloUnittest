package elounittest;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.example.elounittest.EloUnittestApp;
import org.json.JSONException;
import org.json.JSONObject;

public class Stacks {
    private final SortedMap<String, Stack> stacks;
    private String gitSolutionsDir;
    private String gitDevDir;
    private String arcPath;
    private String user;
    private String pwd;

    public Stacks(String jsonFile) throws JSONException {
        stacks = new TreeMap<>();
        gitSolutionsDir = "";
        gitDevDir = "";
        arcPath = "";
        user = "";
        pwd = "";

        JSONObject jobjStacks;
        String jsonString = "";
        BufferedReader in = null;
        String line;

        try {
            in = new BufferedReader(new FileReader(jsonFile));
            while ((line = in.readLine()) != null) {
                // System.out.println("Gelesene Zeile: " + line);
                jsonString = jsonString.concat(line);
            }
        } catch (FileNotFoundException ex) {
            EloUnittestApp.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
        } catch (IOException ex) {
            EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                }
            }
        }
        jobjStacks = new JSONObject(jsonString);
        JSONObject[] jarrayStacks = JsonUtils.getArray(jobjStacks, "stacks");
        for(JSONObject objEloStack: jarrayStacks){
            stacks.put(objEloStack.getString("stack"), new Stack(objEloStack));
        }

        try {
            gitSolutionsDir = jobjStacks.getString("gitSolutionsDir");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            gitDevDir = jobjStacks.getString("gitDevDir");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            arcPath = jobjStacks.getString("arcPath");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            user = jobjStacks.getString("user");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            pwd = jobjStacks.getString("pwd");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    public String getGitSolutionsDir() {
        return gitSolutionsDir;
    }

    public String getDevDir() {
        return gitDevDir;
    }

    public String getUser() {
        return user;
    }

    public String getPwd() {
        return pwd;
    }

    public String getArcPath() {
        return arcPath;
    }

    public Map<String, Stack> getStacks() {
        return stacks;
    }

}
