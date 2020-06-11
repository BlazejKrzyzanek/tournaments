var map;
var marker;

function initMap() {

    var lat = document.getElementById('lat').getAttribute("value")
    var lng = document.getElementById('lng').getAttribute("value")

    var options = {
        center: new google.maps.LatLng(lat, lng),
        zoom: 6
    };

    map = new google.maps.Map(document.getElementById('map'), options);

    marker = new google.maps.Marker({
        position: new google.maps.LatLng(lat, lng),
        map: map,
        draggable: true
    });

    google.maps.event.addListener(marker, 'dragend', function (event) {
        markerLocation();
    });

    map.addListener('click', function (event) {
        var clickedLocation = event.latLng;

        marker.setPosition(clickedLocation);
        markerLocation();
    });
}

function markerLocation() {
    var currentLocation = marker.getPosition();

    document.getElementById('lat').value = currentLocation.lat(); //latitude
    document.getElementById('lng').value = currentLocation.lng(); //longitude
}

google.maps.event.addDomListener(window, 'load', initMap);