<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

    <xsl:template name="millisecs-to-ISO">
    <xsl:param name="millisecs"/>
    <xsl:param name="JDN" select="floor($millisecs div 86400000) + 2440588"/>
    <xsl:param name="mSec" select="$millisecs mod 86400000"/>

    <xsl:param name="f" select="$JDN + 1401 + floor((floor((4 * $JDN + 274277) div 146097) * 3) div 4) - 38"/>
    <xsl:param name="e" select="4*$f + 3"/>
    <xsl:param name="g" select="floor(($e mod 1461) div 4)"/>
    <xsl:param name="h" select="5*$g + 2"/>

    <xsl:param name="d" select="floor(($h mod 153) div 5 ) + 1"/>
    <xsl:param name="m" select="(floor($h div 153) + 2) mod 12 + 1"/>
    <xsl:param name="y" select="floor($e div 1461) - 4716 + floor((14 - $m) div 12)"/>

    <xsl:param name="H" select="floor($mSec div 3600000)"/>
    <xsl:param name="M" select="floor($mSec mod 3600000 div 60000)"/>
    <xsl:param name="S" select="$mSec mod 60000 div 1000"/>

    <xsl:value-of select="concat($y, format-number($m, '-00'), format-number($d, '-00'))" />
    <xsl:value-of select="concat(format-number($H, 'T00'), format-number($M, ':00'), format-number($S, ':00'))" />
</xsl:template>
</xsl:stylesheet>