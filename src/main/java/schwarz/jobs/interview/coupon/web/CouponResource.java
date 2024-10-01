package schwarz.jobs.interview.coupon.web;


import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import schwarz.jobs.interview.coupon.core.services.CouponService;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Coupons API", description = "Manage active promotions and coupons APIs")
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
@Slf4j
public class CouponResource {

    private final CouponService couponService;


    @ApiOperation(value = "Applies currently active promotions and coupons from the request to the requested Basket - Version 1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "coupon applied to requested Basket", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BasketDTO.class))}),
            @ApiResponse(responseCode = "409", description = "coupon not applied to requested Basket", content = @Content),
            @ApiResponse(responseCode = "404", description = "coupon not found", content = @Content)})
    @PostMapping(value = "/{code}/apply", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BasketDTO> apply(@ApiParam(value = "coupon code", required = true) @PathVariable(name = "code") String code,
                                           @ApiParam(value = "requested Basket", required = true) @RequestBody @Valid BasketDTO basketDTO) {


        log.info("Applying coupon {} to requested Basket {} ", code, basketDTO);

        final BasketDTO dto = couponService.apply(basketDTO, code);

        log.info("Applied coupon code {} to requested Basket {} ", code, basketDTO);

        return ResponseEntity.accepted().body(dto);
    }

    @ApiOperation(value = "Creates a new coupon - Version 1")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon created", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CouponDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Coupon code is invalid", content = @Content),
            @ApiResponse(responseCode = "409", description = "Coupon already exists", content = @Content)})
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CouponDTO> create(
            @ApiParam(value = "requested Coupon", required = true) @RequestBody @Valid final CouponDTO couponDTO) {
        log.info("Creating coupon {}", couponDTO);
        final CouponDTO newCoupon = couponService.createCoupon(couponDTO);
        log.info("Coupon {} created", couponDTO);
        return ResponseEntity.ok(newCoupon);
    }

    @ApiOperation(value = "Query coupons by codes - Version 1")
    @ApiResponses(value = @ApiResponse(responseCode = "200", description = "Coupon created",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CouponDTO[].class))}))
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CouponDTO>> getCoupons(@ApiParam(value = "Requested codes", required = true) @RequestParam(name = "codes") String[] codes) {
        log.info("Getting coupons");
        return ResponseEntity.ok(couponService.getCoupons(codes));
    }
}
