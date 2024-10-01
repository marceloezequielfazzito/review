package schwarz.jobs.interview.coupon.core.services.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import schwarz.jobs.interview.coupon.core.domain.Coupon;

@Data
@Builder
public class Basket {

    @NotNull
    private BigDecimal value;

    private BigDecimal appliedDiscount;

    private boolean applicationSuccessful;

    public void applyCoupon(final Coupon coupon) {
        applicationSuccessful = false;
        appliedDiscount = BigDecimal.ZERO;

        if (isCouponApplicable(coupon)){
            applicationSuccessful = true;
            appliedDiscount = coupon.getDiscount();
            value = value.subtract(coupon.getDiscount());
        }
    }

    private boolean isCouponApplicable(final Coupon coupon) {
        return value.compareTo(BigDecimal.ZERO) > 0 &&
               value.compareTo(coupon.getMinBasketValue()) >= 0 &&
               value.compareTo(coupon.getDiscount()) >= 0 ;
    }

}
