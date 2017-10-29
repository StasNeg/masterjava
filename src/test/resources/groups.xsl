<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" encoding="UTF-8" indent="yes" omit-xml-declaration="yes"/>
    <xsl:template match="*">
        <html>
            <head>
                <title>Groups</title>
            </head>
            <body>
                <table border="1">
                    <tr>
                        <th>Group Name</th>
                        <th>Group Type</th>
                    </tr>
                    <xsl:for-each select="/*[name()='Payload']/*[name()='Projects']/*[name()='Project'][@name=$projectName]/*[name()='Group']">
                        <tr>
                            <td>
                                <xsl:value-of select="@id" />
                            </td>
                            <td>
                                <xsl:value-of select="@type" />
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
    <xsl:param name="projectName"/>
</xsl:stylesheet>