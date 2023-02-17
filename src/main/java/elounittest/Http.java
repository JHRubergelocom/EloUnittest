package elounittest;

import com.example.elounittest.EloUnittestApp;
import javafx.application.Platform;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class Http {
    static void OpenUrl(String url) {
        if(java.awt.Desktop.isDesktopSupported() ) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            java.net.URI uri;
            try {
                if(desktop.isSupported(java.awt.Desktop.Action.BROWSE) ) {
                    uri = new java.net.URI(url);
                    try {
                        desktop.browse(uri);
                    } catch (IOException ex) {
                        Platform.runLater(() -> {
                            EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                        });
                    }
                }
            } catch (URISyntaxException ex) {
                Platform.runLater(() -> {
                    EloUnittestApp.showAlert("Achtung!", "URISyntaxException", "System.URISyntaxException message: " + ex.getMessage());
                });
            }
        }
    }

    static String CreateHtmlHead(String title) {
        String htmlHead = "  <head>\n";
        htmlHead += "    <title>" + title + "</title>\n";
        htmlHead += "  </head>\n";
        return htmlHead;
    }

    static String CreateHtmlStyle() {
        String htmlStyle = "  <style>\n";

        htmlStyle += "body {\n";
        htmlStyle += "  font-family: 'Courier', monospace;\n";
        htmlStyle += "  margin: 15px;\n";
        htmlStyle += "  font-size: 12px;\n";
        htmlStyle += "}\n";
        htmlStyle += "span {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: yellow;\n";
        htmlStyle += "}\n";
        htmlStyle += "table {\n";
        htmlStyle += "  font-size: 12px;\n";
        htmlStyle += "  padding-left: 10px;\n";
        htmlStyle += "  border-width: 0px;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "  border-color: #A2A2A2;\n";
        htmlStyle += "}\n";
        htmlStyle += "table td {\n";
        htmlStyle += "  padding: 3px 7px;\n";
        htmlStyle += "}\n";
        htmlStyle += "table tr {\n";
        htmlStyle += "  white-space: nowrap;\n";
        htmlStyle += "}\n";
        htmlStyle += ".tdh {\n";
        htmlStyle += "  font-weight: bold;\n";
        htmlStyle += "  padding: 5px 5px 5px 5px;\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 1px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td1 {\n";
        htmlStyle += "  background-color: white;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 0px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td2 {\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 0px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td1b {\n";
        htmlStyle += "  background-color: white;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td2b {\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td1r {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: white;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 0px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td2r {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 0px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td1br {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: white;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += ".td2br {\n";
        htmlStyle += "  color: red;\n";
        htmlStyle += "  background-color: #A2A2A2;\n";
        htmlStyle += "  border-top-width: 0px;\n";
        htmlStyle += "  border-left-width: 1px;\n";
        htmlStyle += "  border-right-width: 1px;\n";
        htmlStyle += "  border-bottom-width: 1px;\n";
        htmlStyle += "  border-color: grey;\n";
        htmlStyle += "  border-style: solid;\n";
        htmlStyle += "  border-collapse: collapse;\n";
        htmlStyle += "}\n";
        htmlStyle += "h1 {\n";
        htmlStyle += "  padding: 30px 0px 0px 0px;\n";
        htmlStyle += "  font-size: 16px;\n";
        htmlStyle += "  font-weight: bold;\n";
        htmlStyle += "}\n";

        htmlStyle += "  </style>\n";
        return htmlStyle;
    }

    static String CreateHtmlTableDics(String header, String col1, String col2, SortedMap<String, Boolean> dics) {
        List<String> cols = new ArrayList<>();
        cols.add(col1);
        cols.add(col2);
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : dics.entrySet()) {
            List<String> row = new ArrayList<>();
            row.add(entry.getKey());
            row.add(entry.getValue().toString());
            rows.add(row);
        }
        return CreateHtmlTable(header, cols, rows);
    }

    static String CreateHtmlTableDicLibs(String header, String col1, String col2, String col3, SortedMap<String, SortedMap<String, Boolean>> dicLibs) {
        List<String> cols = new ArrayList<>();
        cols.add(col1);
        cols.add(col2);
        cols.add(col3);
        List<List<String>> rows = new ArrayList<>();
        for (Map.Entry<String, SortedMap<String, Boolean>> entryClass : dicLibs.entrySet()) {
            for (Map.Entry<String, Boolean> entryMethod : entryClass.getValue().entrySet()) {
                List<String> row = new ArrayList<>();
                row.add(entryClass.getKey());
                row.add(entryMethod.getKey());
                row.add(entryMethod.getValue().toString());
                rows.add(row);
            }
        }
        return CreateHtmlTable(header, cols, rows);
    }

    static String CreateHtmlTable(String header, List<String> cols, List<List<String>> rows) {
        String htmlTable = "    <h1>" + header + "</h1>\n";
        htmlTable += "    <div class='container'>\n";
        htmlTable += "      <table border='2'>\n";
        htmlTable += "        <colgroup>\n";
        htmlTable = cols.stream().map((_item) -> "          <col width='100'>\n").reduce(htmlTable, String::concat);
        htmlTable += "        </colgroup>\n";
        htmlTable += "        <tr>\n";
        htmlTable = cols.stream().map((col) -> "          <td class = 'tdh' align='left' valign='top'>" + col + "</td>\n").reduce(htmlTable, String::concat);
        htmlTable += "        </tr>\n";

        int i = 0;
        for (List<String> row : rows) {
            String td = "td2";
            if ((i % 2) == 0) {
                td = "td1";
            }
            if (i == (rows.size() - 1)) {
                td += "b";
            }
            htmlTable += "        <tr>\n";

            td = row.stream().filter((cell) -> (cell.equals("false"))).map((_item) -> "r").reduce(td, String::concat);
            for (String cell : row) {
                htmlTable += "          <td class = '" + td + "' align='left' valign='top'>" + cell + "</td>\n";
            }

            htmlTable += "        </tr>\n";
            i++;
        }
        htmlTable += "      </table>\n";
        htmlTable += "    </div>\n";

        return htmlTable;

    }

    static void ShowReport (String htmlDoc) {
        File dir = new File("C:\\Temp");
        String reportPath = "C:\\Temp\\Report.html";
        File reportFile = new File(reportPath);
        URI uri = reportFile.toURI();
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
            if (!reportFile.exists()) {
                reportFile.createNewFile();
            }
            FileWriter fw = new FileWriter(reportFile);
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(htmlDoc);
            }
        } catch (IOException ex) {
            Platform.runLater(() -> {
                EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
            });
        }
        try {
            URL url = uri.toURL();
            Http.OpenUrl(url.toString());
        } catch (MalformedURLException ex) {
            Platform.runLater(() -> {
                EloUnittestApp.showAlert("Achtung!", "MalformedURLException", "System.MalformedURLException message: " + ex.getMessage());
            });
        }
    }
}
