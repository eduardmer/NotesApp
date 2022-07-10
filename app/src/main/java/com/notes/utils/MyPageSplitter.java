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
        StaticLayout staticLayout = new StaticLayout(description, new TextPaint(), pageWidth,
                Layout.Alignment.ALIGN_NORMAL, Constants.SPACING_MULT, Constants.SPACING_ADD, false);
        int titleHeight = titleStaticLayout.getHeight();
        int lineHeight = staticLayout.getHeight() / staticLayout.getLineCount();
        int pageLineCount = pageHeight / lineHeight;
        int firstLine = 0;
        int firstPageLines = (pageHeight - titleHeight) / lineHeight;
        pages.add(description.subSequence(firstLine, staticLayout.getLineEnd(firstPageLines - 1)));
        firstLine = firstLine + firstPageLines;
        while (firstLine < staticLayout.getLineCount()) {
            if (firstLine >= staticLayout.getLineCount() - pageLineCount && firstLine < staticLayout.getLineCount()) {
                pages.add(description.subSequence(staticLayout.getLineStart(firstLine), staticLayout.getLineEnd(staticLayout.getLineCount() - 1)));
                break;
            }

            pages.add(description.subSequence(staticLayout.getLineStart(firstLine), staticLayout.getLineEnd(firstLine + pageLineCount - 1)));
            firstLine = firstLine + pageLineCount;
        }
        return pages;
    }

}
