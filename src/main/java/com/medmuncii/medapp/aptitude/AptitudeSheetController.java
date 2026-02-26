package com.medmuncii.medapp.aptitude;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/aptitude-sheets")
public class AptitudeSheetController {

    private final AptitudeSheetService aptitudeSheetService;

    private final AptitudeSheetRepository aptitudeSheetRepository;
    private final PdfService pdfService;

    public AptitudeSheetController(AptitudeSheetService aptitudeSheetService, 
                                   AptitudeSheetRepository aptitudeSheetRepository,
                                   PdfService pdfService) {
        this.aptitudeSheetService = aptitudeSheetService;
        this.aptitudeSheetRepository = aptitudeSheetRepository;
        this.pdfService = pdfService;
    }

    @GetMapping
    public List<AptitudeSheet> getAll() {
        return aptitudeSheetService.getAllSheets();
    }

    @GetMapping("/employee/{employeeId}")
    public List<AptitudeSheet> getByEmployee(@PathVariable Long employeeId) {
        return aptitudeSheetService.getSheetsByEmployee(employeeId);
    }

    @PostMapping
    public AptitudeSheet create(@RequestBody AptitudeSheet sheet) {
        return aptitudeSheetService.createSheet(sheet);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        aptitudeSheetRepository.deleteById(id);
    }

    @GetMapping("/{id}/pdf")
    public org.springframework.http.ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) {
        AptitudeSheet sheet = aptitudeSheetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fisa nu a fost gasita cu id: " + id));

        byte[] pdfContent = pdfService.generateAptitudeSheetPdf(sheet);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "fisa_aptitudini_" + id + ".pdf");
        
        return new org.springframework.http.ResponseEntity<>(pdfContent, headers, org.springframework.http.HttpStatus.OK);
    }
}
