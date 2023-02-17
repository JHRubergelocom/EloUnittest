package elounittest;

import com.example.elounittest.EloUnittestApp;

import java.io.*;
import java.util.Properties;

public class EloProperties extends Properties {
    private final File propertiesFile = new File("eloproperties.txt");

    public EloProperties() {
        Reader reader = null;
        try {
            if (!propertiesFile.exists()) {
                propertiesFile.createNewFile();
            }
            reader = new FileReader(propertiesFile);
            super.load(reader);
        } catch (FileNotFoundException ex) {
            EloUnittestApp.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
        } catch (IOException ex) {
            EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                }
            }
        }
    }

    private void saveProperties() {
        Writer writer = null;
        try {
            writer = new FileWriter(propertiesFile);
            store(writer, "EloProperties");
        } catch (IOException ex) {
            EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                }
            }
        }
    }

    public void setSelectedStack(String name) {
        setProperty("SelectedStack", name);
        saveProperties();
    }

    public String getSelectedStack() {
        return getProperty("SelectedStack", "");
    }

    public String getSelectedEloCli() {
        return getProperty("SelectedEloCli", "");
    }

    public void setSelectedEloCli(String name) {
        setProperty("SelectedEloCli", name);
        saveProperties();
    }

    public String getSelectedUnittestTools() {
        return getProperty("SelectedUnittestTools", "");
    }

    public void setSelectedUnittestTools(String name) {
        setProperty("SelectedUnittestTools", name);
        saveProperties();
    }

    public String getSelectedEloServices() {
        return getProperty("SelectedEloServices", "");
    }

    public void setSelectedEloServices(String name) {
        setProperty("SelectedEloServices", name);
        saveProperties();
    }


    public void setPattern(String pattern) {
        setProperty("Pattern", pattern);
        saveProperties();
    }
    public String getPattern() {
        return getProperty("Pattern", "");
    }

    public void setCaseSensitiv(boolean caseSensitiv) {
        setProperty("CaseSensitiv", Boolean.toString(caseSensitiv));
        saveProperties();
    }
    public boolean getCaseSensitiv() {
        String value = getProperty("CaseSensitiv");
        if (value == null) {
            value = "false";
        }
        value = value.toLowerCase();
        return value.toLowerCase().equals("true");
    }

}
