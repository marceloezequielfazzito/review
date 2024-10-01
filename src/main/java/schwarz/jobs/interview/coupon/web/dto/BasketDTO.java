package schwarz.jobs.interview.coupon.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(name = "Basket", description = "Basket VO")
@Builder
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
public class BasketDTO {

    @NotNull
    private BigDecimal value;

    private BigDecimal appliedDiscount;

    private boolean applicationSuccessful;


}
