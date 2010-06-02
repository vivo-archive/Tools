/*
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
*/

if( vitroJsLoaded == null ){ 

  alert("seminar.js needs to have the code from vitro.js loaded first"); 

}



addEvent(window, 'load', init);



function init(){

//   if ($('monikerSelect').options.length=1) {
//      $('monikerSelectAlt').disabled = false;
//   }else{
//      $('monikerSelectAlt').disabled = true;
//   }

  $('monikerSelect').onchange = checkMonikers;  

  update();

}



function update(){ //updates moniker list when type is changed

  DWRUtil.useLoadingMessage();

  EntityDWR.monikers(createList,  document.getElementById("field2Value").value );
  
}



function createList(data) { //puts options in moniker select list

  fillList("monikerSelect", data, getCurrentMoniker() );

  var ele = $("monikerSelect");

  var opt = new Option("none","");
  ele.options[ele.options.length] = opt;

  var opt = new Option("[new moniker]","");
  ele.options[ele.options.length] = opt;

  DWRUtil.setValue("monikerSelect",getCurrentMoniker()); // getCurrentMoniker() is defined on jsp

  checkMonikers();
}



function checkMonikers(){ //checks if monikers is on [new moniker] and enables alt field

  var sel = $('monikerSelect');  

  if( sel.value == "" || sel.options.length <= 1){
    $('monikerSelectAlt').disabled = false;

  }else{

    $('monikerSelectAlt').disabled = true; 

  }        

}



function fillList(id, data, selectedtext) {

  var ele = $(id);

  if (ele == null)    {

    alert("fillList() can't find an element with id: " + id + ".");

    throw id;

  }



  ele.options.length = 0;     // Empty the list

  if (data == null) { return; }



  for (var i = 0; i < data.length; i++)    {

    var text = DWRUtil.toDescriptiveString(data[i]);

    var value = text;



    var opt = new Option(text, value);

    if (selectedtext != null && selectedtext == text){

      opt.selected=true;

    }

    ele.options[ele.options.length] = opt;

  }

}

