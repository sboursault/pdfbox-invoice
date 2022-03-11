package poc;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import poc.model.Emitter;
import poc.model.Invoice;
import poc.util.Alignement;
import poc.util.Style;

import java.awt.*;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.MEDIUM;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static poc.util.Alignement.*;
import static org.apache.pdfbox.pdmodel.font.PDType1Font.HELVETICA_BOLD;
import static poc.util.Style.defaultStyle;

public class InvoiceToPdfService {


    private int TABLE_ROW_HEIGHT = 20; // vertical space between 2 text lines
    private int PAGE_MARGIN_X = 30;
    private int PAGE_MARGIN_Y = 40;
    private Color TABLE_COLOR = Color.GRAY;

    void createPdf(Invoice invoice) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            //Prepare Content Stream
            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            float pageTop = page.getMediaBox().getHeight() - PAGE_MARGIN_Y;

            PDImageXObject image = PDImageXObject.createFromFile(invoice.getEmitter().getLogo(), document);
            contentStream.drawImage(
                    image, PAGE_MARGIN_X + 10, pageTop - image.getHeight()
            );

            float y = pageTop - 35;
            drawText(contentStream, page.getMediaBox().getWidth() * 3 / 4, y, defaultStyle().size(16).font(HELVETICA_BOLD), ALIGN_CENTER, "" +
                    "FACTURE " + invoice.getInvoiceNumber());

            //drawText(contentStream, page.getMediaBox().getWidth() - PAGE_MARGIN_X, y - 40, defaultStyle().font(HELVETICA_BOLD), ALIGN_RIGHT, "" +
            //        "Facture du " + invoice.getIssueDate().format(ofLocalizedDate(MEDIUM)) + "\n" +
            //        //"Date début 20/02/2022\n" +
            //        //"Date fin 20/3/2022\n" +
            //        //"\n" +
            //        "PAYÉE");

            y = 630;
            drawText(contentStream, 340, y, defaultStyle(), "" +
                    invoice.getBuyer().getName() + "\n" +
                    String.join("\n", invoice.getBuyer().getAddress()));

            y = 530;
            drawText(contentStream, PAGE_MARGIN_X, y, defaultStyle(), "" +
                    "Facture du " + invoice.getIssueDate().format(ofLocalizedDate(MEDIUM))
                    + " - " + invoice.getStatus() );


            String[] headers = {"Description", "Quantité", "Montant HT", "TVA", "Montant TTC"};

            int[] colWidths = {35, 18, 18, 11, 18};

            Alignement[] alignements = {ALIGN_LEFT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT, ALIGN_RIGHT};

            List<String[]> content = invoice.getEntries().stream()
                    .map(entry -> new String[]{
                            entry.getDescription(),
                            Integer.toString(entry.getQuantity()),
                            formatCurrency(entry.getAmountExclTaxes()),
                            formatPercent(entry.getTaxRate()),
                            formatCurrency(entry.getAmountInclTaxes())})
                    .collect(toList());

            String[] footers = new String[]{"", "Total", formatCurrency(invoice.getTotalExclTaxes()), "", formatCurrency(invoice.getTotalInclTaxes())};


            y -= 12;
            drawTable(page, contentStream, y, PAGE_MARGIN_X, headers, alignements, content, footers, colWidths);

            Emitter emitter = invoice.getEmitter();
            drawText(contentStream, PAGE_MARGIN_X, 40, defaultStyle().size(10).lineHeight(16),
                    emitter.getName() + " - " + emitter.getLegalForm() + " au capital de " + emitter.getShareCapital() + " - " + String.join(", ", emitter.getAddress()) + "\n"
                            + emitter.getLegalIds().stream().map(each -> each.getKey() + " " + each.getValue()).collect(joining(" - ")));

            contentStream.close();

