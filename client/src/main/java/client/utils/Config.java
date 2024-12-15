package client.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class Config {

    @JsonProperty
    private List<Collection> collections;

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    /**
     * loading config from json file
     *
     * @param file config is loaded from here
     * @return instance of config with data from json
     * @throws IOException if file isnt read properly
     */

    public static Config loadConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(file, Config.class);
    }

    public void saveConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(file, this);
        } catch (IOException e) {
            throw new IOException("Could not save config file", e);
        }
    }
}
