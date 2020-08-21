package de.salt.sce.mapper.parser;

import de.salt.sce.mapper.exception.ParserFailedException;
import de.salt.sce.mapper.model.TrackContract;
import de.salt.sce.mapper.model.TrackMapper;
import de.salt.sce.mapper.model.csv.PaketCSV;
import de.salt.sce.mapper.model.edifact.Paket;
import de.salt.sce.mapper.model.edifact.Rff;
import de.salt.sce.mapper.model.edifact.Shipment;
import de.salt.sce.mapper.model.edifact.Transport;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.event.report.HtmlReportGenerator;
import org.milyn.payload.JavaResult;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * XML Parser <br />
 * XML to Java Object
 *
 * @author WRH
 * @since 3.0.1
 */
public class MessageParser {

    private static Smooks smooks;
    private boolean generateReport = false;
    private String reportPath = "/move/move/report.html";
    final Logger log = getLogger(this.getClass());

    /**
     * Gets java bean from XML byte
     *
     * @param data   Input data
     * @param config Smooks config
     * @return Javaresult
     * @throws {@link SmooksException)
     * @throws IOException
     * @throws SAXException
     */

    public JavaResult getBean(byte[] data, String config) throws SmooksException, IOException, SAXException {
        smooks = new Smooks(config);

        ExecutionContext executionContext = smooks.createExecutionContext();

        if (generateReport) {
            executionContext.setEventListener(new HtmlReportGenerator(reportPath));
        }

        JavaResult javaResult = new JavaResult();
        smooks.filterSource(executionContext, new StreamSource(new ByteArrayInputStream(data)), javaResult);

        return javaResult;
    }

    /**
     * Parse a single file
     *
     * @param edcid       EDCID
     * @param config      File config
     * @param messageType Message type
     * @param fileName    Name of file
     * @param fileContent File content
     * @return List of TrackContract objects
     */
    public List<TrackContract> parseFile(
            String edcid,
            String config,
            String messageType,
            String fileName,
            byte[] fileContent
    ) throws ParserFailedException {
        List<TrackContract> trackContracts = new ArrayList<>();
        JavaResult result;

        try {
            result = this.getBean(fileContent, config);

            switch (messageType) {
                case "edifact":
                    Transport transport = (Transport) result.getBean("transport-bean");
                    log.info("Transport size: " + transport.getShipments().size());
                    List<TrackContract> edifactTracks = mapStatus(transport, edcid);
                    trackContracts.addAll(edifactTracks);
                    break;
                case "csv":
                    @SuppressWarnings("unchecked")
                    List<PaketCSV> shipment = (List<PaketCSV>) result.getBean("shipment-bean");
                    List<TrackContract> csvTracks = mapStatusCSV(shipment, edcid);
                    trackContracts.addAll(csvTracks);
                    break;
                default:
                    log.error(edcid + ": unknown message type");
                    break;
            }
        } catch (Exception e) {
            String error = "File Parsing Exception:" + e.getMessage() + " - " + fileName;
            log.error(error);
            throw new ParserFailedException(error);
        }

        return trackContracts;
    }

    /**
     * Map status based on provider
     *
     * @param transport
     * @param dienstleister
     */
    private List<TrackContract> mapStatus(Transport transport, String dienstleister) {
        List<TrackContract> trackContracts = new ArrayList<TrackContract>();
        // Shipment list
        for (Shipment shipment : transport.getShipments()) {

            // Package list
            for (Paket paket : shipment.getPakets()) {

                // Loop if GIN segment is used
                List<String> packageNumbers = new ArrayList<String>();

                if (paket.getGins() != null) {
                    for (int i = 0; i < paket.getGins().size(); i++) {
                        if (paket.getGins().get(i).getId_1_1() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_1_1());
                        }
                        if (paket.getGins().get(i).getId_1_2() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_1_2());
                        }

                        if (paket.getGins().get(i).getId_2_1() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_2_1());
                        }
                        if (paket.getGins().get(i).getId_2_2() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_2_2());
                        }

                        if (paket.getGins().get(i).getId_3_1() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_3_1());
                        }
                        if (paket.getGins().get(i).getId_3_2() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_3_2());
                        }

                        if (paket.getGins().get(i).getId_4_1() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_4_1());
                        }
                        if (paket.getGins().get(i).getId_4_2() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_4_2());
                        }

                        if (paket.getGins().get(i).getId_5_1() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_5_1());
                        }
                        if (paket.getGins().get(i).getId_5_2() != null) {
                            packageNumbers.add(paket.getGins().get(i).getId_5_2());
                        }

                    }
                }

                // If one status has more package numbers
                if (packageNumbers.size() > 0) {
                    for (int i = 0; i < packageNumbers.size(); i++) {

                        // Inject new Reference Object
                        Rff newRff = new Rff();
                        newRff.setReference(packageNumbers.get(i));
                        List<Rff> newRffList = new ArrayList<Rff>();
                        newRffList.add(newRff);
                        paket.setRffs(newRffList);

                        // Map Dienstleister to Track
                        TrackMapper mapper = new TrackMapper();
                        TrackContract track = mapper.mapDienstleister2Track(paket, dienstleister);
                        trackContracts.add(track);
                    }
                } else {
                    // Map Dienstleister to Track
                    TrackMapper mapper = new TrackMapper();
                    TrackContract track;
                    track = mapper.mapDienstleister2Track(paket, dienstleister);
                    trackContracts.add(track);

                }
            }
        }
        return trackContracts;

    }

    /**
     * Map status for CSV
     *
     * @param shipment
     * @param dienstleister
     */
    private List<TrackContract> mapStatusCSV(List<PaketCSV> shipment, String dienstleister) throws ParserFailedException {
        List<TrackContract> trackContracts = new ArrayList<>();
        if(shipment.isEmpty()) {
            throw new ParserFailedException("File format is wrong.");
        }

        // Shipment list
        for (PaketCSV paket : shipment) {

            // Skip header
            // Preprocessor TOF
            if (paket.getSdgdatum().equals("SDGDATUM")) {
                continue;
            }

            // Map Dienstleister to Track
            TrackMapper mapper = new TrackMapper();
            TrackContract track;
            track = mapper.mapDienstleister2Track(paket, dienstleister);
            trackContracts.add(track);
        }
        return trackContracts;
    }
}
