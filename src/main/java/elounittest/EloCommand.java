package elounittest;

import org.json.JSONException;
import org.json.JSONObject;

public class EloCommand {

    private String name;
    private String cmd;
    private String workspace;
    private String version;


    EloCommand(JSONObject obj) {
        name = "";
        cmd = "";
        workspace = "";
        version = "";

        try {
            name = obj.getString("name");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            cmd = obj.getString("cmd");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            workspace = obj.getString("workspace");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        try {
            version = obj.getString("version");
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }
    public String getName() {
        return name;
    }

    public String getCmd() {
        return cmd;
    }

    public String getWorkspace() {
        return workspace;
    }

    public String getVersion() {
        return version;
    }

}
