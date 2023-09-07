package com.udtrucks.adaptor.model.request;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "X_UDT_name",
    "X_UDT_partyID",
    "X_UDT_customerType",
    "X_UDT_parmaID",
    "X_UDT_Address",
    "X_UDT_Identifier",
    "X_UDT_PhoneCommunication",
    "X_UDT_AlternateName",
    "X_UDT_URL",
    "X_UDT_Email",
    "X_UDT_KnownAs"
})

public class Root implements Serializable
{

    @JsonProperty("X_UDT_name")
    private String x_UDT_name;
    @JsonProperty("X_UDT_partyID")
    private String x_UDT_partyID;
    @JsonProperty("X_UDT_customerType")
    private X_UDT_customerType x_UDT_customerType;
    @JsonProperty("X_UDT_parmaID")
    private String x_UDT_parmaID;
    @JsonProperty("X_UDT_Address")
    private List<X_UDT_Address> x_UDT_Address;
    @JsonProperty("X_UDT_Identifier")
    private List<X_UDT_Identifier> x_UDT_Identifier;
    @JsonProperty("X_UDT_PhoneCommunication")
    private List<X_UDT_PhoneCommunication> x_UDT_PhoneCommunication;
    @JsonProperty("X_UDT_AlternateName")
    private List<X_UDT_AlternateName> x_UDT_AlternateName;
    @JsonProperty("X_UDT_URL")
    private List<X_UDT_URL> x_UDT_URL;
    @JsonProperty("X_UDT_Email")
    private List<X_UDT_Email> x_UDT_Email;
    @JsonProperty("X_UDT_KnownAs")
    private List<X_UDT_KnownA> x_UDT_KnownAs;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 1235320834595394711L;

    @JsonProperty("X_UDT_name")
    public String getX_UDT_name() {
        return x_UDT_name;
    }

    @JsonProperty("X_UDT_name")
    public void setX_UDT_name(String x_UDT_name) {
        this.x_UDT_name = x_UDT_name;
    }

    @JsonProperty("X_UDT_partyID")
    public String getX_UDT_partyID() {
        return x_UDT_partyID;
    }

    @JsonProperty("X_UDT_partyID")
    public void setX_UDT_partyID(String x_UDT_partyID) {
        this.x_UDT_partyID = x_UDT_partyID;
    }

    @JsonProperty("X_UDT_customerType")
    public X_UDT_customerType getX_UDT_customerType() {
        return x_UDT_customerType;
    }

    @JsonProperty("X_UDT_customerType")
    public void setX_UDT_customerType(X_UDT_customerType x_UDT_customerType) {
        this.x_UDT_customerType = x_UDT_customerType;
    }

    @JsonProperty("X_UDT_parmaID")
    public String getX_UDT_parmaID() {
        return x_UDT_parmaID;
    }

    @JsonProperty("X_UDT_parmaID")
    public void setX_UDT_parmaID(String x_UDT_parmaID) {
        this.x_UDT_parmaID = x_UDT_parmaID;
    }

    @JsonProperty("X_UDT_Address")
    public List<X_UDT_Address> getX_UDT_Address() {
        return x_UDT_Address;
    }

    @JsonProperty("X_UDT_Address")
    public void setX_UDT_Address(List<X_UDT_Address> x_UDT_Address) {
        this.x_UDT_Address = x_UDT_Address;
    }

    @JsonProperty("X_UDT_Identifier")
    public List<X_UDT_Identifier> getX_UDT_Identifier() {
        return x_UDT_Identifier;
    }

    @JsonProperty("X_UDT_Identifier")
    public void setX_UDT_Identifier(List<X_UDT_Identifier> x_UDT_Identifier) {
        this.x_UDT_Identifier = x_UDT_Identifier;
    }

    @JsonProperty("X_UDT_PhoneCommunication")
    public List<X_UDT_PhoneCommunication> getX_UDT_PhoneCommunication() {
        return x_UDT_PhoneCommunication;
    }

    @JsonProperty("X_UDT_PhoneCommunication")
    public void setX_UDT_PhoneCommunication(List<X_UDT_PhoneCommunication> x_UDT_PhoneCommunication) {
        this.x_UDT_PhoneCommunication = x_UDT_PhoneCommunication;
    }

    @JsonProperty("X_UDT_AlternateName")
    public List<X_UDT_AlternateName> getX_UDT_AlternateName() {
        return x_UDT_AlternateName;
    }

    @JsonProperty("X_UDT_AlternateName")
    public void setX_UDT_AlternateName(List<X_UDT_AlternateName> x_UDT_AlternateName) {
        this.x_UDT_AlternateName = x_UDT_AlternateName;
    }

    @JsonProperty("X_UDT_URL")
    public List<X_UDT_URL> getX_UDT_URL() {
        return x_UDT_URL;
    }

    @JsonProperty("X_UDT_URL")
    public void setX_UDT_URL(List<X_UDT_URL> x_UDT_URL) {
        this.x_UDT_URL = x_UDT_URL;
    }

    @JsonProperty("X_UDT_Email")
    public List<X_UDT_Email> getX_UDT_Email() {
        return x_UDT_Email;
    }

    @JsonProperty("X_UDT_Email")
    public void setX_UDT_Email(List<X_UDT_Email> x_UDT_Email) {
        this.x_UDT_Email = x_UDT_Email;
    }

    @JsonProperty("X_UDT_KnownAs")
    public List<X_UDT_KnownA> getX_UDT_KnownAs() {
        return x_UDT_KnownAs;
    }

    @JsonProperty("X_UDT_KnownAs")
    public void setX_UDT_KnownAs(List<X_UDT_KnownA> x_UDT_KnownAs) {
        this.x_UDT_KnownAs = x_UDT_KnownAs;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
