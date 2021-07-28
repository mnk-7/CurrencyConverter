import converter.CurrencyConverter;
import db.DatabaseConnection;
import nbp.CurrencyExchangeRateList;
import nbp.NbpApi;
import db.CurrencyRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class MainTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        NbpApi nbpApi = new NbpApi();

        String bodyA = nbpApi.callApiA();
        List<CurrencyExchangeRateList> currencyExchangeRateTableA = nbpApi.getCurrencyExchangeRateTable(bodyA);
        System.out.println(currencyExchangeRateTableA);

        String bodyB = nbpApi.callApiB();
        List<CurrencyExchangeRateList> currencyExchangeRateTableB = nbpApi.getCurrencyExchangeRateTable(bodyB);
        System.out.println(currencyExchangeRateTableB);


        DatabaseConnection databaseConnection = DatabaseConnection.getInstance();

        CurrencyRepository currencyRepository = new CurrencyRepository(databaseConnection);
        System.out.println(currencyRepository.findCurrency("EUR"));

        CurrencyConverter currencyConverter = new CurrencyConverter(currencyRepository, nbpApi);
        System.out.println(currencyConverter.convertToPln("EUR", new BigDecimal("10")));
        System.out.println(currencyConverter.convertFromPln("EUR", new BigDecimal("90")));
        System.out.println(currencyConverter.convertToPln("USD", new BigDecimal("20")));
        System.out.println(currencyConverter.convertFromPln("BAM", new BigDecimal("20")));

        //currencyConverter.convertFromPln("ABC", new BigDecimal(10));
    }
}
