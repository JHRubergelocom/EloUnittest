package elounittest;

import de.elo.ix.client.Sord;

public class SordDoc {
    private final Sord sord;
    private String ext;

    public SordDoc(Sord sord) {
        this.sord = sord;
        this.ext = "";
    }

    Integer getId() {
        return sord.getId();
    }

    String getGuid() {
        return sord.getGuid();
    }

    String getName() {
        return sord.getName();
    }

    String getExt() {
        return ext;
    }

    void setExt(String ext) {
        this.ext = ext;
    }
}
