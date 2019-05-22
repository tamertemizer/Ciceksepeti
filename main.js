var routes;

// Initialize and add the map
function initMap() {
    const iconBase = 'http://maps.google.com/mapfiles/kml/paddle/';
    const icons = [
        iconBase + 'red-circle.png',
        iconBase + 'grn-circle.png',
        iconBase + 'blu-circle.png'
    ];
    const storeIcons = [
        iconBase + 'red-stars.png',
        iconBase + 'grn-stars.png',
        iconBase + 'blu-stars.png'
    ];

    var map = new google.maps.Map(
        document.getElementById('map'), {zoom: 14, center: routes[0][0]});

    var poly = {};
    poly[0] = new google.maps.Polyline({
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 3,
        map: map,
    });
    poly[1] = new google.maps.Polyline({
        strokeColor: '#00FF00',
        strokeOpacity: 1.0,
        strokeWeight: 3,
        map: map,
    });
    poly[2] = new google.maps.Polyline({
        strokeColor: '#0000FF',
        strokeOpacity: 1.0,
        strokeWeight: 3,
        map: map,
    });

    for (let i = 0; i < routes.length; i++) {
        // Create markers for stores
        var marker = new google.maps.Marker({position: routes[i][0], map: map, icon: storeIcons[i]});
        var storeLatlng = new google.maps.LatLng(routes[i][0]);
        var path = poly[i].getPath();
        path.push(storeLatlng);
        for (let j = 1; j < routes[i].length; j++) {
            var currLatLng = new google.maps.LatLng(routes[i][j]);
            // Workaround: Old output json didn't have title.
            routes[i][j].title = routes[i][j].title || "";
            // Create markers for orders
            marker = new google.maps.Marker({
                position: routes[i][j],
                map: map,
                icon: icons[i],
                title: "Siparis " + routes[i][j].title
            });
            // Draw lines for the route
            path.push(currLatLng);
        }
        path.push(storeLatlng);
    }
}

function readTextFile(file) {
    var rawFile = new XMLHttpRequest();
    rawFile.open("GET", file, false);
    rawFile.onreadystatechange = function () {
        if (rawFile.readyState === 4) {
            if (rawFile.status === 200 || rawFile.status == 0) {
                var allText = rawFile.responseText;
                routes = JSON.parse(allText);
            }
        }
    };
    rawFile.send(null);
}

readTextFile("output.json");