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
    "country",
    "phoneType",
    "phoneUsageType",
    "phoneNumber",
    "phoneNumberExtension",
    "prefixNumber",
    "internationalPrefix",
    "nationalPrefix",
    "cellPrefix",
    "defaultIndicator",
    "doNotCallIndicator",
    "nationalDestinationCode",
    "validationStatusCode",
    "validationMessage",
    "effectiveStartDate",
    "effectiveEndDate",
    "status",
    "nationalFormat",
    "internationalFormat",
    "iso",
    "enrichedIndicator"
})

public class X_UDT_Phone implements Serializable
{

    @JsonProperty("_id")
    private String _id;
    @JsonProperty("country")
    private Country country;
    @JsonProperty("phoneType")
    private PhoneType phoneType;
    @JsonProperty("phoneUsageType")
    private PhoneUsageType phoneUsageType;
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    @JsonProperty("phoneNumberExtension")
    private String phoneNumberExtension;
    @JsonProperty("prefixNumber")
    private String prefixNumber;
    @JsonProperty("internationalPrefix")
    private String internationalPrefix;
    @JsonProperty("nationalPrefix")
    private String nationalPrefix;
    @JsonProperty("cellPrefix")
    private String cellPrefix;
    @JsonProperty("defaultIndicator")
    private Boolean defaultIndicator;
    @JsonProperty("doNotCallIndicator")
    private Boolean doNotCallIndicator;
    @JsonProperty("nationalDestinationCode")
    private String nationalDestinationCode;
    @JsonProperty("validationStatusCode")
    private String validationStatusCode;
    @JsonProperty("validationMessage")
    private String validationMessage;
    @JsonProperty("effectiveStartDate")
    private Long effectiveStartDate;
    @JsonProperty("effectiveEndDate")
    private Long effectiveEndDate;
    @JsonProperty("status")
    private Status status;
    @JsonProperty("nationalFormat")
    private String nationalFormat;
    @JsonProperty("internationalFormat")
    private String internationalFormat;
    @JsonProperty("iso")
    private String iso;
    @JsonProperty("enrichedIndicator")
    private Boolean enrichedIndicator;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = 2402902230734776633L;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("country")
    public Country getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(Country country) {
        this.country = country;
    }

    @JsonProperty("phoneType")
    public PhoneType getPhoneType() {
        return phoneType;
    }

    @JsonProperty("phoneType")
    public void setPhoneType(PhoneType phoneType) {
        this.phoneType = phoneType;
    }

    @JsonProperty("phoneUsageType")
    public PhoneUsageType getPhoneUsageType() {
        return phoneUsageType;
    }

    @JsonProperty("phoneUsageType")
    public void setPhoneUsageType(PhoneUsageType phoneUsageType) {
        this.phoneUsageType = phoneUsageType;
    }

    @JsonProperty("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phoneNumber")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("phoneNumberExtension")
    public String getPhoneNumberExtension() {
        return phoneNumberExtension;
    }

    @JsonProperty("phoneNumberExtension")
    public void setPhoneNumberExtension(String phoneNumberExtension) {
        this.phoneNumberExtension = phoneNumberExtension;
    }

    @JsonProperty("prefixNumber")
    public String getPrefixNumber() {
        return prefixNumber;
    }

    @JsonProperty("prefixNumber")
    public void setPrefixNumber(String prefixNumber) {
        this.prefixNumber = prefixNumber;
    }

    @JsonProperty("internationalPrefix")
    public String getInternationalPrefix() {
        return internationalPrefix;
    }

    @JsonProperty("internationalPrefix")
    public void setInternationalPrefix(String internationalPrefix) {
        this.internationalPrefix = internationalPrefix;
    }

    @JsonProperty("nationalPrefix")
    public String getNationalPrefix() {
        return nationalPrefix;
    }

    @JsonProperty("nationalPrefix")
    public void setNationalPrefix(String nationalPrefix) {
        this.nationalPrefix = nationalPrefix;
    }

    @JsonProperty("cellPrefix")
    public String getCellPrefix() {
        return cellPrefix;
    }

    @JsonProperty("cellPrefix")
    public void setCellPrefix(String cellPrefix) {
        this.cellPrefix = cellPrefix;
    }

    @JsonProperty("defaultIndicator")
    public Boolean getDefaultIndicator() {
        return defaultIndicator;
    }

    @JsonProperty("defaultIndicator")
    public void setDefaultIndicator(Boolean defaultIndicator) {
        this.defaultIndicator = defaultIndicator;
    }

    @JsonProperty("doNotCallIndicator")
    public Boolean getDoNotCallIndicator() {
        return doNotCallIndicator;
    }

    @JsonProperty("doNotCallIndicator")
    public void setDoNotCallIndicator(Boolean doNotCallIndicator) {
        this.doNotCallIndicator = doNotCallIndicator;
    }

    @JsonProperty("nationalDestinationCode")
    public String getNationalDestinationCode() {
        return nationalDestinationCode;
    }

    @JsonProperty("nationalDestinationCode")
    public void setNationalDestinationCode(String nationalDestinationCode) {
        this.nationalDestinationCode = nationalDestinationCode;
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

    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("nationalFormat")
    public String getNationalFormat() {
        return nationalFormat;
    }

    @JsonProperty("nationalFormat")
    public void setNationalFormat(String nationalFormat) {
        this.nationalFormat = nationalFormat;
    }

    @JsonProperty("internationalFormat")
    public String getInternationalFormat() {
        return internationalFormat;
    }

    @JsonProperty("internationalFormat")
    public void setInternationalFormat(String internationalFormat) {
        this.internationalFormat = internationalFormat;
    }

    @JsonProperty("iso")
    public String getIso() {
        return iso;
    }

    @JsonProperty("iso")
    public void setIso(String iso) {
        this.iso = iso;
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
