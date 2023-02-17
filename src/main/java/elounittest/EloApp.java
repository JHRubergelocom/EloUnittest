package elounittest;

import com.example.elounittest.EloUnittestApp;
import de.elo.ix.client.IXConnection;
import de.elo.ix.client.Sord;
import javafx.application.Platform;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EloApp {

    private static Map<String, String> GetUnittestApp(IXConnection ixConn) throws JSONException {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/development/ELOapps/ClientInfos";
        Sord[] sordELOappsClientInfo = RepoUtils.FindChildren(ixConn, parentId, false, true);
        String configApp = "";
        String configId = "";
        String jsonString;

        Map<String, String> dicApp = new HashMap<>();
        for (Sord s : sordELOappsClientInfo) {
            jsonString = RepoUtils.DownloadDocumentToString(ixConn, s);
            jsonString = jsonString.replaceAll("namespace", "namespace1");
            JSONObject config = new JSONObject(jsonString);
            JSONObject web = config.getJSONObject("web");
            String webId = web.getString("id");
            if (webId != null)
            {
                if (webId.contains("UnitTests"))
                {
                    configApp = web.getString("namespace1") + "." + web.getString("id");
                    configId = config.getString("id");
                }
            }
        }
        dicApp.put("configApp", configApp);
        dicApp.put("configId", configId);

        return dicApp;
    }

    static void ShowUnittests(IXConnection ixConn) throws JSONException {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String appUrl = ixUrl.replaceAll("ix-", "wf-");

        appUrl = appUrl.replaceAll("/ix", "/apps/app");
        appUrl = appUrl + "/";
        Map<String, String> dicApp = GetUnittestApp(ixConn);
        appUrl = appUrl + dicApp.get("configApp");
        appUrl = appUrl + "/?lang=de";
        appUrl = appUrl + "&ciId=" + dicApp.get("configApp");
        appUrl = appUrl + "&ticket=" + ticket;
        appUrl = appUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appUrl);
    }

    static void ShowRancher() {
        try {
            String rancherUrl = "http://rancher.elo.local/env/1a81/apps/stacks?tags=&which=all";
            Http.OpenUrl(rancherUrl);
        } catch (Exception ex) {
            Platform.runLater(() -> {
                EloUnittestApp.showAlert("Achtung!", "Exception", "System.Exception message: " + ex.getMessage());
            });
        }
    }

    static void ShowEloApplicationServer(IXConnection ixConn) {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String[] eloApplicationServer = ixUrl.split("/");
        String eloApplicationServerUrl = eloApplicationServer[0] + "//" + eloApplicationServer[2] + "/manager/html";
        eloApplicationServerUrl = eloApplicationServerUrl + "/?lang=de";
        eloApplicationServerUrl = eloApplicationServerUrl + "&ticket=" + ticket;
        eloApplicationServerUrl = eloApplicationServerUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(eloApplicationServerUrl);
    }

    static void StartAdminConsole(IXConnection ixConn) {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String[] adminConsole = ixUrl.split("/");
        String adminConsoleUrl = adminConsole[0] + "//" + adminConsole[2] + "/AdminConsole";
        adminConsoleUrl = adminConsoleUrl + "/?lang=de";
        adminConsoleUrl = adminConsoleUrl + "&ticket=" + ticket;
        adminConsoleUrl = adminConsoleUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(adminConsoleUrl);
    }

    static void StartAppManager(IXConnection ixConn) {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String appManagerUrl = ixUrl.replace("ix-", "wf-");
        appManagerUrl = appManagerUrl.replace("/ix", "/apps/app");
        appManagerUrl = appManagerUrl + "/elo.webapps.AppManager";
        appManagerUrl = appManagerUrl + "/?lang=de";
        appManagerUrl = appManagerUrl + "&ticket=" + ticket;
        appManagerUrl = appManagerUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appManagerUrl);
    }

    static void StartWebclient(IXConnection ixConn) {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String webclientUrl = ixUrl.replace("ix-", "web-");
        webclientUrl = webclientUrl.replace("/ix", "");
        webclientUrl = webclientUrl + "/?lang=de";
        webclientUrl = webclientUrl + "&ticket=" + ticket;
        webclientUrl = webclientUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(webclientUrl);
    }

    private static Map<String, String> GetKnowledgeBoard(IXConnection ixConn) throws JSONException {
        String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/knowledge/ELOapps/ClientInfos";
        Sord[] sordELOappsClientInfo = RepoUtils.FindChildren(ixConn, parentId, false, true);
        String configApp = "";
        String configId = "";
        String jsonString;

        Map<String, String> dicApp = new HashMap<>();
        for (Sord s : sordELOappsClientInfo) {
            jsonString = RepoUtils.DownloadDocumentToString(ixConn, s);
            jsonString = jsonString.replaceAll("namespace", "namespace1");
            JSONObject config = new JSONObject(jsonString);
            JSONObject web = config.getJSONObject("web");

            String id = config.getString("id");
            if (id != null) {
                if (id.contains("tile-sol-knowledge-board")) {
                    configApp = web.getString("namespace1") + "." + web.getString("id");
                    configId = id;

                }
            }
        }
        dicApp.put("configApp", configApp);
        dicApp.put("configId", configId);

        return dicApp;
    }

    static void ShowKnowledgeBoard(IXConnection ixConn) throws JSONException {
        String ticket = ixConn.getLoginResult().getClientInfo().getTicket();
        String ixUrl = ixConn.getEndpointUrl();
        String appUrl = ixUrl.replace("ix-", "wf-");
        appUrl = appUrl.replace("/ix", "/apps/app");
        appUrl = appUrl + "/";
        Map<String, String> dicApp = GetKnowledgeBoard(ixConn);
        appUrl = appUrl + dicApp.get("configApp");
        appUrl = appUrl + "/?lang=de";
        appUrl = appUrl + "&ciId=" + dicApp.get("configApp");
        appUrl = appUrl + "&ticket=" + ticket;
        appUrl = appUrl + "&timezone=Europe%2FBerlin";
        Http.OpenUrl(appUrl);
    }
}
