package poc;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import static poc.Invoice.Alignement.*;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;

public class Invoice {


    void createPdf() throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            //Prepare Content Stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float pageMarginX = 30;


            addBoldText(contentStream, page.getMediaBox().getWidth() - pageMarginX, 750, "" +
                    "Facture 9430194\n" +
                    "Date 20/02/2022\n" +
                    //"Due Date 26/2/2022\n" +
                    "Date début 20/02/2022\n" +
                    "Date fin 20/3/2022\n" +
                    "\n" +
                    "PAYÉE", ALIGN_RIGHT);


            String[] headers = {"Date", "Description", "Quantité", "Montant (€)"};

            int[] colWidths = {20, 40, 20, 20};

            Alignement[] alignements = {ALIGN_LEFT, ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT};

            String[][] content = {
                    {"01/07/2022", "Abonnement standard mensuel", "1", "45,00"}
            };

            drawTable(page, contentStream, 450, pageMarginX, headers, alignements, content, colWidths);

            drawCellText(contentStream, 0, 40, "My company - 3 rue de blabla", ALIGN_CENTER, page.getMediaBox().getWidth());

            contentStream.close();

            document.save("invoice.pdf");
        }
    }

    public void drawTable(PDPage page, PDPageContentStream contentStream,
                          float tableTop, float pageMarginX, String[] headers, Alignement[] alignements,
                          String[][] content, int[] colWidthsInPercent) throws IOException {


        int rows = content.length;
        float rowHeight = 20f;
        float tableWidth = page.getMediaBox().getWidth() - (2 * pageMarginX);

        int[] colWidths =
                Arrays.stream(colWidthsInPercent)
                        .map(w -> (int) (w * tableWidth / 100))
                        .toArray();

        float cellMargin = 5f;


        //draw the rows
        float nexty = tableTop - rowHeight;
        for (int i = 0; i <= rows; i++) {
            contentStream.moveTo(pageMarginX, nexty);
            contentStream.lineTo(pageMarginX + tableWidth, nexty);
            contentStream.stroke();
            nexty -= rowHeight;
        }


        //draw the columns
        //float nextx = margin;
        //for (int i = 0; i <= cols; i++) {
        //    contentStream.drawLine(nextx, y, nextx, y - tableHeight);
        //    nextx += colWidth;
        //}

        //now add the text
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        contentStream.addRect(pageMarginX, tableTop, tableWidth, -rowHeight);
        contentStream.fill();

        float textx = pageMarginX + cellMargin;
        float texty = tableTop - 15;
        for (int i = 0; i < headers.length; i++) {
            String text = headers[i];
            float cellTextWidth = colWidths[i] - 2 * cellMargin;
            drawHeaderCellText(contentStream, textx, texty, text, alignements[i], cellTextWidth);
            textx += colWidths[i];
        }

        textx = pageMarginX + cellMargin;
        texty = texty - rowHeight;
        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                String text = content[i][j];
                float cellTextWidth = colWidths[i] - 2 * cellMargin;
                drawCellText(contentStream, textx, texty, text, alignements[j], cellTextWidth);
                textx += colWidths[j];
            }
            texty -= rowHeight;
            textx = pageMarginX + cellMargin;
        }
    }

    private void drawHeaderCellText(PDPageContentStream contentStream, float textX, float anchorY, String text, Alignement alignement, float cellTextWidth) throws IOException {
        drawCellText(contentStream, textX, anchorY, text, alignement, cellTextWidth, HELVETICA_BOLD, Color.white);
    }

    private void drawCellText(PDPageContentStream contentStream, float textX, float anchorY, String text, Alignement alignement, float cellTextWidth) throws IOException {
        drawCellText(contentStream, textX, anchorY, text, alignement, cellTextWidth, HELVETICA, Color.black);
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

    private void drawText(PDPageContentStream cs, int x, int y, String text) throws IOException {
        drawText(cs, x, y, text, 14, HELVETICA, ALIGN_LEFT, Color.black);
    }

    private void addBoldText(PDPageContentStream cs, float x, int y, String text) throws IOException {
        addBoldText(cs, x, y, text, ALIGN_LEFT);
    }

    private void addBoldText(PDPageContentStream cs, float x, int y, String text, Alignement align) throws IOException {
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


    public static void main(String args[]) throws IOException {
        new Invoice().createPdf();
        System.out.println("Invoice Generated!");
    }

    public enum Alignement {
        ALIGN_LEFT, ALIGN_CENTER, ALIGN_RIGHT
    }
}