package gov.gtas.summary;

import gov.gtas.model.Address;
import org.springframework.beans.BeanUtils;

public class MessageAddress {
    private String line1;

    private String line2;

    private String line3;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private String flightIdTag;

    private String messageHash;

    public static MessageAddress from(String flightIdTag, String messageHash, Address address) {
        MessageAddress pa = new MessageAddress();
        pa.setCity(address.getCity());
        pa.setCountry(address.getCountry());
        pa.setLine1(address.getLine1());
        pa.setLine2(address.getLine2());
        pa.setLine3(address.getLine3());
        pa.setMessageHash(messageHash);
        pa.setState(address.getState());
        pa.setFlightIdTag(flightIdTag);
        pa.setMessageHash(messageHash);
        return pa;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getFlightIdTag() {
        return flightIdTag;
    }

    public void setFlightIdTag(String flightIdTag) {
        this.flightIdTag = flightIdTag;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public void setMessageHash(String messageHash) {
        this.messageHash = messageHash;
    }
}
