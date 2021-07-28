package nbp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
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

    public String callApiA() throws IOException, InterruptedException {
        return callApi(table_a);
    }

    public String callApiB() throws IOException, InterruptedException {
        return callApi(table_b);
    }

    public List<CurrencyExchangeRateList> getCurrencyExchangeRateTable(String jsonFile)
            throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, new TypeReference<List<CurrencyExchangeRateList>>(){});
    }



}
