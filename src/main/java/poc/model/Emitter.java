package poc.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class Emitter {

    String name;
    List<Pair> legalIds;  // siren, tva number
    List<String> address;
    String legalForm;
    String shareCapital;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Pair> getLegalIds() {
        return legalIds;
    }

    public void setLegalIds(List<Pair> legalIds) {
        this.legalIds = legalIds;
    }

    public List<String> getAddress() {
        return address;
    }

    public void setAddress(List<String> address) {
        this.address = address;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getShareCapital() {
        return shareCapital;
    }

    public void setShareCapital(String shareCapital) {
        this.shareCapital = shareCapital;
    }


    public static final class Builder {
        private Emitter emitter;

        private Builder() {
            emitter = new Emitter();
        }

        public static Builder anEmitter() {
            return new Builder();
        }

        public Builder name(String name) {
            emitter.setName(name);
            return this;
        }

        public Builder legalIds(List<Pair> legalIds) {
            emitter.setLegalIds(legalIds);
            return this;
        }

        public Builder legalIds(Pair... legalIds) {
            return legalIds(List.of(legalIds));
        }

        public Builder address(List<String> address) {
            emitter.setAddress(address);
            return this;
        }

        public Builder address(String... address) {
            return address(List.of(address));
        }

        public Builder legalForm(String legalForm) {
            emitter.setLegalForm(legalForm);
            return this;
        }

        public Builder shareCapital(String shareCapital) {
            emitter.setShareCapital(shareCapital);
            return this;
        }

        public Emitter build() {
            return emitter;
        }
    }
}
