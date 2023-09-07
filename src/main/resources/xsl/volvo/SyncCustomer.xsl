<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xalan="http://xml.apache.org/xslt"
    xmlns:java="http://xml.apache.org/xslt/java"
    xmlns:datetime="http://exslt.org/dates-and-times"
    xmlns:md="http://www.cdb.volvo.com/masterdata/4_0"
    xmlns:volvo="http://www.volvo.com/group/common/1_0"
>

    <!--    <xsl:import href="dateTime.xsl" />-->
    <xsl:output method="xml" indent="yes" />

    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>

    <!--  <xsl:template match="@*|node()">
          <xsl:copy>
              <xsl:apply-templates select="@*|node()"/>
          </xsl:copy>
      </xsl:template>-->

    <xsl:template match="md:SyncCustomer">
        <root>

            <xsl:choose>
                <xsl:when
                    test="not(md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:CommonName = '')">
                    <X_UDT_name>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:CommonName" />
                    </X_UDT_name>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of
                        select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:LegalName" />
                </xsl:otherwise>
            </xsl:choose>

            <X_UDT_partyID>
                <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
            </X_UDT_partyID>

            <X_UDT_parmaID>
                <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:ParmaNumber" />
            </X_UDT_parmaID>

            <X_UDT_customerType>
                <Code>C</Code>
            </X_UDT_customerType>

            <xsl:for-each
                select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Location/md:MainAddress">
                <X_UDT_Address_Custom>
                    <xsl:attribute name="_id">
                        <xsl:value-of select="../../../md:PartyID" />
                    </xsl:attribute>

                    <X_UDT_addressType>
                        <Code>Main</Code>
                    </X_UDT_addressType>

                    <X_UDT_usageType>
                        <Code>B</Code>
                    </X_UDT_usageType>

                    <X_UDT_addressLine1>
                        <xsl:value-of
                            select="md:Line1" />
                    </X_UDT_addressLine1>

                    <X_UDT_addressLine2>
                        <xsl:value-of
                            select="md:Line2" />
                    </X_UDT_addressLine2>

                    <X_UDT_addressLine3>
                        <xsl:value-of
                            select="md:Line3" />
                    </X_UDT_addressLine3>

                    <X_UDT_city>
                        <xsl:value-of
                            select="md:City" />
                    </X_UDT_city>

                    <X_UDT_country>
                        <Code>
                            <xsl:value-of
                                select="md:CountryCode" />
                        </Code>
                    </X_UDT_country>

                    <xsl:choose>
                        <xsl:when test="not(md:StateProvince = '')">
                            <X_UDT_province>
                                <xsl:value-of
                                    select="md:StateProvince" />
                            </X_UDT_province>
                        </xsl:when>
                        <xsl:otherwise>
                            <X_UDT_province>
                                <xsl:value-of
                                    select="md:County" />
                            </X_UDT_province>
                        </xsl:otherwise>
                    </xsl:choose>

                    <X_UDT_postalCode>
                        <xsl:value-of
                            select="md:PostalCode" />
                    </X_UDT_postalCode>

                    <X_UDT_postalCodeExtension>
                        <xsl:value-of
                            select="md:PostalCodeExtension" />
                    </X_UDT_postalCodeExtension>

                    <X_UDT_latitude>
                        <xsl:value-of
                            select="md:Latitude" />
                    </X_UDT_latitude>

                    <X_UDT_longitude>
                        <xsl:value-of
                            select="md:Longitude" />
                    </X_UDT_longitude>

                </X_UDT_Address_Custom>
            </xsl:for-each>

            <xsl:for-each
                select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Location/md:VisitAddress">
                <X_UDT_Address_Custom>
                    <xsl:attribute name="_id">
                        <xsl:value-of select="../../../md:PartyID" />
                    </xsl:attribute>

                    <X_UDT_addressType>
                        <Code>Visit</Code>
                    </X_UDT_addressType>

                    <X_UDT_usageType>
                        <Code>B</Code>
                    </X_UDT_usageType>

                    <X_UDT_addressLine1>
                        <xsl:value-of
                            select="md:Line1" />
                    </X_UDT_addressLine1>

                    <X_UDT_addressLine2>
                        <xsl:value-of
                            select="md:Line2" />
                    </X_UDT_addressLine2>

                    <X_UDT_addressLine3>
                        <xsl:value-of
                            select="md:Line3" />
                    </X_UDT_addressLine3>

                    <X_UDT_city>
                        <xsl:value-of
                            select="md:City" />
                    </X_UDT_city>

                    <X_UDT_country>
                        <Code>
                            <xsl:value-of
                                select="md:CountryCode" />
                        </Code>
                    </X_UDT_country>

                    <xsl:choose>
                        <xsl:when test="not(md:StateProvince = '')">
                            <X_UDT_province>
                                <xsl:value-of
                                    select="md:StateProvince" />
                            </X_UDT_province>
                        </xsl:when>
                        <xsl:otherwise>
                            <X_UDT_province>
                                <xsl:value-of
                                    select="md:County" />
                            </X_UDT_province>
                        </xsl:otherwise>
                    </xsl:choose>

                    <X_UDT_postalCode>
                        <xsl:value-of
                            select="md:PostalCode" />
                    </X_UDT_postalCode>

                    <X_UDT_postalCodeExtension>
                        <xsl:value-of
                            select="md:PostalCodeExtension" />
                    </X_UDT_postalCodeExtension>

                    <X_UDT_latitude>
                        <xsl:value-of
                            select="md:Latitude" />
                    </X_UDT_latitude>

                    <X_UDT_longitude>
                        <xsl:value-of
                            select="md:Longitude" />
                    </X_UDT_longitude>

                </X_UDT_Address_Custom>
            </xsl:for-each>

            <X_UDT_Identifier>
                <xsl:attribute name="_id">
                    <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                </xsl:attribute>
                <X_UDT_identifierType>
                    <Code>COC</Code>
                </X_UDT_identifierType>

                <X_UDT_identifierValue>
                    <xsl:value-of
                        select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:GlobalID/md:ChamberOfCommerce" />
                </X_UDT_identifierValue>

            </X_UDT_Identifier>

            <X_UDT_Identifier>
                <xsl:attribute name="_id">
                    <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                </xsl:attribute>
                <X_UDT_identifierType>
                    <Code>ON</Code>
                </X_UDT_identifierType>
                <X_UDT_identifierValue>
                    <xsl:value-of
                        select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:GlobalID/md:OrganisationNumber" />
                </X_UDT_identifierValue>

            </X_UDT_Identifier>

            <X_UDT_Identifier>
                <xsl:attribute name="_id">
                    <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                </xsl:attribute>
                <X_UDT_identifierType>
                    <Code>VAT</Code>
                </X_UDT_identifierType>
                <X_UDT_identifierValue>
                    <xsl:value-of
                        select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:GlobalID/md:VATNumber" />
                </X_UDT_identifierValue>

            </X_UDT_Identifier>

            <xsl:for-each
                select="//*[local-name(md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Phone)]">
                <X_UDT_Phone_Custom>
                    <xsl:attribute name="_id">
                        <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                    </xsl:attribute>

                    <country>
                        <Code>
                            <xsl:value-of
                                select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Location/md:MainAddress/md:CountryCode" />
                        </Code>
                    </country>

                    <phoneType>
                        <Code>Telephone</Code>
                    </phoneType>

                    <phoneUsageType>
                        <Code>Business</Code>
                    </phoneUsageType>

                    <phoneNumber>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Phone/md:Number" />
                    </phoneNumber>

                    <phoneNumberExtension>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Phone/md:Extension" />
                    </phoneNumberExtension>

                    <internationalPrefix>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Phone/md:InternationalPrefix" />
                    </internationalPrefix>

                </X_UDT_Phone_Custom>
            </xsl:for-each>

            <xsl:for-each
                select="//*[local-name(md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Fax)]">
                <X_UDT_Phone_Custom>
                    <xsl:attribute name="_id">
                        <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                    </xsl:attribute>
                    <country>
                        <Code>
                            <xsl:value-of
                                select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Location/md:MainAddress/md:CountryCode"
                            />
                        </Code>
                    </country>

                    <phoneType>
                        <Code>Fax</Code>
                    </phoneType>

                    <phoneUsageType>
                        <Code>Business</Code>
                    </phoneUsageType>

                    <phoneNumber>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Fax/md:Number"
                        />
                    </phoneNumber>

                    <phoneNumberExtension>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Fax/md:Extension"
                        />
                    </phoneNumberExtension>

                    <internationalPrefix>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Fax/md:InternationalPrefix"
                        />
                    </internationalPrefix>

                </X_UDT_Phone_Custom>
            </xsl:for-each>

            <X_UDT_AlternateName>
                <xsl:attribute name="_id">
                    <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                </xsl:attribute>
                <X_UDT_nameType>
                    <code>L</code>
                </X_UDT_nameType>
                <X_UDT_alternateName>
                    <xsl:value-of
                        select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:LegalName" />
                </X_UDT_alternateName>

            </X_UDT_AlternateName>

            <X_UDT_URL>
                <xsl:attribute name="_id">
                    <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                </xsl:attribute>

                <X_UDT_link>
                    <xsl:value-of
                        select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:WebSite" />
                </X_UDT_link>
            </X_UDT_URL>

            <X_UDT_Email_Custom>
                <xsl:attribute name="_id">
                    <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                </xsl:attribute>

                <electronicAddress>
                    <xsl:value-of
                        select="md:DataArea/md:Customers/md:Customer/md:PublicInformation/md:Communication/md:Email" />
                </electronicAddress>
            </X_UDT_Email_Custom>

            <X_UDT_KnownAs>
                <xsl:attribute name="_id">
                    <xsl:value-of select="md:DataArea/md:Customers/md:Customer/md:PartyID" />
                </xsl:attribute>
                <X_UDT_applicationType>
                    <Code>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:KnownAs/md:Application/md:ID" />
                    </Code>
                    <Name>
                        <xsl:value-of
                            select="md:DataArea/md:Customers/md:Customer/md:KnownAs/md:Application/md:Name" />
                    </Name>
                </X_UDT_applicationType>
                <X_UDT_applicationID>
                    <xsl:value-of
                        select="md:DataArea/md:Customers/md:Customer/md:KnownAs/md:ExternalID" />
                </X_UDT_applicationID>
            </X_UDT_KnownAs>

            <xsl:for-each select="md:Customerstructures/md:Customerstructure">
                <xsl:if test="md:Role = 'Legal'">
                    <x_udt_custlgl_cust_cust>
                        <X_UDT_partyID>
                            <xsl:value-of select="md:PartyID" />
                        </X_UDT_partyID>
                    </x_udt_custlgl_cust_cust>
                </xsl:if>
                <xsl:if
                    test="md:Role = 'Fleet'">
                    <x_udt_fleet_cust_cust>
                        <X_UDT_partyID>
                            <xsl:value-of select="md:PartyID" />
                        </X_UDT_partyID>
                    </x_udt_fleet_cust_cust>
                </xsl:if>
                <!-- <xsl:if
                    test="md:Role = 'Customer'">
                    <x_udt_custlgl_cust_cust>
                        <X_UDT_partyID>
                            <xsl:value-of select="md:PartyID" />
                        </X_UDT_partyID>
                    </x_udt_custlgl_cust_cust>
                </xsl:if> -->
            </xsl:for-each>
            <!-- <xsl:choose>
                <xsl:when test="not(volvo:ApplicationArea/volvo:CreationDateTime = '')">
                    <Src_last_update>
                        <xsl:value-of
                            select='concat(",volvo:ApplicationArea/volvo:CreationDateTime,")' />
                    </Src_last_update>
                </xsl:when>
                <xsl:otherwise> -->
            <Src_last_update>
                <xsl:value-of select="volvo:ApplicationArea/volvo:CreationDateTime" />
            </Src_last_update>
            <!-- </xsl:otherwise>
            </xsl:choose> -->
        </root>
    </xsl:template>

</xsl:stylesheet>