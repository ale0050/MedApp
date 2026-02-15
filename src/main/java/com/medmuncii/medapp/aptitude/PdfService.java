package com.medmuncii.medapp.aptitude;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.medmuncii.medapp.employee.Employee;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateAptitudeSheetPdf(AptitudeSheet sheet) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            // Fonturi
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);

            // Titlu
            Paragraph title = new Paragraph("FIȘĂ DE APTITUDINE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Date Angajator
            if (sheet.getEmployee().getCompany() != null) {
                document.add(new Paragraph("Angajator: " + sheet.getEmployee().getCompany().getName(), boldFont));
                document.add(new Paragraph("Adresa: " + sheet.getEmployee().getCompany().getAddress(), normalFont));
            } else {
                 document.add(new Paragraph("Angajator: Necunoscut", boldFont));
            }
            document.add(Chunk.NEWLINE);

            // Date Angajat
            Employee emp = sheet.getEmployee();
            document.add(new Paragraph("Nume si Prenume: " + emp.getFirstname() + " " + emp.getLastname(), boldFont));
            document.add(new Paragraph("CNP: " + emp.getCnp(), normalFont));
            document.add(new Paragraph("Functia/Meseria: " + emp.getPosition(), normalFont));
            document.add(Chunk.NEWLINE);

            // Examen Medical
            document.add(new Paragraph("Data examinarii: " + 
                sheet.getExamDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), normalFont));
            
            if (sheet.getNextExamDate() != null) {
                document.add(new Paragraph("Urmatoarea examinare: " + 
                    sheet.getNextExamDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), normalFont));
            }
            document.add(Chunk.NEWLINE);

            // Concluzie
            document.add(new Paragraph("CONCLUZIA MEDICALA: " + sheet.getMedicalConclusion(), titleFont));
            document.add(Chunk.NEWLINE);

            if (sheet.getObservations() != null && !sheet.getObservations().isEmpty()) {
                document.add(new Paragraph("Observatii: " + sheet.getObservations(), normalFont));
            }

            // Semnatura
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            Paragraph signature = new Paragraph("Semnatura si Parafa Medicului", normalFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            document.add(signature);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Eroare la generarea PDF-ului", e);
        }
    }
}
