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
    "X_UDT_identifierType",
    "X_UDT_effectiveStartDate",
    "X_UDT_effectiveEndDate",
    "X_UDT_identifierValue",
    "X_UDT_status"
})

public class X_UDT_Identifier implements Serializable
{

    @JsonProperty("_id")
    private String _id;
    @JsonProperty("X_UDT_identifierType")
    private X_UDT_identifierType x_UDT_identifierType;
    @JsonProperty("X_UDT_effectiveStartDate")
    private Long x_UDT_effectiveStartDate;
    @JsonProperty("X_UDT_effectiveEndDate")
    private Long x_UDT_effectiveEndDate;
    @JsonProperty("X_UDT_identifierValue")
    private String x_UDT_identifierValue;
    @JsonProperty("X_UDT_status")
    private X_UDT_status x_UDT_status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 4501302469892954429L;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("X_UDT_identifierType")
    public X_UDT_identifierType getX_UDT_identifierType() {
        return x_UDT_identifierType;
    }

    @JsonProperty("X_UDT_identifierType")
    public void setX_UDT_identifierType(X_UDT_identifierType x_UDT_identifierType) {
        this.x_UDT_identifierType = x_UDT_identifierType;
    }

    @JsonProperty("X_UDT_effectiveStartDate")
    public Long getX_UDT_effectiveStartDate() {
        return x_UDT_effectiveStartDate;
    }

    @JsonProperty("X_UDT_effectiveStartDate")
    public void setX_UDT_effectiveStartDate(Long x_UDT_effectiveStartDate) {
        this.x_UDT_effectiveStartDate = x_UDT_effectiveStartDate;
    }

    @JsonProperty("X_UDT_effectiveEndDate")
    public Long getX_UDT_effectiveEndDate() {
        return x_UDT_effectiveEndDate;
    }

    @JsonProperty("X_UDT_effectiveEndDate")
    public void setX_UDT_effectiveEndDate(Long x_UDT_effectiveEndDate) {
        this.x_UDT_effectiveEndDate = x_UDT_effectiveEndDate;
    }

    @JsonProperty("X_UDT_identifierValue")
    public String getX_UDT_identifierValue() {
        return x_UDT_identifierValue;
    }

    @JsonProperty("X_UDT_identifierValue")
    public void setX_UDT_identifierValue(String x_UDT_identifierValue) {
        this.x_UDT_identifierValue = x_UDT_identifierValue;
    }

    @JsonProperty("X_UDT_status")
    public X_UDT_status getX_UDT_status() {
        return x_UDT_status;
    }

    @JsonProperty("X_UDT_status")
    public void setX_UDT_status(X_UDT_status x_UDT_status) {
        this.x_UDT_status = x_UDT_status;
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
