package elounittest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Stack {
    private String solution;
    private String stack;
    private Map<String, EloPackage> eloPackages;
    private Map<String, EloCommand> eloCommands;

    public Stack(JSONObject obj) {
        solution = "";
        stack = "playground";
        eloPackages = new HashMap<>();
        eloCommands = new HashMap<>();
        try {
            solution = obj.getString("solution");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            stack = obj.getString("stack");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            JSONObject[] jarrayEloPackages = JsonUtils.getArray(obj, "eloPackages");
            for(JSONObject objEloPackage: jarrayEloPackages){
                eloPackages.put(objEloPackage.getString("name"), new EloPackage(objEloPackage));
            }
        } catch (JSONException ex) {
            eloPackages = new HashMap<>();
        }
        try {
            JSONObject[] jarrayEloCommands = JsonUtils.getArray(obj, "eloCommands");
            for(JSONObject objEloCommand: jarrayEloCommands){
                eloCommands.put(objEloCommand.getString("name"), new EloCommand(objEloCommand));
            }
        } catch (JSONException ex) {
            eloCommands = new HashMap<>();
        }
    }

    public String getSolution() {
        return solution;
    }

    public String getStack() {
        return stack;
    }

    Map<String, EloPackage> getEloPackages() {
        return eloPackages;
    }

    public Map<String, EloCommand> getEloCommands() {
        return eloCommands;
    }

    public String getIxUrl() {
        return  "http://" + getStack() + ".dev.elo/ix-Solutions/ix";
    }

    public String getWorkingDir(String gitSolutionsDir) {
        switch (solution) {
            case "recruiting":
                return gitSolutionsDir + "\\hr_" + solution + ".git";
            case "datevaccounting":
                return gitSolutionsDir + "\\datev_accounting.git";
            case "teamroom":
                return gitSolutionsDir + "\\" + solution + "1.git";
            default:
                return gitSolutionsDir + "\\" + solution + ".git";
        }
    }
}
