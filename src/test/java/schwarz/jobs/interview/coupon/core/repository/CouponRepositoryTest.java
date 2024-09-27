package schwarz.jobs.interview.coupon.core.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import schwarz.jobs.interview.coupon.CouponApplication;
import schwarz.jobs.interview.coupon.core.domain.Coupon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = CouponApplication.class)
@DirtiesContext
public class CouponRepositoryTest {

    @Autowired
    private CouponRepository couponRepository;


    @BeforeEach
    public void afterEach() {
        couponRepository.deleteAll();
    }

    @Test
    public void when_find_by_code_then_return_coupon() {

        couponRepository.save(createNewCoupon("code-1", BigDecimal.valueOf(10.012224), BigDecimal.valueOf(20.0)));
        couponRepository.save(createNewCoupon("code-2", BigDecimal.valueOf(100.0), BigDecimal.valueOf(200.0)));

        Optional<Coupon> coupon = couponRepository.findByCode("code-1");

        assertThat(coupon.isPresent()).isTrue();
        assertThat(coupon.get().getCode()).isEqualTo("code-1");
        assertThat(coupon.get().getDiscount()).isEqualTo(BigDecimal.valueOf(10.01).setScale(2, RoundingMode.HALF_EVEN));
        assertThat(coupon.get().getMinBasketValue()).isEqualTo(BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_EVEN));

    }

    @Test
    public void when_find_by_codes_then_return_Coupons() {

        couponRepository.save(createNewCoupon("code-1", BigDecimal.valueOf(10.012224), BigDecimal.valueOf(20.0)));
        couponRepository.save(createNewCoupon("code-2", BigDecimal.valueOf(100.0), BigDecimal.valueOf(200.0)));

        List<Coupon> coupons = couponRepository.findByCodeIn(List.of("code-1", "code-2"));

        assertThat(coupons.isEmpty()).isFalse();
        assertThat(coupons.size()).isEqualTo(2);


        assertThat(coupons.get(0).getCode()).isEqualTo("code-1");
        assertThat(coupons.get(0).getDiscount()).isEqualTo(BigDecimal.valueOf(10.01).setScale(2, RoundingMode.HALF_EVEN));
        assertThat(coupons.get(0).getMinBasketValue()).isEqualTo(BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_EVEN));

        assertThat(coupons.get(1).getCode()).isEqualTo("code-2");
        assertThat(coupons.get(1).getDiscount()).isEqualTo(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_EVEN));
        assertThat(coupons.get(1).getMinBasketValue()).isEqualTo(BigDecimal.valueOf(200.00).setScale(2, RoundingMode.HALF_EVEN));

    }

    @Test
    public void when_save_coupon_with_repeated_code_then_throw_exception() {
        couponRepository.save(createNewCoupon("code-1", BigDecimal.valueOf(10.0), BigDecimal.valueOf(20.0)));
        assertThatThrownBy(() -> couponRepository.save(createNewCoupon("code-1", BigDecimal.valueOf(100.0), BigDecimal.valueOf(200.0))))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("constraint [\"PUBLIC.IDX_UNIQUE_CODE_INDEX");

    }

    @Test
    public void when_save_coupon_with_null_code_then_throw_exception() {
        assertThatThrownBy(() -> couponRepository.save(createNewCoupon(null, BigDecimal.valueOf(100.0), BigDecimal.valueOf(200.0))))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessage("could not execute statement; SQL [n/a]; constraint [null]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement");

    }

    private static Coupon createNewCoupon(String code, BigDecimal discount, BigDecimal minBasketValue) {
        return Coupon.builder().code(code)
                .discount(discount)
                .minBasketValue(minBasketValue)
                .build();
    }


}
