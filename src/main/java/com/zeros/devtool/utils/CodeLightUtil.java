package com.zeros.devtool.utils;


import com.zeros.devtool.constants.CssConstants;
import com.zeros.devtool.enums.CodeTypeEnum;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeLightUtil {


    private static final String[] SQL_KEYWORDS = new String[]{
            "ADD", "CONSTRAINT", "ALTER", "COLUMN", "TABLE",
            "ALL", "AND", "ANY", "AS", "ASC",
            "BACKUP", "DATABASE", "BETWEEN", "CASE", "CHECK",
            "COLUMN", "CONSTRAINT", "CREATE", "INDEX", "PROCEDURE",
            "UNIQUE", "VIEW", "DEFAULT", "DELETE", "DESC",
            "DISTINCT", "DROP", "EXEC", "EXISTS", "FOREIGN",
            "KEY", "FROM", "FULL", "OUTER", "JOIN",
            "GROUP", "BY", "HAVING", "IN", "INSERT",
            "INTO", "SELECT", "IS", "NULL", "NOT",
            "LEFT", "LIKE", "LIMIT", "OR", "ORDER",
            "BY", "KEY", "RIGHT", "ROWNUM", "TOP",
            "SET", "TRUNCATE", "UNION", "UPDATE", "VALUES",
            "WHERE"
    };

    private static final String SQL_KEYWORD_PATTERN = "\\b(" + String.join("|", SQL_KEYWORDS) + ")\\b";


    public static final String COMMENT_PATTERN = "#[^\n]*";

    public static final String LINE_COMMENT_PATTERN = "--[^\n]*";

    public static final String SQL_COMMENT_PATTERN = COMMENT_PATTERN + "|" + LINE_COMMENT_PATTERN;

    private static final String XML_TAG = "^([a-zA-Z]+-?)+[a-zA-Z0-9]+\\\\.[x|X][m|M][l|L]$";

    private static final String HTML_TAG = "(<(\\S*?)[^>]*>.*?|<.*? />)";

    private static final String HTML_COMMENT = "<!--.*?-->";

    public static final String HOST_KEYWORD_PATTERN =
            "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\b";


    public static final Pattern SQL_PATTERN = Pattern
            .compile("(?<KEYWORD>" + SQL_KEYWORD_PATTERN + ")" + "|(?<COMMENT>" + SQL_COMMENT_PATTERN + ")", Pattern.CASE_INSENSITIVE|Pattern.MULTILINE);

    public static final Pattern HOST_PATTERN = Pattern
            .compile("(?<KEYWORD>" + HOST_KEYWORD_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")");


    private static final Pattern XML_PATTERN = Pattern.compile("(?<TAG>" + XML_TAG + ")"
            + "|(?<COMMENT>" + HTML_COMMENT + ")");


    private static final Pattern HTML_PATTERN = Pattern.compile("(?<TAG>" + HTML_TAG + ")"
            + "|(?<COMMENT>" + HTML_COMMENT + ")");


    public static void setCodeLight(String text, CodeArea codeArea, CodeTypeEnum codeTypeEnum) {
        codeArea.getStylesheets().add(CssLoadUtil.getResourceUrl(CssConstants.CODE_LIGHT_CSS));

        switch (codeTypeEnum) {
            case XML:
            case HTML:
                codeArea.setStyleSpans(0, CodeLightUtil.computeHtmlHighlighting(text));
                break;
            case SQL:
                codeArea.setStyleSpans(0, CodeLightUtil.computeSQLHighlighting(text));
                break;
            case HOST:
                codeArea.setStyleSpans(0, CodeLightUtil.computeHostHighlighting(text));
                break;
            default:
                throw new IllegalArgumentException("not support " + codeTypeEnum.toString());

        }


    }


    public static StyleSpans<Collection<String>> computeHostHighlighting(String text) {
        Matcher matcher = HOST_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "hostKeyword" :
                            matcher.group("COMMENT") != null ? "hostComment" :
                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


    public static StyleSpans<Collection<String>> computeHtmlHighlighting(String text) {
        Matcher matcher = HTML_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("TAG") != null ? "htmlKeyword" :
                            matcher.group("COMMENT") != null ? "htmlComment" :
                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


    public static StyleSpans<Collection<String>> computeXMLHighlighting(String text) {
        Matcher matcher = XML_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("TAG") != null ? "xmlKeyword" :
                            matcher.group("COMMENT") != null ? "xmlComment" :
                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }


    public static StyleSpans<Collection<String>> computeSQLHighlighting(String text) {
        Matcher matcher = SQL_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "sqlKeyword" :
                            matcher.group("COMMENT") != null ? "sqlComment" :
                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
