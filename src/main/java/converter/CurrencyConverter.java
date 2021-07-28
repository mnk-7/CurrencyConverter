package converter;

import db.Currency;
import db.CurrencyRepository;
import nbp.NbpApiRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class CurrencyConverter {

    private final CurrencyRepository currencyRepository;
    private final NbpApiRepository nbpRepository;

    public CurrencyConverter(CurrencyRepository currencyRepository, NbpApiRepository nbpRepository) {
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
        } else {
            Currency currencyInNbpBase = findCurrencyInNbpBase(codeOfCurrency);
            addNewCurrencyToDatabase(currencyInNbpBase);
            return currencyInNbpBase;
        }
    }


    private Optional<Currency> findCurrencyInDatabase(String codeOfCurrency){
        return currencyRepository.findCurrency(codeOfCurrency);
    }

    private Currency findCurrencyInNbpBase(String codeOfCurrency) {
        return nbpRepository.findCurrency(codeOfCurrency);
    }

    private void addNewCurrencyToDatabase(Currency newCurrency){
        currencyRepository.addCurrency(newCurrency);
    }

}
