package com.notes.utils;

import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import java.util.List;

public class PDFDocumentUtils {

    public static PdfDocument createPDFDocument(String title, String description) {
        PdfDocument pdfDocument = new PdfDocument();
        MyPageSplitter pageSplitter = new MyPageSplitter(title, description, Constants.PAGE_WIDTH, Constants.PAGE_HEIGHT);
        List<CharSequence> pdfPages = pageSplitter.getPages();
        for (int i=0;i<pdfPages.size();i++) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(Constants.PAGE_WIDTH, Constants.PAGE_HEIGHT, i+1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.save();
            canvas.translate(Constants.PAGE_PADDING, Constants.PAGE_PADDING);
            if (i == 0) {
                TextPaint titleText = new TextPaint();
                titleText.setTextSize(25);
                titleText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                StaticLayout titleStaticLayout = new StaticLayout(title, titleText, Constants.PAGE_WIDTH - 2 * Constants.PAGE_PADDING, Layout.Alignment.ALIGN_NORMAL,
                        Constants.SPACING_MULT, Constants.SPACING_ADD, false);
                titleStaticLayout.draw(canvas);
                canvas.translate(0, titleStaticLayout.getHeight());
            }
            StaticLayout staticLayout = new StaticLayout(pdfPages.get(i).toString(), new TextPaint(), Constants.PAGE_WIDTH - 2 * Constants.PAGE_PADDING, Layout.Alignment.ALIGN_NORMAL,
                    Constants.SPACING_MULT, Constants.SPACING_ADD, false);
            staticLayout.draw(canvas);
            pdfDocument.finishPage(page);
        }
        return pdfDocument;
    }

}
