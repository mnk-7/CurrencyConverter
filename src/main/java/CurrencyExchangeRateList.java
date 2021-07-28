import lombok.Data;

import java.util.List;

@Data
public class CurrencyExchangeRateList {

    private char table;
    private String no;
    private String effectiveDate;
    private List<CurrencyExchangeRate> rates;

}
