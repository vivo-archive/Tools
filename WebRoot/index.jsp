<%@ page language="java" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">
<HTML>
	<HEAD>
		<TITLE>SPARQL Query Builder</TITLE>
		
		<META content="text/html; charset=utf-8" http-equiv="Content-Type">
		<style type="text/css">
			td {
				vertical-align: top;
				border: 1px dotted;
			}
		</style>
		
		<script type="text/javascript" src="scripts/prototype.js"></script>
		<script type="text/javascript" src="scripts/sparql.js"></script>
	</HEAD>
	<BODY onload="init()">
		<div id="sparql" style="width: 800px; margin: 0 auto;">
			<div id="header">SPARQL Query Builder</div>
			<table id="builder" style="width: 800px">
				<tr>
					<td width="33%">Subject</td>
					<td width="33%">Predicate</td>
					<td width="34%">Object</td>
				</tr>
				<tr id="clazz(0)">
					<td id="subject(0)" width="33%">
						<select id="subject(0,0)">
							<option value="">Thing</option>
						</select>
					</td>
					<td id="predicate(0)">
						
					</td>
					<td id="object(0)" width="34%">
						
					</td>
				</tr>
			</table>
			<div><input type="button" value="Generate Query" onclick="genQuery();"/></div>
			<div id="query" style="visibility:hidden;">
				<form action="GetResult.swl" method="post">
					<textarea id="sparqlquery" name="sparqlquery" rows="20" cols="111"></textarea>
					<input type="submit" value="Query"/>
				</form>
			</div>
		</div>
	</BODY>
</HTML>
