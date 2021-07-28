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

    public Currency findCurrency(String codeOfCurrency) {
        Optional<CurrencyExchangeRate> currencyExchangeRate =
                getCurrencyExchangeRate(nbpApi.callApiA(), codeOfCurrency);
        if (currencyExchangeRate.isEmpty()){
            currencyExchangeRate = getCurrencyExchangeRate(nbpApi.callApiB(), codeOfCurrency);
        }
        return convertCurrencyExchangeRateToCurrency(currencyExchangeRate);
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

    private Currency convertCurrencyExchangeRateToCurrency(Optional<CurrencyExchangeRate> currencyExchangeRate){
        if (currencyExchangeRate.isPresent()){
            CurrencyExchangeRate rate = currencyExchangeRate.get();
            return new Currency(rate.getCode(), rate.getCurrency(), rate.getMid());
        }
        throw new NbpApiException("Currency code not found!");
    }

}
