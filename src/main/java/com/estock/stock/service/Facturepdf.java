package com.estock.stock.service;

import com.estock.stock.Dto.ProduitVenduDTO;
import com.estock.stock.Dto.VenteResponseDTO;
import com.estock.stock.Entity.Configuration;
import com.estock.stock.repository.ConfigurationRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

@Service
public class Facturepdf {

    @Autowired
    private ConfigurationRepository configurationRepository;

    public byte[] genererFacturePdf(VenteResponseDTO vente) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // --- Styles ---
            Font fontSociete = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontInfos = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontSmall = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font fontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

            // --- Récupération config société ---
            Configuration config = configurationRepository.findAll().stream().findFirst().orElse(null);

            // --- En-tête Société ---
            PdfPTable headerTable = new PdfPTable(3);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{1, 3, 2});

            // Logo
            if (config != null && config.getLogo() != null) {
                Image logo = Image.getInstance(config.getLogo());
                logo.scaleToFit(70, 70);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                headerTable.addCell(logoCell);
            } else {
                PdfPCell emptyLogo = new PdfPCell(new Phrase(""));
                emptyLogo.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyLogo);
            }

            // Nom et adresse
            PdfPCell centerCell = new PdfPCell();
            centerCell.setBorder(Rectangle.NO_BORDER);
            centerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

            if (config != null) {
                Paragraph nom = new Paragraph(config.getNom(), fontSociete);
                nom.setAlignment(Element.ALIGN_CENTER);
                Paragraph adresse = new Paragraph("Adresse : " + config.getAdresse(), fontInfos);
                adresse.setAlignment(Element.ALIGN_CENTER);
                centerCell.addElement(nom);
                centerCell.addElement(adresse);
            } else {
                centerCell.addElement(new Paragraph("Informations société non disponibles", fontInfos));
            }
            headerTable.addCell(centerCell);

            // Téléphones
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            if (config != null) {
                Paragraph tel1 = new Paragraph("Tel : " + config.getTel1(), fontInfos);
                Paragraph tel2 = new Paragraph("Cel : " + config.getTel2(), fontInfos);
                tel1.setAlignment(Element.ALIGN_RIGHT);
                tel2.setAlignment(Element.ALIGN_RIGHT);
                rightCell.addElement(tel1);
                rightCell.addElement(tel2);
            }
            headerTable.addCell(rightCell);

            document.add(headerTable);

            // Ligne séparatrice
            document.add(new Paragraph("\n"));
            LineSeparator separator = new LineSeparator();
            separator.setLineWidth(1);
            document.add(new Chunk(separator));
            document.add(new Paragraph("\n"));

            // --- Titre ---
            Paragraph titre = new Paragraph("FACTURE DE VENTE", fontTitle);
            titre.setAlignment(Element.ALIGN_CENTER);
            titre.setSpacingAfter(10);
            document.add(titre);

            // --- Informations de la facture ---
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 1});

            PdfPCell cellGauche = new PdfPCell();
            cellGauche.setBorder(Rectangle.NO_BORDER);
            cellGauche.addElement(new Paragraph("N° Facture : " + vente.getId(), fontSmall));
            cellGauche.addElement(new Paragraph("Vendeur : " + vente.getVendeur(), fontSmall));

            PdfPCell cellDroite = new PdfPCell();
            cellDroite.setBorder(Rectangle.NO_BORDER);
            cellDroite.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellDroite.addElement(new Paragraph("Date : " + vente.getDateVente().format(formatter), fontSmall));
            infoTable.addCell(cellGauche);
            infoTable.addCell(cellDroite);
            document.add(infoTable);

            document.add(new Paragraph("\n"));

            // --- Tableau des produits ---
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{4, 2, 2, 2});

            // En-têtes
            Stream.of("Produit", "Quantité", "Prix Unit.", "Total")
                    .forEach(title -> {
                        PdfPCell header = new PdfPCell(new Phrase(title, fontBold));
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setPaddingTop(5);
                        header.setPaddingBottom(5);
                        table.addCell(header);
                    });

            // Lignes
            for (ProduitVenduDTO produit : vente.getProduits()) {
                table.addCell(new Phrase(produit.getNom(), fontSmall));
                PdfPCell qte = new PdfPCell(new Phrase(String.valueOf(produit.getQuantite()), fontSmall));
                qte.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(qte);

                PdfPCell prixU = new PdfPCell(new Phrase(String.format("%.0f F", produit.getPrixUnitaire()), fontSmall));
                prixU.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(prixU);

                PdfPCell total = new PdfPCell(new Phrase(String.format("%.0f F", produit.getQuantite() * produit.getPrixUnitaire()), fontSmall));
                total.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(total);
            }

            document.add(table);

            // --- Total général ---
            document.add(new Paragraph("\n"));
            Paragraph totalFinal = new Paragraph("TOTAL : " + String.format("%.0f F CFA", vente.getTotal()), fontBold);
            totalFinal.setAlignment(Element.ALIGN_RIGHT);
            totalFinal.setSpacingBefore(10);
            document.add(totalFinal);

            // --- Message de fin ---
            document.add(new Paragraph("\n"));
            Paragraph merci = new Paragraph("Merci pour votre achat !", fontSmall);
            merci.setAlignment(Element.ALIGN_CENTER);
            merci.setItalic();
            document.add(merci);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF : " + e.getMessage());
        }

        return out.toByteArray();
    }
}
