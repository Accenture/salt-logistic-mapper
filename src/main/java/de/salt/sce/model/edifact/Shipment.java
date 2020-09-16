package de.salt.sce.model.edifact;

import java.io.Serializable;
import java.util.List;

public class Shipment implements Serializable {

    private Dtm dtm;
    private List<Paket> pakets;

    public Dtm getDtm() {
        return dtm;
    }

    public void setDtm(Dtm dtm) {
        this.dtm = dtm;
    }

    public List<Paket> getPakets() {
        return pakets;
    }

    public void setPakets(List<Paket> pakets) {
        this.pakets = pakets;
    }

}
