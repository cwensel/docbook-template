<xsl:stylesheet version="1.0" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml"/>

  <xsl:template match="@*|node()">
     <xsl:copy>
        <xsl:apply-templates select="@*|node()"/>
     </xsl:copy>
  </xsl:template>

  <xsl:template match="svg:svg">
    <xsl:copy>
      <xsl:attribute name="viewBox">
        <xsl:text>0 0 </xsl:text>
        <xsl:value-of select="/svg:svg/@width"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="/svg:svg/@height"/>
      </xsl:attribute>

       <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>

  </xsl:template>

  <!-- Remove width, height, and viewBox attributes (substitute different things in later on when we've calculated a bounding box) -->
  <xsl:template match="svg:svg/@height"/>
  <xsl:template match="svg:svg/@width"/>
  <xsl:template match="svg:svg/@viewBox"/>

</xsl:stylesheet>
