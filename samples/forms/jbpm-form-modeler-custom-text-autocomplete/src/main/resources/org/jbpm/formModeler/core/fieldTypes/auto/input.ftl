  <script>  
  	<#if restURL??>
    $( function() {
    var cache = {};
    $( "#${inputId}" ).autocomplete({
      minLength: 2,
      source: function( request, response ) {
        var term = request.term;
        if ( term in cache ) {
          response( cache[ term ] );
          return;
        }

        $.getJSON( "${restURL}", request, function( data, status, xhr ) {
          <#if property??>
          
		  var listOfValues =[];
		  for( var i in data ) {
			if (data[i].hasOwnProperty("${property}")) {
			  value = data[i].${property};
			  if (value.indexOf(term) > -1)
			    listOfValues.push(value);
			}
		  }

          cache[ term ] = listOfValues;
          response( listOfValues );
          <#else>
          
          cache[ term ] = data;
          response( data );          
          
		  </#if>
        });
      }
    });
    });
    </#if>
  </script>
<div>
        <div>
            <input type="text" class="dynInputStyle skn-input" name="${inputId}" id="${inputId}"  onchange="processFormInputChange(this);" size="25" maxlength="4000"/>
        </div>
</div>