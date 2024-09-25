package schwarz.jobs.interview.coupon.web.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

// making mutable inmutable
@Data
@Builder
public class CouponDTO {

    private BigDecimal discount;

    private String code;

    private BigDecimal minBasketValue;

}
