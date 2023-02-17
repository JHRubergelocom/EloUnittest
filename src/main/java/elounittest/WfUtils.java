package elounittest;

import byps.RemoteException;
import com.example.elounittest.EloUnittestApp;
import de.elo.ix.client.*;
import javafx.application.Platform;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Pattern;

public class WfUtils {

    private static WFDiagram[] FindWorkflows(IXConnection ixConn, FindWorkflowInfo findWorkflowInfo) {
        int max = 100;
        int idx = 0;
        FindResult findResult = new FindResult();
        List<WFDiagram> wfList = new ArrayList<>();
        WFDiagramZ checkoutOptions = WFDiagramC.mbLean;
        try {
            findResult = ixConn.ix().findFirstWorkflows(findWorkflowInfo, max, checkoutOptions);
            while (true) {
                WFDiagram[] wfArray = findResult.getWorkflows();
                wfList.addAll(Arrays.asList(wfArray));
                if (!findResult.isMoreResults()) {
                    break;
                }
                idx += wfArray.length;
                findResult = ixConn.ix().findNextWorkflows(findResult.getSearchId(), idx, max);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } finally {
            if (findResult != null) {
                try {
                    ixConn.ix().findClose(findResult.getSearchId());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }
        WFDiagram[] workflows = new WFDiagram[wfList.size()];
        workflows = wfList.toArray(workflows);
        return workflows;
    }

    static void FindWorkflows(IXConnection ixConn, File exportPath) {
        FindWorkflowInfo findWorkflowInfo = new FindWorkflowInfo();
        findWorkflowInfo.setType(WFTypeC.TEMPLATE);

        int max = 100;
        int idx = 0;
        FindResult findResult = new FindResult();
        List<WFDiagram> wfList = new ArrayList<>();
        WFDiagramZ checkoutOptions = WFDiagramC.mbLean;
        try {
            findResult = ixConn.ix().findFirstWorkflows(findWorkflowInfo, max, checkoutOptions);
            while (true) {
                WFDiagram[] wfArray = findResult.getWorkflows();
                wfList.addAll(Arrays.asList(wfArray));
                if (!findResult.isMoreResults()) {
                    break;
                }
                idx += wfArray.length;
                findResult = ixConn.ix().findNextWorkflows(findResult.getSearchId(), idx, max);
            }
        } catch (RemoteException ex) {
            System.out.println("RemoteException: " + ex.getMessage());

        } finally {
            if (findResult != null) {
                try {
                    ixConn.ix().findClose(findResult.getSearchId());
                } catch (RemoteException ex) {
                    System.out.println("RemoteException: " + ex.getMessage());
                }
            }
        }
        WFDiagram[] workflows = new WFDiagram[wfList.size()];
        workflows = wfList.toArray(workflows);

        for (WFDiagram wf : workflows) {
            ExportWorkflow(ixConn, exportPath, wf);
        }
    }

    private static WFDiagram[] GetTemplates(IXConnection ixConn) {
        FindWorkflowInfo info = new FindWorkflowInfo();
        info.setType(WFTypeC.TEMPLATE);
        return FindWorkflows(ixConn, info);
    }

    private static int GetWorkflowTemplateId(IXConnection ixConn, String workflowTemplateName) throws RemoteException {
        WFDiagram wfDiag = ixConn.ix().checkoutWorkflowTemplate(workflowTemplateName, "", new WFDiagramZ(WFDiagramC.mbId), LockC.NO);
        return wfDiag.getId();
    }

    private static String GetWorkflowAsJsonText(IXConnection ixConn, int flowId) throws RemoteException, UnsupportedEncodingException {
        WorkflowExportOptions workflowExportOptions = new WorkflowExportOptions();
        workflowExportOptions.setFlowId(Integer.toString(flowId));

        workflowExportOptions.setFormat(WorkflowExportOptionsC.FORMAT_JSON);
        FileData fileData = ixConn.ix().exportWorkflow(workflowExportOptions);
        String jsonData = new String(fileData.getData(), "UTF-8");
        return jsonData;
    }

    private static String ExportWorkflow(IXConnection ixConn, int workflowId) throws RemoteException, UnsupportedEncodingException {
        return GetWorkflowAsJsonText(ixConn, workflowId);
    }

    private static void ExportWorkflow(IXConnection ixConn, File exportPath, WFDiagram wf) {
        try {
            WFDiagram wfDiag = ixConn.ix().checkoutWorkflowTemplate(wf.getName(), "", new WFDiagramZ(WFDiagramC.mbId), LockC.NO);
            WorkflowExportOptions workflowExportOptions = new WorkflowExportOptions();
            workflowExportOptions.setFlowId(Integer.toString(wfDiag.getId()));

            workflowExportOptions.setFormat(WorkflowExportOptionsC.FORMAT_JSON);
            FileData fileData = ixConn.ix().exportWorkflow(workflowExportOptions);
            String jsonData = new String(fileData.getData(), "UTF-8");
            jsonData = JsonUtils.formatJsonString(jsonData);

            String dirName = exportPath + "\\Workflows";
            String fileName = wf.getName();
            FileUtils.SaveToFile(dirName, fileName, jsonData, "json");
            System.out.println("Save Workflow: '" + wf.getName() + "'");
        } catch (RemoteException ex) {
            System.out.println("RemoteException: " + ex.getMessage());

        } catch (UnsupportedEncodingException ex) {
            System.out.println("UnsupportedEncodingException: " + ex.getMessage());
            Platform.runLater(() -> {
                EloUnittestApp.showAlert("Achtung!", "UnsupportedEncodingException", "System.UnsupportedEncodingException message: " + ex.getMessage());
            });
        }
    }

    private static String ExportWorkflowTemplate(IXConnection ixConn, WFDiagram wf) throws RemoteException, UnsupportedEncodingException {
        int workflowTemplateId = GetWorkflowTemplateId(ixConn, wf.getName());
        return ExportWorkflow(ixConn, workflowTemplateId);
    }

    static SortedMap<WFDiagram, SortedMap<Integer, String>> LoadWorkflowLines(IXConnection ixConn, Pattern pattern) throws RemoteException, UnsupportedEncodingException {
        Comparator<WFDiagram> byName = Comparator.comparing(wf -> wf.getName());
        Comparator<WFDiagram> byId = Comparator.comparingInt(wf -> wf.getId());
        Comparator<WFDiagram> byWFDiagram = byName.thenComparing(byId);
        SortedMap<WFDiagram, SortedMap<Integer, String>> dicWorkflowLines = new TreeMap<>(byWFDiagram);
        WFDiagram[] wfTemplates = GetTemplates(ixConn);
        for (WFDiagram wf : wfTemplates) {
            SortedMap<Integer, String> wfLines = new TreeMap<>();
            String wfJsonText = ExportWorkflowTemplate(ixConn, wf);
            wfJsonText = JsonUtils.formatJsonString(wfJsonText);
            String[] lines = wfJsonText.split("\n");
            int linenr = 1;
            for (String line : lines) {
                // System.out.println("Gelesene WFZeile: " + line);
                if (pattern.toString().length() > 0) {
                    if (pattern.matcher(line).find()){
                        wfLines.put(linenr, line);
                    }
                }
                linenr++;
            }
            dicWorkflowLines.put(wf, wfLines);
        }
        return dicWorkflowLines;
    }

}
