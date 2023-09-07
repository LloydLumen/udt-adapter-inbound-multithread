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
    "usageType",
    "electronicAddress",
    "defaultIndicator",
    "electronicAddressStatus",
    "validationStatusCode",
    "validationMessage",
    "netProtectCode",
    "effectiveStartDate",
    "effectiveEndDate",
    "enrichedIndicator"
})

public class X_UDT_Email implements Serializable
{

    @JsonProperty("_id")
    private String _id;
    @JsonProperty("usageType")
    private UsageType usageType;
    @JsonProperty("electronicAddress")
    private String electronicAddress;
    @JsonProperty("defaultIndicator")
    private Boolean defaultIndicator;
    @JsonProperty("electronicAddressStatus")
    private ElectronicAddressStatus electronicAddressStatus;
    @JsonProperty("validationStatusCode")
    private String validationStatusCode;
    @JsonProperty("validationMessage")
    private String validationMessage;
    @JsonProperty("netProtectCode")
    private String netProtectCode;
    @JsonProperty("effectiveStartDate")
    private Long effectiveStartDate;
    @JsonProperty("effectiveEndDate")
    private Long effectiveEndDate;
    @JsonProperty("enrichedIndicator")
    private Boolean enrichedIndicator;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 5921756751060362046L;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("usageType")
    public UsageType getUsageType() {
        return usageType;
    }

    @JsonProperty("usageType")
    public void setUsageType(UsageType usageType) {
        this.usageType = usageType;
    }

    @JsonProperty("electronicAddress")
    public String getElectronicAddress() {
        return electronicAddress;
    }

    @JsonProperty("electronicAddress")
    public void setElectronicAddress(String electronicAddress) {
        this.electronicAddress = electronicAddress;
    }

    @JsonProperty("defaultIndicator")
    public Boolean getDefaultIndicator() {
        return defaultIndicator;
    }

    @JsonProperty("defaultIndicator")
    public void setDefaultIndicator(Boolean defaultIndicator) {
        this.defaultIndicator = defaultIndicator;
    }

    @JsonProperty("electronicAddressStatus")
    public ElectronicAddressStatus getElectronicAddressStatus() {
        return electronicAddressStatus;
    }

    @JsonProperty("electronicAddressStatus")
    public void setElectronicAddressStatus(ElectronicAddressStatus electronicAddressStatus) {
        this.electronicAddressStatus = electronicAddressStatus;
    }

    @JsonProperty("validationStatusCode")
    public String getValidationStatusCode() {
        return validationStatusCode;
    }

    @JsonProperty("validationStatusCode")
    public void setValidationStatusCode(String validationStatusCode) {
        this.validationStatusCode = validationStatusCode;
    }

    @JsonProperty("validationMessage")
    public String getValidationMessage() {
        return validationMessage;
    }

    @JsonProperty("validationMessage")
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }

    @JsonProperty("netProtectCode")
    public String getNetProtectCode() {
        return netProtectCode;
    }

    @JsonProperty("netProtectCode")
    public void setNetProtectCode(String netProtectCode) {
        this.netProtectCode = netProtectCode;
    }

    @JsonProperty("effectiveStartDate")
    public Long getEffectiveStartDate() {
        return effectiveStartDate;
    }

    @JsonProperty("effectiveStartDate")
    public void setEffectiveStartDate(Long effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    @JsonProperty("effectiveEndDate")
    public Long getEffectiveEndDate() {
        return effectiveEndDate;
    }

    @JsonProperty("effectiveEndDate")
    public void setEffectiveEndDate(Long effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    @JsonProperty("enrichedIndicator")
    public Boolean getEnrichedIndicator() {
        return enrichedIndicator;
    }

    @JsonProperty("enrichedIndicator")
    public void setEnrichedIndicator(Boolean enrichedIndicator) {
        this.enrichedIndicator = enrichedIndicator;
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
