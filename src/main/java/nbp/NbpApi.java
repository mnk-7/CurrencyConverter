package nbp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NbpApi {

    private final static String table_a = "https://api.nbp.pl/api/exchangerates/tables/a?format=json";
    private final static String table_b = "https://api.nbp.pl/api/exchangerates/tables/b?format=json";

    private String callApi(String link) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String callApiA() {
        try {
            return callApi(table_a);
        } catch (Exception e) {
            throw new NbpApiException("Calling NBP API table A failed", e.getCause());
        }
    }

    public String callApiB() {
        try {
            return callApi(table_b);
        } catch (Exception e) {
            throw new NbpApiException("Calling NBP API table B failed", e.getCause());
        }
    }

    public List<CurrencyExchangeRateList> getCurrencyExchangeRateTable(String jsonFile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonFile, new TypeReference<List<CurrencyExchangeRateList>>(){});
        } catch (Exception e) {
            throw new NbpApiException("Processing JSON file failed", e.getCause());
        }
    }



}
