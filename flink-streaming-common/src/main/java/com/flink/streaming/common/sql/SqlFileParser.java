package com.flink.streaming.common.sql;


import com.flink.streaming.common.constant.SystemConstant;
import com.flink.streaming.common.enums.SqlCommand;
import com.flink.streaming.common.model.SqlCommandCall;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-06-23
 *
 * @time 02:22
 */
public class SqlFileParser {



    public static List<String>  parserSql(List<String> lineList){
        if (CollectionUtils.isEmpty(lineList)) {
            throw new RuntimeException("lineList is null");
        }
        List<String> sqlList = new ArrayList<>();
        StringBuilder stmt = new StringBuilder();
        for (String line : lineList) {
            //开头是 -- 的表示注释
            if (line.trim().isEmpty() || line.startsWith(SystemConstant.COMMENT_SYMBOL) ||
                trimStart(line).startsWith(SystemConstant.COMMENT_SYMBOL)) {
                continue;
            }
            stmt.append(SystemConstant.LINE_FEED).append(line);
            if (line.trim().endsWith(SystemConstant.SEMICOLON)) {
                sqlList.add(stmt.substring(0, stmt.length() - 1));
                //初始化
                stmt.setLength(0);
            }
        }
        return sqlList;
    }



    public static List<SqlCommandCall> fileToSql(List<String> lineList) {

        if (CollectionUtils.isEmpty(lineList)) {
            throw new RuntimeException("lineList is null");
        }

        List<SqlCommandCall> sqlCommandCallList = new ArrayList<>();

        StringBuilder stmt = new StringBuilder();

        for (String line : lineList) {
            //开头是 -- 的表示注释
            if (line.trim().isEmpty() || line.startsWith(SystemConstant.COMMENT_SYMBOL) ||
                    trimStart(line).startsWith(SystemConstant.COMMENT_SYMBOL)) {
                continue;
            }
            stmt.append(SystemConstant.LINE_FEED).append(line);
            if (line.trim().endsWith(SystemConstant.SEMICOLON)) {
                Optional<SqlCommandCall> optionalCall = parse(stmt.toString());
                if (optionalCall.isPresent()) {
                    sqlCommandCallList.add(optionalCall.get());
                } else {
                    throw new RuntimeException("不支持该语法使用" + stmt.toString() + "'");
                }
                stmt.setLength(0);
            }
        }

        return sqlCommandCallList;

    }

    private static Optional<SqlCommandCall> parse(String stmt) {
        stmt = stmt.trim();
        if (stmt.endsWith(SystemConstant.SEMICOLON)) {
            stmt = stmt.substring(0, stmt.length() - 1).trim();
        }
        for (SqlCommand cmd : SqlCommand.values()) {
            final Matcher matcher = cmd.getPattern().matcher(stmt);
            if (matcher.matches()) {
                final String[] groups = new String[matcher.groupCount()];
                for (int i = 0; i < groups.length; i++) {
                    groups[i] = matcher.group(i + 1);
                }
                return cmd.getOperandConverter().apply(groups)
                        .map((operands) -> new SqlCommandCall(cmd, operands));
            }
        }
        return Optional.empty();
    }


    private static String trimStart(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }
        final char[] value = str.toCharArray();

        int start = 0, last = str.length() - 1;
        int end = last;
        while ((start <= end) && (value[start] <= ' ')) {
            start++;
        }
        if (start == 0 && end == last) {
            return str;
        }
        if (start >= end) {
            return "";
        }
        return str.substring(start, end);
    }


}
