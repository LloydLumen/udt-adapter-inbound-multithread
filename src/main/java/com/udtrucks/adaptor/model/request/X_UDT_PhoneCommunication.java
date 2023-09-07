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
    "_id",
    "X_UDT_Phone",
    "X_UDT_ServiceCommunicationType"
})

public class X_UDT_PhoneCommunication implements Serializable
{

    @JsonProperty("_id")
    private String _id;
    @JsonProperty("X_UDT_Phone")
    private List<X_UDT_Phone> x_UDT_Phone;
    @JsonProperty("X_UDT_ServiceCommunicationType")
    private X_UDT_ServiceCommunicationType x_UDT_ServiceCommunicationType;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 2974157579764554468L;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("X_UDT_Phone")
    public List<X_UDT_Phone> getX_UDT_Phone() {
        return x_UDT_Phone;
    }

    @JsonProperty("X_UDT_Phone")
    public void setX_UDT_Phone(List<X_UDT_Phone> x_UDT_Phone) {
        this.x_UDT_Phone = x_UDT_Phone;
    }

    @JsonProperty("X_UDT_ServiceCommunicationType")
    public X_UDT_ServiceCommunicationType getX_UDT_ServiceCommunicationType() {
        return x_UDT_ServiceCommunicationType;
    }

    @JsonProperty("X_UDT_ServiceCommunicationType")
    public void setX_UDT_ServiceCommunicationType(X_UDT_ServiceCommunicationType x_UDT_ServiceCommunicationType) {
        this.x_UDT_ServiceCommunicationType = x_UDT_ServiceCommunicationType;
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
