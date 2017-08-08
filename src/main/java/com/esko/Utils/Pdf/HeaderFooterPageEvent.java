package com.esko.Utils.Pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HeaderFooterPageEvent extends PdfPageEventHelper {

	private PdfTemplate t;
	private Image total;

	public void onOpenDocument(PdfWriter writer, Document document) {
		t = writer.getDirectContent().createTemplate(30, 16);
		try {
			total = Image.getInstance(t);
		} catch (DocumentException de) {
			throw new ExceptionConverter(de);
		}
	}

	@Override
	public void onEndPage(PdfWriter writer, Document document) {
		addHeader(writer);
		addFooter(writer);
	}

	private void addHeader(PdfWriter writer) {
		PdfPTable header = new PdfPTable(2);
		try {
			header.setWidths(new int[] { 2, 24 });
			header.setTotalWidth(527);
			header.setLockedWidth(true);
			header.getDefaultCell().setFixedHeight(40);
			header.getDefaultCell().setBorder(Rectangle.BOTTOM);
			header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
			String resourcePath = "";
			try {
				resourcePath = getResourcePath();
			} catch (Exception e) {
			}
			Image logo = Image.getInstance(resourcePath+"/esko.png");
			header.addCell(logo);
			header.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			header.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			header.addCell(new Phrase("Spandana Report", new Font(Font.FontFamily.HELVETICA, 20)));
			header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
		} catch (DocumentException de) {
			throw new ExceptionConverter(de);
		} catch (MalformedURLException e) {
			throw new ExceptionConverter(e);
		} catch (IOException e) {
			throw new ExceptionConverter(e);
		}
	}

	private void addFooter(PdfWriter writer) {
		PdfPTable footer = new PdfPTable(3);
		try {
			footer.setWidths(new int[] { 24, 2, 1 });
			footer.setTotalWidth(527);
			footer.setLockedWidth(true);
			footer.getDefaultCell().setFixedHeight(40);
			footer.getDefaultCell().setBorder(Rectangle.TOP);
			footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
			footer.addCell(new Phrase("\u00A9 esko.com", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
			footer.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
			footer.addCell(new Phrase(String.format("Page %d of", writer.getPageNumber()),
					new Font(Font.FontFamily.HELVETICA, 8)));
			PdfPCell totalPageCount = new PdfPCell(total);
			totalPageCount.setBorder(Rectangle.TOP);
			totalPageCount.setBorderColor(BaseColor.LIGHT_GRAY);
			footer.addCell(totalPageCount);
			PdfContentByte canvas = writer.getDirectContent();
			footer.writeSelectedRows(0, -1, 34, 50, canvas);
		} catch (DocumentException de) {
			throw new ExceptionConverter(de);
		}
	}

	public void onCloseDocument(PdfWriter writer, Document document) {
		int totalLength = String.valueOf(writer.getPageNumber()).length();
		int totalWidth = totalLength * 5;
		ColumnText.showTextAligned(t, Element.ALIGN_RIGHT,
				new Phrase(String.valueOf(writer.getPageNumber()), new Font(Font.FontFamily.HELVETICA, 8)), totalWidth,
				6, 0);
	}
	
	  private String getResourcePath() throws Exception {
	       
	        try {
	        	ClassLoader classLoader = getClass().getClassLoader();
				URI resourcePathFile = classLoader.getResource("RESOURCE_PATH").toURI();
	            String resourcePath = Files.readAllLines(Paths.get(resourcePathFile)).get(0);
	            URI rootURI = new File("").toURI();
	            URI resourceURI = new File(resourcePath).toURI();
	            URI relativeResourceURI = rootURI.relativize(resourceURI);
	            return relativeResourceURI.getPath();
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	            throw e;
	        }
	    }
}