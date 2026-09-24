package com.vr.mini_authorizer.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * representa o valor do cartão
 */
public final class Valor {

    private final BigDecimal quantia;

    private Valor(BigDecimal quantia) {
        this.quantia = quantia;
    }

    /**
     * factory, usando 2 casas decimais (base foi o texto "500.00")
     */
    public static Valor de(String quantia) {
        return new Valor(new BigDecimal(quantia).setScale(2, RoundingMode.UNNECESSARY));
    }

    /** Fábrica a partir de um {@code BigDecimal} já existente (ex.: vindo do banco). */
    public static Valor de(BigDecimal quantia) {
        return new Valor(quantia.setScale(2, RoundingMode.UNNECESSARY));
    }

    public BigDecimal bigDecimal() {
        return quantia;
    }

    @Override
    public boolean equals(Object o) {
        // compareTo, e não o equals() do BigDecimal: 10.00 deve ser igual a 10.0.
        if (this == o) {
            return true;
        }
        if (!(o instanceof Valor outro)) {
            return false;
        }
        return this.quantia.compareTo(outro.quantia) == 0;
    }

    @Override
    public int hashCode() {
        // stripTrailingZeros() garante hashCode consistente com o equals acima
        // (dois Valor "iguais" por compareTo devem ter o mesmo hashCode).
        return quantia.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return quantia.toPlainString();
    }
}
