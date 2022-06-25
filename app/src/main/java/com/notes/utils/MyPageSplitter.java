package com.notes.utils;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.ArrayList;
import java.util.List;

public class MyPageSplitter {

    private final String title, description;
    private final int pageWidth, pageHeight;
    private final float lineSpacingMultiplier, lineSpacingExtra;
    private final List<CharSequence> pages;

    public MyPageSplitter(String title, String description, int pageWidth, int pageHeight, float lineSpacingMultiplier, float lineSpacingExtra) {
        this.title = title;
        this.description = description;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.lineSpacingMultiplier = lineSpacingMultiplier;
        this.lineSpacingExtra = lineSpacingExtra;
        this.pages = new ArrayList<>();
    }

    public List<CharSequence> getPages() {
        String text = title + "\n" + description;
        StaticLayout staticLayout = new StaticLayout(text, new TextPaint(), pageWidth,
                Layout.Alignment.ALIGN_NORMAL, lineSpacingMultiplier, lineSpacingExtra, false);
        int lineHeight = staticLayout.getHeight() / staticLayout.getLineCount();
        int pageLineCount = pageHeight / lineHeight;
        int firstLine = 0;
        while (firstLine < staticLayout.getLineCount()) {
            if (firstLine >= staticLayout.getLineCount() - pageLineCount && firstLine < staticLayout.getLineCount()) {
                pages.add(text.subSequence(staticLayout.getLineStart(firstLine), staticLayout.getLineEnd(staticLayout.getLineCount() - 1)));
                break;
            }

            pages.add(text.subSequence(staticLayout.getLineStart(firstLine), staticLayout.getLineEnd(firstLine + pageLineCount - 1)));
            firstLine = firstLine + pageLineCount;
        }
        return pages;
    }

}
