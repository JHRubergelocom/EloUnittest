package elounittest;

import byps.RemoteException;
import com.example.elounittest.EloUnittestApp;
import de.elo.ix.client.IXConnFactory;
import de.elo.ix.client.IXConnection;

public class Connection {
    static IXConnection getIxConnection(Stack stack, Stacks stacks) throws Exception{
        IXConnection ixConn;
        IXConnFactory connFact;
        try {
            connFact = new IXConnFactory(stack.getIxUrl(), "IXConnection", "1.0");
        } catch (Exception ex) {
            EloUnittestApp.showAlert("Achtung!", "ELO Connection", "Falsche Verbindungsdaten zu ELO \n: " + ex.getMessage());
            System.out.println("IllegalStateException message: " +  ex.getMessage());
            throw new Exception("Connection");
        }
        try {
            ixConn = connFact.create(stacks.getUser(), stacks.getPwd(), null, null);
        } catch (RemoteException ex) {
            EloUnittestApp.showAlert("Achtung!", "ELO Connection", "Indexserver-Verbindung ungültig \n User: " + stacks.getUser() + "\n IxUrl: " + stack.getIxUrl());
            System.out.println("RemoteException message: " + ex.getMessage());
            throw new Exception("Connection");
        }
        return ixConn;
    }
}
