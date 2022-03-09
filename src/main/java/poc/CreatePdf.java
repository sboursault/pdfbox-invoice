package poc;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static poc.model.Buyer.Builder.aBuyer;
import static poc.model.Emitter.Builder.anEmitter;
import static poc.model.Invoice.Builder.anInvoice;
import static poc.model.InvoiceEntry.Builder.anInvoiceEntry;

public class CreatePdf {



    public static void main(String args[]) throws IOException {

        new InvoiceToPdfService().createPdf(anInvoice()
                .invoiceNumber("INV-4987")
                .issueDate(LocalDate.now())
                .buyer(
                        aBuyer()
                                .name("Tom Jedusor")
                                .address("44 Queen Charlotte St", "Edinburgh", "Midlothian EH6 7EX")
                                .logo("your_brand.png"))
                .emitter(anEmitter()
                        .name("Big company")
                        .legalIds(
                                Pair.of("Siren", "123456798"),
                                Pair.of("Numéro TVA", "FR51123456798"))
                        .address("41 Rue de Richelieu", "75001 Paris")
                        .legalForm("Société européenne")
                        .shareCapital("25 €"))
                .entries(
                        anInvoiceEntry()
                                .description("1 abonnement BASIC")
                                .quantity(1)
                                .amountExclTaxes(1160)
                                .amountInclTaxes(200),
                        anInvoiceEntry()
                                .description("1 abonnement BASIC")
                                .quantity(1)
                                .amountExclTaxes(260)
                                .amountInclTaxes(300)
                )
                .build());

        System.out.println("Invoice Generated!");
    }
}
