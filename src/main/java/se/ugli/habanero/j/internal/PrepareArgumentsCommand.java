package se.ugli.habanero.j.internal;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import se.ugli.habanero.j.HabaneroException;
import se.ugli.habanero.j.TypeAdaptor;
import se.ugli.habanero.j.TypeRegister;
import se.ugli.habanero.j.util.Option;

public class PrepareArgumentsCommand {

	public static PrepareArgumentsCommand apply(final PreparedStatement statement) {
		return new PrepareArgumentsCommand(statement);
	}

	private final PreparedStatement statement;

	private PrepareArgumentsCommand(final PreparedStatement statement) {
		this.statement = statement;
	}

	public void exec(final Object... args) throws SQLException {
		int parameterIndex = 1;
		for (final Object arg : args)
			statement.setObject(parameterIndex++, convertArgument(arg));
	}

	private Object convertArgument(final Object object) {
		final Class<?> type = object.getClass();
		final Option<TypeAdaptor> typeAdaptorOpt = TypeRegister.get(type);
		if (typeAdaptorOpt.isDefined())
			return typeAdaptorOpt.get().toJdbcValue(object);
		throw new HabaneroException(type.getName() + " isn't registered.");
	}

}
