package poc.model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Mandatory information on a french invoice: https://entreprendre.service-public.fr/vosdroits/F31808
 */
public class Invoice {

    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    private Buyer buyer;
    private Emitter emitter;

    private List<InvoiceEntry> entries;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public Buyer getBuyer() {
        return buyer;
    }

    public void setBuyer(Buyer buyer) {
        this.buyer = buyer;
    }

    public Emitter getEmitter() {
        return emitter;
    }

    public void setEmitter(Emitter emitter) {
        this.emitter = emitter;
    }

    public List<InvoiceEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<InvoiceEntry> entries) {
        this.entries = entries;
    }


    public static final class Builder {
        private Invoice invoice;

        private Builder() {
            invoice = new Invoice();
        }

        public static Builder anInvoice() {
            return new Builder();
        }

        public Builder invoiceNumber(String invoiceNumber) {
            invoice.setInvoiceNumber(invoiceNumber);
            return this;
        }

        public Builder issueDate(LocalDate issueDate) {
            invoice.setIssueDate(issueDate);
            return this;
        }

        public Builder periodStartDate(LocalDate periodStartDate) {
            invoice.setPeriodStartDate(periodStartDate);
            return this;
        }

        public Builder periodEndDate(LocalDate periodEndDate) {
            invoice.setPeriodEndDate(periodEndDate);
            return this;
        }

        public Builder buyer(Buyer.Builder buyer) {
            return buyer(buyer.build());
        }

        public Builder buyer(Buyer buyer) {
            invoice.setBuyer(buyer);
            return this;
        }

        public Builder emitter(Emitter emitter) {
            invoice.setEmitter(emitter);
            return this;
        }

        public Builder emitter(Emitter.Builder emitter) {
            return emitter(emitter.build());
        }

        public Builder entries(List<InvoiceEntry> entries) {
            invoice.setEntries(entries);
            return this;
        }

        public Builder entries(InvoiceEntry.Builder... entries) {
            return entries(List.of(entries).stream().map(InvoiceEntry.Builder::build).collect(toList()));
        }

        public Invoice build() {
            return invoice;
        }
    }
}
