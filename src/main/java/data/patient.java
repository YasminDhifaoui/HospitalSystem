package data;

import javafx.beans.property.SimpleStringProperty;

public class patient {
    private SimpleStringProperty cin;
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty address;
    private SimpleStringProperty phone;

    public patient(String cin, String firstName, String lastName, String address, String phone) {
        this.cin = new SimpleStringProperty(cin);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.address = new SimpleStringProperty(address);
        this.phone = new SimpleStringProperty(phone);
    }

    public String getCin() { return cin.get(); }
    public SimpleStringProperty cinProperty() { return cin; }

    public String getFirstName() { return firstName.get(); }
    public SimpleStringProperty firstNameProperty() { return firstName; }

    public String getLastName() { return lastName.get(); }
    public SimpleStringProperty lastNameProperty() { return lastName; }

    public String getAddress() { return address.get(); }
    public SimpleStringProperty addressProperty() { return address; }

    public String getPhone() { return phone.get(); }
    public SimpleStringProperty phoneProperty() { return phone; }

    public void setCin(String cin) {
        this.cin.set(cin);
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }
}
