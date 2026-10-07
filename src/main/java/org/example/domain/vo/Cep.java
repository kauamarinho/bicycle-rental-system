package org.example.domain.vo;

import org.example.domain.exception.InvalidCepException;

/**
 * Value Object representing a valid CEP (Brazilian postal code).
 * Validation happens in the constructor, so the external lookup is
 * never called with a malformed value.
 */
public final class Cep {

    private final String value;

    public Cep(String raw) {
        if (raw == null) {
            throw new InvalidCepException("CEP cannot be null.");
        }

        String normalized = raw.replace("-", "").replace(".", "").trim();

        if (normalized.length() != 8) {
            throw new InvalidCepException("CEP must have 8 digits.");
        }

        for (int i = 0; i < normalized.length(); i++) {
            if (!Character.isDigit(normalized.charAt(i))) {
                throw new InvalidCepException("CEP must contain only digits.");
            }
        }

        this.value = normalized;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cep other)) return false;
        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
