package de.salt.sce.mapper.model;


import de.salt.sce.mapper.util.ScalaTool;

/**
 * Contract class for webservice
 *
 * @author WRH
 * @since 3.0.1
 */
public class TrackContract implements java.io.Serializable {

    private String statusData;
    private String mandt;
    private String refId;
    private String refType;
    private String stateId;
    private String stateTextExt;
    private String timestamp;
    private String edcid;
    private String titleMedi;
    private String name1;
    private String name2;
    private String name3;
    private String street;
    private String houseNo;
    private String pcode1;
    private String city;
    private String district;
    private String countryiso;
    private String countryisonum;
    private String phoneNo;
    private String faxNo;
    private String mail;

    public String getStatusData() {
        if (statusData == null || statusData.equals("")) return null;
        else return statusData;
    }

    public void setStatusData(String statusData) {
        this.statusData = statusData;
    }

    public void setMandt(String mandt) {
        this.mandt = mandt;
    }

    public String getRefId() {
        if (refId == null || refId.equals("")) return null;
        else return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getRefType() {
        if (refType == null || refType.equals("")) return null;
        else return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public String getStateId() {
        if (stateId == null || stateId.equals("")) return null;
        else return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateTextExt() {
        if (stateTextExt == null || stateTextExt.equals("")) return null;
        else return stateTextExt;
    }

    public void setStateTextExt(String stateTextExt) {
        this.stateTextExt = stateTextExt;
    }

    public String getTimestamp() {
        if (timestamp == null || timestamp.equals("")) return null;
        else return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEdcid() {
        if (edcid == null || edcid.equals("")) return null;
        else return edcid;
    }

    public void setEdcid(String edcid) {
        this.edcid = edcid;
    }

    public String getTitleMedi() {
        if (titleMedi == null || titleMedi.equals("")) return null;
        else return titleMedi;
    }

    public void setTitleMedi(String titleMedi) {
        this.titleMedi = titleMedi;
    }

    public String getName1() {
        if (name1 == null || name1.equals("")) return null;
        else return name1;
    }

    public void setName1(String name1) {
        // Max 40: Sonst geht mapping im SAP nicht
        /* Prüfen ob ein & vorkommt, da & oder Umlaute maskiert werden
         * Beispiel: das & Zeichen wird im XML so dargestellt: &amp; // Ein Umlaut Ä so: &aml;
         * Jetzt kann es passieren, dass beim Limitieren auf 40 Zeichen 1-4 Stellen nach dem & abgeschnitten werden
         * Dann kommt es im SAP beim Konvertieren in XML zu einem Fehler und die Daten können nicht verarbeitet werden
         */
        this.name1 = ScalaTool.truncate(name1, 40);
    }

    public String getName2() {
        if (name2 == null || name2.equals("")) return null;
        else return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        if (name3 == null || name3.equals("")) return null;
        else return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }

    public String getStreet() {
        if (street == null || street.equals("")) return null;
        else return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouseNo() {
        if (houseNo == null || houseNo.equals("")) return null;
        else return houseNo;
    }

    public void setHouseNo(String houseNo) {
        this.houseNo = houseNo;
    }

    public String getPcode1() {
        if (pcode1 == null || pcode1.equals("")) return null;
        else return pcode1;
    }

    public void setPcode1(String pcode1) {
        this.pcode1 = pcode1;
    }

    public String getCity() {
        if (city == null || city.equals("")) return null;
        else return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        if (district == null || district.equals("")) return null;
        else return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCountryiso() {
        if (countryiso == null || countryiso.equals("")) return null;
        else return countryiso;
    }

    public void setCountryiso(String countryiso) {
        this.countryiso = countryiso;
    }

    public String getCountryisonum() {
        if (countryisonum == null || countryisonum.equals("")) return null;
        else return countryisonum;
    }

    public void setCountryisonum(String countryisonum) {
        this.countryisonum = countryisonum;
    }

    public String getPhoneNo() {
        if (phoneNo == null || phoneNo.equals("")) return null;
        else return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getFaxNo() {
        if (faxNo == null || faxNo.equals("")) return null;
        else return faxNo;
    }

    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo;
    }

    public String getMail() {
        if (mail == null || mail.equals("")) return null;
        else return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

}
