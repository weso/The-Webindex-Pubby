﻿function getCountryName(id) {
    var officialNames = [];
    officialNames['ao'] = 'Angola, Republic of';
    officialNames['bf'] = 'Burkina Faso';
    officialNames['bi'] = 'Burundi, Republic of';
    officialNames['bj'] = 'Benin, Republic of';
    officialNames['bw'] = 'Botswana, Republic of';
    officialNames['cd'] = 'Congo, Democratic Republic of the';
    officialNames['cf'] = 'Central African, Central African Republic';
    officialNames['cg'] = 'Congo, Republic of the';
    officialNames['ci'] = 'Cote d\'Ivoire, Republic of';
    officialNames['cm'] = 'Cameroon, Republic of';
    officialNames['cv'] = 'Cape Verde, Republic of';
    officialNames['dj'] = 'Djibouti, Republic of';
    officialNames['dz'] = 'Algeria, People\'s Democratic Republic of';
    officialNames['eg'] = 'Egypt, Arab Republic of';
    officialNames['eh'] = 'Western Sahara';
    officialNames['er'] = 'Eritrea, State of';
    officialNames['et'] = 'Ethiopia, Federal Democratic Republic of';
    officialNames['ga'] = 'Gabon, Gabonese Republic';
    officialNames['gh'] = 'Ghana, Republic of';
    officialNames['gm'] = 'Gambia, Republic of the';
    officialNames['gn'] = 'Guinea, Republic of';
    officialNames['gq'] = 'Equatorial Guinea, Republic of';
    officialNames['gw'] = 'Guinea-Bissau, Republic of';
    officialNames['ke'] = 'Kenya, Republic of';
    officialNames['km'] = 'Comoros, Union of the';
    officialNames['lr'] = 'Liberia, Republic of';
    officialNames['ls'] = 'Lesotho, Kingdom of';
    officialNames['ly'] = 'Libya, Libyan Arab Jamahiriya';
    officialNames['ma'] = 'Morocco, Kingdom of';
    officialNames['mg'] = 'Madagascar, Republic of';
    officialNames['ml'] = 'Mali, Republic of';
    officialNames['mr'] = 'Mauritania, Islamic Republic of';
    officialNames['mu'] = 'Mauritius, Republic of';
    officialNames['mw'] = 'Malawi, Republic of';
    officialNames['mz'] = 'Mozambique, Republic of';
    officialNames['na'] = 'Namibia, Republic of';
    officialNames['ne'] = 'Niger, Republic of';
    officialNames['ng'] = 'Nigeria, Federal Republic of';
    officialNames['re'] = 'Reunion';
    officialNames['rw'] = 'Rwanda, Republic of';
    officialNames['sc'] = 'Seychelles, Republic of';
    officialNames['sd'] = 'Sudan, Republic of';
    officialNames['sh'] = 'Saint Helena';
    officialNames['sl'] = 'Sierra Leone, Republic of';
    officialNames['sn'] = 'Senegal, Republic of';
    officialNames['so'] = 'Somalia, Somali Republic';
    officialNames['st'] = 'Sao Tome, Democratic Republic of Sao Tome, and Principe';
    officialNames['sz'] = 'Swaziland, Kingdom of';
    officialNames['td'] = 'Chad, Republic of';
    officialNames['tg'] = 'Togo, Togolese Republic';
    officialNames['tn'] = 'Tunisia, Tunisian Republic';
    officialNames['tz'] = 'Tanzania, United Republic of';
    officialNames['ug'] = 'Uganda, Republic of';
    officialNames['yt'] = 'Mayotte';
    officialNames['za'] = 'South Africa, Republic of';
    officialNames['zm'] = 'Zambia, Republic of';
    officialNames['zw'] = 'Zimbabwe, Republic of';
    officialNames['aq'] = 'Antarctica';
    officialNames['bv'] = 'Bouvet Island, Bouvetoya';
    officialNames['gs'] = 'South Georgia, South Georgia and the South Sandwich Islands';
    officialNames['hm'] = 'Heard Island and McDonald Islands';
    officialNames['tf'] = 'French Southern Territories';
    officialNames['ae'] = 'UAE, United Arab Emirates';
    officialNames['af'] = 'Afghanistan, Islamic Republic of';
    officialNames['am'] = 'Armenia, Republic of';
    officialNames['az'] = 'Azerbaijan, Republic of';
    officialNames['bd'] = 'Bangladesh, People\'s Republic of';
    officialNames['bh'] = 'Bahrain, Kingdom of';
    officialNames['bn'] = 'Brunei Darussalam';
    officialNames['bt'] = 'Bhutan, Kingdom of';
    officialNames['cc'] = 'Cocos (Keeling) Islands';
    officialNames['cn'] = 'China, People\'s Republic of';
    officialNames['cx'] = 'Christmas Island';
    officialNames['cy'] = 'Cyprus, Republic of';
    officialNames['ge'] = 'Georgia';
    officialNames['hk'] = 'Hong Kong, Special Administrative Region of China';
    officialNames['id'] = 'Indonesia, Republic of';
    officialNames['il'] = 'Israel, State of';
    officialNames['in'] = 'India, Republic of';
    officialNames['io'] = 'British Indian Ocean Territory (Chagos Archipelago)';
    officialNames['iq'] = 'Iraq, Republic of';
    officialNames['ir'] = 'Iran, Islamic Republic of';
    officialNames['jo'] = 'Jordan, Hashemite Kingdom of';
    officialNames['jp'] = 'Japan';
    officialNames['kg'] = 'Kyrgyz Republic';
    officialNames['kh'] = 'Cambodia, Kingdom of';
    officialNames['kp'] = 'Korea, Democratic People\'s Republic of';
    officialNames['kr'] = 'Korea, Republic of';
    officialNames['kw'] = 'Kuwait, State of';
    officialNames['kz'] = 'Kazakhstan, Republic of';
    officialNames['la'] = 'Laos, Lao People\'s Democratic Republic';
    officialNames['lb'] = 'Lebanon, Lebanese Republic';
    officialNames['lk'] = 'Sri Lanka, Democratic Socialist Republic of';
    officialNames['mm'] = 'Myanmar, Union of';
    officialNames['mn'] = 'Mongolia';
    officialNames['mo'] = 'Macao, Special Administrative Region of China';
    officialNames['mv'] = 'Maldives, Republic of';
    officialNames['my'] = 'Malaysia';
    officialNames['np'] = 'Nepal, State of';
    officialNames['om'] = 'Oman, Sultanate of';
    officialNames['ph'] = 'Philippines, Republic of the';
    officialNames['pk'] = 'Pakistan, Islamic Republic of';
    officialNames['ps'] = 'Palestine, Palestinian Territory';
    officialNames['qa'] = 'Qatar, State of';
    officialNames['ru'] = 'Russia, Russian Federation';
    officialNames['sa'] = 'Saudi Arabia, Kingdom of';
    officialNames['sg'] = 'Singapore, Republic of';
    officialNames['sy'] = 'Syria, Syrian Arab Republic';
    officialNames['th'] = 'Thailand, Kingdom of';
    officialNames['tj'] = 'Tajikistan, Republic of';
    officialNames['tl'] = 'Timor-Leste, Democratic Republic of';
    officialNames['tm'] = 'Turkmenistan';
    officialNames['tr'] = 'Turkey, Republic of';
    officialNames['tw'] = 'Taiwan';
    officialNames['uz'] = 'Uzbekistan, Republic of';
    officialNames['vn'] = 'Vietnam, Socialist Republic of';
    officialNames['ye'] = 'Yemen';
    officialNames['ad'] = 'Andorra, Principality of';
    officialNames['al'] = 'Albania, Republic of';
    officialNames['am'] = 'Armenia, Republic of';
    officialNames['at'] = 'Austria, Republic of';
    officialNames['ax'] = 'Åland Islands';
    officialNames['az'] = 'Azerbaijan, Republic of';
    officialNames['ba'] = 'Bosnia, Bosnia and Herzegovina';
    officialNames['be'] = 'Belgium, Kingdom of';
    officialNames['bg'] = 'Bulgaria, Republic of';
    officialNames['by'] = 'Belarus, Republic of';
    officialNames['ch'] = 'Switzerland, Swiss Confederation';
    officialNames['cy'] = 'Cyprus, Republic of';
    officialNames['cz'] = 'Czech, Czech Republic';
    officialNames['de'] = 'Germany, Federal Republic of';
    officialNames['dk'] = 'Denmark, Kingdom of';
    officialNames['ee'] = 'Estonia, Republic of';
    officialNames['es'] = 'Spain, Kingdom of';
    officialNames['fi'] = 'Finland, Republic of';
    officialNames['fo'] = 'Faroe, Faroe Islands';
    officialNames['fr'] = 'France, French Republic';
    officialNames['gb'] = 'UK, United Kingdom of Great Britain & Northern Ireland';
    officialNames['ge'] = 'Georgia';
    officialNames['gg'] = 'Guernsey, Bailiwick of';
    officialNames['gi'] = 'Gibraltar';
    officialNames['gr'] = 'Greece, Hellenic Republic';
    officialNames['hr'] = 'Croatia, Republic of';
    officialNames['hu'] = 'Hungary, Republic of';
    officialNames['ie'] = 'Ireland';
    officialNames['im'] = 'Isle of Man';
    officialNames['is'] = 'Iceland, Republic of';
    officialNames['it'] = 'Italy, Italian Republic';
    officialNames['je'] = 'Jersey, Bailiwick of';
    officialNames['kz'] = 'Kazakhstan, Republic of';
    officialNames['li'] = 'Liechtenstein, Principality of';
    officialNames['lt'] = 'Lithuania, Republic of';
    officialNames['lu'] = 'Luxembourg, Grand Duchy of';
    officialNames['lv'] = 'Latvia, Republic of';
    officialNames['mc'] = 'Monaco, Principality of';
    officialNames['md'] = 'Moldova, Republic of';
    officialNames['me'] = 'Montenegro, Republic of';
    officialNames['mk'] = 'Macedonia, Republic of';
    officialNames['mt'] = 'Malta, Republic of';
    officialNames['nl'] = 'Netherlands, Kingdom of the';
    officialNames['no'] = 'Norway, Kingdom of';
    officialNames['pl'] = 'Poland, Republic of';
    officialNames['pt'] = 'Portugal, Portuguese Republic';
    officialNames['ro'] = 'Romania';
    officialNames['rs'] = 'Serbia, Republic of';
    officialNames['ru'] = 'Russia, Russian Federation';
    officialNames['se'] = 'Sweden, Kingdom of';
    officialNames['si'] = 'Slovenia, Republic of';
    officialNames['sj'] = 'Svalbard & Jan Mayen Islands';
    officialNames['sk'] = 'Slovakia (Slovak Republic)';
    officialNames['sm'] = 'San Marino, Republic of';
    officialNames['tr'] = 'Turkey, Republic of';
    officialNames['ua'] = 'Ukraine';
    officialNames['va'] = 'Vatican, Holy See (Vatican City State)';
    officialNames['ag'] = 'Antigua, Antigua and Barbuda';
    officialNames['ai'] = 'Anguilla';
    officialNames['an'] = 'Netherlands Antilles';
    officialNames['aw'] = 'Aruba';
    officialNames['bb'] = 'Barbados';
    officialNames['bl'] = 'Saint Barthelemy';
    officialNames['bm'] = 'Bermuda';
    officialNames['bq'] = 'Bonaire, Sint Eustatius and Saba';
    officialNames['bs'] = 'Bahamas, Commonwealth of the';
    officialNames['bz'] = 'Belize';
    officialNames['ca'] = 'Canada';
    officialNames['cr'] = 'Costa Rica, Republic of';
    officialNames['cu'] = 'Cuba, Republic of';
    officialNames['cw'] = 'Curaçao';
    officialNames['dm'] = 'Dominica, Commonwealth of';
    officialNames['do'] = 'Dominican Republic';
    officialNames['gd'] = 'Grenada';
    officialNames['gl'] = 'Greenland';
    officialNames['gp'] = 'Guadeloupe';
    officialNames['gt'] = 'Guatemala, Republic of';
    officialNames['hn'] = 'Honduras, Republic of';
    officialNames['ht'] = 'Haiti, Republic of';
    officialNames['jm'] = 'Jamaica';
    officialNames['kn'] = 'Saint Kitts and Nevis, Federation of';
    officialNames['ky'] = 'Cayman Islands';
    officialNames['lc'] = 'Saint Lucia';
    officialNames['mf'] = 'Saint Martin';
    officialNames['mq'] = 'Martinique';
    officialNames['ms'] = 'Montserrat';
    officialNames['mx'] = 'Mexico, United Mexican States';
    officialNames['ni'] = 'Nicaragua, Republic of';
    officialNames['pa'] = 'Panama, Republic of';
    officialNames['pm'] = 'Saint Pierre and Miquelon';
    officialNames['pr'] = 'Puerto Rico, Commonwealth of';
    officialNames['sv'] = 'El Salvador, Republic of';
    officialNames['sx'] = 'Sint Maarten (Netherlands)';
    officialNames['tc'] = 'Turks and Caicos Islands';
    officialNames['tt'] = 'Trinidad and Tobago, Republic of';
    officialNames['um'] = 'United States Minor Outlying Islands';
    officialNames['us'] = 'USA, United States of America';
    officialNames['vc'] = 'Saint Vincent and the Grenadines';
    officialNames['vg'] = 'British Virgin Islands';
    officialNames['vi'] = 'United States Virgin Islands';
    officialNames['as'] = 'American Samoa';
    officialNames['au'] = 'Australia, Commonwealth of';
    officialNames['ck'] = 'Cook Islands';
    officialNames['fj'] = 'Fiji, Republic of the Fiji Islands';
    officialNames['fm'] = 'Micronesia, Federated States of';
    officialNames['gu'] = 'Guam';
    officialNames['ki'] = 'Kiribati, Republic of';
    officialNames['mh'] = 'Marshall Islands, Republic of the';
    officialNames['mp'] = 'Northern Mariana Islands, Commonwealth of the';
    officialNames['nc'] = 'New Caledonia';
    officialNames['nf'] = 'Norfolk Island';
    officialNames['nr'] = 'Nauru, Republic of';
    officialNames['nu'] = 'Niue';
    officialNames['nz'] = 'New Zealand';
    officialNames['pf'] = 'French Polynesia';
    officialNames['pg'] = 'Papua New Guinea, Independent State of';
    officialNames['pn'] = 'Pitcairn Islands';
    officialNames['pw'] = 'Palau, Republic of';
    officialNames['sb'] = 'Solomon Islands';
    officialNames['tk'] = 'Tokelau';
    officialNames['to'] = 'Tonga, Kingdom of';
    officialNames['tv'] = 'Tuvalu';
    officialNames['um'] = 'United States Minor Outlying Islands';
    officialNames['vu'] = 'Vanuatu, Republic of';
    officialNames['wf'] = 'Wallis and Futuna';
    officialNames['ws'] = 'Samoa, Independent State of';
    officialNames['ar'] = 'Argentina, Argentine Republic';
    officialNames['bo'] = 'Bolivia, Republic of';
    officialNames['br'] = 'Brazil, Federative Republic of';
    officialNames['cl'] = 'Chile, Republic of';
    officialNames['co'] = 'Colombia, Republic of';
    officialNames['ec'] = 'Ecuador, Republic of';
    officialNames['fk'] = 'Falkland Islands (Malvinas)';
    officialNames['gf'] = 'French Guiana';
    officialNames['gy'] = 'Guyana, Co-operative Republic of';
    officialNames['pe'] = 'Peru, Republic of';
    officialNames['py'] = 'Paraguay, Republic of';
    officialNames['sr'] = 'Suriname, Republic of';
    officialNames['uy'] = 'Uruguay, Eastern Republic of';
    officialNames['ve'] = 'Venezuela, Bolivarian Republic of';

    return officialNames[id];
}