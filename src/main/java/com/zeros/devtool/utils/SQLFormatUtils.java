package com.zeros.devtool.utils;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import java.util.List;

public class SQLFormatUtils {

    private static final SQLParserFeature[]  FORMAT_DEFAULT_FEATURES = new SQLParserFeature[]{SQLParserFeature.KeepComments, SQLParserFeature.EnableSQLBinaryOpExprGroup};

    public static String format(String sql, DbType dbType) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, FORMAT_DEFAULT_FEATURES);
        List<SQLStatement> statementList = parser.parseStatementList();
        return SQLUtils.toSQLString(statementList, dbType, null, null);
    }
}
