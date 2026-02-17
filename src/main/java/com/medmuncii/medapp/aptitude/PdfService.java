package com.medmuncii.medapp.aptitude;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.medmuncii.medapp.employee.Employee;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generateAptitudeSheetPdf(AptitudeSheet sheet) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Document in format Portrait A4 cu margini mici
            Document document = new Document(PageSize.A4, 15, 15, 15, 15);
            PdfWriter.getInstance(document, out);

            document.open();

            // Tabel principal cu 1 coloana pentru a le aseza vertical
            PdfPTable mainTable = new PdfPTable(1);
            mainTable.setWidthPercentage(100);
            mainTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            mainTable.setSpacingAfter(0);
            mainTable.setSpacingBefore(0);

            // Adaugam continutul de doua ori, fiecare intr-o celula separata
            // pentru a le aseza una sub alta
            PdfPCell sheet1Cell = new PdfPCell(createSheetContent(sheet));
            sheet1Cell.setBorder(Rectangle.NO_BORDER);
            sheet1Cell.setPadding(5); // Adjust padding as needed for vertical stacking
            mainTable.addCell(sheet1Cell);

            PdfPCell sheet2Cell = new PdfPCell(createSheetContent(sheet));
            sheet2Cell.setBorder(Rectangle.NO_BORDER);
            sheet2Cell.setPadding(5); // Adjust padding as needed for vertical stacking
            mainTable.addCell(sheet2Cell);

            document.add(mainTable);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Eroare la generarea PDF-ului", e);
        }
    }

    private PdfPTable createSheetContent(AptitudeSheet sheet) {
        // Cream un tabel intern pentru a avea border pe fiecare fisa separat
        PdfPTable innerTable = new PdfPTable(1);
        innerTable.setWidthPercentage(100);
        innerTable.setPaddingTop(5); // Spacing from previous sheet
        innerTable.setPaddingBottom(5); // Spacing for next sheet
        
        PdfPCell innerCell = new PdfPCell();
        innerCell.setBorder(Rectangle.BOX);
        innerCell.setPadding(10);
        innerCell.setBorderWidth(1.2f);
        innerCell.setMinimumHeight(390f); // Roughly half of A4 height (842pt) minus margins/padding
        innerCell.setVerticalAlignment(Element.ALIGN_TOP);

        // Fonturi
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.BLACK);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        Font italicFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.BLACK);

        // Header Dublu: Angajator Stanga, Cabinet Dreapta
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Angajator
        String companyName = sheet.getEmployee().getCompany() != null ? sheet.getEmployee().getCompany().getName() : "................................";
        String companyAddress = sheet.getEmployee().getCompany() != null ? sheet.getEmployee().getCompany().getAddress() : "................................";
        
        PdfPCell employerCell = new PdfPCell();
        employerCell.setBorder(Rectangle.NO_BORDER);
        employerCell.addElement(new Paragraph("Unitatea: " + companyName, boldFont));
        employerCell.addElement(new Paragraph("Adresa: " + companyAddress, smallFont));
        headerTable.addCell(employerCell);

        // Cabinet Medical
        PdfPCell clinicCell = new PdfPCell();
        clinicCell.setBorder(Rectangle.NO_BORDER);
        clinicCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph clinicPara = new Paragraph("CABINET MEDICAL\nMEDICINA MUNCII", boldFont);
        clinicPara.setAlignment(Element.ALIGN_RIGHT);
        clinicCell.addElement(clinicPara);
        headerTable.addCell(clinicCell);

        innerCell.addElement(headerTable);

        // Titlu
        Paragraph title = new Paragraph("\nFIȘĂ DE APTITUDINE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(10);
        title.setSpacingAfter(20);
        innerCell.addElement(title);

        // Date Angajat
        Employee emp = sheet.getEmployee();
        
        Paragraph empData = new Paragraph();
        empData.add(new Chunk("Subsemnatul(a): ", normalFont));
        empData.add(new Chunk(emp.getFirstname() + " " + emp.getLastname() + "\n", boldFont));
        
        empData.add(new Chunk("CNP: ", normalFont));
        empData.add(new Chunk((emp.getCnp() != null ? emp.getCnp() : "................................") + "\n", normalFont));
        
        empData.add(new Chunk("Angajat la unitatea de mai sus, în funcția: ", normalFont));
        empData.add(new Chunk(emp.getPosition() + "\n", boldFont));
        
        empData.setSpacingAfter(15);
        innerCell.addElement(empData);

        // Examen Medical
        Paragraph examData = new Paragraph();
        examData.add(new Chunk("În urma examenului medical din data de: ", normalFont));
        examData.add(new Chunk(sheet.getExamDate().format(DATE_FORMATTER) + "\n", boldFont));
        
        examData.setSpacingAfter(15);
        innerCell.addElement(examData);

        // Concluzie
        Paragraph conclusionLabel = new Paragraph("S-A STABILIT CONCLUZIA:", subTitleFont);
        conclusionLabel.setAlignment(Element.ALIGN_CENTER);
        innerCell.addElement(conclusionLabel);

        Paragraph conclusionValue = new Paragraph(sheet.getMedicalConclusion().toUpperCase(), titleFont);
        conclusionValue.setAlignment(Element.ALIGN_CENTER);
        conclusionValue.setSpacingBefore(10);
        conclusionValue.setSpacingAfter(20);
        innerCell.addElement(conclusionValue);

        // Observatii
        Paragraph obs = new Paragraph();
        obs.add(new Chunk("Recomandări / Observații: ", boldFont));
        if (sheet.getObservations() != null && !sheet.getObservations().isEmpty()) {
            obs.add(new Chunk(sheet.getObservations(), normalFont));
        } else {
            obs.add(new Chunk("Conform protocolului de medicina muncii.", italicFont));
        }
        obs.setSpacingAfter(20);
        innerCell.addElement(obs);

        // Footer / Semnatura
        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        PdfPCell nextExamCell = new PdfPCell();
        nextExamCell.setBorder(Rectangle.NO_BORDER);
        if (sheet.getNextExamDate() != null) {
            nextExamCell.addElement(new Paragraph("Următoarea examinare:", smallFont));
            nextExamCell.addElement(new Paragraph(sheet.getNextExamDate().format(DATE_FORMATTER), boldFont));
        }
        footerTable.addCell(nextExamCell);

        PdfPCell sigCell = new PdfPCell();
        sigCell.setBorder(Rectangle.NO_BORDER);
        Paragraph sig = new Paragraph("Semnătura și Parafa\nMedicului de Medicina Muncii", smallFont);
        sig.setAlignment(Element.ALIGN_CENTER);
        sigCell.addElement(sig);
        sigCell.addElement(new Paragraph("\n\n\n", smallFont)); // Spatiu pentru stampila
        footerTable.addCell(sigCell);

        innerCell.addElement(footerTable);

        innerTable.addCell(innerCell);
        cell.addElement(innerTable);
        return cell;
    }
}
