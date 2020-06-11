var map;
var marker;

function initMap() {
    var lat = document.getElementById('lat').getAttribute("value")
    var lng = document.getElementById('lng').getAttribute("value")

    console.log(lat)
    console.log(lng)

    var options = {
        center: new google.maps.LatLng(lat, lng),
        zoom: 15
    };

    map = new google.maps.Map(document.getElementById('map'), options);

    marker = new google.maps.Marker({
        position: new google.maps.LatLng(lat, lng),
        map: map,
        draggable: false
    });
}

google.maps.event.addDomListener(window, 'load', initMap);