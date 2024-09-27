package schwarz.jobs.interview.coupon;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import schwarz.jobs.interview.coupon.core.domain.Coupon;
import schwarz.jobs.interview.coupon.core.repository.CouponRepository;
import schwarz.jobs.interview.coupon.web.dto.BasketDTO;
import schwarz.jobs.interview.coupon.web.dto.CouponDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class CouponApplicationTests {


	@Autowired
	private CouponRepository couponRepository;
	@LocalServerPort
	private int port;

	public static TestRestTemplate restTemplate = new TestRestTemplate();

	@AfterEach
	public void afterEach(){
		couponRepository.deleteAll();
	}


	@Test
	void when_call_get_coupons_by_code_return_coupons() {

		couponRepository.save(createNewCoupon("code-1", BigDecimal.valueOf(10.012224), BigDecimal.valueOf(20.0)));
		couponRepository.save(createNewCoupon("code-2", BigDecimal.valueOf(100.0), BigDecimal.valueOf(200.0)));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Void> entity = new HttpEntity<>(null, headers);

		ResponseEntity< CouponDTO[]> response = restTemplate.exchange(
				createURLWithPort("/api/v1/coupons?codes=code-1,code-2"),
				HttpMethod.GET, entity, CouponDTO[].class);


		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		CouponDTO[] coupons = response.getBody();
		assertThat(coupons).isNotNull();
		assertThat(coupons.length).isEqualTo(2);

		assertThat(coupons[0].getCode()).isEqualTo("code-1");
		assertThat(coupons[0].getDiscount()).isEqualTo(BigDecimal.valueOf(10.01).setScale(2, RoundingMode.HALF_EVEN));
		assertThat(coupons[0].getMinBasketValue()).isEqualTo(BigDecimal.valueOf(20.00).setScale(2, RoundingMode.HALF_EVEN));

		assertThat(coupons[1].getCode()).isEqualTo("code-2");
		assertThat(coupons[1].getDiscount()).isEqualTo(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_EVEN));
		assertThat(coupons[1].getMinBasketValue()).isEqualTo(BigDecimal.valueOf(200.00).setScale(2, RoundingMode.HALF_EVEN));

	}

	@Test
	void when_call_post_coupon_return_created_coupon() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CouponDTO> entity = new HttpEntity<>(CouponDTO.builder()
				.code("code-1")
				.discount(BigDecimal.valueOf(10.01))
				.minBasketValue(BigDecimal.valueOf(20.05))
				.build(), headers);

		ResponseEntity<CouponDTO> response = restTemplate.exchange(
				createURLWithPort("/api/v1/coupons"),
				HttpMethod.POST, entity, CouponDTO.class);


		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		CouponDTO coupon = response.getBody();
		assertThat(coupon).isNotNull();
		assertThat(coupon.getCode()).isEqualTo("code-1");
		assertThat(coupon.getDiscount()).isEqualTo(BigDecimal.valueOf(10.01).setScale(2, RoundingMode.HALF_EVEN));
		assertThat(coupon.getMinBasketValue()).isEqualTo(BigDecimal.valueOf(20.05).setScale(2, RoundingMode.HALF_EVEN));


	}


	@Test
	void when_call_post_coupon_with_null_code_return_bad_request() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CouponDTO> entity = new HttpEntity<>(CouponDTO.builder()
				.code(null)
				.discount(BigDecimal.valueOf(10.01))
				.minBasketValue(BigDecimal.valueOf(20.05))
				.build(), headers);

		ResponseEntity<CouponDTO> response = restTemplate.exchange(
				createURLWithPort("/api/v1/coupons"),
				HttpMethod.POST, entity, CouponDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

	}

	@Test
	void when_call_post_coupon_with_empty_code_return_bad_request() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CouponDTO> entity = new HttpEntity<>(CouponDTO.builder()
				.code("")
				.discount(BigDecimal.valueOf(10.01))
				.minBasketValue(BigDecimal.valueOf(20.05))
				.build(), headers);

		ResponseEntity<CouponDTO> response = restTemplate.exchange(
				createURLWithPort("/api/v1/coupons"),
				HttpMethod.POST, entity, CouponDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

	}

	@Test
	void when_call_post_coupon_existing_code_return_conflict() {

		couponRepository.save(createNewCoupon("code-2", BigDecimal.valueOf(100.0), BigDecimal.valueOf(200.0)));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CouponDTO> entity = new HttpEntity<>(CouponDTO.builder()
				.code("code-2")
				.discount(BigDecimal.valueOf(10.01))
				.minBasketValue(BigDecimal.valueOf(20.05))
				.build(), headers);

		ResponseEntity<CouponDTO> response = restTemplate.exchange(
				createURLWithPort("/api/v1/coupons"),
				HttpMethod.POST, entity, CouponDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

	}


	@Test
	void when_call_apply_coupon_to_basket_return_applied_basket() {

		couponRepository.save(createNewCoupon("code-1", BigDecimal.valueOf(10), BigDecimal.valueOf(20)));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<BasketDTO> entity = new HttpEntity<>(BasketDTO.builder().value(BigDecimal.valueOf(100)).build(), headers);

		ResponseEntity<BasketDTO> response = restTemplate.exchange(
				createURLWithPort("/api/v1/coupons/code-1/apply"),
				HttpMethod.POST, entity, BasketDTO.class);


		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
		BasketDTO basketDTO = response.getBody();
		assertThat(basketDTO).isNotNull();
		assertThat(basketDTO.getValue()).isEqualTo(BigDecimal.valueOf(90.0).setScale(2, RoundingMode.HALF_EVEN));
		assertThat(basketDTO.isApplicationSuccessful()).isTrue();
		assertThat(basketDTO.getAppliedDiscount()).isEqualTo(BigDecimal.valueOf(10).setScale(2, RoundingMode.HALF_EVEN));



	}

	@Test
	void when_call_apply_non_existing_coupon_to_basket_return_not_found() {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<BasketDTO> entity = new HttpEntity<>(BasketDTO.builder().value(BigDecimal.valueOf(100)).build(), headers);

		ResponseEntity<BasketDTO> response = restTemplate.exchange(
				createURLWithPort("/api/v1/coupons/code-1/apply"),
				HttpMethod.POST, entity, BasketDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void when_call_apply_negative_value_basket_return_conflict() {

		couponRepository.save(createNewCoupon("code-1", BigDecimal.valueOf(10), BigDecimal.valueOf(20)));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<BasketDTO> entity = new HttpEntity<>(BasketDTO.builder().value(BigDecimal.valueOf(-100)).build(), headers);

		ResponseEntity<BasketDTO> response = restTemplate.exchange(
				createURLWithPort("/api/v1/coupons/code-1/apply"),
				HttpMethod.POST, entity, BasketDTO.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}



	private static Coupon createNewCoupon(String code, BigDecimal discount, BigDecimal minBasketValue) {
		return Coupon.builder().code(code)
				.discount(discount)
				.minBasketValue(minBasketValue)
				.build();
	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}


}
