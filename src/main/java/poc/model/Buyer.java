package poc.model;

import java.util.List;

public class Buyer {
    private String name;
    private List<String> address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public static final class Builder {
        private Buyer buyer;

        private Builder() {
            buyer = new Buyer();
        }

        public static Builder aBuyer() {
            return new Builder();
        }

        public Builder name(String name) {
            buyer.setName(name);
            return this;
        }

        public Builder address(String... address) {
            return address(List.of(address));
        }

        public Builder address(List<String> address) {
            buyer.setAddress(address);
            return this;
        }

        public Buyer build() {
            return buyer;
        }
    }

}
