package se.ugli.habanero.j.metadata;

import java.sql.Types;

public enum SqlType {
    ARRAY(Types.ARRAY),
    BIGINT(Types.BIGINT),
    BINARY(Types.BINARY),
    BIT(Types.BIT),
    BLOB(Types.BLOB),
    BOOLEAN(Types.BOOLEAN),
    CHAR(Types.CHAR),
    CLOB(Types.CLOB),
    DATALINK(Types.DATALINK),
    DATE(Types.DATE),
    DECIMAL(Types.DECIMAL),
    DISTINCT(Types.DISTINCT),
    DOUBLE(Types.DOUBLE),
    FLOAT(Types.FLOAT),
    INTEGER(Types.INTEGER),
    JAVA_OBJECT(Types.JAVA_OBJECT),
    LONGNVARCHAR(Types.LONGNVARCHAR),
    LONGVARBINARY(Types.LONGVARBINARY),
    LONGVARCHAR(Types.LONGVARCHAR),
    NCHAR(Types.NCHAR),
    NCLOB(Types.NCLOB),
    NULL(Types.NULL),
    NUMERIC(Types.NUMERIC),
    NVARCHAR(Types.NVARCHAR),
    OTHER(Types.OTHER),
    REAL(Types.REAL),
    REF(Types.REF),
    REF_CURSOR(Types.REF_CURSOR),
    ROWID(Types.ROWID),
    SMALLINT(Types.SMALLINT),
    SQLXML(Types.SQLXML),
    STRUCT(Types.STRUCT),
    TIME(Types.TIME),
    TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
    TIMESTAMP(Types.TIMESTAMP),
    TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE),
    TINYINT(Types.TINYINT),
    VARBINARY(Types.VARBINARY),
    VARCHAR(Types.VARCHAR);

    public final int typeNumber;

    private SqlType(final int typeNumber) {
        this.typeNumber = typeNumber;
    }

    public static SqlType applyTypeNumber(final int typeNumber) {
        for (final SqlType sqlType : values())
            if (sqlType.typeNumber == typeNumber)
                return sqlType;
        throw new IllegalStateException();
    }
}
