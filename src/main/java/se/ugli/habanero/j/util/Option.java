package se.ugli.habanero.j.util;

import java.util.NoSuchElementException;

public class Option<T> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Option<T> apply(final T value) {
		return new Option(value);
	}

	public static <T> Option<T> none() {
		return apply(null);
	}

	private T value;

	private Option(T value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Option<?> other = (Option<?>) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public T get() {
		if (isDefined())
			return value;
		throw new NoSuchElementException();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	public boolean isDefined() {
		return value != null;
	}

	@Override
	public String toString() {
		return "Option [value=" + value + "]";
	}

}
