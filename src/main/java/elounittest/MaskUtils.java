package elounittest;

import byps.RemoteException;
import de.elo.ix.client.*;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

public class MaskUtils {

    private static DocMask[] GetDocMasks(IXConnection ixConn) throws RemoteException {
        String arcPath = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions";
        EditInfo ed = ixConn.ix().checkoutSord(arcPath, EditInfoC.mbAll, LockC.NO);
        List<DocMask> dmList = new ArrayList<>();
        for (MaskName mn : ed.getMaskNames()) {
            boolean canRead = (mn.getAccess() & AccessC.LUR_READ) != 0;
            System.out.println("id=" + mn.getId() +
                    ", name=" + mn.getName() +
                    ", folderMask=" + mn.isFolderMask() +
                    ", documentMask=" + mn.isDocumentMask() +
                    ", searchMask=" + mn.isSearchMask() +
                    ", canRead=" + canRead);

            DocMask dm = ixConn.ix().checkoutDocMask(mn.getName(), DocMaskC.mbAll, LockC.NO);
            dmList.add(dm);
        }
        DocMask[] docMasks = new DocMask[dmList.size()];
        docMasks = dmList.toArray(docMasks);
        return docMasks;
    }

    private static void clearIds(AclItem[] aclItems) {
        for (AclItem aclItem: aclItems) {
            aclItem.setId(-1);
        }
    }

    static void FindDocMasks(IXConnection ixConn, File exportPath) {
        String arcPath = "ARCPATH[(E10E1000-E100-E100-E100-E10E10E10E00)]:/Business Solutions";
        try {
            EditInfo ed = ixConn.ix().checkoutSord(arcPath, EditInfoC.mbAll, LockC.NO);
            List<DocMask> dmList = new ArrayList<>();
            for (MaskName mn : ed.getMaskNames()) {
                boolean canRead = (mn.getAccess() & AccessC.LUR_READ) != 0;
                System.out.println("id=" + mn.getId() +
                        ", name=" + mn.getName() +
                        ", folderMask=" + mn.isFolderMask() +
                        ", documentMask=" + mn.isDocumentMask() +
                        ", searchMask=" + mn.isSearchMask() +
                        ", canRead=" + canRead);

                DocMask dm = ixConn.ix().checkoutDocMask(mn.getName(), DocMaskC.mbAll, LockC.NO);
                dmList.add(dm);
            }
            DocMask[] docMasks = new DocMask[dmList.size()];
            docMasks = dmList.toArray(docMasks);

            for (DocMask dm : docMasks) {
                ExportDocMask(ixConn, exportPath, dm);
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
    }


    private static String getUserName(IXConnection ixConn, int id) throws RemoteException {
        String[] ids = new String[]{id + ""};
        UserName[] userNames = ixConn.ix().getUserNames(ids, CheckoutUsersC.BY_IDS_RAW);
        String name = userNames[0].getName();
        return name;
    }

    private static void adjustAcl(IXConnection ixConn, AclItem[] aclItems) throws RemoteException {
        String aclName;
        String adminName = getUserName(ixConn, UserInfoC.ID_ADMINISTRATOR);
        String everyoneName = getUserName(ixConn, UserInfoC.ID_EVERYONE_GROUP);
        for (AclItem aclItem: aclItems) {
            aclName = aclItem.getName();
            if (aclName.equals(adminName)) {
                aclItem.setId(0);
                aclItem.setName("");
            } else if (aclName.equals(everyoneName)) {
                aclItem.setId(9999);
                aclItem.setName("");
            }
        }
    }

    private static void adjustMask(IXConnection ixConn, DocMask dm) throws RemoteException {
        dm.setId(-1);
        dm.setTStamp("2018.01.01.00.00.00");
        adjustAcl(ixConn, dm.getAclItems());

        for (DocMaskLine line: dm.getLines()) {
            line.setMaskId(-1);
            adjustAcl(ixConn, line.getAclItems());
        }
    }

    private static String ExportDocMask(IXConnection ixConn, DocMask dm) throws RemoteException {
        dm.setAcl("");
        dm.setDAcl("");
        clearIds(dm.getAclItems());
        clearIds(dm.getDocAclItems());
        for (DocMaskLine line: dm.getLines()) {
            line.setAcl("");
            clearIds(line.getAclItems());
        }
        String json = JsonUtils.getJsonString(dm);
        dm = JsonUtils.getDocMask(json);
        adjustMask(ixConn, dm);
        json = JsonUtils.formatJsonString(json);
        return json;
    }

    private static void ExportDocMask(IXConnection ixConn, File exportPath, DocMask dm) {
        try {
            dm.setAcl("");
            dm.setDAcl("");
            clearIds(dm.getAclItems());
            clearIds(dm.getDocAclItems());
            for (DocMaskLine line: dm.getLines()) {
                line.setAcl("");
                clearIds(line.getAclItems());
            }
            String json = JsonUtils.getJsonString(dm);
            dm = JsonUtils.getDocMask(json);
            adjustMask(ixConn, dm);
            json = JsonUtils.formatJsonString(json);

            String dirName = exportPath + "\\DocMasks";
            String fileName = dm.getName();
            FileUtils.SaveToFile(dirName, fileName, json, "json");
            System.out.println("Save DocMask: '" + dm.getName() + "'");

        } catch (RemoteException ex) {
            System.out.println("RemoteException: " + ex.getMessage());
        }
    }

    static SortedMap<DocMask, SortedMap<Integer, String>> LoadDocMaskLines(IXConnection ixConn, Pattern pattern) throws RemoteException {
        Comparator<DocMask> byName = Comparator.comparing(dm -> dm.getName());
        Comparator<DocMask> byId = Comparator.comparingInt(dm -> dm.getId());
        Comparator<DocMask> byDocMask = byName.thenComparing(byId);
        SortedMap<DocMask, SortedMap<Integer, String>> dicDocMaskLines = new TreeMap<>(byDocMask);
        DocMask[] docMasks = GetDocMasks(ixConn);
        for (DocMask dm : docMasks) {
            SortedMap<Integer, String> dmLines = new TreeMap<>();
            String dmJsonText = ExportDocMask(ixConn, dm);
            String[] lines = dmJsonText.split("\n");
            int linenr = 1;
            for (String line : lines) {
                // System.out.println("Gelesene WFZeile: " + line);
                if (pattern.toString().length() > 0) {
                    if (pattern.matcher(line).find()){
                        dmLines.put(linenr, line);
                    }
                }
                linenr++;
            }
            dicDocMaskLines.put(dm, dmLines);
        }
        return dicDocMaskLines;
    }
}
