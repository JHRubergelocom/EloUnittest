package elounittest;

import de.elo.ix.client.IXConnection;
import com.example.elounittest.*;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;



public class EloService extends Service<Boolean> {
    private static final String ELOCLI = "eloCli";
    private static final String UNITTESTTOOLS = "unitTestTools";
    private static final String ELOSERVICES = "eloServices";

    private String typeCommand;
    private EloCommand eloCommand;
    private Stack stack;
    private Stacks stacks;
    private EloUnittestApp eloUnittestApp;
    private String unittestTool;
    private String eloService;
    private IXConnection ixConn;

    private void setTypeCommand(String typeCommand) {
        this.typeCommand = typeCommand;
    }

    private void setEloCommand(EloCommand eloCommand) {
        this.eloCommand = eloCommand;
    }

    private void setStack(Stack stack) {
        this.stack = stack;
    }

    private void setStacks(Stacks stacks) {
        this.stacks = stacks;
    }

    private void setEloTest(EloUnittestApp eloUnittestApp) {
        this.eloUnittestApp = eloUnittestApp;
    }

    private void setUnittestTool(String unittestTool) {
        this.unittestTool = unittestTool;
    }

    private void setEloService(String eloService) {
        this.eloService = eloService;
    }

    private void setIxConn(IXConnection ixConn) {
        this.ixConn = ixConn;
    }

    private void setProgress(double pgProgress, String txtProgress, String txtColor) {
        Platform.runLater(() -> {
            eloUnittestApp.getPgBar().setProgress(pgProgress);
            eloUnittestApp.getTxtProgress().setText(txtProgress);
            EloUnittestApp.setTextFieldColor(eloUnittestApp.getTxtProgress(), txtColor);
        });
    }

    private void executeEloCli() throws JSONException {
        try {
            setProgress(0.0, "Start!", "");
            String psCommand = eloCommand.getCmd() + " -stack " + stack.getStack() + " -workspace " + eloCommand.getWorkspace();
            if (eloCommand.getVersion().length() > 0) {
                psCommand = psCommand + " -version " + eloCommand.getVersion();
            }

            ProcessBuilder pb = new ProcessBuilder("powershell.exe", psCommand);
            pb.directory(new File(stack.getWorkingDir(stacks.getGitSolutionsDir())));
            Process p;
            p = pb.start();

            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            String htmlDoc = "<html>\n";
            String htmlHead = Http.CreateHtmlHead(psCommand);
            String htmlStyle = Http.CreateHtmlStyle();
            String htmlBody = "<body>\n";

            htmlBody += "<h1>"+ psCommand + "</h1>";
            setProgress(0.0, psCommand, "");

            String jsonWorkspace = stack.getWorkingDir(stacks.getGitSolutionsDir()) + "\\.workspace\\" + eloCommand.getWorkspace() + ".json";
            BufferedReader in = new BufferedReader(new FileReader(jsonWorkspace));
            String jsonString = "";
            while ((line = in.readLine()) != null) {
                // System.out.println("Gelesene Zeile: " + line);
                jsonString = jsonString.concat(line);

            }
            JSONObject jobjWorkspace = new JSONObject(jsonString);
            JSONObject[] jarrayDependencies = JsonUtils.getArray(jobjWorkspace, "dependencies");
            int countFolder = jarrayDependencies.length + 1;
            double pgIncrement = (1.0/countFolder);
            double pgProgress = 0.0;
            setProgress(pgProgress, "", "");

            while ((line = br.readLine()) != null) {
                htmlBody += "<h4>"+ line + "</h4>";
                System.out.println(line);

                if (line.contains("Starting development stack") || line.contains("Waiting for services")
                        || line.contains("Mandatory services are up")
                        || line.contains("Pushing Workspace de")) {
                    setProgress(pgProgress, line, "");
                }

                if (line.contains("- Pushing") || line.contains("- Pulling")) {
                    pgProgress = pgProgress + pgIncrement;
                    setProgress(pgProgress, line, "");
                }
                if (line.contains("already exists") || line.contains("is on branch")){
                    break;
                }
            }

            pgProgress = pgProgress + pgIncrement;
            setProgress(pgProgress, "Ready!", "");

            if (line != null) {
                if (line.contains("already exists") || line.contains("is on branch")) {
                    try (OutputStream os = p.getOutputStream()) {
                        OutputStreamWriter osr = new OutputStreamWriter(os);
                        BufferedWriter bw = new BufferedWriter(osr);

                        Scanner sc = new Scanner("n");
                        String input = sc.nextLine();
                        input += "\n";
                        bw.write(input);
                        bw.flush();
                        setProgress(1.0, line, "red");
                    }
                    br.close();
                }
            }

            htmlBody += "</body>\n";
            htmlDoc += htmlHead;
            htmlDoc += htmlStyle;
            htmlDoc += htmlBody;
            htmlDoc += "</html>\n";

            Http.ShowReport(htmlDoc);

            System.out.println("Programmende");

        } catch (IOException ex) {
            Platform.runLater(() -> {
                EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
            });
        }
    }

