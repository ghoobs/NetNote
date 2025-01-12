package client.utils;

import commons.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigTest {

    /**
     * creating a temporary file to use for testing, to avoid hard-coding file names and unneccessary permanent files
     */

    private Config config;
    private File temporaryFile;

    /**
     * short set up for the tests
     * the temporary file config.json is constructed
     * for some reason the collection needs 2 singletonLists to be compatible and work
     * @throws IOException
     */

    @BeforeEach
    void setUp() throws IOException {
        temporaryFile = Files.createTempFile("config", ".json").toFile();
        temporaryFile.deleteOnExit();
        config = new Config();
        config.addNewCollection("http://localhost:8080/", 0L);
        config.addNewCollection("http://localhost:8090/", 1L);
    }

    /**
     * test for the save config, checking if saving works without generating errors
     * @throws IOException if file not found
     */

    @Test
    void saveConfig() throws IOException {
        config.saveConfig(temporaryFile);
        assertTrue(temporaryFile.exists());
        assertTrue(temporaryFile.length() > 0);
    }

    /**
     * test for the load config, checking if loading works without generating errors
     * @throws IOException if file not found
     */

    @Test
    void loadConfig() throws IOException {
        config.saveConfig(temporaryFile);
        Config loadedConfig = Config.loadConfig(temporaryFile);

        assertNotNull(loadedConfig);
        assertEquals(loadedConfig, config);
    }

    @Test
    void writeAndLoadConfigStatic() throws IOException {
        config.saveConfig();
        assertEquals(config, Config.loadConfig());
    }

    @Test
    void commonMethods(){
        var allIDs = config.getAllCollectionIds();
        assertTrue(allIDs.contains(1L));
        assertTrue(allIDs.contains(0L));

        var allServers = config.getAllServers();
        assertTrue(allServers.contains("http://localhost:8080/"));
        assertTrue(allServers.contains("http://localhost:8090/"));

        assertEquals(config.getIdsFromServer("http://localhost:8080/"), List.of(0L));
    }

}
