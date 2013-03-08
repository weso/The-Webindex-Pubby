var maxCountriesToCompare = 4;

var regionSelectedFunction = function (e, code, isSelected, selectedRegions) {
    if (isSelected) {
        var countryName = map.getRegionName(code);
        if (selectedRegions.length <= maxCountriesToCompare) {
            centerMapOnRegions(map, selectedRegions);
            alert('Añadir país a la comparación: ' + countryName + ' ' + code);
        } else {
            removeSelectedCountry(selectedRegions, code);
            alert('No se pueden añadir mas países a la comparación');
        }
    } else {
        //centerMapOnRegions(map, selectedRegions);
        alert('Quitar de la compración')
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

