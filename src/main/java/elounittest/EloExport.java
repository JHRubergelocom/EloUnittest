package elounittest;

import com.example.elounittest.EloUnittestApp;
import de.elo.ix.client.IXConnection;
import javafx.application.Platform;

import java.io.File;

public class EloExport {
    static private final boolean REFERENCES = false;

    static void StartExport(IXConnection ixConn, Stack stack, Stacks stacks) {
        try {
            String solutionname = stack.getSolution();
            String exportPath = "C:\\Temp\\ExportElo\\" + solutionname;
            String arcPath = stacks.getArcPath();
            File exportDir = new File(exportPath);
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            RepoUtils.FindChildren(ixConn, arcPath, exportDir, REFERENCES);
            WfUtils.FindWorkflows(ixConn, exportDir);
            MaskUtils.FindDocMasks(ixConn, exportDir);
            System.out.println("ticket=" + ixConn.getLoginResult().getClientInfo().getTicket());

        } catch (Exception ex) {
            Platform.runLater(() -> {
                EloUnittestApp.showAlert("Achtung!", "Exception", "System.Exception message: " + ex.getMessage());
            });
        }
    }
}
