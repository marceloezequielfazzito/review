package schwarz.jobs.interview.coupon.core.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    //When_StateUnderTest_Expect_ExpectedBehavior

    @Test
    public void when_create_coupon_then_should_return_coupon() {

        when(couponRepository.save(Coupon.builder()
                .code("12345")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build()))
                .thenReturn(Coupon.builder()
                        .code("12345")
                        .discount(BigDecimal.TEN)
                        .minBasketValue(BigDecimal.valueOf(50))
                        .build());

        CouponDTO dto = CouponDTO.builder()
                .code("12345")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        CouponDTO createdCoupon = couponService.createCoupon(dto);

        verify(couponRepository, times(1)).save(any());
        assertThat(createdCoupon.getCode()).isEqualTo("12345");
        assertThat(createdCoupon.getDiscount()).isEqualTo(BigDecimal.TEN);
        assertThat(createdCoupon.getMinBasketValue()).isEqualTo(BigDecimal.valueOf(50));
    }


    @Test
    public void when_create_null_coupon_then_should_throw_exception() {

        CouponDTO dto = CouponDTO.builder()
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build();

        assertThatThrownBy(() -> couponService.createCoupon(dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"Coupon code cannot be null or empty\"");


    }

    @Test
    public void when_apply_coupon_then_should_return_applied_basket() {

        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build()));

        final BasketDTO firstBasket = BasketDTO.builder()
                .value(BigDecimal.valueOf(100))
                .build();


        BasketDTO basketDTO = couponService.apply(firstBasket, "1111");


        assertThat(basketDTO.isApplicationSuccessful()).isTrue();
        assertThat(basketDTO.getAppliedDiscount()).isEqualTo(BigDecimal.TEN);
        assertThat(basketDTO.getValue()).isEqualTo(BigDecimal.valueOf(90));

    }

    @Test
    public void when_apply_with_less_than_min_basket_then_should_not_apply() {

        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build()));

        final BasketDTO inputBasket = BasketDTO.builder()
                .value(BigDecimal.valueOf(0))
                .build();

        assertThatThrownBy(() -> couponService.apply(inputBasket, "1111"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT \"Could not apply Coupon code 1111 to basket");


    }


    @Test
    public void when_apply_non_existing_coupon_then_throw_exception() {

        when(couponRepository.findByCode("1111")).thenReturn(Optional.empty());

        final BasketDTO inputBasket = BasketDTO.builder()
                .value(BigDecimal.valueOf(1000))
                .build();


        assertThatThrownBy(() -> couponService.apply(inputBasket, "1111"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND \"Coupon code 1111 not found");


    }


    @Test
    public void when_apply_negative_value_basket_then_should_not_apply() {

        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(50))
                .build()));

        final BasketDTO inputBasket = BasketDTO.builder()
                .value(BigDecimal.valueOf(-1))
                .build();

        assertThatThrownBy(() -> couponService.apply(inputBasket, "1111"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT \"Could not apply Coupon code 1111 to basket");


    }


    @Test
    public void when_discount_greater_than_value_then_should_not_apply() {

        when(couponRepository.findByCode("1111")).thenReturn(Optional.of(Coupon.builder()
                .code("1111")
                .discount(BigDecimal.TEN)
                .minBasketValue(BigDecimal.valueOf(5))
                .build()));

        final BasketDTO inputBasket = BasketDTO.builder()
                .value(BigDecimal.valueOf(9))
                .build();

        assertThatThrownBy(() -> couponService.apply(inputBasket, "1111"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("409 CONFLICT \"Could not apply Coupon code 1111 to basket");

    }


    @Test
    public void when_get_coupons_by_code_then_should_return_coupons() {
        String[] codes = {"1111", "1234"};
        when(couponRepository.findByCodeIn(List.of(codes)))
                .thenReturn(List.of(Coupon.builder()
                                .code("1111")
                                .discount(BigDecimal.TEN)
                                .minBasketValue(BigDecimal.valueOf(50))
                                .build(),
                        Coupon.builder()
                                .code("1234")
                                .discount(BigDecimal.TEN)
                                .minBasketValue(BigDecimal.valueOf(50))
                                .build()));


        List<CouponDTO> returnedCoupons = couponService.getCoupons(codes);

        assertThat(returnedCoupons.get(0).getCode()).isEqualTo("1111");
        assertThat(returnedCoupons.get(1).getCode()).isEqualTo("1234");
    }
}
