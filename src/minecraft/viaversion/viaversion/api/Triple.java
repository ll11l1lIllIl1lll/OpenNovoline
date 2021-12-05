package viaversion.viaversion.api;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C third;

    public Triple(@Nullable A first, @Nullable B second, @Nullable C third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Nullable
    public A getFirst() {
        return first;
    }

    @Nullable
    public B getSecond() {
        return second;
    }

    @Nullable
    public C getThird() {
        return third;
    }

    @Override
    public String toString() {
        return "Triple{" + first + ", " + second + ", " + third + '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        if (!Objects.equals(first, triple.first)) return false;
        if (!Objects.equals(second, triple.second)) return false;
        return Objects.equals(third, triple.third);
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }
}
