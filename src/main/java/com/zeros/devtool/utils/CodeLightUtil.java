package com.zeros.devtool.utils;

import com.zeros.devtool.constants.Constants;
import com.zeros.devtool.constants.CssConstants;
import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeLightUtil {


    public static final String HOST_KEYWORD_PATTERN =
            "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}\\b";

    public static final String HOST_COMMENT_PATTERN = "#[^\n]*";

    public static final Pattern HOST_PATTERN = Pattern
            .compile("(?<KEYWORD>" + HOST_KEYWORD_PATTERN + ")" + "|(?<COMMENT>" + HOST_COMMENT_PATTERN + ")");


    private static final String HTML_TAG = "(<(\\S*?)[^>]*>.*?|<.*? />)";

    private static final String HTML_COMMENT = "<!--.*?-->";

    private static final Pattern HTML_PATTERN = Pattern.compile("(?<TAG>" + HTML_TAG + ")"
            + "|(?<COMMENT>"+HTML_COMMENT+")");

    private final static ExecutorService executor = Executors.newSingleThreadExecutor();


    public static void setCodeLight(CodeArea codeArea, Task<StyleSpans<Collection<String>>> task) {
        codeArea.getStylesheets().add(CssLoadUtil.getResourceUrl(CssConstants.CODE_LIGHT_CSS));
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(new Supplier<Task<StyleSpans<Collection<String>>>>() {
                    @Override
                    public Task<StyleSpans<Collection<String>>> get() {
                        executor.execute(task);
                        return task;
                    }
                })
                .awaitLatest(codeArea.richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(new Consumer<StyleSpans<Collection<String>>>() {
                    @Override
                    public void accept(StyleSpans<Collection<String>> highlighting) {
                        codeArea.setStyleSpans(0, highlighting);
                    }
                });
    }

    public static void setHostLight(CodeArea codeArea) {
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                String text = codeArea.getText();
                return computeHostHighlighting(text);
            }
        };
        setCodeLight(codeArea, task);
    }


    private static StyleSpans<Collection<String>> computeHostHighlighting(String text) {
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


    public static void setHtmlLight(CodeArea codeArea) {
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                String text = codeArea.getText();
                return computeHtmlHighlighting(text);
            }
        };
        setCodeLight(codeArea, task);
    }

    private static StyleSpans<Collection<String>> computeHtmlHighlighting(String text) {
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
}
