package data;

import java.time.LocalDate;

public class PatMed {
    private String patientCIN;
    private String medRef;
    private String dateAffectation;

    public PatMed(String patientCIN, String medRef, String dateAffectation) {
        this.patientCIN = patientCIN;
        this.medRef = medRef;
        this.dateAffectation = dateAffectation;
    }

    // Getters and setters
    public String getPatientCIN() {
        return patientCIN;
    }

    public void setPatientCIN(String patientCIN) {
        this.patientCIN = patientCIN;
    }

    public String getMedRef() {
        return medRef;
    }

    public void setMedRef(String medRef) {
        this.medRef = medRef;
    }

    public String getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(String dateAffectation) {
        this.dateAffectation = dateAffectation;
    }
}
