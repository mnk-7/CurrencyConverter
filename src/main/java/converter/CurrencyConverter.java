package converter;

import db.Currency;
import db.CurrencyRepository;
import nbp.CurrencyExchangeRate;
import nbp.NbpApi;
import nbp.NbpApiException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class CurrencyConverter {

    private final CurrencyRepository currencyRepository;
    private final NbpApi nbpRepository;

    public CurrencyConverter(CurrencyRepository currencyRepository, NbpApi nbpRepository) {
        this.currencyRepository = currencyRepository;
        this.nbpRepository = nbpRepository;
    }

    public BigDecimal convertToPln(String codeOfCurrencyToConvert, BigDecimal amountToConvert) {
        Currency currencyToConvert = findCurrency(codeOfCurrencyToConvert);
        return currencyToConvert.getAvgRate().multiply(amountToConvert);
    }

    public BigDecimal convertFromPln(String codeOfCurrencyToConvert, BigDecimal amountToConvert) {
        Currency currencyToConvert = findCurrency(codeOfCurrencyToConvert);
        return amountToConvert.divide(currencyToConvert.getAvgRate(), 2, RoundingMode.HALF_UP);
    }


    private Currency findCurrency(String codeOfCurrency) {
        Optional<Currency> currencyInDatabase = findCurrencyInDatabase(codeOfCurrency);
        if (currencyInDatabase.isPresent()){
            return currencyInDatabase.get();
        }
        Currency currencyInNbpBase = findCurrencyInNbpBase(codeOfCurrency);
        currencyRepository.addCurrency(currencyInNbpBase);
        return currencyInNbpBase;
    }

    private Optional<Currency> findCurrencyInDatabase(String codeOfCurrency){
        return currencyRepository.findCurrency(codeOfCurrency);
    }

    private Currency findCurrencyInNbpBase(String codeOfCurrency) {
        Optional<CurrencyExchangeRate> currencyExchangeRate =
                getCurrencyExchangeRate(nbpRepository.callApiA(), codeOfCurrency);
        if (currencyExchangeRate.isEmpty()){
            currencyExchangeRate = getCurrencyExchangeRate(nbpRepository.callApiB(), codeOfCurrency);
        }
        if (currencyExchangeRate.isPresent()){
            CurrencyExchangeRate rate = currencyExchangeRate.get();
            return new Currency(rate.getCode(), rate.getCurrency(), rate.getMid());
        }
        throw new NbpApiException("Currency not found!");
    }

    private Optional<CurrencyExchangeRate> getCurrencyExchangeRate(String nbpTable, String codeOfCurrency) {
        return nbpRepository.getCurrencyExchangeRateTable(nbpTable)
                    .stream()
                    .flatMap(rateList -> rateList.getRates().stream())
                    .filter(x -> x.getCode().equalsIgnoreCase(codeOfCurrency))
                    .findFirst();
    }


}
