package de.salt.sce.mapper.model;


import de.salt.sce.mapper.model.csv.PaketCSV;
import de.salt.sce.mapper.model.edifact.Paket;
import de.salt.sce.mapper.util.Modulo10Plus1;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrackMapper {

    private static final Logger log = LoggerFactory.getLogger(TrackMapper.class);

    private String refId = "";
    private String refType = "";
    private String stateId = "";
    private String stateText = "";
    private String timestamp = "";
    private String edcid = "";
    private String name1 = "";
    private String street = "";
    private String pcode1 = "";
    private String city = "";
    private String district = "";
    private String country = "";

    /**
     * Maps Dienstleister XML to TrackDto
     *
     * @param paket             Paket Dto
     * @param dienstleisterCode Code of Dienstleister
     * @return TrackDto
     * @author wrh
     */
    public TrackContract mapDienstleister2Track(final Paket paket, final String dienstleisterCode) {

        // DEV: Debug
        /*
         * System.out.println("*******************");
         * System.out.println("Dienstleister: " + dienstleisterCode);
         * System.out.println("Size :" + paket.getDtms().size());
         * System.out.println(paket.getDtms().get(0).getDateTimePeriod());
         * System.out.println("*******************");
         * //
         */

        switch (dienstleisterCode) {
            case "ups":
                /*
                 * Ref id
                 */
                refId = paket.getRffs().get(0).getReference();

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                stateId = paket.getSts().getEvent();

                /*
                 * Time stamp
                 */
                if (paket.getDtms().get(0).getDateTimePeriod() != null) {
                    timestamp = checkTimestamp(paket.getDtms().get(0).getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Street, pcode1, city, district, country
                 * If NAD#3 - data for street available
                 */
                if (paket.getNads() != null && paket.getNads().size() > 2) {
                    if (paket.getNads().get(2).getStreet1() != null) {
                        street = street + paket.getNads().get(2).getStreet1();
                    }
                    if (paket.getNads().get(2).getStreet2() != null) {
                        street = street + "; " + paket.getNads().get(2).getStreet2();
                    }
                    if (paket.getNads().get(2).getPcode1() != null) {
                        pcode1 = paket.getNads().get(2).getPcode1();
                    }
                    if (paket.getNads().get(2).getCity() != null) {
                        city = paket.getNads().get(2).getCity();
                    }
                    if (paket.getNads().get(2).getDistrict() != null) {
                        district = paket.getNads().get(2).getDistrict();
                    }
                    if (paket.getNads().get(2).getCountry() != null) {
                        country = paket.getNads().get(2).getCountry();
                    }
                }
                break;
            case "dsv":
                /*
                 * Ref id
                 */
                refId = paket.getCni().getDocumentMessageNumber();

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                stateId = paket.getSts().getEvent()
                        + "+" + checkNullInt(paket.getSts().getReason());

                /*
                 * State text
                 */
                if (paket.getFtx() != null) {
                    stateText = paket.getFtx().getFreeText();
                }

                /*
                 * Time stamp
                 */
                if (paket.getDtms().get(0).getDateTimePeriod() != null) {
                    timestamp = checkTimestamp(paket.getDtms().get(0).getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paket.getNads() != null && paket.getNads().size() > 0) {
                    name1 = checkNullString(paket.getNads().get(0).getName1());
                }
                break;
            case "hep":
                /*
                 * Ref id
                 */
                refId = paket.getCni().getDocumentMessageNumber();

                /*
                 * Ref type
                 */
                refType = "DELI";

                /*
                 * State id
                 */
                stateId = paket.getSts().getEvent();

                /*
                 * State text
                 */
                stateText = paket.getSts().getReason();

                /*
                 * Time stamp
                 */
                if (paket.getDtms() != null && paket.getDtms().size() > 0) {
                    timestamp = checkTimestamp(paket.getDtms().get(0).getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paket.getNads() != null && paket.getNads().size() > 0) {
                    name1 = checkNullString(paket.getNads().get(0).getName1());
                }

                break;
            case "tof": {
                // Downcast
                final PaketCSV paketCSV = (PaketCSV) paket;

                /*
                 * Ref id
                 */
                refId = paketCSV.getLangreferenz().trim();

                /*
                 * Ref type
                 */
                refType = "DELI";

                /*
                 * State text
                 */
                stateText = paketCSV.getDienst2().trim();

                /*
                 * State id
                 */
                if (stateText.toLowerCase().contains("retour")) {
                    stateId = "R"; // for Retoure
                } else {
                    stateId = "A"; // for Abrechnung
                }

                /*
                 * Time stamp
                 */
                if (paketCSV.getSdgdatum() != null && paketCSV.getSdgdatum().length() > 0) {
                    timestamp = checkTimestamp(paketCSV.getSdgdatum().trim());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paketCSV.getEmpfaenger() != null && paketCSV.getEmpfaenger().length() > 0) {
                    name1 = checkNullString(paketCSV.getEmpfaenger().trim());
                    name1 = removeIllegalChar(name1);
                }

                /*
                 * Country
                 */
                if (paketCSV.getLkz() != null && paketCSV.getLkz().length() > 0) {
                    country = paketCSV.getLkz().trim();
                }

                /*
                 * PLZ
                 */
                if (paketCSV.getPlz() != null && paketCSV.getPlz().length() > 0) {
                    pcode1 = paketCSV.getPlz().trim();
                }

                /*
                 * City
                 */
                if (paketCSV.getOrt() != null && paketCSV.getOrt().length() > 0) {
                    city = paketCSV.getOrt().trim();
                }

                break;
            }
            case "kun":
                /*
                 * Ref id
                 */
                // Check if the RFF segments exist
                if (paket.getRffs() != null) {
                    // Loop all RFF segments
                    for (int i = 0; i < paket.getRffs().size(); i++) {
                        // Only RFF+CR Segment
                        if (paket.getRffs().get(i).getQualifier().equals("CR")) {
                            refId = paket.getRffs().get(i).getReference();
                        }
                    }
                }

                /*
                 * Ref type
                 */
                refType = "DELI";

                /*
                 * State id
                 */
                // Check if the STS segment exists
                if (paket.getSts() != null) {
                    stateId = paket.getSts().getEvent();
                }

                /*
                 * State text
                 */
                switch (stateId) {
                    case "MP2":
                        stateText = "Messpunkt NV-Entladung";
                        break;
                    case "MP4":
                        stateText = "Messpunkt FV-Verladung";
                        break;
                    case "U01":
                        stateText = "Erfasst";
                        break;
                    case "U02":
                        stateText = "DFÜ eingegangen";
                        break;
                    case "300":
                        stateText = "SDG. AUF DEM WEG ZUM EMPFANGSSPEDITEUR";
                        break;
                    case "500":
                        stateText = "SENDUNG IN DER ZUSTELLUNG";
                        break;
                    case "501":
                        stateText = "Ankunft am Zustellort";
                        break;
                    case "600":
                        stateText = "SENDUNG ZUGESTELLT - REINE QUITTUNG";
                        break;
                    case "610":
                        stateText = "Sendung zugestellt, aber....";
                        break;
                    case "611":
                        stateText = "Sendung zugestellt, aber Ware beschädigt";
                        break;
                    case "612":
                        stateText = "Sendung zugestellt, aber Teilverlust";
                        break;
                    case "620":
                        stateText = "SDG nicht zugestellt, zu lange Wartezeit beim Empfänger";
                        break;
                    case "630":
                        stateText = "Empfänger nicht angetroffen-Warenannahme geschlossen";
                        break;
                    case "631":
                        stateText = "Empfänger nicht angetroffen-Warenannahme geschlossen-Streik";
                        break;
                    case "632":
                        stateText = "Empfänger nicht angetroffen, Karte hinterlegt";
                        break;
                    case "640":
                        stateText = "Annahme verweigert";
                        break;
                    case "650":
                        stateText = "Zustelltour nicht geschafft";
                        break;
                    case "680":
                        stateText = "Erledigt durch - Retoure";
                        break;
                    case "990":
                        stateText = "Sendung storniert";
                        break;
                    case "992":
                        stateText = "erledigt durch - SDG storniert - Komplettverlust";
                        break;
                    default:
                        stateText = stateId;
                        break;
                }

                /*
                 * Time stamp
                 */
                if (paket.getDtms().get(3).getDateTimePeriod() != null) {
                    timestamp = checkTimestamp(paket.getDtms().get(3).getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paket.getNads() != null && paket.getNads().size() > 0) {
                    name1 = checkNullString(paket.getNads().get(0).getName2());
                }
                break;
            case "dpd": {

                // Downcast
                final PaketCSV paketCSV = (PaketCSV) paket;

                /*
                 * Ref id
                 */
                refId = paketCSV.getLangreferenz().trim();

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                stateId = paketCSV.getStatus().trim();

                /*
                 * State text
                 */
                switch (stateId) {
                    case "01":
                        stateText = "Konsilidierung";
                        break;
                    case "02":
                        stateText = "Eingang";
                        break;
                    case "03":
                        stateText = "Ausrollung";
                        break;
                    case "04":
                        stateText = "Ausrollretoure";
                        break;
                    case "05":
                        stateText = "Einrollung";
                        break;
                    case "06":
                        stateText = "Systemretoure";
                        break;
                    case "08":
                        stateText = "Lager";
                        break;
                    case "09":
                        stateText = "Eingang Differenz";
                        break;
                    case "10":
                        stateText = "HUB Durchlauf";
                        break;
                    case "12":
                        stateText = "In Verzollung";
                        break;
                    case "13":
                        stateText = "Zustellung";
                        break;
                    case "14":
                        stateText = "Zustellversuch nicht erfolgreich";
                        break;
                    case "15":
                        stateText = "Abholung";
                        break;
                    case "17":
                        stateText = "Export Import abgefertigt";
                        break;
                    case "18":
                        stateText = "Zusatzinformation";
                        break;
                    case "20":
                        stateText = "Beladung";
                        break;
                    default:
                        stateText = stateId;
                        break;
                }

                /*
                 * Time stamp
                 */
                if (paketCSV.getSdgdatum() != null && paketCSV.getSdgdatum().length() > 0) {
                    timestamp = checkTimestamp(paketCSV.getSdgdatum().trim());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paketCSV.getEmpfaenger() != null && paketCSV.getEmpfaenger().length() > 0) {
                    name1 = checkNullString(paketCSV.getEmpfaenger().trim());
                    name1 = removeIllegalChar(name1);
                }

                /*
                 * City
                 */
                if (paketCSV.getOrt() != null && paketCSV.getOrt().length() > 0) {
                    city = paketCSV.getOrt().trim();
                }

                break;
            }
            case "gls2": {
                // Downcast
                final PaketCSV paketCSV = (PaketCSV) paket;

                /*
                 * Ref id
                 */
                refId = paketCSV.getLangreferenz().trim();
                // Modulo + 1 is needed for GLS

                refId = refId + Modulo10Plus1.calculate(refId);



                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                stateId = paketCSV.getStatus().trim();

                /*
                 * Time stamp
                 */
                if (paketCSV.getSdgdatum() != null && paketCSV.getSdgdatum().length() > 0) {
                    timestamp = paketCSV.getSdgdatum().trim() + paketCSV.getSdgzeit().trim();
                    timestamp = checkTimestamp(timestamp);
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paketCSV.getEmpfaenger() != null && paketCSV.getEmpfaenger().length() > 0) {
                    name1 = checkNullString(paketCSV.getEmpfaenger().trim());
                    // name1 = removeIllegalChar(name1); " Methode wird bei GLS2 nicht benötigt, Dienstleister schickt Daten schon aufbereitet
                }

                /*
                 * City
                 */
                if (paketCSV.getOrt() != null && paketCSV.getOrt().length() > 0) {
                    city = paketCSV.getOrt().trim();
                }

                break;
            }
            case "dhl": {
                // Downcast
                final PaketCSV paketCSV = (PaketCSV) paket;

                /*
                 * Ref id
                 */
                refId = paketCSV.getLangreferenz().trim();

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                stateId = paketCSV.getStatus().trim();

                /*
                 * Time stamp
                 */
                if (paketCSV.getSdgdatum() != null && paketCSV.getSdgdatum().length() > 0) {
                    timestamp = checkTimestamp(paketCSV.getSdgdatum().trim());
                    timestamp = timestamp.replace('-', ' ');
                    timestamp = timestamp.replace(':', ' ');
                    timestamp = timestamp.replaceAll(" ", "");
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paketCSV.getEmpfaenger() != null && paketCSV.getEmpfaenger().length() > 0) {
                    name1 = checkNullString(paketCSV.getEmpfaenger().trim());
                    name1 = removeIllegalChar(name1);
                }

                /*
                 * City
                 */
                if (paketCSV.getOrt() != null && paketCSV.getOrt().length() > 0) {
                    city = paketCSV.getOrt().trim();
                }

                break;
            }
            case "hel":
                /*
                 * Ref id
                 */
                // Check if the RFF segments exist
                if (paket.getRffs() != null) {
                    refId = paket.getRffs().get(0).getReference();
                }

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                stateId = paket.getSts().getEvent();

                /*
                 * State text
                 */
                if (paket.getFtx() != null) {
                    stateText = paket.getFtx().getFreeText();
                }

                /*
                 * Time stamp
                 */
                if (paket.getDtms().get(0).getDateTimePeriod() != null) {
                    timestamp = checkTimestamp(paket.getDtms().get(0)
                            .getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paket.getNad() != null) {
                    name1 = checkNullString(paket.getNad().getName1() + " "
                            + paket.getNad().getName2());
                }
                break;
            case "emons":
                /*
                 * Ref id
                 */
                if (paket.getCni() != null) {
                    refId = paket.getCni().getDocumentMessageNumber();
                }
                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                if (paket.getSts() != null) {
                    stateId = paket.getSts().getEvent();
                }
                /*
                 * State text
                 */
                if (paket.getSts() != null) {
                    stateText = paket.getSts().getReason();
                }
                /*
                 * Time stamp
                 */
                if (paket.getDtms() != null && paket.getDtms().size() > 0) {
                    timestamp = checkTimestamp(paket.getDtms().get(0).getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paket.getNads() != null && paket.getNads().size() > 0) {
                    name1 = checkNullString(paket.getNads().get(0).getName1());
                }
                break;
            case "amm":
                /*
                 * Ref id
                 */
                if (paket.getCni() != null) {
                    refId = paket.getCni().getDocumentMessageNumber();
                }
                /*
                 * Ref type
                 */
                refType = "XSISHP";

                /*
                 * State id
                 */
                if (paket.getSts() != null) {
                    stateId = paket.getSts().getEvent();
                }
                /*
                 * State text
                 */
                if (paket.getSts() != null) {
                    stateText = paket.getSts().getReason();
                }
                /*
                 * Time stamp
                 */
                if (paket.getDtms() != null && paket.getDtms().size() > 0) {
                    timestamp = checkTimestamp(paket.getDtms().get(0).getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paket.getNads() != null && paket.getNads().size() > 0) {
                    name1 = checkNullString(paket.getNads().get(0).getName1());
                }
                break;
            case "dachser":
                /*
                 * Ref id
                 */
                // Check if the RFF segments exist
                if (paket.getRffs() != null) {
                    // Loop all RFF segments
                    for (int i = 0; i < paket.getRffs().size(); i++) {
                        // Only RFF+CR Segment
                        if (paket.getRffs().get(i).getQualifier().equals("CR")) {
                            refId = paket.getRffs().get(i).getReference();
                        }
                    }
                }

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                if (paket.getSts() != null) {
                    stateId = paket.getSts().getEvent();
                }

                /*
                 * State text
                 */
                if (paket.getSts() != null) {
                    stateText = paket.getSts().getReason();
                }

                /*
                 * Time stamp
                 */
                if (paket.getDtms().get(0).getDateTimePeriod() != null) {
                    timestamp = checkTimestamp(paket.getDtms().get(0)
                            .getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paket.getNads() != null) {
                    // Loop all NAD segments
                    for (int i = 0; i < paket.getNads().size(); i++) {
                        // Only NAD+AP Segment
                        if (paket.getNads().get(i).getQualifier().equals("AP") || paket.getNads().get(i).getQualifier().equals("MI")) {
                            name1 = checkNullString(paket.getNads().get(i).getName1());
                        }
                    }
                }
                break;

            case "geis":
                /*
                 * Ref id
                 */
                if (paket.getCni() != null) {
                    refId = paket.getCni().getDocumentMessageNumber();
                }

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                stateId = paket.getSts().getEvent();

                /*
                 * State text
                 */
                if (paket.getFtx() != null) {
                    stateText = paket.getFtx().getFreeText();
                }

                /*
                 * Time stamp
                 */
                if (paket.getDtm().getDateTimePeriod() != null) {
                    timestamp = checkTimestamp(paket.getDtm().getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;
                break;

            case "dhlf":
                /*
                 * Ref id
                 */
                // Check if the RFF segments exist
                if (paket.getRffs() != null) {
                    // Loop all RFF segments
                    for (int i = 0; i < paket.getRffs().size(); i++) {
                        // Only RFF+SN Segment
                        if (paket.getRffs().get(i).getQualifier().equals("CU")) {
                            refId = paket.getRffs().get(i).getReference();
                        }
                    }
                }

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                if (paket.getSts() != null) {
                    stateId = paket.getSts().getEvent();
                }

                /*
                 * State text
                 */
                if (paket.getSts() != null) {
                    stateText = paket.getSts().getReason();
                }

                /*
                 * Time stamp
                 */
                if (paket.getDtms().get(0).getDateTimePeriod() != null) {
                    timestamp = checkTimestamp(paket.getDtms().get(0)
                            .getDateTimePeriod());
                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paket.getNads() != null) {
                    // Loop all NAD segments
                    for (int i = 0; i < paket.getNads().size(); i++) {
                        // Only NAD+AP Segment
                        if (paket.getNads().get(i).getQualifier().equals("AP") || paket.getNads().get(i).getQualifier().equals("MI")) {
                            name1 = checkNullString(paket.getNads().get(i).getName1());
                        }
                    }
                }
                break;


            case "sander":
                // Downcast
                final PaketCSV paketCSV = (PaketCSV) paket;

                /*
                 * Ref id
                 */
                refId = paketCSV.getLangreferenz().trim();

                /*
                 * Ref type
                 */
                refType = "XSITRA";

                /*
                 * State id
                 */
                stateId = paketCSV.getStatus().trim();

                /*
                 * Time stamp
                 */
                if (paketCSV.getSdgdatum() != null && paketCSV.getSdgdatum().length() > 0) {
                    timestamp = checkTimestamp(paketCSV.getSdgdatum().trim());
                    timestamp = timestamp.replace('.', ' ');
                    timestamp = timestamp.replace(':', ' ');
                    timestamp = timestamp.replaceAll(" ", "");

                    // The SANDER timestamp is in the wrong format, so we have to format it like yyyymmddhhmmss
                    String day = timestamp.substring(0, 2);
                    String month = timestamp.substring(2, 4);
                    String year = timestamp.substring(4, 8);
                    String time = timestamp.substring(8, 14);
                    timestamp = year + month + day + time;

                }

                /*
                 * EDCID
                 */
                edcid = dienstleisterCode;

                /*
                 * Name
                 */
                if (paketCSV.getEmpfaenger() != null && paketCSV.getEmpfaenger().length() > 0) {
                    name1 = checkNullString(paketCSV.getEmpfaenger().trim());
                    name1 = removeIllegalChar(name1);
                }
                break;

            default:
                log.error("******** Need Dienstleister code ************");
                break;
        }

        final TrackContract track = new TrackContract();

        track.setStatusData("");
        track.setMandt("");

        //refId = Tool.checkTrackModelLengthString(refId, "refId");
        track.setRefId(refId);

        //refType = DevHelper.checkTrackModelLengthString(refType, "refType");
        track.setRefType(refType);

        //stateId = DevHelper.checkTrackModelLengthString(stateId, "stateId");
        track.setStateId(stateId);

        stateText = removeIllegalChar(stateText);
        //stateText = DevHelper.checkTrackModelLengthString(stateText, "stateTextExt");
        track.setStateTextExt(stateText);

        //timestamp = DevHelper.checkTrackModelLengthString(timestamp, "timestamp");
        track.setTimestamp(timestamp);

        track.setEdcid(edcid);

        track.setTitleMedi("");

        //name1 = DevHelper.checkTrackModelLengthString(name1, "name1");
        track.setName1(name1);

        track.setName2("");

        track.setName3("");

        //street = DevHelper.checkTrackModelLengthString(street, "street");
        track.setStreet(street);

        track.setHouseNo("");

        //pcode1 = DevHelper.checkTrackModelLengthString(pcode1, "pcode1");
        track.setPcode1(pcode1);

        //city = DevHelper.checkTrackModelLengthString(city, "city");
        track.setCity(city);

        //district = DevHelper.checkTrackModelLengthString(district, "district");
        track.setDistrict(district);

        //country = DevHelper.checkTrackModelLengthString(country, "countryiso");
        track.setCountryiso(country);

        track.setCountryisonum("");

        track.setPhoneNo("");

        track.setFaxNo("");

        track.setMail("");

        return track;
    }

    private String checkNullInt(String string) {
        if (string == null) {
            string = "000";
        }
        return string;
    }

    private String checkNullString(String string) {
        if (string == null) {
            string = "";
        }
        string = removeIllegalChar(string);
        return string;
    }

    private String checkTimestamp(String timestamp) {
        if (timestamp.length() < 14) {
            StringBuilder timestampBuilder = new StringBuilder(timestamp);
            for (int j = timestampBuilder.length(); j < 14; j++) {
                timestampBuilder.append("0");
            }
            timestamp = timestampBuilder.toString();
        }
        return timestamp;
    }

    private String removeIllegalChar(final String string) {
        return StringEscapeUtils.escapeXml10(string);
    }

}
