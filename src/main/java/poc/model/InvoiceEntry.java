package poc.model;

public class InvoiceEntry {

    private String description;
    private int quantity = 1;

    private double amountExclTaxes;
    private double amountInclTaxes;

    private double taxRate;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmountExclTaxes() {
        return amountExclTaxes;
    }

    public void setAmountExclTaxes(double amountExclTaxes) {
        this.amountExclTaxes = amountExclTaxes;
    }

    public double getAmountInclTaxes() {
        return amountInclTaxes;
    }

    public void setAmountInclTaxes(double amountInclTaxes) {
        this.amountInclTaxes = amountInclTaxes;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public static final class Builder {
        private InvoiceEntry invoiceEntry;

        private Builder() {
            invoiceEntry = new InvoiceEntry();
        }

        public static Builder anInvoiceEntry() {
            return new Builder();
        }

        public Builder description(String description) {
            invoiceEntry.setDescription(description);
            return this;
        }

        public Builder quantity(int quantity) {
            invoiceEntry.setQuantity(quantity);
            return this;
        }

        public Builder amountExclTaxes(double amountExclTaxes) {
            invoiceEntry.setAmountExclTaxes(amountExclTaxes);
            return this;
        }

        public Builder amountInclTaxes(double amountInclTaxes) {
            invoiceEntry.setAmountInclTaxes(amountInclTaxes);
            return this;
        }

        public Builder taxRate(double taxRate) {
            invoiceEntry.setTaxRate(taxRate);
            return this;
        }

        public InvoiceEntry build() {
            return invoiceEntry;
        }
    }
}
