
import java.math.BigDecimal;

public class BaseServiceImpl implements BaseService {

    @Override
    public java.math.BigDecimal var() {
        BigDecimal a = new BigDecimal("1").multiply(new BigDecimal("2"));
        BigDecimal b = new BigDecimal("1").multiply(new BigDecimal("2"));
        return a.add(b);
    }
}