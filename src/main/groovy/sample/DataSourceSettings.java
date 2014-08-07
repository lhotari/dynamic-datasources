package sample;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.AutoPopulatingList;

public class DataSourceSettings {
    private Map<String, String> clientDataSources = null;

    private List<String> clients = new AutoPopulatingList<String>(String.class);

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }

    public Map<String, String> clientDataSources() {
        if (clientDataSources == null) {
            clientDataSources = new LinkedHashMap<>();
            initialize();
        }
        return clientDataSources;
    }

    private void initialize() {
        for (String client : clients) {
            // extract client name
            String[] parts = client.split("\\|");
            String clientName = parts[0];
            String url = parts[1];
            // client to datasource mapping
            String dsName = url.substring(url.lastIndexOf("/") + 1);
            if (clientName.contains(",")) {
                // multiple clients with same datasource
                String[] clientList = clientName.split(",");
                for (String c : clientList) {
                    clientDataSources.put(c, dsName);
                }
            }
            else {
                clientDataSources.put(clientName, dsName);
            }
        }
    }
}
