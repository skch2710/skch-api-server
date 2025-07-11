package com.skch.skch_api_server.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.skch.skch_api_server.common.Constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfHelper {

	public static Font headerFont = new Font(FontFamily.TIMES_ROMAN, 14, Font.BOLD, new BaseColor(0, 0, 0));

	public static Font poppinsFontColor = FontFactory.getFont("Poppins", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 14,
			Font.BOLD, new BaseColor(135, 206, 250));

	public static Font getPoppinsBoldFont(int size, BaseColor color) {
		if(color == null) {
			color = new BaseColor(0,0,0); //default as black colour
		}
		return FontFactory.getFont("Poppins", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, size, Font.BOLD, color);
	}
	
	public static Font getPoppinsFont(int size, BaseColor color) {
		if(color == null) {
			color = new BaseColor(0,0,0); //default as black colour
		}
		return FontFactory.getFont("Poppins", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, size, Font.NORMAL, color);
	}

	public static Font getFont(int size, int style, BaseColor color) {
		return new Font(FontFamily.TIMES_ROMAN, size, style, color);
	}

	public static Image createImage(String filename) throws Exception {
		Image image;
		try {
			// Create an Image object
			image = Image.getInstance(filename);
			image.setAlignment(Image.ALIGN_CENTER);
			// Set position and size of the image
			image.scalePercent(30f);
		} catch (Exception e) {
			log.error("error in createImage", e);
			throw new Exception(e);
		}
		return image;
	}
	
	public static void createLogo(PdfPTable table,String path,float left,
			float top,float right,float heigt,int alignment) throws Exception {
		try {
			// Create an Image object
			Image image = Image.getInstance(path);
			PdfPCell cell = new PdfPCell(image);
			cell.setPaddingLeft(left);
			cell.setPaddingTop(top);
			cell.setPaddingRight(right);
			cell.setFixedHeight(heigt);
			cell.setHorizontalAlignment(alignment);
//			cell.setBorderColor(BaseColor.GREEN);
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
		} catch (Exception e) {
			log.error("error in createImage", e);
			throw new Exception(e);
		}
	}
	
	public static void imageBgm(String path,PdfPTable table,PdfPCell cell,float hieht) throws Exception {
		try {
			Image image = Image.getInstance(path);
			cell.setFixedHeight(hieht); //check once
			cell.setCellEvent(new ImageBackgroundEvent(image));
//			cell.setBorderColor(BaseColor.RED);
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
		} catch (Exception e) {
			log.error("error in createImage", e);
			throw new Exception(e);
		}
	}
	

	public static Image createImage(byte[] imageData) {
		Image image = null;
		try {
			// Create an Image object
			image = Image.getInstance(imageData);
//			image.setAbsolutePosition(width, hight);
//			image.setAlignment(Image.ALIGN_CENTER);
//			image.scaleToFit(width, hight);
			// Set position and size of the image
//			image.scalePercent(100f);
		} catch (Exception e) {
			log.error("error in createImage", e);
		}
		return image;
	}
	
	public static PdfPTable createTable(int size, float spacingBefore, float spacingAfter, int width) {
		PdfPTable table = new PdfPTable(size);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.setSpacingBefore(spacingBefore);
		table.setSpacingAfter(spacingAfter);
		table.setWidthPercentage(width);
		return table;
	}

	public static PdfPTable createNoBorderTable(int size, float spacingBefore, float spacingAfter, int width) {
		PdfPTable table = new PdfPTable(size);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		table.getDefaultCell().setBorder(1);
		table.getDefaultCell().setBorderColor(new BaseColor(245,245,245));
		table.setSpacingBefore(spacingBefore);
		table.setSpacingAfter(spacingAfter);
		table.setWidthPercentage(width);
		return table;
	}

	public static Paragraph createParagraph(String title, int spacingBefore, int spacingAfter) {
		Paragraph paragraph = new Paragraph(title, poppinsFontColor);
		paragraph.setSpacingBefore(spacingBefore);
		paragraph.setSpacingAfter(spacingAfter);
		return paragraph;
	}
	
	public static Paragraph createParagraph(String title, int spacingBefore, int spacingAfter,Font font) {
		Paragraph paragraph = new Paragraph(title, font);
		paragraph.setSpacingBefore(spacingBefore);
		paragraph.setSpacingAfter(spacingAfter);
		return paragraph;
	}

	public static void headerCell(PdfPTable table, String text,BaseColor backgroundColor,Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
//		cell.setFixedHeight(32f);
		cell.setPaddingTop(5);
		cell.setPaddingBottom(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setBackgroundColor(backgroundColor);
		table.addCell(cell);
	}

	public static void noBorderCell(PdfPTable table, String text,int size,BaseColor color,int pading,int alignment) {
		PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", getPoppinsFont(size,color)));
		cell.setPaddingTop(pading);
		cell.setPaddingBottom(pading);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(alignment);
		table.addCell(cell);
	}

	public static void noBorderCell(PdfPTable table, String text,Font font,int pading,int alignment, int colspan,int paddingLeft) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setColspan(colspan);
		cell.setPaddingTop(pading);
		cell.setPaddingBottom(pading);
		cell.setPaddingLeft(paddingLeft);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(alignment);
		table.addCell(cell);
	}

	public static void createPdfPCell(PdfPTable table, String content, Font font, int padding,
			int horizontalAlignment) {
		PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "", font));
		cell.setPaddingTop(padding);
		cell.setPaddingBottom(padding);
		cell.setHorizontalAlignment(horizontalAlignment);
		table.addCell(cell);
	}
	
	//This is for grid Component
	public static void createPdfPCell(PdfPTable table, Object content) {
		PdfPCell cell = new PdfPCell(new Phrase(content != null ? content.toString() : "",
											getPoppinsFont(6, null)));
		cell.setPaddingTop(5);
		cell.setPaddingBottom(5);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}

	public static void createPdfPCell(PdfPTable table, String content, Font font, int padding, int paddingLeft,
			int horizontalAlignment) {
		PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "", font));
		cell.setPaddingTop(padding);
		cell.setPaddingBottom(padding);
		cell.setPaddingLeft(paddingLeft);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(horizontalAlignment);
		table.addCell(cell);
	}

	public static String numberConvert(Long number) {
		return number != null ? number.toString() : "";
	}

	public static String numberConvert(BigDecimal number) {
		return number != null ? number.toString() : "";
	}

	public static String dateConvert(LocalDate date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		return date != null ? date.format(formatter) : "";
	}

	public static String numberFormatGrid(BigDecimal number) {

		if (number != null && number.compareTo(BigDecimal.ZERO) > 0) {
			return new DecimalFormat("$ #,##0.00").format(number);
		} else if (number != null && number.compareTo(BigDecimal.ZERO) < 0) {
			number = number.abs();
			return new DecimalFormat("$( #,##0.00 )").format(number);
		} else {
			return "$ 0.00";
		}

	}
	
	public static String numberFormat(Long number) {
		return number != null ? new DecimalFormat("$ #,##0").format(number) : "$ 0.00";
	}
	
	public static void createPdfPCell(PdfPTable table, LineSeparator ls) {
		PdfPCell cell = new PdfPCell();
		ls.setLineColor(BaseColor.BLACK);
		cell.addElement(new Chunk(ls));
		cell.setPaddingTop(0);
		cell.setPaddingBottom(10);
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}
	
	public static void cellBottomBorder(PdfPTable table, String content, Font font, int paddingTop,int paddingBottom, int horizontalAlignment) {
		 PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "", font));
		    cell.setPaddingTop(paddingTop);
		    cell.setPaddingBottom(paddingBottom);
		    cell.setBorder(Rectangle.BOTTOM); // Set only the bottom border
		    cell.setBorderColorBottom(BaseColor.BLACK); // Set the color of the bottom border
		    cell.setBorderWidthBottom(2f); // Set the width of the bottom border
		    cell.setHorizontalAlignment(horizontalAlignment);
		    table.addCell(cell);
	}
	
	public static void cellBottomTopBorder(PdfPTable table, String content, Font font, int paddingTop,int paddingBottom, int horizontalAlignment) {
		 PdfPCell cell = new PdfPCell(new Phrase(content != null ? content : "", font));
		    cell.setPaddingTop(paddingTop);
		    cell.setPaddingBottom(paddingBottom);
		    cell.setBorder(Rectangle.BOTTOM | Rectangle.TOP); 
		    cell.setBorderColorBottom(BaseColor.BLACK); 
		    cell.setBorderColorTop(BaseColor.BLACK); 
		    cell.setBorderWidthTop(2f); 
		    cell.setBorderWidthBottom(2f); 
		    cell.setHorizontalAlignment(horizontalAlignment);
		    cell.setBackgroundColor(new BaseColor(242, 242, 242));
		    table.addCell(cell);
	}
	
	public static void lineSeparator(Document document,String data){
		try {
			LineSeparator ls = new LineSeparator();
			ls.setOffset(5);

			PdfPTable lineTable = PdfHelper.createNoBorderTable(3, 5, 5, 80);
			lineTable.setTotalWidth(new float[] { 35, 20, 35 });
			PdfHelper.createPdfPCell(lineTable, ls);
			PdfHelper.noBorderCell(lineTable, data, 12,new BaseColor(0,0,0) , 5, Element.ALIGN_CENTER);
			PdfHelper.createPdfPCell(lineTable, ls);

			document.add(lineTable);
		} catch (Exception e) {
			log.error("Error in createLineSeparator :: ", e);
		}
	}
	
	public static Image getQRCode(String data, int width,int height) {
		Image qrCodeImage = null;
		try {
			BarcodeQRCode qrCode = new BarcodeQRCode(data, width, height, null);
			qrCodeImage = qrCode.getImage();
	        qrCodeImage.scaleAbsolute(20, 20);
		} catch (Exception e) {
			log.error("Error in getQRCode :: ", e);
		}
		return qrCodeImage;
	}
	
	public static void getQRCode(PdfPTable table,String data,float left,
			float top,float right,float heigt,int alignment) {
		try {
			BarcodeQRCode qrCode = new BarcodeQRCode(data, 1000, 1000, null);
			Image qrCodeImage = qrCode.getImage();
			qrCodeImage.scaleAbsolute(heigt, heigt);
			PdfPCell cell = new PdfPCell(qrCodeImage);
			cell.setPaddingLeft(left);
			cell.setPaddingTop(top);
			cell.setPaddingRight(right);
			cell.setFixedHeight(heigt);
			cell.setHorizontalAlignment(alignment);
//			cell.setBorderColor(BaseColor.GREEN);
			cell.setBorder(Rectangle.NO_BORDER);
			table.addCell(cell);
		} catch (Exception e) {
			log.error("Error in getQRCode :: ", e);
		}
	}
	
	public static <T> void gridComponent(List<T> dataList, List<String> headers,
            List<String> fieldNames, String gridName, Document document) {
		try {
			Paragraph tableTitle = createParagraph(gridName + " (" + 
						NumberFormats.numFormart(dataList.size(),Constant.QTY_FORMAT) + ")",
					14, 5, getPoppinsFont(12, new BaseColor(255, 165, 0)));
			document.add(tableTitle);

			PdfPTable mainTable = createTable(headers.size(), 5, 5, 100);
//		mainTable.setTotalWidth(new float[] { 10, 14, 10, 4, 8, 8, 12, 10, 10, 8, 6 });

			// Add table headers
			headers.forEach(header -> {
				headerCell(mainTable, header, new BaseColor(229, 242, 255),
						getPoppinsFont(8, null));
			});

			for (int i = 0; i < dataList.size(); i++) {
				T item = dataList.get(i);
				for (int j = 0; j < fieldNames.size(); j++) {
					String fieldName = fieldNames.get(j);
					Field field = item.getClass().getDeclaredField(fieldNames.get(j));
					field.setAccessible(true);
					Object value = field.get(item);
					if (ExcelUtil.DATE_FIELDS.contains(fieldName)) {
						String cellValue = DateUtility.objToString(value,Constant.DATE_FORMAT);
						createPdfPCell(mainTable, cellValue);
					}else if (ExcelUtil.QTY_FIELDS.contains(fieldName)) {
						String cellValue = NumberFormats.numFormart(value,Constant.QTY_FORMAT);
						createPdfPCell(mainTable, cellValue);
					} else if (ExcelUtil.CURRENCY_FIELDS.contains(fieldName)) {
						String cellValue = NumberFormats.numFormart(value,Constant.D_CURRENCY_FORMAT_NEGITIVE);
						createPdfPCell(mainTable, cellValue);
					}else {
						createPdfPCell(mainTable, value);
					}
				}
			}

			document.add(mainTable);
			
		} catch (Exception e) {
			log.error("Error in Pdf Grid Component :: ",e);
		}
	}
	
}
