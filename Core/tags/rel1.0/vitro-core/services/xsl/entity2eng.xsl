<?xml version="1.0"?>
<!--
Copyright (c) 2010, Cornell University
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice,
      this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice,
      this list of conditions and the following disclaimer in the documentation
      and/or other materials provided with the distribution.
    * Neither the name of Cornell University nor the names of its contributors
      may be used to endorse or promote products derived from this software
      without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">    

<!-- bdc34:  -->
<!--  For converting an Entity to be displayed using the cals css -->

	<xsl:template match="/">
			<xsl:apply-templates />
	</xsl:template>

   	<xsl:template match="/entity">
   	<div class="entity">
      <p>
        <h4><xsl:value-of select="name"/> | <xsl:value-of select="moniker"/> 
        <xsl:if test="url"> | <i><xsl:copy-of select="url"/></i> </xsl:if></h4>
        <xsl:if test="campus_address"><br/>address: <xsl:value-of select="campus_address"/></xsl:if>
        <xsl:if test="campus_phone"><br/>phone: <xsl:value-of select="campus_phone"/></xsl:if>
        <xsl:if test="email_address"><br/>email: <xsl:value-of select="email_address"/></xsl:if>
      </p>
      <xsl:if test="imageThumb">
        <p class="thumbnail"><img src="images/people/{imageThumb}" width="150"/></p>    
      </xsl:if>
      <xsl:apply-templates select="PropertyInstances"/>
      <div class="entityBody">
        <h5><xsl:value-of select="blurb"/></h5>
        <xsl:copy-of select="description"/>
      </div>          
      </div>
  </xsl:template>

  <xsl:template match="PropertyInstances">
      <!--p class="entityRelations"><xsl:value-of select="entity"/>:</p-->
      <ul class="entityListElements">
        <xsl:apply-templates select="entity"/>
      </ul>    
  </xsl:template>

  <xsl:template match="entity" >
    <li>
      <!--a class="entityLink" href="entityxml?id={@id}&amp;dir=eng"><xsl:value-of select="name"/></a-->
      <xsl:if test="name"><xsl:copy-of select="name"/></xsl:if>
      <xsl:if test="moniker"> | <xsl:value-of select="moniker"/></xsl:if> 
      <xsl:if test="url"> | <i><xsl:copy-of select="url"/></i></xsl:if>
      </li>
  </xsl:template>
</xsl:stylesheet>
