package com.esko.Utils.Pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import com.esko.Controllers.ReportController;
import com.esko.FrontEndObjects.AbsentDetails;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class FirstPdf {
	final static Logger LOGGER = Logger.getLogger(ReportController.class.getName());
	
	public String generatePDF(String File, List<AbsentDetails> empList) {
		try {
			String resourcePath = getResourcePath();
			String outputFile = resourcePath+File;
			Document document = new Document(PageSize.A4, 36, 36, 120, 60);
			File file = new File(outputFile);
			if (file.delete()) {
				LOGGER.info(file.getName() + " is deleted!");
			} else {
				LOGGER.info("Delete operation is failed.");
			}
			file = new File(outputFile);
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			HeaderFooterPageEvent event = new HeaderFooterPageEvent();
			writer.setPageEvent(event);
			document.open();
			addMetaData(document);
			addTitlePage(document);
			addContent(document, empList);
			document.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return File;
	}

	// iText allows to add metadata to the PDF which can be viewed in your Adobe
	// Reader
	// under File -> Properties
	private static void addMetaData(Document document) {
		document.addTitle("GRC Reconciliation Report");
		document.addSubject("Using iText");
		document.addKeywords("Java, PDF, iText");
		document.addAuthor("EKMA");
		document.addCreator("EKMA");
	}

	private static void addTitlePage(Document document) throws DocumentException, MalformedURLException, IOException {
		PdfPTable table = new PdfPTable(1);

		PdfPCell c1 = new PdfPCell();
		Paragraph p = new Paragraph(
				new Phrase("GRC Reconciliation Report", new Font(Font.FontFamily.TIMES_ROMAN, 35, Font.BOLD)));
		p.setAlignment(Element.ALIGN_CENTER);
		p.setSpacingAfter(30);
		c1.addElement(p);
		c1.setBackgroundColor(new BaseColor(140, 221, 8));
		table.addCell(c1);
		table.setTotalWidth(30);
		document.add(table);
		// Start a new page
		document.newPage();
	}

	private static void addContent(Document document, java.util.List<AbsentDetails> empList) throws DocumentException {
		// add a table // now add all this to the document
		PdfPTable table = createTable(empList);
		document.add(table);
	}

	private static PdfPTable createTable(java.util.List<AbsentDetails> empList) throws BadElementException {
		ReportController sp = new ReportController();
		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		String[] header = null;
		header = PropertiesFile.readProperties("spandana.properties").getProperty("ReportHeaders").split(",");

		PdfPCell c1 = new PdfPCell(new Phrase(header[0], new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBackgroundColor(new BaseColor(140, 221, 8));
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(header[1], new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBackgroundColor(new BaseColor(140, 221, 8));
		table.addCell(c1);

		c1 = new PdfPCell(new Phrase(header[2], new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBackgroundColor(new BaseColor(140, 221, 8));
		table.addCell(c1);
		c1 = new PdfPCell(new Phrase(header[3], new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBackgroundColor(new BaseColor(140, 221, 8));
		table.addCell(c1);
		c1 = new PdfPCell(new Phrase(header[4], new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		c1.setBackgroundColor(new BaseColor(140, 221, 8));
		table.addCell(c1);

		DateFormat srcDf = new SimpleDateFormat("yyyy-MM-dd");
		table.setHeaderRows(1);
		for (AbsentDetails emp : empList) {
			table.addCell(emp.getEmployeeID());
			table.addCell(emp.getManagerID());
			table.addCell(emp.getDepartment());
			table.addCell(emp.getDay());
			table.addCell(srcDf.format(sp.getDate(emp.getAbsentRecordDate()).getTime()));
			table.addCell(emp.getCheckIn());
			table.addCell(emp.getCheckOut());
			table.addCell(emp.getRemarks());
		}
		return table;
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
            throw e;
        }
    }

}