package com.notes.utils;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import java.util.ArrayList;
import java.util.List;

public class MyPageSplitter {

    private final String title, description;
    private final int pageWidth, pageHeight;
    private final List<CharSequence> pages;

    public MyPageSplitter(String title, String description, int pageWidth, int pageHeight) {
        this.title = title;
        this.description = description;
        this.pageWidth = pageWidth - 100;
        this.pageHeight = pageHeight - 100;
        this.pages = new ArrayList<>();
    }

    public List<CharSequence> getPages() {
        TextPaint titleText = new TextPaint();
        titleText.setTextSize(25);
        StaticLayout titleStaticLayout = new StaticLayout(title, titleText, pageWidth,
                Layout.Alignment.ALIGN_NORMAL, Constants.SPACING_MULT, Constants.SPACING_ADD, false);
        StaticLayout contentStaticLayout = new StaticLayout(description, new TextPaint(), pageWidth,
                Layout.Alignment.ALIGN_NORMAL, Constants.SPACING_MULT, Constants.SPACING_ADD, false);
        int titleHeight = titleStaticLayout.getHeight();
        int lineHeight = contentStaticLayout.getHeight() / contentStaticLayout.getLineCount();
        int pageLineCount = pageHeight / lineHeight;
        int firstLine = 0;
        int firstPageLines = (pageHeight - titleHeight) / lineHeight;
        pages.add(description.subSequence(firstLine, contentStaticLayout.getLineEnd(Math.min(firstPageLines - 1, contentStaticLayout.getLineCount()))));
        firstLine = firstLine + firstPageLines;
        while (firstLine < contentStaticLayout.getLineCount()) {
            if (firstLine >= contentStaticLayout.getLineCount() - pageLineCount && firstLine < contentStaticLayout.getLineCount()) {
                pages.add(description.subSequence(contentStaticLayout.getLineStart(firstLine), contentStaticLayout.getLineEnd(contentStaticLayout.getLineCount() - 1)));
                break;
            }

            pages.add(description.subSequence(contentStaticLayout.getLineStart(firstLine), contentStaticLayout.getLineEnd(firstLine + pageLineCount - 1)));
            firstLine = firstLine + pageLineCount;
        }
        return pages;
    }

}
