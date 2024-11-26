package com.skch.skch_api_server.util;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

public class ImageBackgroundEvent implements PdfPCellEvent {
    private Image image;

    public ImageBackgroundEvent(Image image) {
        this.image = image;
    }

    @Override
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
    	try {
        PdfContentByte canvas = canvases[PdfPTable.BACKGROUNDCANVAS];
        image.scaleAbsolute(position.getWidth(), position.getHeight());
        image.setAbsolutePosition(position.getLeft(), position.getBottom());
            canvas.addImage(image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
