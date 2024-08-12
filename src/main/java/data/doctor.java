package data;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class doctor {
    private String id;
    private String firstName;
    private String lastName;
    private String address;
    private String phone;

    public doctor(String id, String firstName, String lastName, String address, String phone) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    // Properties for TableView
    public StringProperty idProperty() { return new SimpleStringProperty(id); }
    public StringProperty firstNameProperty() { return new SimpleStringProperty(firstName); }
    public StringProperty lastNameProperty() { return new SimpleStringProperty(lastName); }
    public StringProperty addressProperty() { return new SimpleStringProperty(address); }
    public StringProperty phoneProperty() { return new SimpleStringProperty(phone); }
}
