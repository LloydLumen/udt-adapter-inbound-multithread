<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/root">
        <root>
            <X_UDT_name>
                <xsl:value-of
                    select="X_UDT_name" />
            </X_UDT_name>
            <X_UDT_partyID>
                <xsl:value-of select="X_UDT_partyID" />
            </X_UDT_partyID>
            <X_UDT_parmaID>
                <xsl:value-of select="X_UDT_parmaID" />
            </X_UDT_parmaID>
            <X_UDT_customerType>
                <Code>C</Code>
            </X_UDT_customerType>
            <x_udt_custmarkt_cust_org>
                <businessId>
                    <xsl:value-of
                        select="x_udt_custmarkt_cust_org[position()=last()]/businessId" />
                </businessId>
            </x_udt_custmarkt_cust_org>
            <Src_last_update>
                <xsl:value-of select="Src_last_update" />
            </Src_last_update>
        </root>
    </xsl:template>
</xsl:stylesheet>