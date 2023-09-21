<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:md="http://www.udtrucks.com/Customer/5_0"
    xmlns:udt="http://www.udtrucks.com/group/common/1_0">
    <xsl:variable name="ExclusionList"
                  select="document('../../ExclusionListFile/SyncRUExclusionList.xml')"></xsl:variable>

    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="/md:SyncAccount">
        <root>

            <xsl:variable name="countryCode" select="//md:DataArea/md:Accounts/md:Account/CountryCode"></xsl:variable>
            <X_UDT_name>
                <xsl:value-of
                    select="md:DataArea/md:Accounts/md:Account/md:PublicInformation/md:CommonName" />
            </X_UDT_name>

            <X_UDT_partyID>
                <xsl:value-of select="md:DataArea/md:Accounts/md:Account/md:PartyID" />
            </X_UDT_partyID>

            <X_UDT_parmaID>
                <xsl:value-of select="md:DataArea/md:Accounts/md:Account/md:ParmaNumber" />
            </X_UDT_parmaID>

            <X_UDT_customerType>
                <Code>C</Code>
            </X_UDT_customerType>

            <xsl:for-each
                    select="md:DataArea/md:Accounts/md:Account/md:ResponsibleUnitRelationships/md:ResponsibleUnitRelationship">
                <xsl:choose>
                    <xsl:when
                            test="count(md:ResponsibleUnits/md:ResponsibleUnit)&gt;1">
                        <xsl:if
                                test="md:BABrand/md:Brand/md:Code = 130">
                            <xsl:for-each select="md:ResponsibleUnits/md:ResponsibleUnit">
                                <xsl:if
                                        test="not(md:PartyID=$ExclusionList//root/CommonPartyIDs/PartyID)">
                                    <xsl:if
                                            test="not(md:PartyID=$ExclusionList//root/Countrys/Country[@code=$countryCode]/PartyID)">
                                        <x_udt_custmarkt_cust_org>
                                            <businessId>
                                                <xsl:value-of
                                                        select="md:PartyID"/>
                                            </businessId>
                                        </x_udt_custmarkt_cust_org>
                                    </xsl:if>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:if>
                    </xsl:when>
                    <xsl:otherwise>
                        <x_udt_custmarkt_cust_org>
                            <businessId>
                                <xsl:value-of
                                        select="md:ResponsibleUnits/md:ResponsibleUnit/md:PartyID"/>
                            </businessId>
                        </x_udt_custmarkt_cust_org>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            
            <Src_last_update>
                <xsl:value-of select="udt:ApplicationArea/udt:CreationDateTime" />
            </Src_last_update>
            


        </root>
    </xsl:template>

</xsl:stylesheet>