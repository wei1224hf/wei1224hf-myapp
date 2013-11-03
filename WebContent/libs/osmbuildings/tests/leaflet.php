<!DOCTYPE html>
<html>
<head>
    <title>OSM Buildings - Leaflet</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8">
	<style>
    html, body {
        border: 0;
        margin: 0;
        padding: 0;
        width: 100%;
        height: 100%;
        overflow: hidden;
    }
    #map {
        height: 100%;
    }
    </style>
    <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.6.4/leaflet.css">
    <script src="http://cdn.leafletjs.com/leaflet-0.6.4/leaflet.js"></script>
    <script src="../dist/OSMBuildings-Leaflet.js"></script>
</head>

<body>
    <div id="map"></div>

    <script>
    var map = new L.Map('map').setView([52.50440, 13.33522], 17);

    new L.TileLayer(
        'http://{s}.tiles.mapbox.com/v3/osmbuildings.map-c8zdox7m/{z}/{x}/{y}.png',
        { attribution: 'Map tiles &copy; <a href="http://mapbox.com">MapBox</a>', maxZoom: 17 }
    ).addTo(map);

    var osmb = new OSMBuildingsr(map).loadData();
    L.control.layers({}, { Buildings: osmb }).addTo(map);
    </script>
</body>
</html>