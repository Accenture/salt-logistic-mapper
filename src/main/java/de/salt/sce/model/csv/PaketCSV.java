package de.salt.sce.model.csv;

import de.salt.sce.model.edifact.Paket;

import java.io.Serializable;

public class PaketCSV extends Paket implements Serializable {

    private String sdgdatum;
    private String langreferenz;
    private String tofsdgnr;
    private String empfaenger;
    private String lkz;
    private String plz;
    private String ort;
    private String dienst2;
    private String status;
    private String sdgzeit;

    public String getSdgzeit() {
        return sdgzeit;
    }

    public void setSdgzeit(String sdgzeit) {
        this.sdgzeit = sdgzeit;
    }

    public String getSdgdatum() {
        return sdgdatum;
    }

    public void setSdgdatum(String sdgdatum) {
        this.sdgdatum = sdgdatum;
    }

    public String getLangreferenz() {
        return langreferenz;
    }

    public void setLangreferenz(String langreferenz) {
        this.langreferenz = langreferenz;
    }

    public String getTofsdgnr() {
        return tofsdgnr;
    }

    public void setTofsdgnr(String tofsdgnr) {
        this.tofsdgnr = tofsdgnr;
    }

    public String getEmpfaenger() {
        return empfaenger;
    }

    public void setEmpfaenger(String empfaenger) {
        this.empfaenger = empfaenger;
    }

    public String getLkz() {
        return lkz;
    }

    public void setLkz(String lkz) {
        this.lkz = lkz;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public String getDienst2() {
        return dienst2;
    }

    public void setDienst2(String dienst2) {
        this.dienst2 = dienst2;
    }

    public String toString() {
        return "[" +
                sdgdatum + ", " + langreferenz + ", " + tofsdgnr + ", " + empfaenger
                + lkz + ", " + plz + ", " + ort + ", " + dienst2
                + "]";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
