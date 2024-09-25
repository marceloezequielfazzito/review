package schwarz.jobs.interview.coupon.web.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
// making mutable inmutable
// this dto makes not sense
@Data
@Builder
public class CouponRequestDTO {

    @NotNull
    private List<String> codes;

}
