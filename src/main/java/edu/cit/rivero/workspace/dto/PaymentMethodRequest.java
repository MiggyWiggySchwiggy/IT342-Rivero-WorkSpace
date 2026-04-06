package edu.cit.rivero.workspace.dto;

public class PaymentMethodRequest {

    private String cardNumber;
    private String expiryDate;
    private String cvv;

    public PaymentMethodRequest() {
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}
