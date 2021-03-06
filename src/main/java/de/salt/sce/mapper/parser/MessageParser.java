package de.salt.sce.mapper.parser;

import de.salt.sce.mapper.exception.ParserFailedException;
import de.salt.sce.model.csv.PaketCSV;
import de.salt.sce.model.edifact.Transport;
import org.milyn.Smooks;
import org.milyn.SmooksException;
import org.milyn.container.ExecutionContext;
import org.milyn.payload.JavaResult;
import org.slf4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static de.salt.sce.mapper.util.ObjectSerializer.serialize;
import static java.nio.file.Paths.*;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * XML Parser <br />
 * XML to Java Object which serialized and endoded to String.
 */
public class MessageParser {

    private static final Logger log = getLogger(MessageParser.class);

    /**
     * Gets java bean from byte
     *
     * @param data   Input data
     * @param config Smooks config
     * @return {@link JavaResult]
     * @throws {@link       SmooksException)
     * @throws IOException
     * @throws SAXException
     */
    public JavaResult getBean(byte[] data, String config) throws SmooksException, IOException, SAXException {
        JavaResult javaResult = new JavaResult();

        Smooks smooks = new Smooks(buildRelativePathToConfig(config));
        ExecutionContext executionContext = smooks.createExecutionContext();
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
     * @return encoded serialized object of smooks
     */
    public Optional<String> parseFile(
            String edcid,
            String config,
            String messageType,
            String fileName,
            byte[] fileContent
    ) throws ParserFailedException {
        try {
            JavaResult result = this.getBean(fileContent, config);

            switch (messageType) {
                case "edifact":
                    Transport transport = (Transport) result.getBean("transport-bean");
                    log.info("Transport size: " + transport.getShipments().size());
                    return Optional.of(encodeBase64String(serialize(transport)));

                case "csv":
                    @SuppressWarnings("unchecked")
                    List<PaketCSV> shipment = (List<PaketCSV>) result.getBean("shipment-bean");
                    return Optional.of(encodeBase64String(serialize(shipment)));
                default:
                    log.error(edcid + ": unknown message type");
                    return Optional.empty();
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = "File Parsing Exception:" + e.getMessage() + " - " + fileName;
            log.error(error);
            throw new ParserFailedException(error);
        }
    }

    private String buildRelativePathToConfig(String configPath) throws IOException {

        // if the SMOOKS_CONFIG_HOME is not an absolute path. And we need it pro local unit/integration tests
        if(configPath.startsWith("smooks") || configPath.startsWith("classpath")) {
            return configPath;
        }

        String appHomePath = new File(".").getCanonicalPath();

        return get(appHomePath).relativize(get(configPath))
                .toString()
                .replace(" ", "%20")
                .replace("\\", "/");
    }

}
