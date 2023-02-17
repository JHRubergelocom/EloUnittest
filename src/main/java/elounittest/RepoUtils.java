package elounittest;

import byps.RemoteException;
import com.example.elounittest.EloUnittestApp;
import de.elo.ix.client.*;
import javafx.application.Platform;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class RepoUtils {
    public static Sord[] FindChildren(IXConnection ixConn, String objId, boolean references, boolean recursive1) {
        System.out.println("FindChildren: objId " + objId + " ixConn " + ixConn);
        FindResult findResult = new FindResult();
        List<Sord> sordList = new ArrayList<>();
        try {
            ixConn.ix().checkoutSord(objId, SordC.mbAll, LockC.NO);

            FindInfo findInfo = new FindInfo();
            FindChildren findChildren = new FindChildren();
            FindByIndex findByIndex = new FindByIndex();
            Boolean includeReferences = references;
            SordZ sordZ = SordC.mbAll;
            Boolean recursive = recursive1;
            int level = -1;
            findChildren.setParentId(objId);
            findChildren.setMainParent(!includeReferences);
            findChildren.setEndLevel((recursive) ? level : 1);
            findInfo.setFindChildren(findChildren);
            findInfo.setFindByIndex(findByIndex);

            int idx = 0;
            findResult = ixConn.ix().findFirstSords(findInfo, 1000, sordZ);
            while (true) {
                Sord[] sordArray = findResult.getSords();
                sordList.addAll(Arrays.asList(sordArray));
                if (!findResult.isMoreResults()) {
                    break;
                }
                idx += sordArray.length;
                findResult = ixConn.ix().findNextSords(findResult.getSearchId(), idx, 1000, sordZ);
            }

        } catch (RemoteException ex) {
            ex.printStackTrace();
        } finally {
            if (findResult != null)
            {
                try {
                    ixConn.ix().findClose(findResult.getSearchId());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        }
        Sord[] children = new Sord[sordList.size()];
        children = sordList.toArray(children);
        return children;
    }

    public static void FindChildren(IXConnection ixConn, String arcPath, File exportPath, boolean exportReferences) {
        FindResult fr = new FindResult();
        try {
            EditInfo ed = ixConn.ix().checkoutSord(arcPath, EditInfoC.mbOnlyId, LockC.NO);

            int parentId = ed.getSord().getId();

            FindInfo fi = new FindInfo();
            fi.setFindChildren(new FindChildren());
            fi.getFindChildren().setParentId(Integer.toString(parentId));
            fi.getFindChildren().setEndLevel(1);
            SordZ sordZ = SordC.mbMin;

            int idx = 0;
            fr = ixConn.ix().findFirstSords(fi, 1000, sordZ);
            while (true) {
                for (Sord sord : fr.getSords()) {
                    boolean isFolder = sord.getType() < SordC.LBT_DOCUMENT;
                    boolean isDocument = sord.getType() >= SordC.LBT_DOCUMENT && sord.getType() <= SordC.LBT_DOCUMENT_MAX;
                    boolean isReference = sord.getParentId() != parentId;

                    boolean doExportScript = false;
                    // Keine Referenzen ausgeben
                    if (!exportReferences) {
                        if (!isReference) {
                            doExportScript = true;
                        }
                    }
                    // Referenzen mit ausgeben
                    else {
                        doExportScript = true;
                    }
                    if (doExportScript) {
                        // Wenn Ordner rekursiv aufrufen
                        if (isFolder) {
                            // Neuen Ordner in Windows anlegen, falls noch nicht vorhanden
                            File subFolderPath = new File(exportPath + "\\" + sord.getName());
                            if (!subFolderPath.exists()) {
                                try {
                                    subFolderPath.mkdirs();
                                } catch (Exception ex) {
                                    System.out.println("Exception mkdir(): " + ex.getMessage() + " " + subFolderPath);
                                }
                            }
                            FindChildren(ixConn, arcPath + "/" + sord.getName(), subFolderPath, exportReferences);
                        }
                        // Wenn Dokument Pfad und Name ausgeben
                        if (isDocument) {
                            File outFile = new File("");
                            try {
                                // Dokument aus Archiv downloaden und in Windows anlegen
                                ed = ixConn.ix().checkoutDoc(Integer.toString(sord.getId()), null, EditInfoC.mbDocument, LockC.NO);
                                DocVersion dv = ed.getDocument().getDocs()[0];
                                outFile = new File(exportPath + "\\" + sord.getName() + "." + dv.getExt());
                                if (outFile.exists()) {
                                    outFile.delete();
                                }
                                ixConn.download(dv.getUrl(), outFile);
                                System.out.println("Arcpath=" + arcPath + "/" + sord.getName() + "  Maskname=" + sord.getMaskName());
                            } catch (RemoteException ex) {
                                System.out.println("RemoteException: " + ex.getMessage() + " " + outFile);
                            } catch (ArrayIndexOutOfBoundsException ex) {
                                System.out.println("ArrayIndexOutOfBoundsException: " + ex.getMessage() + " " + outFile);
                            }
                        }
                    }
                }
                if (!fr.isMoreResults()) break;
                idx += fr.getSords().length;
                fr = ixConn.ix().findNextSords(fr.getSearchId(), idx, 1000, sordZ);
            }
        } catch (RemoteException ex) {
            System.out.println("RemoteException: " + ex.getMessage());
        } finally {
            if (fr != null) {
                try {
                    ixConn.ix().findClose(fr.getSearchId());
                } catch (RemoteException ex) {
                    System.out.println("RemoteException: " + ex.getMessage());
                }
            }
        }
    }

    static String DownloadDocumentToString (IXConnection ixConn, Sord s) {
        String docText = "";
        try {
            String objId = s.getId() + "";
            String line;
            BufferedReader in = null;
            String bom = "\uFEFF"; // ByteOrderMark (BOM);
            EditInfo editInfo = ixConn.ix().checkoutDoc(objId, null, EditInfoC.mbSordDoc, LockC.NO);
            if (editInfo.getDocument().getDocs().length > 0) {
                DocVersion dv = editInfo.getDocument().getDocs()[0];
                String url = dv.getUrl();
                InputStream inputStream = ixConn.download(url, 0, -1);
                try {
                    in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    while ((line = in.readLine()) != null) {
                        // System.out.println("Gelesene Zeile: " + line);
                        docText = docText.concat(line);
                    }
                } catch (FileNotFoundException ex) {
                    Platform.runLater(() -> {
                        EloUnittestApp.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
                    });
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                    });
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Platform.runLater(() -> {
                                EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                            });
                        }
                    }
                }
                docText = docText.replaceAll(bom, "");
                docText = docText.replaceAll("\b", "");
                docText = docText.replaceAll("\n", "");
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return docText;
    }

    static List<String> DownloadDocumentToList (IXConnection ixConn, Sord s) {
        List<String> docList = new ArrayList<>();
        try {
            String objId = s.getId() + "";
            String line;
            BufferedReader in = null;
            String bom = "\uFEFF"; // ByteOrderMark (BOM);
            EditInfo editInfo = ixConn.ix().checkoutDoc(objId, null, EditInfoC.mbSordDoc, LockC.NO);
            if (editInfo.getDocument().getDocs().length > 0) {
                DocVersion dv = editInfo.getDocument().getDocs()[0];
                String url = dv.getUrl();
                InputStream inputStream = ixConn.download(url, 0, -1);
                try {
                    in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    while ((line = in.readLine()) != null) {
                        // System.out.println("Gelesene Zeile: " + line);
                        line = line.replaceAll(bom, "");
                        line = line.replaceAll("\b", "");
                        line = line.replaceAll("\n", "");
                        docList.add(line);
                    }
                } catch (FileNotFoundException ex) {
                    Platform.runLater(() -> {
                        EloUnittestApp.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
                    });
                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                    });
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Platform.runLater(() -> {
                                EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                            });
                        }
                    }
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return docList;
    }

    static String[] LoadTextDocs(IXConnection ixConn, String parentId) throws RemoteException {
        Sord[] sordRFInfo = FindChildren(ixConn, parentId, true, true);
        List<String> docTexts = new ArrayList<>();
        for (Sord s : sordRFInfo) {
            String docText = DownloadDocumentToString(ixConn, s);
            docTexts.add(docText);
        }
        String[] docArray = new String[docTexts.size()];
        docArray = docTexts.toArray(docArray);
        return docArray;
    }

    static SortedMap<String, List<String>> LoadTextDocsToSortedMap(IXConnection ixConn, String parentId) throws RemoteException {
        Sord[] sordRFInfo = FindChildren(ixConn, parentId, true, true);
        SortedMap<String, List<String>> docTexts = new TreeMap<>();
        for (Sord s : sordRFInfo) {
            List<String> docTextList = DownloadDocumentToList(ixConn, s);
            docTexts.put(s.getName(), docTextList);
        }
        return docTexts;
    }

    private static SortedMap<Integer, String> DownloadDocumentToLines(IXConnection ixConn, SordDoc sDoc, Pattern p) {
        SortedMap<Integer, String> docLines = new TreeMap<>();
        try {
            String objId = sDoc.getId() + "";
            String line;
            BufferedReader in = null;
            String bom = "\uFEFF"; // ByteOrderMark (BOM);
            EditInfo editInfo = ixConn.ix().checkoutDoc(objId, null, EditInfoC.mbSordDoc, LockC.NO);
            if (editInfo.getDocument().getDocs().length > 0) {
                DocVersion dv = editInfo.getDocument().getDocs()[0];
                String url = dv.getUrl();
                sDoc.setExt(dv.getExt());
                InputStream inputStream = ixConn.download(url, 0, -1);
                try {
                    in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    int linenr = 1;
                    while ((line = in.readLine()) != null) {
                        // System.out.println("Gelesene Zeile: " + line);
                        line = line.replaceAll(bom, "");
                        line = line.replaceAll("\b", "");
                        line = line.replaceAll("\n", "");
                        if (p.matcher(line).find()){
                            docLines.put(linenr, line);
                        }
                        linenr++;
                    }
                } catch (FileNotFoundException ex) {
                    Platform.runLater(() -> {
                        EloUnittestApp.showAlert("Achtung!", "FileNotFoundException", "System.FileNotFoundException message: " + ex.getMessage());
                    });

                } catch (IOException ex) {
                    Platform.runLater(() -> {
                        EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                    });
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException ex) {
                            Platform.runLater(() -> {
                                EloUnittestApp.showAlert("Achtung!", "IOException", "System.IOException message: " + ex.getMessage());
                            });
                        }
                    }
                }
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        return docLines;
    }

    static SortedMap<SordDoc, SortedMap<Integer, String>> LoadSordDocLines(IXConnection ixConn, Map<String, EloPackage> eloPackages, Pattern pattern) {
        Comparator<SordDoc> byName = Comparator.comparing(sd -> sd.getName());
        Comparator<SordDoc> byId = Comparator.comparingInt(sd -> sd.getId());
        Comparator<SordDoc> bySordDoc = byName.thenComparing(byId);
        SortedMap<SordDoc, SortedMap<Integer, String>> dicSordDocLines = new TreeMap<>(bySordDoc);

        if (pattern.toString().length() > 0) {
            if (eloPackages.isEmpty()) {
                String parentId;
                parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions";
                Sord[] sords = FindChildren(ixConn, parentId, true, true);
                for (Sord s : sords) {
                    SordDoc sDoc = new SordDoc(s);
                    SortedMap<Integer, String> docLines = DownloadDocumentToLines(ixConn, sDoc, pattern);
                    dicSordDocLines.put(sDoc, docLines);
                }
            } else {
                eloPackages.forEach((n, p) -> {
                    SortedMap<SordDoc, SortedMap<Integer, String>> dicEloPackageSordDocLines = new TreeMap<>(bySordDoc);
                    Map<String, String> folders = p.getFolders();
                    folders.forEach((k, v) -> {
                        final String parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/" + v;
                        Sord[] sords = FindChildren(ixConn, parentId, true, true);
                        for (Sord s : sords) {
                            SordDoc sDoc = new SordDoc(s);
                            SortedMap<Integer, String> docLines = DownloadDocumentToLines(ixConn, sDoc, pattern);
                            dicEloPackageSordDocLines.put(sDoc, docLines);
                            dicSordDocLines.putAll(dicEloPackageSordDocLines);
                        }
                    });
                });
            }

            // Unittests durchsuchen
            String parentId;
            parentId = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions/_global/Unit Tests";
            Sord[] sords = FindChildren(ixConn, parentId, true, true);
            for (Sord s : sords) {
                SordDoc sDoc = new SordDoc(s);
                SortedMap<Integer, String> docLines = DownloadDocumentToLines(ixConn, sDoc, pattern);
                dicSordDocLines.put(sDoc, docLines);
            }

        }
        return dicSordDocLines;
    }
}
