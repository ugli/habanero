package se.ugli.habanero.j;

public class HabaneroException extends RuntimeException {

	private static final long serialVersionUID = 8697180611643224014L;

	public HabaneroException(final Throwable t) {
		super(t);
	}

	public HabaneroException(final String msg) {
		super(msg);
	}
}
