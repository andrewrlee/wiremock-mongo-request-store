
          var results;
          var setSelect = function(selectId, vals, withEmpty) {
            var store = $(selectId);
            store.children().remove();
            if (withEmpty) {
              store.prepend("<option value='' selected='selected'></option>");
            }
            $.each(vals, (key, v) => {
              store.append($("<option />").val(v).text(v));
            });
          };

          var reset = function() {
                $("#details-req").html("");
                $("#details-res").html("");
                $("#details-tags").html("");
                $("#details-fields").html("");
          }

          var getUrl = function(store, tag, field, value) {

             var url = (tag === "")
                 ? '/__admin/store/' + store + '/entries/'
                 : '/__admin/store/' + store + '/entries/tag/' + tag;

             var params = "";

             if (field !== "" && value !== "") {
                var searchTerm = {}
                searchTerm[field] = value;
                params = "?" + $.param( searchTerm );
             }

             return url + params;
          }

          var search = function() {
            var store = $('#store').find(":selected").text();
            var tag = $('#tag').find(":selected").text();
            var field = $('#field').find(":selected").text();
            var value = $('#value').val();

            var url = getUrl(store, tag, field, value)

            $.getJSON( url, function( data ) {

                results = data.entries;
                var timestamps = results.map(d => d.timestamp["$date"])
                var ul = $("<ul>");

                for (var i = 0, l = timestamps.length; i < l; ++i) {
                    ul.append("<li><a class='request' href='#' data-request-id='" + timestamps[i] + "'>" + timestamps[i] + "</a></li>");
                }
                $("#requests").html(ul);
                reset();
                $(".request").click(event =>  {
                  event.preventDefault();
                  var requestedId = $(event.currentTarget).data('request-id');

                  var found = results.find(e => e.timestamp["$date"] === requestedId);
                  console.log(found);
                  $("#details-req").html(JSON.stringify(found.request, null, 2));
                  $("#details-res").html(JSON.stringify(found.response, null, 2));
                  $("#details-tags").html(found.tags.join(", "));
                  $("#details-fields").html(JSON.stringify(found.fields, null, 2));

                });
             });

          };


          var url = '/__admin/store/fields'
          $.getJSON( url, function( data ) {

           const stores = Object.keys(data);
           const fields = new Set();
           const tags = new Set();

           for(var storeIndex in data){

               var store = data[storeIndex]

               for(var tag in store){
                 tags.add(tag);
                 for(var field in store[tag]) {
                    fields.add(store[tag][field]);
                 }
               }
            }

            setSelect('#store', stores)
            setSelect('#tag', Array.from(tags), true)
            setSelect('#field', Array.from(fields), true)
            search();
        });
        $(document).ready(function(){
            $("#search").click(function(event) {
               event.preventDefault();
               search();
            });
        });

