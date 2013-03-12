/*This variable represents a map that will be painted*/
var map = null;
var selector = 'path[data-code=';
var regionStyle = {
    initial: {
        fill: '#BBBBBB'
    },
    selected: {
        fill: '#F4A582'
    }
};

/*
This function draw a new world map in the element of the html that has the selector
that the function receive as a parameter
*/
function drawWorldMap(selector, onRegionSelectedEvent) {
        map = new jvm.WorldMap({
            container: $(selector),
            map: 'world_mill_en',
            zoomOnScroll: false,
            regionsSelectable: true,
            regionStyle: regionStyle,
            onRegionSelected: onRegionSelectedEvent
        });
}

/*
This function draw a world map centered on a country. This function receive as a parameter
the selector of the html element that will contain the map, the name of the function that
defines the behavior of the map when a region of the map is selected. Other paramater is 
the code of the country with ISO 3166-1 alfa-2 normalization. The other two parameteres 
are latitude and longitude of the country expressed in degrees
*/
function drawMapCenterOnACountry(selector, onRegionSelectedEvent, code, lat, long) {
    map = new jvm.WorldMap({
        container: $(selector),
        map: 'world_mill_en',
        zoomOnScroll: true,
        regionsSelectable: true,
        regionStyle: regionStyle,
        onRegionSelected: onRegionSelectedEvent
    });
    var center = map.latLngToPoint(lat, long);
    map.setFocus(code, center.x, center.y);
    var selectedRegions = new Array(code);
    map.setSelectedRegions(selectedRegions);
}

/*
This function center the map that receives as a parameter to see all the countries 
that are specificated in the paramater regions
*/
function centerMapOnRegions(map, regions) {
    map.setFocus(regions, 0, 0);
}

/*
This function create a group of countries and give them a colour
*/
function createGroup(color, regions) {
	if(regions == null) {
		regions = map.getSelectedRegions();
	}
    for (var i = 0; i < regions.length; i++) {
        $(selector + regions[i] + ']').css("fill", color);
    }
    map.clearSelectedRegions();
    return regions;
}

/*
This function delete the group of countries that receive as a parameter
*/
function deleteGroup(regions) {
    for (var i = 0; i < regions.length; i++) {
        $(selector + regions[i] + ']').css("fill", regionStyle.initial.fill);
    }
}

