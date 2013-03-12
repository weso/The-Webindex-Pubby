var regionSelectedFunction = function (e, code, isSelected, selectedRegions) {
    $('#bt_create').removeAttr('disabled')
    if (isSelected) {
        var countryName = map.getRegionName(code);
        //centerMapOnRegions(map, selectedRegions);
    } else {
        //centerMapOnRegions(map, selectedRegions);
        //alert('Quitar de la compración')
    }
}

function removeSelectedCountry(selectedRegions, code) {
    var selected = '';
    for (var i = 0; i < selectedRegions.length; i++) {
        if(selectedRegions[i] != code) {
            selected += selectedRegions[i] + ',';
        }
    }
    selected = selected.substring(0, selected.lastIndexOf(','));
    map.clearSelectedRegions();
    map.setSelectedRegions(selected.split(','));
}

