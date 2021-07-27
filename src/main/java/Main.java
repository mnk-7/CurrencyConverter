import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        NbpApi nbpApi = new NbpApi();

        String bodyA = nbpApi.callApiA();
        List<CurrencyExchangeRateList> currencyExchangeRateTableA = nbpApi.getCurrencyExchangeRateTable(bodyA);
        System.out.println(currencyExchangeRateTableA);

        String bodyB = nbpApi.callApiB();
        List<CurrencyExchangeRateList> currencyExchangeRateTableB = nbpApi.getCurrencyExchangeRateTable(bodyB);
        System.out.println(currencyExchangeRateTableB);
    }
}
