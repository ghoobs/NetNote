package client.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config {

  @JsonProperty Map<String, List<Long>> serversToIds;

  public List<Long> getAllCollectionIds() {
    return serversToIds.values().stream().flatMap(x -> x.stream()).toList();
  }

  public Set<String> getAllServers(){
    return serversToIds.keySet();
  }

  public void addNewCollection(String server, Long id) {

    if (serversToIds.containsKey(server)) {
      serversToIds.get(server).add(id);
    } else {
      serversToIds.put(server, List.of(id));
    }
  }

  

  public Config() { serversToIds = new HashMap<>(); }


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
