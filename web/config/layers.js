petroresConfig.layersConfig = [
    /*{
        petroType: OpenLayers.Layer.OSM,
        params: [
            'OpenStreetMap'
        ]
    }
    ,{
        petroType: OpenLayers.Layer.XYZ,
        params: [
            'OpenStreetMap'
            ,'MapBox'
            ,[
                "http://a.tiles.mapbox.com/v3/mapbox.mapbox-streets/\${z}/\${x}/\${y}.png",
                "http://b.tiles.mapbox.com/v3/mapbox.mapbox-streets/\${z}/\${x}/\${y}.png",
                "http://c.tiles.mapbox.com/v3/mapbox.mapbox-streets/\${z}/\${x}/\${y}.png",
                "http://d.tiles.mapbox.com/v3/mapbox.mapbox-streets/\${z}/\${x}/\${y}.png"

            ], {
                attribution: "Tiles &copy; <a href='http://mapbox.com/'>MapBox</a> | " + 
                    "Data &copy; <a href='http://www.openstreetmap.org/'>OpenStreetMap</a> " +
                    "and contributors, CC-BY-SA",
                sphericalMercator: true,
                wrapDateLine: true,
                transitionEffect: "resize",
                buffer: 1,
                numZoomLevels: 17
            }
        ]
    }
    , {
        petroType: OpenLayers.Layer.Bing,
        params: [
            {
                name: "Satellite",
                key: 'AlSjFhANk1LeS8B3SimhN04C4fxfzNAuLuG_ZpKTD2fhvtTLFAXG1MRsbuk68qqI',
                type: "AerialWithLabels"
            }
        ]
    }
    
    , {
        petroType: OpenLayers.Layer.WMS,
        params: [
            'Blank', 
            'http://oceanviewer.ru/cache/service/wms', 
            {
                layers: 'eko_blank',
                projection: 'EPSG:900913'
            }, {
                transitionEffect: 'resize'
            }
        ]
    }


    , *//*{
        petroType: OpenLayers.Layer.WMS,
        params: [
                'Map Box', 
                'http://uno:10000/service', 
                {
                    layers: 'osm_mapbox'
                }, {
                    transitionEffect: 'resize'
                    ,projection: 'EPSG:900913'
                }
        ]
    }
    , {
        petroType: OpenLayers.Layer.WMS,
        params: [
            'OSM', 
            'http://uno:10000/service', 
            {
                layers: 'osm_orig'
            }, {
                transitionEffect: 'resize'
                ,projection: 'EPSG:900913'
            }
        ]
    }
    , {
        petroType: OpenLayers.Layer.WMS,
        params: [
            'Satellite', 
            'http://uno:10000/service', 
            {
                layers: 'bing_imagery'
            }, {
                transitionEffect: 'resize'
                ,projection: 'EPSG:900913'
            }
        ]
    }
    /*, {
        petroType: OpenLayers.Layer.Vector,
        params: [
            'Regional Geology', 
            {
                psLayerType: 'poly',
                isBaseLayer: false,
                visibility: false,
                defaultLabelField: 'Name',
                strategies: [
                    {
                        petroType: OpenLayers.Strategy.BBOX
                    }, {
                        petroType: OpenLayers.Strategy.Save
                    }
                ],
                protocol: {
                    petroType: OpenLayers.Protocol.WFS,
                    params: [
                        {
                            url: petroresConfig.vectorWfs,
                            featureType: "Regional_Geology",
                            featureNS: "http://petroresurs.com/geoportal",
                            geometryName: "GEOM"
                        }
                    ]
                }
                ,defaultIdField: 'OBJECTID'
                ,schema: petroresConfig.vectorWfs + 
                    "/DescribeFeatureType?version=1.1.0&typename=PetroResurs:Regional_Geology"
                ,projection: {
                    petroType: OpenLayers.Projection,
                    params: [
                        'EPSG:32639'
                    ]
                }
                ,version: "1.1.0"
                , eventListeners: {
                    beforefeaturesadded: function(obj){
                        petroresConfig.showFeatureEditor(obj.object, obj.features)
                    },
                    loadend: function(eventsObj){
                        petroresConfig.loadWfsSchema(eventsObj.object);
                        petroresConfig.createEditingPanel(eventsObj.object)
                    }
                }
            }
        ]
    }

    , {
        petroType: OpenLayers.Layer.Vector,
        params: [
            'Ecological Limitations', 
            {
                psLayerType: 'poly',
                isBaseLayer: false,
                visibility: false,
                defaultLabelField: 'Name',
                strategies: [
                    {
                        petroType: OpenLayers.Strategy.BBOX
                    }, {
                        petroType: OpenLayers.Strategy.Save
                    }
                ],
                protocol: {
                    petroType: OpenLayers.Protocol.WFS,
                    params: [
                        {
                            url: petroresConfig.vectorWfs,
                            featureType: "Ecological_limitations",
                            featureNS: "http://petroresurs.com/geoportal",
                            geometryName: "GEOM"
                        }
                    ]
                }
                ,defaultIdField: 'OBJECTID'
                ,schema: petroresConfig.vectorWfs + 
                    "/DescribeFeatureType?version=1.1.0&typename=PetroResurs:Ecological_limitations"
                ,projection: {
                    petroType: OpenLayers.Projection,
                    params: [
                        'EPSG:32639'
                    ]
                }
                ,version: "1.1.0"
                , eventListeners: {
                    beforefeaturesadded: function(obj){
                        petroresConfig.showFeatureEditor(obj.object, obj.features)
                    },
                    loadend: function(eventsObj){
                        petroresConfig.loadWfsSchema(eventsObj.object);
                        petroresConfig.createEditingPanel(eventsObj.object)
                    }
                }
            }
        ]
    }
    ,*/ {
        petroType: OpenLayers.Layer.Vector,
        params: [
            'Seismic 3D', 
            {
                psLayerType: 'poly',
                isBaseLayer: false,
                visibility: false,
                defaultLabelField: 'Name',
                strategies: [
                    {
                        petroType: OpenLayers.Strategy.BBOX
                    }, {
                        petroType: OpenLayers.Strategy.Save
                    }
                ],
                protocol: {
                    petroType: OpenLayers.Protocol.WFS,
                    params: [
                        {
                            url: petroresConfig.vectorWfs,
                            featureType: "Seismic_3D_PRS",
                            featureNS: "http://petroresurs.com/geoportal",
                            geometryName: "GEOM"
                        }
                    ]
                }
                ,defaultIdField: 'OBJECTID'
                ,schema: petroresConfig.vectorWfs + 
                    "/DescribeFeatureType?version=1.1.0&typename=PetroResurs:Seismic_3D_PRS"
                ,projection: {
                    petroType: OpenLayers.Projection,
                    params: [
                        'EPSG:32639'
                    ]
                }
                ,version: "1.1.0"
                , eventListeners: {
                    beforefeaturesadded: function(obj){
                        petroresConfig.showFeatureEditor(obj.object, obj.features)
                    },
                    loadend: function(eventsObj){
                        petroresConfig.loadWfsSchema(eventsObj.object);
                        petroresConfig.createEditingPanel(eventsObj.object)
                    }
                }
            }
        ]
    }
    /*, {
        petroType: OpenLayers.Layer.Vector,
        params: [
            'Seismic 2D', 
            {
                psLayerType: 'line',
                isBaseLayer: false,
                visibility: false,
                defaultLabelField: 'Name',
                strategies: [
                    {
                        petroType: OpenLayers.Strategy.BBOX
                    }, {
                        petroType: OpenLayers.Strategy.Save
                    }
                ],
                protocol: {
                    petroType: OpenLayers.Protocol.WFS,
                    params: [
                        {
                            url: petroresConfig.vectorWfs,
                            featureType: "Seismic_2D_PRS",
                            featureNS: "http://petroresurs.com/geoportal",
                            geometryName: "GEOM"
                        }
                    ]
                }
                ,defaultIdField: 'OBJECTID'
                ,schema: petroresConfig.vectorWfs + 
                    "/DescribeFeatureType?version=1.1.0&typename=PetroResurs:Seismic_2D_PRS"
                ,projection: {
                    petroType: OpenLayers.Projection,
                    params: [
                        'EPSG:32639'
                    ]
                }
                ,version: "1.1.0"
                , eventListeners: {
                    beforefeaturesadded: function(obj){
                        petroresConfig.showFeatureEditor(obj.object, obj.features)
                    },
                    loadend: function(eventsObj){
                        petroresConfig.loadWfsSchema(eventsObj.object);
                        petroresConfig.createEditingPanel(eventsObj.object)
                    }
                }
            }
        ]
    }    
    , {
        petroType: OpenLayers.Layer.Vector,
        params: [
            'Structures', 
            {
                psLayerType: 'poly',
                isBaseLayer: false,
                visibility: false,
                defaultLabelField: 'Name',
                strategies: [
                    {
                        petroType: OpenLayers.Strategy.BBOX
                    }, {
                        petroType: OpenLayers.Strategy.Save
                    }
                ],
                protocol: {
                    petroType: OpenLayers.Protocol.WFS,
                    params: [
                        {
                            url: petroresConfig.vectorWfs,
                            featureType: "Structures_PRS",
                            featureNS: "http://petroresurs.com/geoportal",
                            geometryName: "GEOM"
                        }
                    ]
                }
                , styleMap: {
                    petroType: OpenLayers.StyleMap,
                    params: [{
                        "default": {
                            petroType: OpenLayers.Style,
                            params: [{
                                fillColor: "#ddf7d0",
                                fillOpacity: 0.5,
                                strokeColor: "#b3b3b3",
                                strokeWidth: 0.4
                            }]
                        }, "select": {
                            petroType: OpenLayers.Style,
                            params: [{
                                fillColor: "#ddf7d0",
                                fillOpacity: 0.8,
                                strokeColor: "#385827",
                                strokeOpacity: 0.7,
                                strokeWidth: 1.5
                            }]
                        }
                    }]
                }
                ,defaultIdField: 'OBJECTID'
                ,schema: petroresConfig.vectorWfs + 
                    "/DescribeFeatureType?version=1.1.0&typename=PetroResurs:Structures_PRS"
                ,projection: {
                    petroType: OpenLayers.Projection,
                    params: [
                        'EPSG:32639'
                    ]
                }
                ,version: "1.1.0"
                , eventListeners: {
                    beforefeaturesadded: function(obj){
                        petroresConfig.showFeatureEditor(obj.object, obj.features)
                    },
                    loadend: function(eventsObj){
                        petroresConfig.loadWfsSchema(eventsObj.object);
                        petroresConfig.createEditingPanel(eventsObj.object)
                    }
                }
            }
        ]
    }
    , {
        petroType: OpenLayers.Layer.Vector,
        params: [
            'Licenses', 
            {
                psLayerType: 'poly',
                isBaseLayer: false,
                visibility: false,
                defaultLabelField: 'Name',
                strategies: [
                    {
                        petroType: OpenLayers.Strategy.BBOX
                    }, {
                        petroType: OpenLayers.Strategy.Save
                    }
                ],
                protocol: {
                    petroType: OpenLayers.Protocol.WFS,
                    params: [
                        {
                            url: petroresConfig.vectorWfs,
                            featureType: "Licenses",
                            featureNS: "http://petroresurs.com/geoportal",
                            geometryName: "GEOM"
                        }
                    ]
                }
                , styleMap: {
                    petroType: OpenLayers.StyleMap,
                    params: [{
                        "default": {
                            petroType: OpenLayers.Style,
                            params: [{
                                fillColor: "#9555d4",
                                fillOpacity: 0,
                                strokeColor: "#9555d4",
                                strokeWidth: 0.4
                            }]
                        }, "select": {
                            petroType: OpenLayers.Style,
                            params: [{
                                fillColor: "#9555d4",
                                fillOpacity: 0.4,
                                strokeColor: "#9555d4",
                                strokeWidth: 1.5
                            }]
                        }
                    }]
                }
                ,defaultIdField: 'OBJECTID'
                ,schema: petroresConfig.vectorWfs + 
                    "/DescribeFeatureType?version=1.1.0&typename=PetroResurs:Licenses"
                ,projection: {
                    petroType: OpenLayers.Projection,
                    params: [
                        'EPSG:32639'
                    ]
                }
                ,version: "1.1.0"
                , eventListeners: {
                    beforefeaturesadded: function(obj){
                        petroresConfig.showFeatureEditor(obj.object, obj.features)
                    },
                    loadend: function(eventsObj){
                        petroresConfig.loadWfsSchema(eventsObj.object);
                        petroresConfig.createEditingPanel(eventsObj.object)
                    }
                }
            }
        ]
    }    
    , {
        petroType: OpenLayers.Layer.Vector,
        params: [
            'Wells', 
            {
                psLayerType: 'point',
                isBaseLayer: false,
                visibility: false,
                defaultLabelField: 'Name',
                strategies: [
                    {
                        petroType: OpenLayers.Strategy.BBOX
                    }, {
                        petroType: OpenLayers.Strategy.Save
                    }
                ],
                protocol: {
                    petroType: OpenLayers.Protocol.WFS,
                    params: [
                        {
                            url: petroresConfig.vectorWfs,
                            featureType: "point",
                            featureNS: "http://petroresurs.com/geoportal",
                            geometryName: "GEOM"
                        }
                    ]
                }
                ,defaultIdField: 'OBJECTID'
                ,schema: petroresConfig.vectorWfs + 
                    "/DescribeFeatureType?version=1.1.0&typename=PetroResurs:point"
                ,projection: {
                    petroType: OpenLayers.Projection,
                    params: [
                        'EPSG:32639'
                    ]
                }
                ,version: "1.1.0"
                , eventListeners: {
                    beforefeaturesadded: function(obj){
                        petroresConfig.showFeatureEditor(obj.object, obj.features)
                    },
                    loadend: function(eventsObj){
                        petroresConfig.loadWfsSchema(eventsObj.object);
                        petroresConfig.createEditingPanel(eventsObj.object)
                    }
                }
            }
        ]
    }        */
];