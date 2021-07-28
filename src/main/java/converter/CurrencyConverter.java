package converter;

import db.Currency;
import db.CurrencyRepository;
import nbp.CurrencyExchangeRate;
import nbp.NbpApi;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class CurrencyConverter {

    private final CurrencyRepository currencyRepository;
    private final NbpApi nbpBase;

    public CurrencyConverter(CurrencyRepository currencyRepository, NbpApi nbpBase) {
        this.currencyRepository = currencyRepository;
        this.nbpBase = nbpBase;
    }

    public BigDecimal convertToPln(String codeOfCurrencyToConvert, BigDecimal amountToConvert) throws IOException, InterruptedException {
        Currency currencyToConvert = findCurrency(codeOfCurrencyToConvert);
        return currencyToConvert.getAvgRate().multiply(amountToConvert);
    }

    public BigDecimal convertFromPln(String codeOfCurrencyToConvert, BigDecimal amountToConvert) throws IOException, InterruptedException {
        Currency currencyToConvert = findCurrency(codeOfCurrencyToConvert);
        return amountToConvert.divide(currencyToConvert.getAvgRate(), 2, RoundingMode.HALF_UP);
    }


    private Currency findCurrency(String codeOfCurrency) throws IOException, InterruptedException {
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

    private Currency findCurrencyInNbpBase(String codeOfCurrency) throws IOException, InterruptedException {
        String tableA = nbpBase.callApiA();
        Optional<CurrencyExchangeRate> exchangeRate = nbpBase.getCurrencyExchangeRateTable(tableA)
                .stream()
                .flatMap(rateList -> rateList.getRates().stream())
                .filter(x -> x.getCode().equalsIgnoreCase(codeOfCurrency))
                .findFirst();
        if (exchangeRate.isEmpty()){
            String tableB = nbpBase.callApiB();
            exchangeRate = nbpBase.getCurrencyExchangeRateTable(tableB)
                    .stream()
                    .flatMap(rateList -> rateList.getRates().stream())
                    .filter(x -> x.getCode().equalsIgnoreCase(codeOfCurrency))
                    .findFirst();
        }
        if (exchangeRate.isPresent()){
            CurrencyExchangeRate currencyExchangeRate = exchangeRate.get();
            return new Currency(currencyExchangeRate.getCode(),
                    currencyExchangeRate.getCurrency(), currencyExchangeRate.getMid());
        }
        throw new RuntimeException("Currency not found!");
    }

}
