package com.notes.utils;

import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class PDFDocumentUtils {

    public static File createPDFDocument(String title, String description) {
        PdfDocument pdfDocument = new PdfDocument();
        MyPageSplitter pageSplitter = new MyPageSplitter(title, description, Constants.PAGE_WIDTH, Constants.PAGE_HEIGHT);
        List<CharSequence> pdfPages = pageSplitter.getPages();
        for (int i=0;i<pdfPages.size();i++) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(Constants.PAGE_WIDTH, Constants.PAGE_HEIGHT, i+1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            StaticLayout staticLayout = new StaticLayout(pdfPages.get(i).toString(), new TextPaint(), Constants.PAGE_WIDTH - 2 * Constants.PAGE_PADDING, Layout.Alignment.ALIGN_NORMAL,
                    Constants.SPACING_MULT, Constants.SPACING_ADD, false);
            canvas.save();
            canvas.translate(Constants.PAGE_PADDING, Constants.PAGE_PADDING);
            staticLayout.draw(canvas);
            pdfDocument.finishPage(page);
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/Notes");
        if (!file.exists())
            file.mkdirs();
        File dir = new File(file, "Document.pdf");
        try{
            pdfDocument.writeTo(new FileOutputStream(dir));
            Log.i("pergjigja", "sakte");
        } catch (Exception e) {
            Log.i("pergjigja", e.toString());
        }
        pdfDocument.close();
        return dir;
    }

}
