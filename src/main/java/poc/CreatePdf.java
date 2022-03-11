package poc;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.time.LocalDate;

import static poc.model.Buyer.Builder.aBuyer;
import static poc.model.Emitter.Builder.anEmitter;
import static poc.model.Invoice.Builder.anInvoice;
import static poc.model.InvoiceEntry.Builder.anInvoiceEntry;

public class CreatePdf {



    public static void main(String args[]) throws IOException {

        new InvoiceToPdfService().createPdf(anInvoice()
                .invoiceNumber("4987895")
                .issueDate(LocalDate.now())
                .status("PAYÉE")
                .buyer(
                        aBuyer()
                                .name("Tom Jedusor")
                                .address("44 Queen Charlotte St", "Edinburgh", "Midlothian EH6 7EX"))
                .emitter(anEmitter()
                        .name("My company")
                        .legalIds(
                                Pair.of("Siren", "123456789"),
                                Pair.of("TVA intracommunautaire", "123456789"))
                        .address("78 rue de la Liberté", "59000 Lille")
                        .legalForm("SASU")
                        .shareCapital("50 000 €")
                        .logo("your_brand.png"))
                .entries(
                        anInvoiceEntry()
                                .description("Basic subscription\n(from 25/02/2022 to 31/03/2022)")
                                .quantity(1)
                                .amountExclTaxes(160)
                                .amountInclTaxes(200)
                                .taxRate(.20)
                        /*,
                        anInvoiceEntry()
                                .description("1 abonnement BASIC")
                                .quantity(1)
                                .amountExclTaxes(260)
                                .amountInclTaxes(300)*/
                )
                .build());

        System.out.println("Invoice Generated!");
    }
}