    private void executeUnittestTools() throws JSONException {
        switch(unittestTool) {
            case "show":
                EloApp.ShowUnittests(ixConn);
                break;
            case "matching":
                UnittestUtils.ShowReportMatchUnittest(ixConn, stack);
                break;
            case "create":
                UnittestUtils.CreateUnittest(ixConn, stack);
                break;
            case "ranger":
                EloApp.ShowRancher();
                break;
            case "gitpullall":
                executeGitPullAll(stacks.getDevDir());
                executeGitPullAll(stacks.getGitSolutionsDir());
                break;
            case "search":
                try {
                    SearchUtils.ShowSearchResult(ixConn, stack, eloUnittestApp);
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
                break;
            case "export":
                EloExport.StartExport(ixConn, stack, stacks);
                break;
            default:
                Platform.runLater(() -> {
                    EloUnittestApp.showAlert("Not supported", "unittestTool", unittestTool);
                });
                break;
        }
    }

    private void executeEloServices() throws JSONException {
        switch(eloService) {
            case "Application Server":
                EloApp.ShowEloApplicationServer(ixConn);
                break;
            case "Admin Console":
                EloApp.StartAdminConsole(ixConn);
                break;
            case "App Manager":
                EloApp.StartAppManager(ixConn);
                break;
            case "Webclient":
                EloApp.StartWebclient(ixConn);
                break;
            case "KnowledgeBoard":
                EloApp.ShowKnowledgeBoard(ixConn);
                break;
            default:
                Platform.runLater(() -> {
                    EloUnittestApp.showAlert("Not supported", "eloService", eloService);
                });
                break;
        }
    }



    private void executeGitPullAll(String workingDir) {
        try {
            SubDirectories(workingDir);
        } catch (IOException ex) {
            Platform.runLater(() -> {
                EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
            });
        }
    }

    private static String GitPull (String htmlBody, String gitDir) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("git", "pull");
        pb.directory(new File (gitDir));
        Process powerShellProcess = pb.start();
        // Getting the results
        powerShellProcess.getOutputStream().close();
        String line;


        System.out.println("Standard Output:");
        try (BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()))) {
            while ((line = stdout.readLine()) != null) {
                htmlBody += "<h4>"+ line + "</h4>";
                System.out.println(line);
            }
        }
        System.out.println("Standard Error:");
        try (BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()))) {
            while ((line = stderr.readLine()) != null) {
                System.err.println(line);
            }
        }
        System.out.println("Done");
        return htmlBody;
    }

    private static void SubDirectories(String directory) throws IOException {
        Optional<ArrayList<File>> optFiles = getPaths(new File(directory), new ArrayList<>());
        ArrayList<File> files;
        if (optFiles.isPresent()) {
            files = optFiles.get();
        } else {
            return;
        }

        String gitCommand;
        gitCommand = "directory " + directory + ": " + "GitPullAll";
        String htmlDoc = "<html>\n";
        String htmlHead = Http.CreateHtmlHead(gitCommand);
        String htmlStyle = Http.CreateHtmlStyle();
        String htmlBody = "<body>\n";

        htmlBody += "<h1>"+ gitCommand + "</h1>";

        for (File file: files) {
            htmlBody += "<h4>"+ file.getCanonicalPath() + "</h4>";
            System.out.println(file.getCanonicalPath());
            htmlBody = GitPull(htmlBody, file.getCanonicalPath());
        }

        htmlBody += "</body>\n";
        htmlDoc += htmlHead;
        htmlDoc += htmlStyle;
        htmlDoc += htmlBody;
        htmlDoc += "</html>\n";

        Http.ShowReport(htmlDoc);

    }

    private static Optional<ArrayList<File>> getPaths(File file, ArrayList<File> list) {
        if (file == null || list == null || !file.isDirectory())
            return Optional.empty();
        File[] fileArr = file.listFiles(f ->(f.getName().endsWith(".git")) && !(f.getName().contentEquals(".git")));
        for (File f : fileArr) {
            if (f.isDirectory()) {
                getPaths(f, list);
                list.add(f);
            }
        }
        return Optional.of(list);
    }

    public void runEloCommand(EloCommand eloCommand, Stack stack, Stacks stacks, EloUnittestApp eloTest) {
        setTypeCommand(ELOCLI);
        setEloCommand(eloCommand);
        setStack(stack);
        setStacks(stacks);
        setEloTest(eloTest);

        if (!eloCommand.getName().equals("eloPrepare")) {
            try {
                ixConn = Connection.getIxConnection(stack, stacks);
                if (isRunning()) {
                    System.out.println("Already running. Nothing to do.");
                } else {
                    reset();
                    start();
                }
            } catch (Exception ex) {
                EloUnittestApp.showAlert("Achtung!", "Exception", "System.Exception message: " + ex.getMessage());
            }
        } else {
            if (isRunning()) {
                System.out.println("Already running. Nothing to do.");
            } else {
                reset();
                start();
            }
        }
    }

    public void runUnittestTools(String unittestTool, Stack stack, Stacks stacks, EloUnittestApp eloTest) {
        try {
            ixConn = Connection.getIxConnection(stack, stacks);
            setTypeCommand(UNITTESTTOOLS);
            setUnittestTool(unittestTool);
            setStack(stack);
            setStacks(stacks);
            setEloTest(eloTest);
            setIxConn(ixConn);
            if (isRunning()) {
                System.out.println("Already running. Nothing to do.");
            } else {
                reset();
                start();
            }
        } catch (Exception ex) {
            EloUnittestApp.showAlert("Achtung!", "Exception", "System.Exception message: " + ex.getMessage());
        }
    }

    public void runEloServices(String eloService, Stack stack, Stacks stacks, EloUnittestApp eloTest) {
        try {
            ixConn = Connection.getIxConnection(stack, stacks);
            setTypeCommand(ELOSERVICES);
            setEloService(eloService);
            setStack(stack);
            setStacks(stacks);
            setEloTest(eloTest);
            setIxConn(ixConn);
            if (isRunning()) {
                System.out.println("Already running. Nothing to do.");
            } else {
                reset();
                start();
            }
        } catch (Exception ex) {
            EloUnittestApp.showAlert("Achtung!", "Exception", "System.Exception message: " + ex.getMessage());
        }

    }



    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                eloUnittestApp.setDisableControls(true);
                switch(typeCommand) {
                    case ELOCLI:
                        if (eloCommand.getName().equals("eloPrepare")) {
                            executeGitPullAll(stacks.getDevDir());
                            executeGitPullAll(stacks.getGitSolutionsDir());
                        }
                        executeEloCli();
                        break;
                    case UNITTESTTOOLS:
                        executeUnittestTools();
                        break;
                    case ELOSERVICES:
                        executeEloServices();
                        break;
                    default:
                        break;
                }
                eloUnittestApp.setDisableControls(false);
                return true;
            }
        };
    }

}
