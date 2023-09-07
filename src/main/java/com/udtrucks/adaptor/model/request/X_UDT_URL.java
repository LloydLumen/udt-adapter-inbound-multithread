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
    "X_UDT_uRLType",
    "X_UDT_link"
})

public class X_UDT_URL implements Serializable
{

    @JsonProperty("_id")
    private String _id;
    @JsonProperty("X_UDT_uRLType")
    private X_UDT_uRLType x_UDT_uRLType;
    @JsonProperty("X_UDT_link")
    private String x_UDT_link;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 2275387609747470870L;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("X_UDT_uRLType")
    public X_UDT_uRLType getX_UDT_uRLType() {
        return x_UDT_uRLType;
    }

    @JsonProperty("X_UDT_uRLType")
    public void setX_UDT_uRLType(X_UDT_uRLType x_UDT_uRLType) {
        this.x_UDT_uRLType = x_UDT_uRLType;
    }

    @JsonProperty("X_UDT_link")
    public String getX_UDT_link() {
        return x_UDT_link;
    }

    @JsonProperty("X_UDT_link")
    public void setX_UDT_link(String x_UDT_link) {
        this.x_UDT_link = x_UDT_link;
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
