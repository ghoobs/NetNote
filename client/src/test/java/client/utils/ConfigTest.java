package client.utils;

import commons.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

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
        Collection collection = new Collection("collection");
        config.setCollections(Collections.singletonList(Collections.singletonList(collection)));
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
        Config loadedConfig = config.loadConfig(temporaryFile);

        assertNotNull(loadedConfig);
        assertEquals(loadedConfig.getCollections().size(), 1);
    }
}
