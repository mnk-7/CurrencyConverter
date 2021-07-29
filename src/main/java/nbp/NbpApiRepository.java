package nbp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import db.Currency;

import java.util.List;
import java.util.Optional;

public class NbpApiRepository {

    private final NbpApi nbpApi;

    public NbpApiRepository(NbpApi nbpApi) {
        this.nbpApi = nbpApi;
    }

    public Optional<Currency> findCurrency(String codeOfCurrency) {
        return getCurrencyExchangeRate(nbpApi.callApiA(), codeOfCurrency)
                        .or(() -> getCurrencyExchangeRate(nbpApi.callApiB(), codeOfCurrency))
                        .map(x -> convertCurrencyExchangeRateToCurrency(x));
    }

    private Optional<CurrencyExchangeRate> getCurrencyExchangeRate(String nbpTable, String codeOfCurrency) {
        return getCurrencyExchangeRateTable(nbpTable)
                .stream()
                .flatMap(rateList -> rateList.getRates().stream())
                .filter(x -> x.getCode().equalsIgnoreCase(codeOfCurrency))
                .findFirst();
    }

    private List<CurrencyExchangeRateList> getCurrencyExchangeRateTable(String jsonFile) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(jsonFile, new TypeReference<List<CurrencyExchangeRateList>>(){});
        } catch (Exception e) {
            throw new NbpApiException("Processing JSON file failed", e.getCause());
        }
    }

    private Currency convertCurrencyExchangeRateToCurrency(CurrencyExchangeRate rate){
            return new Currency(rate.getCode(), rate.getCurrency(), rate.getMid());
    }

}

