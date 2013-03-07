var lastPoint = [0, 0];
var currentCountryId = '';
var lastCountryId = '';
var timer;
var countryBoxFadeOut = false;

function WorldMap(config) {
    var inactiveCountryOpacity = 1.0;
    var inactiveCountryFill = '#4af';
    var inactiveCountryStroke = '#fff';
    var inactiveCountryStrokeWidth = 3;

    var activeCountryOpacity = 1.0;
    var activeCountryFill = 'orange';
    var activeCountryStroke = '#fff';
    var activeCountryStrokeWidth = 3;

    var showCountryBoxOnMouserEnter = true;

    var drawNorthAmerica = true;
    var drawCentralAmerica = true;
    var drawSouthAmerica = true;
    var drawEurope = true;
    var drawAfrica = true;
    var drawAsia = true;
    var drawOceania = true;
    var drawAntarctic = true;

    var margin = '0';
    var height = '100%';
    var width = '100%';

    if (config.inactiveCountryOpacity == null)
        config.inactiveCountryOpacity = inactiveCountryOpacity;

    if (config.inactiveCountryFill == null)
        config.inactiveCountryFill = inactiveCountryFill;

    if (config.inactiveCountryStroke == null)
        config.inactiveCountryStroke = inactiveCountryStroke;

    if (config.inactiveCountryStrokeWidth == null)
        config.inactiveCountryStrokeWidth = inactiveCountryStrokeWidth;

    if (config.activeCountryOpacity == null)
        config.activeCountryOpacity = activeCountryOpacity;

    if (config.activeCountryFill == null)
        config.activeCountryFill = activeCountryFill;

    if (config.activeCountryStroke == null)
        config.activeCountryStroke = activeCountryStroke;

    if (config.activeCountryStrokeWidth == null)
        config.activeCountryStrokeWidth = activeCountryStrokeWidth;

    if (config.showCountryBoxOnMouserEnter == null)
        config.showCountryBoxOnMouserEnter = showCountryBoxOnMouserEnter;

    if (config.drawNorthAmerica == null)
        config.drawNorthAmerica = drawNorthAmerica;

    if (config.drawCentralAmerica == null)
        config.drawCentralAmerica = drawCentralAmerica;

    if (config.drawSouthAmerica == null)
        config.drawSouthAmerica = drawSouthAmerica;

    if (config.drawEurope == null)
        config.drawEurope = drawEurope;

    if (config.drawAfrica == null)
        config.drawAfrica = drawAfrica;

    if (config.drawAsia == null)
        config.drawAsia = drawAsia;

    if (config.drawOceania == null)
        config.drawOceania = drawOceania;

    if (config.drawAntarctic == null)
        config.drawAntarctic = drawAntarctic;

    if (config.margin == null)
        config.margin = margin;

    if (config.height == null)
        config.height = height;

    if (config.width == null)
        config.width = width;

    var svg = $(config.selector).svg('get');

    $(config.selector).css('margin', config.margin);
    $(config.selector).css('height', config.height);
    $(config.selector).css('width', config.width);

    if (!config.countryId)
        config.countryId = 0;

    $(config.selector).svg({ id: 'worldMap', onLoad: function (svg) {
        createPaths(svg, config);
        }
    });

    return this;
}

function createPaths(svg, config) {
    
    if (config.drawSouthAmerica)
        createSouthAmerica(svg, config);

    if (config.drawCentralAmerica)
        createCentralAmerica(svg, config);

    if (config.drawNorthAmerica)
        createNorthAmerica(svg, config);

    if (config.drawAfrica)
        createAfrica(svg, config, [29.90172, 45.07447]);

    if (config.drawAsia)
        createAsia(svg, config, [29.90172, 45.07447]);

    if (config.drawEurope)
        createEurope(svg, config, [29.90172, 45.07447]);

    if (config.drawOceania)
        createOceania(svg, config);

    if (config.drawAntarctic)
        createAntarctic(svg, config);

    createTextBox(config, svg);
}

