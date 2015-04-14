package se.ugli.habanero.j;

import java.sql.Types;

public class OutParam {

	public final static OutParam ARRAY = apply(Types.ARRAY);
	public final static OutParam BIGINT = apply(Types.BIGINT);
	public final static OutParam BINARY = apply(Types.BINARY);
	public final static OutParam BIT = apply(Types.BIT);
	public final static OutParam BLOB = apply(Types.BLOB);
	public final static OutParam BOOLEAN = apply(Types.BOOLEAN);
	public final static OutParam CHAR = apply(Types.CHAR);
	public final static OutParam CLOB = apply(Types.CLOB);
	public final static OutParam DATALINK = apply(Types.DATALINK);
	public final static OutParam DATE = apply(Types.DATE);
	public final static OutParam DECIMAL = apply(Types.DECIMAL);
	public final static OutParam DISTINCT = apply(Types.DISTINCT);
	public final static OutParam DOUBLE = apply(Types.DOUBLE);
	public final static OutParam FLOAT = apply(Types.FLOAT);
	public final static OutParam INTEGER = apply(Types.INTEGER);
	public final static OutParam JAVA_OBJECT = apply(Types.JAVA_OBJECT);
	public static final OutParam LONGNVARCHAR = apply(Types.LONGNVARCHAR);
	public final static OutParam LONGVARBINARY = apply(Types.LONGVARBINARY);
	public final static OutParam LONGVARCHAR = apply(Types.LONGVARCHAR);
	public static final OutParam NCHAR = apply(Types.NCHAR);
	public static final OutParam NCLOB = apply(Types.NCLOB);
	public final static OutParam NULL = apply(Types.NULL);
	public final static OutParam NUMERIC = apply(Types.NUMERIC);
	public static final OutParam NVARCHAR = apply(Types.NVARCHAR);
	public final static OutParam OTHER = apply(Types.OTHER);
	public final static OutParam REAL = apply(Types.REAL);
	public final static OutParam REF = apply(Types.REF);
	public final static OutParam ROWID = apply(Types.ROWID);
	public final static OutParam SMALLINT = apply(Types.SMALLINT);
	public static final OutParam SQLXML = apply(Types.SQLXML);
	public final static OutParam STRUCT = apply(Types.STRUCT);
	public final static OutParam TIME = apply(Types.TIME);
	public final static OutParam TIMESTAMP = apply(Types.TIMESTAMP);
	public final static OutParam TINYINT = apply(Types.TINYINT);
	public final static OutParam VARBINARY = apply(Types.VARBINARY);
	public final static OutParam VARCHAR = apply(Types.VARCHAR);

	// --------------------------JDBC 4.2 -----------------------------
	// public static final int REF_CURSOR = 2012;
	// public static final int TIME_WITH_TIMEZONE = 2013;
	// public static final int TIMESTAMP_WITH_TIMEZONE = 2014;

	public static OutParam apply(final int sqlType) {
		return new OutParam(sqlType);
	}

	public final int sqlType;

	private OutParam(final int sqlType) {
		this.sqlType = sqlType;
	}

}
