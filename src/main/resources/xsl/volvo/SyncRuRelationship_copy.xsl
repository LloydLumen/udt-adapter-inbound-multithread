<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:md="http://www.cdb.volvo.com/masterdata/4_0"
    xmlns:volvo="http://www.volvo.com/group/common/1_0">
    <xsl:template match="/">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="/md:SyncAccount">
        <root>

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
                                <xsl:choose>
                                    <xsl:when
                                        test="//md:DataArea/md:Accounts/md:Account/CountryCode = 'MY'">
                                        <xsl:if
                                            test="not(md:PartyID=3461718290)">
                                            <xsl:if
                                                test="not(md:PartyID=6297540286) and
                                                    not(md:PartyID=8312988066)">
                                                <x_udt_custmarkt_cust_org>
                                                    <businessId>
                                                        <xsl:value-of
                                                            select="md:PartyID" />
                                                    </businessId>
                                                </x_udt_custmarkt_cust_org>
                                            </xsl:if>
                                        </xsl:if>
                                    </xsl:when>
                                    <xsl:when
                                        test="//md:DataArea/md:Accounts/md:Account/CountryCode = 'ZA'">
                                        <xsl:if
                                            test="not(md:PartyID='7470817535') and
                                                    not(md:PartyID='1681025380') and
                                                    not(md:PartyID='8767256646') and
                                                    not(md:PartyID='4528520562') and
                                                    not(md:PartyID='1305368723') and
                                                    not(md:PartyID='7723868734') and
                                                    not(md:PartyID='2288627573') and
                                                    not(md:PartyID='7278649608') and
                                                    not(md:PartyID='4587111832') and
                                                    not(md:PartyID='7455185126')">
                                            <xsl:if
                                                test="not(md:PartyID='6297540286') and
                                                        not(md:PartyID='8312988066')">
                                                <x_udt_custmarkt_cust_org>
                                                    <businessId>
                                                        <xsl:value-of
                                                            select="md:PartyID" />
                                                    </businessId>
                                                </x_udt_custmarkt_cust_org>
                                            </xsl:if>
                                        </xsl:if>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:if
                                            test="not(md:PartyID='6297540286') and
                                                    not(md:PartyID='8312988066')">
                                            <x_udt_custmarkt_cust_org>
                                                <businessId>
                                                    <xsl:value-of
                                                        select="md:PartyID" />
                                                </businessId>
                                            </x_udt_custmarkt_cust_org>
                                        </xsl:if>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:for-each>
                        </xsl:if>
                    </xsl:when>
                    <xsl:otherwise>
                        <x_udt_custmarkt_cust_org>
                            <businessId>
                                <xsl:value-of
                                    select="md:ResponsibleUnits/md:ResponsibleUnit/md:PartyID" />
                            </businessId>
                        </x_udt_custmarkt_cust_org>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>

            <Src_last_update>
                <xsl:value-of select="volvo:ApplicationArea/volvo:CreationDateTime" />
            </Src_last_update>


        </root>
    </xsl:template>

</xsl:stylesheet>