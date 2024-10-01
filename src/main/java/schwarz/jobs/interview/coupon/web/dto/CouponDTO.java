package schwarz.jobs.interview.coupon.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;

@Schema(name = "Coupon", description = "Coupon VO")
@Builder
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CouponDTO {

    private BigDecimal discount;

    @NotNull
    private String code;

    private BigDecimal minBasketValue;


}