function drawCountries(svg, config, countries, translate) {
    //Let's draw one country at a time
    for (var c = 0; c < countries.length; c++) {
        var country = countries[c];
        var g;

        if (!translate) {
            translate = [0, 0];
        }

        if (country.translate) {
            g = svg.group({ id: country.id, 
                fill: config.inactiveCountryFill, stroke: config.inactiveCountryStroke, strokeWidth: config.inactiveCountryStrokeWidth,
                transform: 'translate(' + (country.translate[0] + translate[0]) * config.scale + ',' + (country.translate[1] + translate[1]) * config.scale + ') scale(' + config.scale + ',' + config.scale + ')' });
        }
        else {
            g = svg.group({ id: country.id,
                fill: config.inactiveCountryFill, stroke: config.inactiveCountryStroke, strokeWidth: config.inactiveCountryStrokeWidth,
                transform: 'translate(' + (translate[0]) * config.scale + ',' + (translate[1]) * config.scale + ') scale(' + config.scale + ',' + config.scale + ')'
            });
        }

        var defs = svg.defs(g);
        
        //Each country has a collection of path. Let's draw them
        for (var i = 0; i < country.pathCollection.length; i++) {
            var splitted = country.pathCollection[i].split(' ');

            var path = svg.createPath();

            var index = 0;
            while (index < splitted.length) {
                var command = splitted[index];

                switch (command) {
                    //M x,y = "Move to point (x,y)", that is, the path will start at this absolute position (x,y) 
                    case 'M':
                        var moveconfig1 = splitted[index + 1].split(',');
                        path.move(moveconfig1[0], moveconfig1[1]);
                        index += 2;
                        break;
                    //C x1,y1 x2,y2 x3,y3 = "Curve (x1,y1,x2,y2,x3,y3)", and draws a bézier curve using 3 control points (xn,yn) 
                    case 'C':
                        var curveCconfig1 = splitted[index + 1].split(',');
                        var curveCconfig2 = splitted[index + 2].split(',');
                        var curveCconfig3 = splitted[index + 3].split(',');
                        path.curveC(curveCconfig1[0], curveCconfig1[1],
                                    curveCconfig2[0], curveCconfig2[1],
                                    curveCconfig3[0], curveCconfig3[1]
                                    );
                        index += 4;
                        break;
                    case 'L':
                        //L x,y = "Straight Line (x,y)", a line segment starting at the current position ans ending in (x,y) point
                        var lineconfig1 = splitted[index + 1].split(',');
                        path.line(lineconfig1[0], lineconfig1[1]);
                        index += 2;
                        break;
                }
            }

            svg.path(g, path, { id: country.id, countryId: country.id });
        }

        $(svg.root()).bind('mousemove',
            function (path) {
                var offset = $(config.selector).position();

                $('#' + config.id + 'box').attr('transform', 'translate(' + (path.pageX - $(this).position().left) + ' ' + (path.pageY - $(this).position().top) + ')');
            });

            $('#' + country.id, svg.root()).bind('mousemove',
            function (path) {
                var g = path.target.parentNode;

                if (countryBoxFadeOut) {

                    if (config.showCountryBoxOnMouserEnter &&
                        (lastPoint[0] != path.pageX &&
                        lastPoint[1] != path.pageY)) {
                        showCountryBox(config, svg);
                    }

                    timer = setTimeout(function () {
                        if (lastCountryId == currentCountryId) {
                            clearTimeout(timer);
                            hideCountryBox(config, svg);
                        }
                    }, 1000);
                }

                lastPoint = [path.pageX, path.pageY];

                if (config.onCountryMouseMove) {
                    config.onCountryMouseMove(config);
                }
            });

            $('#' + country.id, svg.root()).bind('mouseenter',
            function (path) {

                $('#' + config.id + 'box').attr('transform', 'translate(' + path.pageX + ' ' + path.pageY + ')');

                var g = path.target.parentNode;

                $(g).attr('opacity', config.activeCountryOpacity);
                $(g).attr('fill', config.activeCountryFill);
                $(g).attr('stroke', config.activeCountryStroke);
                $(g).attr('strokeWidth', config.activeCountryStrokeWidth);

                config.countryId = path.target.id;
                config.pos = [path.pageX, path.pageY];

                lastCountryId = currentCountryId;

                currentCountryId = config.countryId;

                if (config.showCountryBoxOnMouserEnter) {
                    showCountryBox(config, svg);
                    var box = $('#' + config.id + 'box', svg.root());
                    $(box).stop();
                    $(box).animate({ svgOpacity: 1.0 }, 100);
                    var txt = $('#' + config.id + 'txtBox', svg.root());
                    var name = getCountryName(config.countryId).split(',')[0];
                    txt[0].textContent = name.toUpperCase();
                }

                timer = setTimeout(function () {
                    if (lastCountryId == currentCountryId) {
                        clearTimeout(timer);

                        if (config.showCountryBoxOnMouserEnter)
                            hideCountryBox(config, svg);
                    }
                }, 1000);

                if (config.onCountryMouseEnter) {
                    config.onCountryMouseEnter(config);
                }
            }
        );

        $('#' + country.id, svg.root()).bind('mouseout',
        function (path) {
            var g = path.target.parentNode;
            $(g).attr('fill', config.inactiveCountryFill);
            $(g).attr('opacity', config.inactiveCountryOpacity);
            $(g).attr('stroke', config.inactiveCountryStroke);
            $(g).attr('strokeWidth', config.inactiveCountryStrokeWidth);
            $('#' + config.id + 'box').stop();

            if (config.onCountryMouseOut) {
                config.onCountryMouseOut(config);
            }
        });

        $('#' + country.id, svg.root()).bind('click',
        function (path) {
            if (config.onCountryMouseClick) {
                var g = path.target.parentNode;

                config.onCountryMouseClick(getCountryName(g.id));
            }
        });
    }
}

function createTextBox(config, svg) {

    var g = svg.group({ id: config.id + 'box', opacity: 0.0 });

    var pathString = 'M-100,0 L60,0 L60,20 L-100,20 L-100,0';

    svg.path(g, pathString, { fill: '#000', stroke: '#000', strokeWidth: 1, transform: 'translate(5,5)', opacity: 0.5 });

    svg.path(g, pathString, { fill: '#000', stroke: '#000', strokeWidth: 1 });

    svg.text(g, -20, 15, '', { id: config.id + 'txtBox', stroke: '#fff', fill: 'white', strokeWidth: 1, fontFamily: 'Verdana', fontSize: 12, textAnchor: 'middle' });
}

function hideCountryBox(config, svg) {
    var box = $('#' + config.id + 'box', svg.root());
    $(box).stop();
    $(box).animate({ svgOpacity: 0.0 }, 1000);
    countryBoxFadeOut = true;
}

function showCountryBox(config, svg) {
    var box = $('#' + config.id + 'box', svg.root());
    $(box).stop();
    $(box).animate({ svgOpacity: 1.0 }, 1000);
    countryBoxFadeOut = false;
}
