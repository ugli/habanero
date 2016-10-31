package se.ugli.habanero.j.internal;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import se.ugli.habanero.j.Habanero;
import se.ugli.habanero.j.TypeAdaptor;
import se.ugli.habanero.j.metadata.SqlType;

public class PrepareArgumentsCommand {

    private final PreparedStatement statement;

    private PrepareArgumentsCommand(final PreparedStatement statement) {
        this.statement = statement;
    }

    public static PrepareArgumentsCommand apply(final PreparedStatement statement) {
        return new PrepareArgumentsCommand(statement);
    }

    public void exec(final Object... args) throws SQLException {
        int parameterIndex = 1;
        for (final Object arg : args)
            if (arg instanceof SqlType && statement instanceof CallableStatement) {
                final SqlType outParam = (SqlType) arg;
                final CallableStatement callableStatement = (CallableStatement) statement;
                callableStatement.registerOutParameter(parameterIndex, outParam.typeNumber);
            }
            else
                statement.setObject(parameterIndex++, convertArgument(arg));
    }

    private static Object convertArgument(final Object object) {
        if (object != null) {
            final Class<?> type = object.getClass();
            final TypeAdaptor typeAdaptor = Habanero.getTypeAdaptor(type);
            return typeAdaptor.toJdbcValue(object);
        }
        return null;
    }
}
