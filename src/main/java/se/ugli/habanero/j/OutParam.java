package se.ugli.habanero.j;

import java.sql.Types;

public enum OutParam {
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
	ROWID(Types.ROWID),
	SMALLINT(Types.SMALLINT),
	SQLXML(Types.SQLXML),
	STRUCT(Types.STRUCT),
	TIME(Types.TIME),
	TIMESTAMP(Types.TIMESTAMP),
	TINYINT(Types.TINYINT),
	VARBINARY(Types.VARBINARY),
	VARCHAR(Types.VARCHAR);

	// --------------------------JDBC 4.2 -----------------------------
	// public static final int REF_CURSOR = 2012;
	// public static final int TIME_WITH_TIMEZONE = 2013;
	// public static final int TIMESTAMP_WITH_TIMEZONE = 2014;

	public final int sqlType;

	private OutParam(final int sqlType) {
		this.sqlType = sqlType;
	}

}
