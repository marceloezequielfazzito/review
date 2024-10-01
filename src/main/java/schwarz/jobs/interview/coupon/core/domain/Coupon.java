package schwarz.jobs.interview.coupon.core.domain;

import java.math.BigDecimal;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "coupons",
        indexes = @Index(name = "idx_code", columnList = "code"),
        uniqueConstraints = {@UniqueConstraint(name = "idx_unique_code", columnNames = {"code"})}
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "discount", precision = 10, scale = 2, nullable = false)
    private BigDecimal discount;

    @Column(name = "min_basket_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal minBasketValue;

}
