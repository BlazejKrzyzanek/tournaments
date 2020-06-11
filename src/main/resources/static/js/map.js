var map;
var marker = false;

function initMap() {

    var options = {
        center: new google.maps.LatLng(52.000099, 18.999957),
        zoom: 6
    };

    map = new google.maps.Map(document.getElementById('map'), options);

    map.addListener('click', function (event) {
        var clickedLocation = event.latLng;
        if (marker === false) {
            marker = new google.maps.Marker({
                position: clickedLocation,
                map: map,
                draggable: true
            });

            google.maps.event.addListener(marker, 'dragend', function (event) {
                markerLocation();
            });
        } else {
            marker.setPosition(clickedLocation);
        }
        markerLocation();
    });
}

function markerLocation() {
    var currentLocation = marker.getPosition();

    document.getElementById('lat').value = currentLocation.lat(); //latitude
    document.getElementById('lng').value = currentLocation.lng(); //longitude
}

google.maps.event.addDomListener(window, 'load', initMap);