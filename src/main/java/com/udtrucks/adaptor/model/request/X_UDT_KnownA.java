package com.udtrucks.adaptor.model.request;

import java.io.Serializable;
import java.util.LinkedHashMap;
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
    "X_UDT_applicationType",
    "X_UDT_applicationID"
})

public class X_UDT_KnownA implements Serializable
{

    @JsonProperty("_id")
    private String _id;
    @JsonProperty("X_UDT_applicationType")
    private X_UDT_applicationType x_UDT_applicationType;
    @JsonProperty("X_UDT_applicationID")
    private String x_UDT_applicationID;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -8041583798108509635L;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("X_UDT_applicationType")
    public X_UDT_applicationType getX_UDT_applicationType() {
        return x_UDT_applicationType;
    }

    @JsonProperty("X_UDT_applicationType")
    public void setX_UDT_applicationType(X_UDT_applicationType x_UDT_applicationType) {
        this.x_UDT_applicationType = x_UDT_applicationType;
    }

    @JsonProperty("X_UDT_applicationID")
    public String getX_UDT_applicationID() {
        return x_UDT_applicationID;
    }

    @JsonProperty("X_UDT_applicationID")
    public void setX_UDT_applicationID(String x_UDT_applicationID) {
        this.x_UDT_applicationID = x_UDT_applicationID;
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
