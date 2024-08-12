package data;

import javafx.beans.property.*;

public class Medicine {
    private final StringProperty ref;
    private final StringProperty libelle;
    private final DoubleProperty price;

    public Medicine(String ref, String libelle, double price) {
        this.ref = new SimpleStringProperty(ref);
        this.libelle = new SimpleStringProperty(libelle);
        this.price = new SimpleDoubleProperty(price);
    }

    // Getters
    public String getRef() {
        return ref.get();
    }

    public StringProperty refProperty() {
        return ref;
    }

    public String getLibelle() {
        return libelle.get();
    }

    public StringProperty libelleProperty() {
        return libelle;
    }

    public double getPrice() {
        return price.get();
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public void setRef(String ref) {
        this.ref.set(ref);
    }

    public void setLibelle(String libelle) {
        this.libelle.set(libelle);
    }

    public void setPrice(double price) {
        this.price.set(price);
    }

}
