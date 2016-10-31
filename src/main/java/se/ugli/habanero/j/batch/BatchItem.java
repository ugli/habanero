package se.ugli.habanero.j.batch;

import java.util.ArrayList;
import java.util.List;

public class BatchItem {

    public static class BatchItemBuilder {

        private final List<Object> args = new ArrayList<>();
        private final StringBuilder sqlBuilder = new StringBuilder();

        public BatchItemBuilder addArg(final Object arg) {
            args.add(arg);
            return this;
        }

        public BatchItemBuilder appendSql(final String sql) {
            sqlBuilder.append(sql);
            return this;
        }

        public BatchItem build() {
            return new BatchItem(sqlBuilder.toString(), args.toArray());
        }

        @Override
        public String toString() {
            return "BatchItemBuilder [sqlBuilder=" + sqlBuilder + ", args=" + args + "]";
        }

    }

    public final Object[] args;
    public final String sql;

    public BatchItem(final String sql, final Object... args) {
        this.sql = sql;
        this.args = args;
    }

}
