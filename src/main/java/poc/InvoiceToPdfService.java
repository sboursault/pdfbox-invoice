package poc;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import poc.model.Invoice;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;
import static poc.InvoiceToPdfService.Alignement.*;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;
import static poc.model.Buyer.Builder.aBuyer;
import static poc.model.Emitter.Builder.anEmitter;
import static poc.model.Invoice.Builder.anInvoice;
import static poc.model.InvoiceEntry.Builder.anInvoiceEntry;

public class InvoiceToPdfService {


    private float TABLE_ROW_HEIGHT = 20f;
    private float PAGE_MARGIN_X = 30;
    private float PAGE_MARGIN_Y = 30;

    void createPdf(Invoice invoice) throws IOException {

        try (PDDocument document = new PDDocument()) {


            PDPage page = new PDPage();
            document.addPage(page);

            //Prepare Content Stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float pageTop = page.getMediaBox().getHeight() - PAGE_MARGIN_Y;

            PDImageXObject image = PDImageXObject.createFromFile(invoice.getBuyer().getLogo(), document);
            contentStream.drawImage(
                    image, PAGE_MARGIN_X, pageTop - image.getHeight()
            );

            addBoldText(contentStream, page.getMediaBox().getWidth() - PAGE_MARGIN_X, pageTop, "" +
                    "Facture " + invoice.getInvoiceNumber() + "\n" +
                    "Date " + invoice.getIssueDate().format(ofLocalizedDate(MEDIUM)) + "\n" +
                    //"Date début 20/02/2022\n" +
                    //"Date fin 20/3/2022\n" +
                    //"\n" +
                    "PAYÉE", ALIGN_RIGHT);


            String[] headers = {"Description", "Quantité", "Montant HT", "Montant TTC"};

            int[] colWidths = {40, 20, 20, 20};

            Alignement[] alignements = {ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT};

            String[][] content = invoice.getEntries().stream()
                    .map(entry -> new String[]{
                            entry.getDescription(),
                            Integer.toString(entry.getQuantity()),
                            format(entry.getAmountExclTaxes()),
                            format(entry.getAmountInclTaxes())})
                    .toArray(String[][]::new);


            drawTable(page, contentStream, 450, PAGE_MARGIN_X, headers, alignements, content, colWidths);

            drawCellText(contentStream, 0, 40, "My company - 3 rue de blabla", ALIGN_CENTER, page.getMediaBox().getWidth(), false);

            contentStream.close();

            document.save("invoice.pdf");
        }
    }

    public void drawTable(PDPage page, PDPageContentStream contentStream,
                          float tableTop, float pageMarginX, String[] headers, Alignement[] alignements,
                          String[][] content, int[] colWidthsInPercent) throws IOException {


        float tableWidth = page.getMediaBox().getWidth() - (2 * pageMarginX);

        int[] colWidths =
                Arrays.stream(colWidthsInPercent)
                        .map(w -> (int) (w * tableWidth / 100))
                        .toArray();

        //draw the columns
        //float nextx = margin;
        //for (int i = 0; i <= cols; i++) {
        //    contentStream.drawLine(nextx, y, nextx, y - tableHeight);
        //    nextx += colWidth;
        //}


        drawRow(contentStream, headers, alignements, tableWidth, colWidths, pageMarginX, tableTop, true);

        float rowTop = tableTop - TABLE_ROW_HEIGHT;
        for (String[] row : content) {
            drawRow(contentStream, row, alignements, tableWidth, colWidths, pageMarginX, rowTop, false);
            rowTop -= TABLE_ROW_HEIGHT;
        }

    }

    private void drawRow(PDPageContentStream contentStream, String[] headers, Alignement[] alignements, float tableWidht, int[] colWidths, float rowX, float rowTop, boolean isHeader) throws IOException {

        if (isHeader) {
            contentStream.addRect(rowX, rowTop, tableWidht, -TABLE_ROW_HEIGHT);
            contentStream.fill();
        } else {
            contentStream.moveTo(rowX, rowTop - TABLE_ROW_HEIGHT);
            contentStream.lineTo(rowX + tableWidht, rowTop - TABLE_ROW_HEIGHT);
            contentStream.stroke();
        }

        float cellMargin = 5f;
        float texty = rowTop - 15;
        float textx = rowX + cellMargin;
        for (int i = 0; i < headers.length; i++) {
            String text = headers[i];
            float cellTextWidth = colWidths[i] - 2 * cellMargin;
            drawCellText(contentStream, textx, texty, text, alignements[i], cellTextWidth, isHeader);
            textx += colWidths[i];
        }

    }


    private void drawCellText(PDPageContentStream contentStream, float textX, float anchorY, String text, Alignement alignement, float cellTextWidth, boolean isHeader) throws IOException {
        if (isHeader) {
            drawCellText(contentStream, textX, anchorY, text, alignement, cellTextWidth, HELVETICA_BOLD, Color.white);
        } else {
            drawCellText(contentStream, textX, anchorY, text, alignement, cellTextWidth, HELVETICA, Color.black);
        }
    }

    private void drawCellText(PDPageContentStream contentStream, float textX, float anchorY, String text, Alignement alignement, float cellTextWidth, PDType1Font helvetica, Color color) throws IOException {

        float anchorX;
        switch (alignement) {
            case ALIGN_LEFT:
                anchorX = textX;
                break;
            case ALIGN_RIGHT:
                anchorX = textX + cellTextWidth;
                break;
            case ALIGN_CENTER:
                anchorX = textX + cellTextWidth / 2;
                break;
            default:
                throw new UnsupportedOperationException("not implemented");
        }

        drawText(contentStream, anchorX, anchorY, text, 14, helvetica, alignement, color);
    }


    private void addBoldText(PDPageContentStream cs, float x, float y, String text, Alignement align) throws IOException {
        drawText(cs, x, y, text, 14, HELVETICA_BOLD, align, Color.black);
    }

    private void drawText(PDPageContentStream contentStream, float anchorX, float anchorY, String text, int size, PDType1Font font, Alignement align, Color color) throws IOException {
        contentStream.beginText();
        contentStream.setFont(font, size);
        contentStream.setNonStrokingColor(color);
        contentStream.setLeading(20f);
        contentStream.newLineAtOffset(anchorX, anchorY);

        for (String line : StringUtils.splitByWholeSeparatorPreserveAllTokens(text, "\n")) {
            float textWidth;
            switch (align) {
                case ALIGN_LEFT:
                    contentStream.showText(line);
                    break;
                case ALIGN_RIGHT:
                    textWidth = (font.getStringWidth(line) / 1000.0f) * size;
                    contentStream.newLineAtOffset(-textWidth, 0);
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(textWidth, 0);
                    break;
                case ALIGN_CENTER:
                    textWidth = (font.getStringWidth(line) / 1000.0f) * size;
                    contentStream.newLineAtOffset(-textWidth / 2, 0);
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(textWidth / 2, 0);
                    break;
                default:
                    throw new UnsupportedOperationException("not implemented");
            }
            contentStream.newLine();
        }

        contentStream.endText();

    }


    public enum Alignement {
        ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT
    }

    private String format(double amountExclTaxes) {
        NumberFormat format = (NumberFormat) NumberFormat.getCurrencyInstance(Locale.FRANCE).clone();
        format.setGroupingUsed(false);
        return format.format(amountExclTaxes);
    }
}