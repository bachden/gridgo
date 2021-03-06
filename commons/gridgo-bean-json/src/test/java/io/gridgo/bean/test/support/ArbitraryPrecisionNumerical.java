package io.gridgo.bean.test.support;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArbitraryPrecisionNumerical {

    private BigDecimal decimal;

    private BigInteger integer;

    private double d;
}
