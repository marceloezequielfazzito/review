package schwarz.jobs.interview.coupon.core.services;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public BasketDTO apply(final BasketDTO basketDTO, final String code) {


        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Coupon code %s not found", code)));


        Basket basket = Basket.builder().value(basketDTO.getValue()).build();
        basket.applyCoupon(coupon);

        if (!basket.isApplicationSuccessful()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Could not apply Coupon code %s to basket", code));
        }

        return BasketDTO.builder()
                .value(basket.getValue())
                .appliedDiscount(basket.getAppliedDiscount())
                .applicationSuccessful(basket.isApplicationSuccessful())
                .build();
    }

    public CouponDTO createCoupon(final CouponDTO couponDTO) {

        if (couponDTO.getCode() == null || couponDTO.getCode().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon code cannot be null or empty");
        }

        try {
            Coupon coupon = couponRepository.save(buildCoupon(couponDTO));
            return buildCouponDTO(coupon);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("Coupon code %s already exists", couponDTO.getCode()));
        }

    }

    public List<CouponDTO> getCoupons(final String[] codes) {
        return couponRepository.findByCodeIn(List.of(codes))
                .stream().map(this::buildCouponDTO)
                .collect(Collectors.toList());

    }

    private Coupon buildCoupon(CouponDTO couponDTO) {
        return Coupon.builder()
                .code(couponDTO.getCode())
                .discount(couponDTO.getDiscount())
                .minBasketValue(couponDTO.getMinBasketValue())
                .build();
    }

    private CouponDTO buildCouponDTO(Coupon coupon) {
        return CouponDTO.builder()
                .code(coupon.getCode())
                .discount(coupon.getDiscount())
                .minBasketValue(coupon.getMinBasketValue())
                .build();
    }
}
