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
    "X_UDT_alternateName",
    "X_UDT_nameType",
    "X_UDT_effectiveStartDate",
    "X_UDT_effectiveEndDate"
})

public class X_UDT_AlternateName implements Serializable
{

    @JsonProperty("_id")
    private String _id;
    @JsonProperty("X_UDT_alternateName")
    private String x_UDT_alternateName;
    @JsonProperty("X_UDT_nameType")
    private X_UDT_nameType x_UDT_nameType;
    @JsonProperty("X_UDT_effectiveStartDate")
    private Long x_UDT_effectiveStartDate;
    @JsonProperty("X_UDT_effectiveEndDate")
    private Long x_UDT_effectiveEndDate;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -7362501090376906084L;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("X_UDT_alternateName")
    public String getX_UDT_alternateName() {
        return x_UDT_alternateName;
    }

    @JsonProperty("X_UDT_alternateName")
    public void setX_UDT_alternateName(String x_UDT_alternateName) {
        this.x_UDT_alternateName = x_UDT_alternateName;
    }

    @JsonProperty("X_UDT_nameType")
    public X_UDT_nameType getX_UDT_nameType() {
        return x_UDT_nameType;
    }

    @JsonProperty("X_UDT_nameType")
    public void setX_UDT_nameType(X_UDT_nameType x_UDT_nameType) {
        this.x_UDT_nameType = x_UDT_nameType;
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

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
