var drawMap = function (continent) {
    var config = {
        id: 'map1',
        selector: '#svgWorldMap1',
        scale: 0.1,
        margin: '0',
        top: '50',
        height: '200',
        width: '200',
        inactiveCountryFill: '#4af',
        inactiveCountryStroke: '#fff',
        inactiveCountryStrokeWidth: 6,
        showCountryBoxOnMouserEnter: false,
        drawNorthAmerica: false,
        drawCentralAmerica: false,
        drawSouthAmerica: false,
        drawEurope: false,
        drawAfrica: false,
        drawAsia: false,
        drawOceania: false,
        drawAntarctic: false,
        onCountryMouseEnter: function (config) {
            var id = config.countryId;
        },
        onCountryMouseMove: function (config) {
            var id = config.countryId;
        },
        onCountryMouseOut: function (config) {
            var id = config.countryId;
        },
        onCountryMouseClick: function (countryId) {
            var id = countryId;
        }
    };
    var wm1 = WorldMap(config);
    var svg = $(config.selector).svg('get');
    if (continent && continent === "Europe") {
        config.scale = 0.4;
        createEurope(svg, config, [-1100, 0]);
    }
};
