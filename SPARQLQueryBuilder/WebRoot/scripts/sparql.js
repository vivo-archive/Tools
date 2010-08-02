			// namespaces shown in the SPARQL query box
			var namespaces = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\nPREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\nPREFIX swrc: <http://swrc.ontoware.org/ontology#>\n\n";
			
			var level = 0;
			function init(){
				var url = "GetClazz.swl";
				
				var base = document.getElementById("subject(0,0)");
				var myAjax = new Ajax.Request( url, {method: "get", parameters: "", onComplete: function(originalRequest){
							var response = originalRequest.responseXML;
							var options = response.getElementsByTagName("option");
							for(i=0; i<options.length; i++){
								base[base.length] = new Option(options[i].firstChild.data, options[i].firstChild.data);
							}
							var subdiv = document.getElementById("subject(0)");
							subdiv.appendChild(document.createElement("br"));
							var addprop = document.createElement("input");
							addprop.type = "button";
							addprop.value = "Add Property";
							addprop.count = 0;
							addprop.level = 0;
							addprop.onclick = function() {
								return getProperty(this);
							}
							subdiv.appendChild(addprop);
							level ++;
						}
					}
				);
			}
			
			
			function getProperty(addprop){
				var url = "GetProperty.swl";
				var subject = document.getElementById("subject(" + addprop.level + ",0)").value;
				if (subject == ""){
					alert("Please select a class...");
				}
				else{
					var params = "subject=" + subject;
					var myAjax = new Ajax.Request( url, {method: "get", parameters: params, onComplete: function(originalRequest){
								var response = originalRequest.responseXML;
								var property = document.createElement("select");
								property.id = "predicate(" + addprop.level + "," + addprop.count + ")";
								property[property.length] = new Option("Properties", "");
								var options = response.getElementsByTagName('option');
								
								for(i=0; i<options.length; i++){
									property[property.length] = new Option(options[i].firstChild.data, options[i].firstChild.data);
								}
								
								property.level = addprop.level;
								property.count = addprop.count;
								
								property.onchange = function() {
									return getObject(this);
								}
								
								var prediv = document.getElementById("predicate(" + addprop.level + ")");
								prediv.appendChild(property);
								
								addprop.count += 1
								prediv.appendChild(document.createElement("br"));
							}
						}
					);
				}
			}
			
			function getObject(property){
				var url = "GetObject.swl";
				
				
				var subject = document.getElementById("subject(" + property.level + ",0)").value;
				
				//DEL PROPERTY
				var delprop = document.createElement("input");
				delprop.type = "button";
				delprop.value = "Delete";
				delprop.count = property.count;
				delprop.level = property.level;
				delprop.onclick = function() {
					return delProperty(this);
				}
				var prediv = document.getElementById("predicate(" + property.level + ")");
				prediv.insertBefore(delprop, property.nextSibling);
				
				
				var predicate = property.value;
				
				var params = "subject=" + subject + "&predicate=" + predicate;
				
				var myAjax = new Ajax.Request( url, {method: "get", parameters: params, onComplete: function(originalRequest){
								var response = originalRequest.responseXML;
								var objdiv = document.getElementById("object(" + property.level + ")");
								var options = response.getElementsByTagName('option');
								var obj = document.getElementById("object(" + property.level + "," + property.count + ")");
								if (obj == null){
									if (options.length > 0){
										obj = document.createElement("select");
										obj[obj.length] = new Option("Classes", "");
										for(i=0; i<options.length; i ++){
											obj[obj.length] = new Option(options[i].firstChild.data, options[i].firstChild.data);
										}
										obj.onchange = function(){
											return addClass(this);
										}
										
									}
									else{
										obj = document.createElement("input");
										obj.type = "text";
									}
									obj.id = "object(" + property.level + "," + property.count + ")";
									
									obj.level = property.level;
									obj.count = property.count;
									objdiv.appendChild(obj);
									objdiv.appendChild(document.createElement("br"));
								}
								else{
									var objpar = obj.parentNode;
									
									if (options.length > 0){
										var newobj = document.createElement("select");
										newobj[newobj.length] = new Option("Classes", "");
										for(i=0; i<options.length; i ++){
											newobj[newobj.length] = new Option(options[i].firstChild.data, options[i].firstChild.data);
										}
										newobj.onchange = function(){
											return addClass(this);
										}
										
									}
									else{
										newobj = document.createElement("input");
										newobj.type = "text";
									}
									newobj.id = "object(" + property.level + "," + property.count + ")";
									
									newobj.level = property.level;
									newobj.count = property.count;
									objpar.replaceChild(newobj, obj);
								}
							}
						}
					);
			}
			
			function addClass(obj){
				addClazz();
				
				var subject = document.createElement("select");
				
				subject[subject.length] = new Option(obj.value, obj.value);
				subject.level = level;
				level ++;
				subject.count = 0;
				subject.id = "subject(" + subject.level + "," + subject.count + ")";
				
				var subdiv = document.getElementById("subject(" + subject.level +")");
				subdiv.appendChild(subject);
				
				var delclazz = document.createElement("input");
				delclazz.type = "button";
				delclazz.value = "Delete";
				delclazz.count = subject.count;
				delclazz.level = subject.level;
				delclazz.onclick = function() {
					return delClazz(this.level);
				}
				subdiv.appendChild(delclazz);
				subdiv.appendChild(document.createElement("br"));
				
				var addprop = document.createElement("input");
				addprop.type = "button";
				addprop.value = "Add Property";
				addprop.count = subject.count;
				addprop.level = subject.level;
				addprop.onclick = function() {
					return getProperty(this);
				}
				subdiv.appendChild(addprop);
				
				
			}
			
			function addClazz(){
				var builder = document.getElementById("builder");
				
				var clazz = document.createElement("tr");
				clazz.id = "clazz(" + level + ")";
				var subject = document.createElement("td");
				subject.id = "subject(" + level + ")";
				var predicate = document.createElement("td");
				predicate.id = "predicate(" + level + ")";
				var object = document.createElement("td");
				object.id = "object(" + level + ")";
				
				clazz.appendChild(subject);
				clazz.appendChild(predicate);
				clazz.appendChild(object);
				
				builder.appendChild(clazz);
			}
			
			function delClazz(level){
				var clazz = document.getElementById("clazz(" + level +")");
				var builder = document.getElementById("builder");
				builder.removeChild(clazz);
			}
			
			function delProperty(delprop){
				var sub = document.getElementById("predicate(" + delprop.level + "," + delprop.count + ")");
				var obj = document.getElementById("object(" + delprop.level + "," + delprop.count + ")");
				var subdiv = document.getElementById("predicate(" + delprop.level +")");
				var objdiv = document.getElementById("object(" + delprop.level +")");
				
				
				subdiv.removeChild(sub.nextSibling.nextSibling);
				subdiv.removeChild(sub.nextSibling);
				subdiv.removeChild(sub);
				
				objdiv.removeChild(obj.nextSibling);
				objdiv.removeChild(obj);
				
			}
			
			function genQuery(){
				var items = new Array();
				var criterias = new Array();
				var clazz = new Array();
				var number = 0;
				var _sub;
				var _obj;
				
				
				for (i=0; i < level; i++){
					var subjects = document.getElementById("subject(" + i + ")");
					if (subjects == null){
						continue;
					}
					var subNodes = subjects.getElementsByTagName("select");
					var sub = subNodes[0].value;
					if (!clazz[sub.substring(sub.indexOf(":") + 1)]){
						clazz[sub.substring(sub.indexOf(":") + 1)] = 1;
						_sub = sub.substring(sub.indexOf(":") + 1) + 1;
					}
					else{
						_sub = sub.substring(sub.indexOf(":") + 1) + clazz[sub.substring(sub.indexOf(":") + 1)];
					}
					var subname = "?" + _sub;
					criterias[criterias.length] = subname + " rdf:type " + sub + " .";
					
					
					var predicates = document.getElementById("predicate(" + i + ")");
					var preNodes = predicates.getElementsByTagName("select");
					var num = preNodes.length;
					var offset = 0;
					for (j=0; j<num; j++){
						var obj = document.getElementById("object(" + i + "," + j + ")");
						while (obj == null){
							offset++;
							var newj = j + offset;
							obj = document.getElementById("object(" + i + "," + newj + ")");
						}
						var pre = preNodes[j];
						if (obj.tagName == "INPUT"){
							var objname = subname + "_" + pre.value.substring(pre.value.indexOf(":") + 1);
							
							criterias[criterias.length] = subname + " " + pre.value + " " + objname + " .";
							items[items.length] = objname;
							if (obj.value != ""){
								criterias[criterias.length] = "FILTER REGEX (str(" + objname + "), '" + obj.value + "', 'i')";
							}
						}
						else{
							if (!clazz[obj.value.substring(obj.value.indexOf(":") + 1)]){
								clazz[obj.value.substring(obj.value.indexOf(":") + 1)] = 1;
								_obj = obj.value.substring(obj.value.indexOf(":") + 1) + 1;
							}
							else{
								number = clazz[obj.value.substring(obj.value.indexOf(":") + 1)] + 1;
								clazz[obj.value.substring(obj.value.indexOf(":") + 1)] = number
								_obj = obj.value.substring(obj.value.indexOf(":") + 1) + number;
							}
							var objname = "?" + _obj;
							criterias[criterias.length] = subname + " " + pre.value + " " + objname + " .";
						}
					}
					
				}
				var item = "distinct " + items.join(" ");
				var criteria = criterias.join("\n");
				
				var query = namespaces+ "SELECT " + item + "\nWHERE{\n" + criteria + "}\n";
				var quediv = document.getElementById("query");
				var quetextarea = document.getElementById("sparqlquery");
				quediv.style.visibility = "visible";
				quetextarea.value = query;
				//alert(query);
			}