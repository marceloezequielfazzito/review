package schwarz.jobs.interview.coupon.web;


import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.core.services.model.Basket;
import schwarz.jobs.interview.coupon.web.dto.ApplicationRequestDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponRequestDTO;

// no read me
// no test
// no docker image
// no docker compose
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")  // change url
@Slf4j  //?????????
public class CouponResource {

    // no swaagger

    private final CouponService couponService;

    /**
     * @param applicationRequestDTO
     * @return
     */
    //@ApiOperation(value = "Applies currently active promotions and coupons from the request to the requested Basket - Version 1")
    @PostMapping(value = "/apply") // check url
    public ResponseEntity<Basket> apply( // check return value  exposing entity
        //@ApiParam(value = "Provides the necessary basket and customer information required for the coupon application", required = true)
        @RequestBody @Valid final ApplicationRequestDTO applicationRequestDTO) {

        //logic makes no sense move to service

        log.info("Applying coupon");

        final Optional<Basket> basket =
            couponService.apply(applicationRequestDTO.getBasket(), applicationRequestDTO.getCode()); // signature makes no sense

        if (basket.isEmpty()) { //logic makes no sense
            return ResponseEntity.notFound().build();
        }

        if (!applicationRequestDTO.getBasket().isApplicationSuccessful()) { // check logic make no sense
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        log.info("Applied coupon");

        return ResponseEntity.ok().body(applicationRequestDTO.getBasket()); // ???????????????
    }

    @PostMapping("/create") // check url
    public ResponseEntity<Void> create(@RequestBody @Valid final CouponDTO couponDTO) {

        final Coupon coupon = couponService.createCoupon(couponDTO); // return value noy used return value is entity

        return ResponseEntity.ok().build();
    }

    @GetMapping("/coupons")
    public List<Coupon> getCoupons(@RequestBody @Valid final CouponRequestDTO couponRequestDTO ) { // use path params
         // not pagination
        return couponService.getCoupons(couponRequestDTO);
    }
}