            document.save("invoice.pdf");
        }
    }


    private void drawText(PDPageContentStream contentStream, float anchorX, float anchorY, Style style, String text) throws IOException {
        drawText(contentStream, anchorX, anchorY, style, ALIGN_LEFT, text);
    }

    private void drawText(PDPageContentStream contentStream, float anchorX, float anchorY, Style style, Alignement alignement, String text) throws IOException {
        drawText(contentStream, anchorX, anchorY, text, style, alignement);
    }

    public void drawTable(PDPage page, PDPageContentStream contentStream,
                          float tableTop, float pageMarginX, String[] headers, Alignement[] alignements,
                          List<String[]> content, String[] footers, int[] colWidthsInPercent) throws IOException {


        float tableWidth = page.getMediaBox().getWidth() - (2 * pageMarginX);

        int[] colWidths =
                Arrays.stream(colWidthsInPercent)
                        .map(w -> (int) (w * tableWidth / 100))
                        .toArray();


        float rowTop = tableTop;
        drawRow(contentStream, headers, alignements, tableWidth, colWidths, pageMarginX, rowTop, TABLE_ROW_HEIGHT, defaultStyle().font(HELVETICA_BOLD).color(Color.white), false);
        rowTop -= TABLE_ROW_HEIGHT;
        for (String[] row : content) {
            int maxLineCountOnRow = Arrays.stream(row).mapToInt(text -> StringUtils.countMatches(text, "\n") + 1).max().orElse(1);
            int rowHeight = maxLineCountOnRow * TABLE_ROW_HEIGHT;
            drawRow(contentStream, row, alignements, tableWidth, colWidths, pageMarginX, rowTop, rowHeight, defaultStyle(), false);
            rowTop -= rowHeight;
        }

        drawRow(contentStream, footers, alignements, tableWidth, colWidths, pageMarginX, rowTop, TABLE_ROW_HEIGHT, defaultStyle().font(HELVETICA_BOLD), true);


    }


    private void drawRow(PDPageContentStream contentStream, String[] headers, Alignement[] alignements, float tableWidht, int[] colWidths, float rowX, float rowTop, int rowHeight, Style style, boolean isFooter) throws IOException {

        if (style.getColor().equals(Color.white)) {
            drawHeaderBackground(contentStream, tableWidht, rowX, rowTop, rowHeight);
        } else if (!isFooter) {
            drowBottomLine(contentStream, tableWidht, rowX, rowTop, rowHeight);
        }

        float cellMargin = 5f;
        float texty = rowTop - 15;
        float textx = rowX + cellMargin;
        for (int i = 0; i < headers.length; i++) {
            String text = headers[i];
            float cellTextWidth = colWidths[i] - 2 * cellMargin;
            drawCellText(contentStream, textx, texty, alignements[i], cellTextWidth, style, text);
            textx += colWidths[i];
        }

    }

    private void drowBottomLine(PDPageContentStream contentStream, float tableWidht, float rowX, float rowTop, int rowHeight) throws IOException {
        contentStream.moveTo(rowX, rowTop - rowHeight);
        contentStream.lineTo(rowX + tableWidht, rowTop - rowHeight);
        contentStream.setStrokingColor(TABLE_COLOR);
        contentStream.stroke();
    }

    private void drawHeaderBackground(PDPageContentStream contentStream, float tableWidht, float rowX, float rowTop, int rowHeight) throws IOException {
        contentStream.addRect(rowX, rowTop, tableWidht, -rowHeight);
        contentStream.setNonStrokingColor(TABLE_COLOR);
        contentStream.fill();
    }


    private void drawCellText(PDPageContentStream contentStream, float textX, float anchorY, Alignement alignement, float cellTextWidth, Style style, String text) throws IOException {

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

        drawText(contentStream, anchorX, anchorY, text, style, alignement);
    }


    private void drawText(PDPageContentStream contentStream, float anchorX, float anchorY, String text, Style style, Alignement align) throws IOException {
        contentStream.beginText();
        contentStream.setFont(style.getFont(), style.getSize());
        contentStream.setNonStrokingColor(style.getColor());
        contentStream.setLeading(style.getLineHeight());
        contentStream.newLineAtOffset(anchorX, anchorY);

        for (String line : StringUtils.splitByWholeSeparatorPreserveAllTokens(text, "\n")) {
            float textWidth;
            switch (align) {
                case ALIGN_LEFT:
                    contentStream.showText(line);
                    break;
                case ALIGN_RIGHT:
                    textWidth = (style.getFont().getStringWidth(line) / 1000.0f) * style.getSize();
                    contentStream.newLineAtOffset(-textWidth, 0);
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(textWidth, 0);
                    break;
                case ALIGN_CENTER:
                    textWidth = (style.getFont().getStringWidth(line) / 1000.0f) * style.getSize();
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


    private String formatCurrency(double amountExclTaxes) {
        NumberFormat format = (NumberFormat) NumberFormat.getCurrencyInstance(Locale.FRANCE).clone();
        format.setGroupingUsed(false);
        return format.format(amountExclTaxes);
    }

    private String formatPercent(double amountExclTaxes) {
        NumberFormat format = (NumberFormat) NumberFormat.getPercentInstance(Locale.FRANCE).clone();
        format.setGroupingUsed(false);
        return format.format(amountExclTaxes);
    }


}