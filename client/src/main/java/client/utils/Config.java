package client.utils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class Config {

    /**
     * The constant storageLocation. Where the config will be storred
     */
    public static final String storageLocation = Paths.get(System.getProperty("user.dir")
            , "configs"
            , "config.json").toString();

    @JsonProperty
    Map<String, List<Long>> serversToIds;


    /**
     * Instantiates a new Config.
     */
    public Config() {
        serversToIds = new HashMap<>();
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

    /**
     * Loads the config from the correct position
     *
     *
     * @return instance of config with data from json
     * @throws IOException
     */
    public static Config loadConfig() throws IOException {
        File f = new File(storageLocation);
        return loadConfig(f);
    }

    /**
     * Gets all collection ids.
     *
     * @return the all collection ids
     */
    @JsonIgnore
    public List<Long> getAllCollectionIds() {
        return serversToIds
                .values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * Gets all the collection ids associated with the server
     *
     * @param server
     * @return all the collection ids from the server
     */
    @JsonIgnore
    public List<Long> getIdsFromServer(String server) {
        return serversToIds.get(server);
    }

    /**
     * Get all servers set.
     *
     * @return the set
     */
    @JsonIgnore
    public Set<String> getAllServers() {
        return serversToIds.keySet();
    }

    /**
     * Mostly used for testing
     * Test if 2 objects are equals
     * @param o
     * @return if equals true otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return Objects.equals(serversToIds, config.serversToIds);
    }

    /**
     * Returns the hash value of the Config
     *
     * @return hash value
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(serversToIds);
    }



    /*
     * public List<Collection> getCollections() {
     * return collections;
     * }
     *
     * public void setCollections(List<Collection> collections) {
     * this.collections = collections;
     * }
     */

    /**
     * Add new collection.
     *
     * @param server the server
     * @param id     the id
     */
    public void addNewCollection(String server, Long id) {

        if (serversToIds.containsKey(server)) {
            serversToIds.get(server).add(id);
        } else {
            serversToIds.put(server, List.of(id));
        }
    }

    /**
     * Stores config in canonical path
     * referenced in the static variable storage location
     * Prefer the use of this over the other overloaded function
     *
     * @throws IOException
     */
    public void saveConfig() throws IOException {
        File f = new File(storageLocation);
        f.getParentFile().mkdirs();
        this.saveConfig(f);
    }

    /**
     * Save config.
     *
     * @param file the file
     * @throws IOException the io exception
     */
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
