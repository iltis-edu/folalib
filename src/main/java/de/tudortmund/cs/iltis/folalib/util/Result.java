package de.tudortmund.cs.iltis.folalib.util;

import java.util.function.Function;

public interface Result<T, E> {
    <S> S match(Function<T, S> okHandler, Function<E, S> errorHandler);

    default T unwrap() {
        return match(
                t -> t,
                e -> {
                    throw new RuntimeException(".unwrap() called on error variant");
                });
    }

    default <U> Result<U, E> map(Function<T, U> f) {
        return match(t -> new Ok<>(f.apply(t)), Err::new);
    }

    default E unwrapErr() {
        return match(
                t -> {
                    throw new RuntimeException(".unwrapErr() called on Ok variant");
                },
                e -> e);
    }

    final class Ok<T, E> implements Result<T, E> {
        private final T value;

        public Ok(T value) {
            this.value = value;
        }

        @Override
        public <S> S match(Function<T, S> okHandler, Function<E, S> errorHandler) {
            return okHandler.apply(value);
        }
    }

    final class Err<T, E> implements Result<T, E> {
        private final E error;

        public Err(E error) {
            this.error = error;
        }

        @Override
        public <S> S match(Function<T, S> okHandler, Function<E, S> errorHandler) {
            return errorHandler.apply(error);
        }
    }
}
