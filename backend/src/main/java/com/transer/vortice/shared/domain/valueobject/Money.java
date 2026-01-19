package com.transer.vortice.shared.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Value Object para representar valores monetarios
 * Inmutable y con operaciones de negocio
 */
@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Money implements Serializable, Comparable<Money> {

    private BigDecimal amount;
    private String currency;

    public Money(BigDecimal amount) {
        this(amount, "COP"); // Pesos colombianos por defecto
    }

    public Money(String amount) {
        this(new BigDecimal(amount), "COP");
    }

    /**
     * Suma dos valores monetarios
     */
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Resta dos valores monetarios
     */
    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Multiplica por un factor
     */
    public Money multiply(BigDecimal factor) {
        return new Money(this.amount.multiply(factor).setScale(2, RoundingMode.HALF_UP), this.currency);
    }

    /**
     * Multiplica por un número entero
     */
    public Money multiply(int factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    /**
     * Divide por un factor
     */
    public Money divide(BigDecimal divisor) {
        return new Money(this.amount.divide(divisor, 2, RoundingMode.HALF_UP), this.currency);
    }

    /**
     * Verifica si es cero
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Verifica si es positivo
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Verifica si es negativo
     */
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Verifica si es mayor que otro monto
     */
    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Verifica si es menor que otro monto
     */
    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Valida que dos montos tengan la misma moneda
     */
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                String.format("No se pueden operar monedas diferentes: %s y %s",
                    this.currency, other.currency)
            );
        }
    }

    /**
     * Crea un Money con valor cero
     */
    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    /**
     * Crea un Money con valor cero en COP
     */
    public static Money zeroCOP() {
        return zero("COP");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 &&
               Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public int compareTo(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount);
    }

    @Override
    public String toString() {
        return String.format("%s %s", Currency.getInstance(currency).getSymbol(),
            amount.setScale(2, RoundingMode.HALF_UP));
    }
}
