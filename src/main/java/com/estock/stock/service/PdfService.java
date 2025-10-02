package com.estock.stock.service;

import com.estock.stock.Entity.Produit;
import com.estock.stock.repository.ProduitRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import com.itextpdf.text.BaseColor;


@Service
public class PdfService {

    private final ProduitRepository produitRepository;

    public PdfService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }

    public ByteArrayInputStream generatePdf() {
        List<Produit> produits = produitRepository.findAll();

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);

            // Récupérer date et heure du jour
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy HH:mm", Locale.FRENCH);
            String dateTimeString = now.format(formatter);

            Paragraph title = new Paragraph("Inventaire des Produits - " + dateTimeString, fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Tableau avec 3 colonnes au lieu de 4
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 3, 2}); // Adapter les largeurs

            // Header (sans prix)
            Stream.of("ID", "Nom", "Quantité").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setBorderWidth(1);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });

            // Rows
            for (Produit p : produits) {
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(p.getId())));
                idCell.setPadding(5);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                PdfPCell nomCell = new PdfPCell(new Phrase(p.getNom()));
                nomCell.setPadding(5);
                nomCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(nomCell);

                PdfPCell qteCell = new PdfPCell(new Phrase(String.valueOf(p.getQte())));
                qteCell.setPadding(5);
                qteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qteCell);
            }

            document.add(table);
            document.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
