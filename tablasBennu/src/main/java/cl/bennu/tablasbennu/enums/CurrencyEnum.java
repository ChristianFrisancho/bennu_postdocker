package cl.bennu.tablasbennu.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CurrencyEnum implements Serializable {

    //@formatter:off
    USD(1, "USD", "USD", "Dolar")
    , CLP(2, "CLP", "CLP", "Peso Chileno")
    , BRL(3, "BRL", "REAL", "Real")
    , EUR(4, "EUR", "ECU", "Euro")
    , CLF(5, "CLF", "UF", "Unidad de Fomento")
    , PEN(6, "PEN", "PEN", "Sol Peruano")
    , GBP(7, "GBP", "GBP", "Libra Esterlina")
    , COP(8, "COP", "COP", "Peso Colombiano")
    , KRW(9, "KRW", "KRW", "Won Surcoreano")
    , MXN(10, "MXN", "MXN", "Peso Mexicano")
    , JPY(11, "JPY", "JPY", "Yen")
    , CHF(12, "CHF", "CHF", "Franco Suizo")
    , THB(13, "THB", "THB", "Baht")
    , HKD(14, "HKD", "HKD", "Dolar Hongkones")
    , TWD(15, "TWD", "TWD", "Dolar Taiwanes")
    , ZAR(16, "ZAR", "ZAR", "Rand Sudafricano")
    , RUB(17, "RUB", "RUB", "Rublo Ruso")
    , INR(18, "INR", "INR", "Rupia India")
    , CNY(19, "CNY", "CNY", "Yuan")
    , IDR(20, "IDR", "IDR", "Rupia Indonesia")
    , MYR(21, "MYR", "MYR", "Ringgit Malayo")
    , AUD(22, "AUD", "AUD", "Dolar Australiano")
    ;
    //@formatter:on


    CurrencyEnum(Integer id, String code, String codePMS, String name) {
        this.id = id;
        this.code = code;
        this.codePMS = codePMS;
        this.name = name;
    }

    private final Integer id;
    private final String code;
    private final String codePMS;
    private final String name;


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CurrencyEnum valueOf(Object o) {
        if (o instanceof Integer id) {
            return Arrays.stream(values()).filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        } else if (o instanceof Map<?, ?> map) {
            Integer id = (Integer) map.get("id");
            return Arrays.stream(values()).filter(e -> e.getId().equals(id)).findFirst().orElse(null);
        } else if (o instanceof String code) {
            return Arrays.stream(values()).filter(e -> e.getCode().equals(code) || e.getCodePMS().equals(code)).findFirst().orElse(null);
        } else {
            return null;
        }
    }

}

