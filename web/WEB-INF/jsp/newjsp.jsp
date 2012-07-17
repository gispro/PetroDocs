<%-- 
    Document   : newjsp
    Created on : Apr 4, 2012, 3:30:40 PM
    Author     : fkravchenko
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="lib/ext4/resources/css/ext-all.css"/>
        <script src="http://openlayers.org/api/OpenLayers.js"></script>
        <script type="text/javascript">
            OpenLayers.ProxyHost= "form/proxy?url=";
            petroresConfig = {};
            petroresConfig.pathFolderSeparator = '\<%=File.separator%>';
            petroresConfig.domainRootId = ${initParam.domainRootId};
            //petroresConfig.layers = $ {initParam.layersCreateScript};
            petroresConfig.layers = [
                new OpenLayers.Layer.WMS(
                'Base', 
                'http://oceanviewer.ru/cache/service/wms', 
                {
                    layers: 'eko_merge'
                }, {
                    transitionEffect: 'resize'
                }), 
                new OpenLayers.Layer.WMS(
                'Blank', 
                'http://oceanviewer.ru/cache/service/wms', 
                {
                    layers: 'eko_blank'
                }, {
                    transitionEffect: 'resize'
                }), 
                new OpenLayers.Layer.Vector('Wells', {
                    isBaseLayer: false,
                    defaultLabelField: 'NAME',
                    strategies: [new OpenLayers.Strategy.BBOX()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: '/geoserver/wfs',
                        featureType: "Wells_PRS",
                        featureNS: "PetroResource",
                        geometryName: "GEOM"
                    })
                })
            ];
            
            
            var map = new OpenLayers.Map("mmaapp", {
                            controls: [
                                new OpenLayers.Control.Navigation(),
                                new OpenLayers.Control.PanZoom(),
                                new OpenLayers.Control.LayerSwitcher(),
                                new OpenLayers.Control.ArgParser(),
                                new OpenLayers.Control.Attribution()
                            ],
                            layers: petroresConfig.layers
                        });
                        map.zoomToMaxExtent();            
        </script>
        <script type="text/javascript" src="lib/ext4/ext-all-debug.js"></script>
        <script type="text/javascript" src="app.js"></script>
        <title>Petroresource Documents System!!</title>
    </head>

    <body>
        
        <div id="mmaapp"></div>
    </body>
</html>
