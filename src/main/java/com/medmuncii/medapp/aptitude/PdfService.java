package com.medmuncii.medapp.aptitude;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.medmuncii.medapp.employee.Employee;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generateAptitudeSheetPdf(AptitudeSheet sheet) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 25, 25, 15, 15);
            PdfWriter.getInstance(document, out);
            document.open();

            PdfPTable mainTable = new PdfPTable(1);
            mainTable.setWidthPercentage(100);

            PdfPCell sheet1Cell = new PdfPCell(createSheetContent(sheet));
            sheet1Cell.setBorder(Rectangle.NO_BORDER);
            sheet1Cell.setPaddingBottom(15);
            mainTable.addCell(sheet1Cell);

            PdfPCell divider = new PdfPCell(new Phrase("--------------------------------------------------------- Decupați aici (A5) ---------------------------------------------------------", 
                    FontFactory.getFont(FontFactory.HELVETICA, 6, Color.LIGHT_GRAY)));
            divider.setBorder(Rectangle.NO_BORDER);
            divider.setHorizontalAlignment(Element.ALIGN_CENTER);
            divider.setPaddingBottom(15);
            mainTable.addCell(divider);

            PdfPCell sheet2Cell = new PdfPCell(createSheetContent(sheet));
            sheet2Cell.setBorder(Rectangle.NO_BORDER);
            mainTable.addCell(sheet2Cell);

            document.add(mainTable);
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Eroare la generarea PDF-ului: " + e.getMessage(), e);
        }
    }

    private PdfPTable createSheetContent(AptitudeSheet sheet) {
        PdfPTable container = new PdfPTable(1);
        container.setWidthPercentage(100);
        
        PdfPCell containerCell = new PdfPCell();
        containerCell.setBorder(Rectangle.BOX);
        containerCell.setBorderWidth(0.8f);
        containerCell.setPadding(10);
        containerCell.setMinimumHeight(380f);

        Font titleFont, labelFont, valueFont, clinicFont, clinicBoldFont;
        try {
            BaseFont bf = BaseFont.createFont("C:\\Windows\\Fonts\\arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            titleFont = new Font(bf, 10, Font.BOLD);
            labelFont = new Font(bf, 8, Font.NORMAL);
            valueFont = new Font(bf, 8, Font.BOLD);
            clinicFont = new Font(bf, 6.5f, Font.NORMAL);
            clinicBoldFont = new Font(bf, 7.5f, Font.BOLD);
        } catch (Exception e) {
            titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            labelFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            valueFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            clinicFont = FontFactory.getFont(FontFactory.HELVETICA, 6.5f);
            clinicBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 7.5f);
        }

        Employee emp = sheet.getEmployee();

        // --- 1. HEADER CLINICA ---
        Paragraph cp = new Paragraph();
        cp.setLeading(9f);
        cp.add(new Chunk("CABINET MEDICAL MEDICINA MUNCII\n", clinicBoldFont));
        cp.add(new Chunk("Brașov, Calea București Nr. 9 Bl 43 Ap 1\n", clinicFont));
        cp.add(new Chunk("Tel: 0744496446 | Email: office@medicalprevent.ro\n", clinicFont));
        cp.add(new Chunk("Web: www.medicalprevent.ro | Orar: L-V: 09:00-17:00, S: 09:00-12:00", clinicFont));
        containerCell.addElement(cp);

        // --- 2. LINIE NEAGRA ---
        LineSeparator ls = new LineSeparator();
        ls.setLineWidth(1f);
        ls.setOffset(1f);
        containerCell.addElement(new Chunk(ls));

        // --- 3. TIP EXAMINARE (Orizontal - Perfect Aliniat) ---
        String examType = (sheet.getExamType() != null) ? sheet.getExamType() : "";
        PdfPTable typeTable = new PdfPTable(5);
        typeTable.setWidthPercentage(100);
        typeTable.setSpacingBefore(4f);
        try { 
            typeTable.setWidths(new float[]{15f, 25f, 18f, 27f, 15f}); 
        } catch (Exception e) {}
        
        addPerfectCheckCell(typeTable, "Angajare", examType.equals("Angajare"), labelFont, 0);
        addPerfectCheckCell(typeTable, "Control\nMedical Periodic", examType.equals("Control Medical Periodic"), labelFont, 0);
        addPerfectCheckCell(typeTable, "Reluare a muncii", examType.equals("Reluare a muncii"), labelFont, 0);
        addPerfectCheckCell(typeTable, "Supraveghere\nMedicală", examType.equals("Supraveghere Medicală"), labelFont, 0);
        addPerfectCheckCell(typeTable, "Alte", examType.equals("Alte"), labelFont, 0);
        containerCell.addElement(typeTable);

        // --- 4. TITLU CENTRAL ---
        String nrFisa = (sheet.getId() != null ? sheet.getId().toString() : "____");
        Paragraph title = new Paragraph("\nMEDICINA MUNCII FIȘĂ DE APTITUDINE NR: " + nrFisa, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(6);
        containerCell.addElement(title);

        // --- 5. SECTIUNE UNITATE ---
        PdfPTable unitTable = new PdfPTable(2);
        unitTable.setWidthPercentage(100);
        try { unitTable.setWidths(new float[]{20f, 80f}); } catch (Exception e) {}
        String companyName = (emp != null && emp.getCompany() != null) ? emp.getCompany().getName() : "________________________";
        String companyAddr = (emp != null && emp.getCompany() != null) ? emp.getCompany().getAddress() : "________________________";
        String companyPhone = (emp != null && emp.getCompany() != null) ? emp.getCompany().getPhone() : "________________________";
        addLabelValueRow(unitTable, "Societate / Unitate:", companyName, labelFont, valueFont);
        addLabelValueRow(unitTable, "Adresă:", companyAddr, labelFont, valueFont);
        addLabelValueRow(unitTable, "Tel / Fax:", companyPhone, labelFont, valueFont);
        containerCell.addElement(unitTable);

        // --- 6. CHENAR DATE ANGAJAT (Condensat, Prenume Aproape) ---
        PdfPTable empBoxTable = new PdfPTable(1);
        empBoxTable.setWidthPercentage(100);
        empBoxTable.setSpacingBefore(3);
        PdfPCell empBoxCell = new PdfPCell();
        empBoxCell.setBorder(Rectangle.BOX);
        empBoxCell.setPadding(3);
        empBoxCell.setBackgroundColor(new Color(253, 253, 253));

        PdfPTable nameTable = new PdfPTable(2);
        nameTable.setWidthPercentage(100);
        try { nameTable.setWidths(new float[]{45f, 55f}); } catch (Exception e) {}
        addLabelValueCell(nameTable, "Nume:", (emp != null ? emp.getLastname().toUpperCase() : ""), labelFont, valueFont);
        addLabelValueCell(nameTable, "Prenume:", (emp != null ? emp.getFirstname() : ""), labelFont, valueFont);
        empBoxCell.addElement(nameTable);

        PdfPTable otherDetails = new PdfPTable(1);
        otherDetails.setWidthPercentage(100);
        addLabelValueCell(otherDetails, "CNP:", (emp != null ? emp.getCnp() : ""), labelFont, valueFont);
        String pos = (emp != null) ? ((emp.getPosition() != null && !emp.getPosition().isEmpty()) ? emp.getPosition() : emp.getWorkplace()) : "";
        addLabelValueCell(otherDetails, "Funcție / Ocupație:", (pos != null ? pos : ""), labelFont, valueFont);
        addLabelValueCell(otherDetails, "Loc de muncă:", (emp != null && emp.getCompany() != null ? emp.getCompany().getName() : ""), labelFont, valueFont);
        empBoxCell.addElement(otherDetails);

        empBoxTable.addCell(empBoxCell);
        containerCell.addElement(empBoxTable);

        // --- 7. AVIZ MEDICAL ȘI RECOMANDĂRI ---
        PdfPTable footerGrid = new PdfPTable(2);
        footerGrid.setWidthPercentage(100);
        footerGrid.setSpacingBefore(10);
        try { footerGrid.setWidths(new float[]{45f, 55f}); } catch (Exception e) {}

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setPadding(0);
        leftCell.addElement(new Paragraph("AVIZ MEDICAL:", titleFont));
        
        String conclusion = (sheet.getMedicalConclusion() != null) ? sheet.getMedicalConclusion().toUpperCase() : "";
        PdfPTable avizList = new PdfPTable(1);
        avizList.setWidthPercentage(100);
        avizList.setSpacingBefore(2f);
        addPerfectCheckCell(avizList, "APT", conclusion.equals("APT"), labelFont, 2);
        addPerfectCheckCell(avizList, "APT CONDIȚIONAT", conclusion.equals("APT CONDITIONAT"), labelFont, 2);
        addPerfectCheckCell(avizList, "INAPT CONDIȚIONAT", conclusion.equals("INAPT CONDITIONAT"), labelFont, 2);
        addPerfectCheckCell(avizList, "INAPT", conclusion.equals("INAPT"), labelFont, 2);
        leftCell.addElement(avizList);
        
        Paragraph datesPara = new Paragraph();
        datesPara.setLeading(11f);
        datesPara.setSpacingBefore(8f);
        datesPara.add(new Chunk("Data examinării: ", labelFont));
        datesPara.add(new Chunk((sheet.getExamDate() != null ? sheet.getExamDate().format(DATE_FORMATTER) : "__________") + "\n", valueFont));
        datesPara.add(new Chunk("Data expirării:    ", labelFont));
        datesPara.add(new Chunk((sheet.getNextExamDate() != null ? sheet.getNextExamDate().format(DATE_FORMATTER) : "__________"), valueFont));
        leftCell.addElement(datesPara);
        footerGrid.addCell(leftCell);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setPadding(0);
        rightCell.addElement(new Paragraph("RECOMANDĂRI:", titleFont));
        
        Paragraph recoPara = new Paragraph();
        recoPara.setLeading(10f);
        recoPara.setSpacingBefore(5f);
        String obs = (sheet.getObservations() != null && !sheet.getObservations().isEmpty() ? sheet.getObservations() : "Conform protocolului medical.");
        recoPara.add(new Chunk(obs, valueFont));
        rightCell.addElement(recoPara);
        
        Paragraph sig = new Paragraph("\n\nSemnătura și Parafa Medicului", labelFont);
        sig.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(sig);
        footerGrid.addCell(rightCell);

        containerCell.addElement(footerGrid);
        container.addCell(containerCell);
        return container;
    }

    private void addLabelValueRow(PdfPTable table, String label, String value, Font lFont, Font vFont) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, lFont));
        lCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(lCell);
        PdfPCell vCell = new PdfPCell(new Phrase(value != null ? value : "", vFont));
        vCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(vCell);
    }

    private void addLabelValueCell(PdfPTable table, String label, String value, Font lFont, Font vFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(0.2f);
        Phrase p = new Phrase();
        p.add(new Chunk(label + " ", lFont));
        p.add(new Chunk(value != null ? value : "", vFont));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void addPerfectCheckCell(PdfPTable mainTable, String label, boolean checked, Font font, float verticalPadding) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingTop(verticalPadding);
        cell.setPaddingBottom(verticalPadding);
        cell.setVerticalAlignment(Element.ALIGN_TOP);

        PdfPTable subTable = new PdfPTable(2);
        try { 
            subTable.setWidths(new float[]{22f, 78f}); // Latime marita pentru checkbox ca sa nu faca wrap textul langa el
        } catch (Exception e) {}
        subTable.setWidthPercentage(100);

        PdfPCell boxCell = new PdfPCell(new Phrase(checked ? "[ X ]" : "[   ]", font));
        boxCell.setBorder(Rectangle.NO_BORDER);
        boxCell.setPadding(0);
        boxCell.setNoWrap(true); // OBLIGATORIU: [ ] nu se rupe niciodata de text
        boxCell.setVerticalAlignment(Element.ALIGN_TOP);
        subTable.addCell(boxCell);

        PdfPCell textCell = new PdfPCell(new Phrase(label, font));
        textCell.setBorder(Rectangle.NO_BORDER);
        textCell.setPadding(0);
        textCell.setLeading(8.5f, 0); 
        textCell.setVerticalAlignment(Element.ALIGN_TOP);
        subTable.addCell(textCell);

        cell.addElement(subTable);
        mainTable.addCell(cell);
    }
}
