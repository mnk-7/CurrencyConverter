package nbp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyExchangeRate {

    private String currency;
    private String code;
    private BigDecimal mid;

}
