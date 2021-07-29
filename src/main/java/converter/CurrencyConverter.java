package converter;

import db.Currency;
import db.CurrencyRepository;
import nbp.NbpApiException;
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


    public BigDecimal convert(String codeFrom, String codeTo, BigDecimal amountToConvert){
        if (codeFrom.equals("PLN")){
            return convertFromPln(codeTo,amountToConvert);
        } else if (codeTo.equals("PLN")){
            return convertToPln(codeFrom, amountToConvert);
        } else{
            return convertOtherCurrencies(codeFrom,codeTo,amountToConvert);
        }
    }

    private BigDecimal convertOtherCurrencies(String codeFrom, String codeTo, BigDecimal amountToConvert){
        Currency currencyFromToConvert = findCurrency(codeFrom);
        Currency currencyToToConvert = findCurrency(codeTo);
        return amountToConvert
                .multiply(currencyFromToConvert.getAvgRate())
                .divide(currencyToToConvert.getAvgRate(), 2, RoundingMode.HALF_UP);
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
            Optional<Currency> currencyInNbpBase = findCurrencyInNbpBase(codeOfCurrency);
            if (currencyInNbpBase.isPresent()){
                addNewCurrencyToDatabase(currencyInNbpBase.get());
                return currencyInNbpBase.get();
            } else {
                throw new NbpApiException("Currency not found");
            }
        }
    }


    private Optional<Currency> findCurrencyInDatabase(String codeOfCurrency){
        return currencyRepository.findCurrency(codeOfCurrency);
    }

    private Optional<Currency> findCurrencyInNbpBase(String codeOfCurrency) {
        return nbpRepository.findCurrency(codeOfCurrency);
    }

    private void addNewCurrencyToDatabase(Currency newCurrency){
        currencyRepository.addCurrency(newCurrency);
    }

}
