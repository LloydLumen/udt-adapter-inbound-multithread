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
    "defaultIndicator",
    "addressType",
    "usageType",
    "addressStatus",
    "addressLine1",
    "city",
    "county",
    "country",
    "state",
    "postalCode",
    "postalCodeExtension",
    "coordinateSystem",
    "locationCoordinateDesc",
    "latitude",
    "longitude",
    "effectiveStartDate",
    "effectiveEndDate",
    "enrichedIndicator",
    "addressLine2",
    "addressLine3"
})

public class X_UDT_Address implements Serializable
{

    @JsonProperty("_id")
    private String _id;
    @JsonProperty("defaultIndicator")
    private Boolean defaultIndicator;
    @JsonProperty("addressType")
    private AddressType addressType;
    @JsonProperty("usageType")
    private UsageType usageType;
    @JsonProperty("addressStatus")
    private AddressStatus addressStatus;
    @JsonProperty("addressLine1")
    private String addressLine1;
    @JsonProperty("city")
    private String city;
    @JsonProperty("county")
    private String county;
    @JsonProperty("country")
    private Country country;
    @JsonProperty("state")
    private State state;
    @JsonProperty("postalCode")
    private String postalCode;
    @JsonProperty("postalCodeExtension")
    private String postalCodeExtension;
    @JsonProperty("coordinateSystem")
    private String coordinateSystem;
    @JsonProperty("locationCoordinateDesc")
    private String locationCoordinateDesc;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("effectiveStartDate")
    private Long effectiveStartDate;
    @JsonProperty("effectiveEndDate")
    private Long effectiveEndDate;
    @JsonProperty("enrichedIndicator")
    private EnrichedIndicator enrichedIndicator;
    @JsonProperty("addressLine2")
    private String addressLine2;
    @JsonProperty("addressLine3")
    private String addressLine3;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();
    private final static long serialVersionUID = -1695740093101549147L;

    @JsonProperty("_id")
    public String get_id() {
        return _id;
    }

    @JsonProperty("_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    @JsonProperty("defaultIndicator")
    public Boolean getDefaultIndicator() {
        return defaultIndicator;
    }

    @JsonProperty("defaultIndicator")
    public void setDefaultIndicator(Boolean defaultIndicator) {
        this.defaultIndicator = defaultIndicator;
    }

    @JsonProperty("addressType")
    public AddressType getAddressType() {
        return addressType;
    }

    @JsonProperty("addressType")
    public void setAddressType(AddressType addressType) {
        this.addressType = addressType;
    }

    @JsonProperty("usageType")
    public UsageType getUsageType() {
        return usageType;
    }

    @JsonProperty("usageType")
    public void setUsageType(UsageType usageType) {
        this.usageType = usageType;
    }

    @JsonProperty("addressStatus")
    public AddressStatus getAddressStatus() {
        return addressStatus;
    }

    @JsonProperty("addressStatus")
    public void setAddressStatus(AddressStatus addressStatus) {
        this.addressStatus = addressStatus;
    }

    @JsonProperty("addressLine1")
    public String getAddressLine1() {
        return addressLine1;
    }

    @JsonProperty("addressLine1")
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    @JsonProperty("city")
    public String getCity() {
        return city;
    }

    @JsonProperty("city")
    public void setCity(String city) {
        this.city = city;
    }

    @JsonProperty("county")
    public String getCounty() {
        return county;
    }

    @JsonProperty("county")
    public void setCounty(String county) {
        this.county = county;
    }

    @JsonProperty("country")
    public Country getCountry() {
        return country;
    }

    @JsonProperty("country")
    public void setCountry(Country country) {
        this.country = country;
    }

    @JsonProperty("state")
    public State getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(State state) {
        this.state = state;
    }

    @JsonProperty("postalCode")
    public String getPostalCode() {
        return postalCode;
    }

    @JsonProperty("postalCode")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @JsonProperty("postalCodeExtension")
    public String getPostalCodeExtension() {
        return postalCodeExtension;
    }

    @JsonProperty("postalCodeExtension")
    public void setPostalCodeExtension(String postalCodeExtension) {
        this.postalCodeExtension = postalCodeExtension;
    }

    @JsonProperty("coordinateSystem")
    public String getCoordinateSystem() {
        return coordinateSystem;
    }

    @JsonProperty("coordinateSystem")
    public void setCoordinateSystem(String coordinateSystem) {
        this.coordinateSystem = coordinateSystem;
    }

    @JsonProperty("locationCoordinateDesc")
    public String getLocationCoordinateDesc() {
        return locationCoordinateDesc;
    }

    @JsonProperty("locationCoordinateDesc")
    public void setLocationCoordinateDesc(String locationCoordinateDesc) {
        this.locationCoordinateDesc = locationCoordinateDesc;
    }

    @JsonProperty("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
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
    public EnrichedIndicator getEnrichedIndicator() {
        return enrichedIndicator;
    }

    @JsonProperty("enrichedIndicator")
    public void setEnrichedIndicator(EnrichedIndicator enrichedIndicator) {
        this.enrichedIndicator = enrichedIndicator;
    }

    @JsonProperty("addressLine2")
    public String getAddressLine2() {
        return addressLine2;
    }

    @JsonProperty("addressLine2")
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @JsonProperty("addressLine3")
    public String getAddressLine3() {
        return addressLine3;
    }

    @JsonProperty("addressLine3")
    public void setAddressLine3(String addressLine3) {
        this.addressLine3 = addressLine3;
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
