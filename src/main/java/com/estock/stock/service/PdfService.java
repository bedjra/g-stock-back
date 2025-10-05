package com.estock.stock.service;

import com.estock.stock.Entity.Configuration;
import com.estock.stock.Entity.Produit;
import com.estock.stock.Entity.Utilisateur;
import com.estock.stock.repository.ConfigurationRepository;
import com.estock.stock.repository.ProduitRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.itextpdf.text.Image; //

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import com.itextpdf.text.BaseColor;


@Service
public class PdfService {

    private final ProduitRepository produitRepository;

    @Autowired
    private ConfigurationRepository configurationRepository;

    public PdfService(ProduitRepository produitRepository) {
        this.produitRepository = produitRepository;
    }


    public ByteArrayInputStream generatePdf() {
        List<Produit> produits = produitRepository.findAll();
        Configuration config = configurationRepository.findAll().stream().findFirst().orElse(null);

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Styles ---
            Font fontSociete = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontInfos = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // --- En-tête société ---
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3, 2}); // logo / nom+adresse / téléphones

            // Logo à gauche
            if (config != null && config.getLogo() != null) {
                Image logo = Image.getInstance(config.getLogo());
                logo.scaleToFit(70, 70);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                headerTable.addCell(logoCell);
            } else {
                PdfPCell emptyLogo = new PdfPCell(new Phrase(""));
                emptyLogo.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyLogo);
            }

            // Nom + adresse au centre
            PdfPCell infoCenter = new PdfPCell();
            infoCenter.setBorder(Rectangle.NO_BORDER);
            infoCenter.setHorizontalAlignment(Element.ALIGN_CENTER);
            infoCenter.setVerticalAlignment(Element.ALIGN_MIDDLE);

            if (config != null) {
                Paragraph nom = new Paragraph(config.getNom(), fontSociete);
                nom.setAlignment(Element.ALIGN_CENTER);
                Paragraph adresse = new Paragraph("Adresse : " + config.getAdresse(), fontInfos);
                adresse.setAlignment(Element.ALIGN_CENTER);

                infoCenter.addElement(nom);
                infoCenter.addElement(adresse);
            } else {
                infoCenter.addElement(new Paragraph("Informations société non disponibles", fontInfos));
            }
            headerTable.addCell(infoCenter);

            // Téléphones à droite
            PdfPCell infoRight = new PdfPCell();
            infoRight.setBorder(Rectangle.NO_BORDER);
            infoRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
            infoRight.setVerticalAlignment(Element.ALIGN_MIDDLE);

            if (config != null) {
                Paragraph tel1 = new Paragraph("Tél : " + config.getTel1(), fontInfos);
                Paragraph tel2 = new Paragraph("Tél 2 : " + config.getTel2(), fontInfos);
                tel1.setAlignment(Element.ALIGN_RIGHT);
                tel2.setAlignment(Element.ALIGN_RIGHT);
                infoRight.addElement(tel1);
                infoRight.addElement(tel2);
            }
            headerTable.addCell(infoRight);
            document.add(headerTable);

            // --- Ligne de séparation ---
            document.add(new Paragraph("\n"));
            // --- Ligne de séparation ---
            LineSeparator separator = new LineSeparator();
            separator.setLineWidth(1);
            separator.setOffset(-2); // décale légèrement vers le haut
            document.add(new Chunk(separator));

            document.add(new Paragraph("\n")); // seulement un petit espace après


            // --- Titre de la fiche ---
            Paragraph titre = new Paragraph("FICHE D'INVENTAIRE", fontTitle);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(10);
            document.add(titre);

            // --- Infos fiche (Date, Heure) ---
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});

            PdfPCell dateCell = new PdfPCell(new Phrase("Date : " + now.format(dateFormatter), fontSmall));
            dateCell.setBorder(Rectangle.NO_BORDER);
            dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            dateCell.setPaddingBottom(8);

            PdfPCell heureCell = new PdfPCell(new Phrase("Heure : " + now.format(timeFormatter), fontSmall));
            heureCell.setBorder(Rectangle.NO_BORDER);
            heureCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            heureCell.setPaddingBottom(8);

            infoTable.addCell(dateCell);
            infoTable.addCell(heureCell);
            document.add(infoTable);

            document.add(new Paragraph("\n"));

            // --- Tableau Produits ---
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new int[]{1, 4, 2});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // --- En-têtes ---
            Stream.of("Id", "Nom", "Quantité").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, headerFont));
                header.setBorder(Rectangle.BOTTOM);
                header.setBorderColor(BaseColor.LIGHT_GRAY);
                header.setBorderWidth(0.5f);
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPaddingBottom(5);
                header.setPaddingTop(5);
                table.addCell(header);
            });

            // --- Contenu ---
            for (Produit p : produits) {
                PdfPCell idCell = new PdfPCell(new Phrase(String.valueOf(p.getId()), cellFont));
                idCell.setBorder(Rectangle.BOTTOM);
                idCell.setBorderColor(BaseColor.LIGHT_GRAY);
                idCell.setBorderWidth(0.5f);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                idCell.setPaddingTop(5);
                idCell.setPaddingBottom(5);
                table.addCell(idCell);

                PdfPCell nomCell = new PdfPCell(new Phrase(p.getNom(), cellFont));
                nomCell.setBorder(Rectangle.BOTTOM);
                nomCell.setBorderColor(BaseColor.LIGHT_GRAY);
                nomCell.setBorderWidth(0.5f);
                nomCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                nomCell.setPaddingLeft(120); // petit décalage à gauche
                nomCell.setPaddingTop(5);
                nomCell.setPaddingBottom(5);
                table.addCell(nomCell);

                PdfPCell qteCell = new PdfPCell(new Phrase(String.valueOf(p.getQte()), cellFont));
                qteCell.setBorder(Rectangle.BOTTOM);
                qteCell.setBorderColor(BaseColor.LIGHT_GRAY);
                qteCell.setBorderWidth(0.5f);
                qteCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                qteCell.setPaddingTop(5);
                qteCell.setPaddingBottom(5);
                table.addCell(qteCell);
            }

            document.add(table);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
