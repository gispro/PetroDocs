<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.io.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!--<link rel="stylesheet" type="text/css" href="lib/ext4/resources/css/ext-all.css"/>-->
        <link rel="stylesheet" type="text/css" href="lib/ext41/resources/css/ext-all.css"/>
        <link rel="stylesheet" type="text/css"  href="css/buttons.css"/>
        <!--<script src="lib/openlayers/OpenLayers.js"></script>-->
        <script src="lib/openlayers212/OpenLayers.debug.js"></script>
        <script src="lib/proj4js-combined.js"></script>
        <script type="text/javascript">
            OpenLayers.ProxyHost= "form/proxy?url=";
            Proj4js.defs["EPSG:32639"] = "+proj=utm +zone=39 +ellps=WGS84 +datum=WGS84 +units=m +no_defs";
            petroresConfig = {};
            petroresConfig.pathFolderSeparator = '\<%=File.separator%>';
            petroresConfig.loggedInAs = '${pageContext.request.userPrincipal.name}';
            petroresConfig.userRoles = [
            <c:forEach items="${pageContext.request.userPrincipal.roles}" varStatus="varSt" var="rol">
                <c:if test="${varSt.count>1}">,</c:if>'${rol}'
            </c:forEach>
            ];
            
            <c:set var="roles" scope="page" value="${pageContext.request.userPrincipal.roles}"/>
            <%
            for(String role: (String[])pageContext.getAttribute("roles")){
                if(role.equalsIgnoreCase((String)application.getInitParameter("roleReader"))){
                    out.print("petroresConfig.userIsReader = true; //" + role + "\n");
                    break;
                }
            }
            for(String role: (String[])pageContext.getAttribute("roles")){
                if(role.equalsIgnoreCase((String)application.getInitParameter("roleEditor"))){
                    out.print("petroresConfig.userIsEditor = true; //" + role + "\n");
                    break;
                }
            }
            for(String role: (String[])pageContext.getAttribute("roles")){
                if(role.equalsIgnoreCase((String)application.getInitParameter("roleAdmin"))){
                    out.print("petroresConfig.userIsAdmin = true; //" + role + "\n");
                    break;
                }
            }
            %>
            petroresConfig.vectorWfs = 'http://playground:9000/geoserver/wfs';
            //petroresConfig.vectorWfs = 'http://oceanviewer.ru/geoserver/wfs';
            petroresConfig.domainRootId = ${initParam.domainRootId};
            //petroresConfig.saveStrategy = new OpenLayers.Strategy.Save();
            //petroresConfig.saveStrategy.events.register("success", '', function(){alert('Success')});
            //petroresConfig.saveStrategy.events.register("failure", '', function(){alert('Failure')});   
            //set up the modification tools
            petroresConfig.DeleteFeature = OpenLayers.Class(OpenLayers.Control, {
                initialize: function(layer, options) {
                    OpenLayers.Control.prototype.initialize.apply(this, [options]);
                    this.layer = layer;
                    this.handler = new OpenLayers.Handler.Feature(
                    this, layer, {click: this.clickFeature}
                );
                },
                clickFeature: function(feature) {
                    // if feature doesn't have a fid, destroy it
                    if(feature.fid == undefined) {
                        this.layer.destroyFeatures([feature]);
                    } else {
                        feature.state = OpenLayers.State.DELETE;
                        this.layer.events.triggerEvent("afterfeaturemodified",
                        {feature: feature});
                        feature.renderIntent = "select";
                        this.layer.drawFeature(feature);
                    }
                },
                setMap: function(map) {
                    this.handler.setMap(map);
                    OpenLayers.Control.prototype.setMap.apply(this, arguments);
                },
                CLASS_NAME: "OpenLayers.Control.DeleteFeature"
            });            

            petroresConfig.showFeatureEditor = function(layer, features){
                            var wnd = Ext.getCmp('MainWindow');
                            for(var featNum in features){
                                var feature = features[featNum];
                                if(feature.state==='Insert' && layer.schemaLoaded){
                                    var form = Ext.create('Ext.form.Panel', {
                                        items: layer.schemaLoaded.featureTypes[0].fields,
                                        buttons: [
                                            {
                                                text: 'OK',
                                                handler: function(){
                                                    if(form.getForm().isValid()){
                                                        feature.attributes = form.getForm().getFieldValues(true);
                                                        wnd.openPetroWindows.attrWnd.close();
                                                        // what the hack?
                                                        //OpenLayers.Projection.transform(
                                                        feature.geometry.transform( 
                                                        feature.layer.projection, 
                                                        feature.layer.map.projection);
                                                        layer.saveStrategy.save([feature]);
                                                    }
                                                }
                                            },                                            
                                            {
                                                text: 'Cancel',
                                                handler: function(){
                                                    wnd.openPetroWindows.attrWnd.close();
                                                    if(feature.fid == undefined) {
                                                        layer.destroyFeatures([feature]);
                                                    } else {
                                                        feature.state = OpenLayers.State.DELETE;
                                                        layer.events.triggerEvent("afterfeaturemodified",
                                                        {feature: feature});
                                                        feature.renderIntent = "select";
                                                        layer.drawFeature(feature);
                                                        //layer.saveStrategy.save([feature]);
                                                    }
                                                }
                                            }                                            
                                            
                                        ]
                                    });
                                    wnd.openPetroWindow('attrWnd', {
                                        closable: true,
                                        title: 'Specify Attributes',
                                        maximizable: false,
                                        maximized: false,
                                        layout: 'fit',
                                        items: [
                                            form
                                        ]
                                    });
                                }
                            }
            };

            petroresConfig.createEditingPanel = function(layer){
                for(var i in layer.strategies){
                    // is it a save strategy? what is the correct way to access it? damnit openlayers
                    if(layer.strategies[i].CLASS_NAME === 'OpenLayers.Strategy.Save'){
                        layer.saveStrategy = layer.strategies[i];
                        break;
                    }
                }

                // add the custom editing toolbar
                var panel = new OpenLayers.Control.Panel(
                    {'displayClass': 'customEditingToolbar'}
                );

                var navigate = new OpenLayers.Control.Navigation({
                    title: "Pan Map"
                });

                var drawPoly = new OpenLayers.Control.DrawFeature(
                    layer, OpenLayers.Handler.Polygon,
                    {
                        title: "Draw Polygon",
                        displayClass: "olControlDrawFeaturePolygon",
                        multi: false
                    }
                );
                var drawLine = new OpenLayers.Control.DrawFeature(
                    layer, OpenLayers.Handler.Path,
                    {
                        title: "Draw Line",
                        displayClass: "olControlDrawFeatureLine",
                        multi: false
                    }
                );
                var drawPoint = new OpenLayers.Control.DrawFeature(
                    layer, OpenLayers.Handler.Point,
                    {
                        title: "Draw Point",
                        displayClass: "olControlDrawFeaturePoint",
                        multi: false
                    }
                );

                var edit = new OpenLayers.Control.ModifyFeature(layer, {
                    title: "Modify Feature",
                    displayClass: "olControlModifyFeature"
                });

                var del = new petroresConfig.DeleteFeature(layer, {title: "Delete Feature"});

                var save = new OpenLayers.Control.Button({
                    title: "Save Changes",
                    trigger: function() {
                        if(edit.feature) {
                            edit.selectControl.unselectAll();
                        }
                        layer.saveStrategy.save();
                    },
                    displayClass: "olControlSaveFeatures"
                });

                var draw;
                switch(layer.psLayerType){
                    case 'line': draw = drawLine; break;
                    case 'poly': draw = drawPoly; break;
                    case 'point': draw = drawPoint; break;
                }
                
                //console.log(['слой: ', layer, layer.geometryType]);
                panel.addControls([navigate, save, del, edit, draw]);
                panel.defaultControl = navigate;
                panel.autoActivate = false;
                
                layer.editingPanel = panel;
                layer.map.addControl(panel); 
                
            }

            petroresConfig.loadWfsSchema = function(layer){
                OpenLayers.Request.GET({
                    url: layer.schema,
                    success: function (response) {
                        var reader = new OpenLayers.Format.WFSDescribeFeatureType();
                        var schemaLoaded = reader.read(response.responseXML);
                        for(var ftypeid in schemaLoaded.featureTypes){
                            var newProps = [];
                            var fields = [];
                            var ftype = schemaLoaded.featureTypes[ftypeid];
                            for(var propid in ftype.properties){
                                var prop = ftype.properties[propid];
                                
                                if(prop.name === layer.defaultIdField)
                                    continue;
                                if(prop.name === layer.protocol.geometryName)
                                    continue;
                                
                                newProps.push(prop);
                                
                                var fld = {}
                                
                                if(prop.localType === 'double'){
                                    fld.xtype = 'numberfield';
                                    fld.allowDecimals = true;
                                }else if(prop.localType === 'int'){
                                    fld.xtype = 'numberfield';
                                    fld.allowDecimals = false;
                                }else  if(prop.localType === 'dateTime'){
                                    fld.xtype = 'datefield';
                                }else{
                                    fld.xtype = 'textfield';
                                }
                                
                                fld.allowBlank = prop.nillable==='true';
                                //fld.allowBlank = false;
                                
                                fld.name = prop.name;
                                fld.fieldLabel = prop.name;
                                
                                fields.push(fld);
                            }
                            ftype.properties = newProps;
                            ftype.fields = fields;
                        }
                        layer.schemaLoaded = schemaLoaded;
                    }
                });                
            }

            petroresConfig.layersCreator = function(){ 
                return [
                /*new OpenLayers.Layer.WMS(
                'Base', 
                'http://oceanviewer.ru/cache/service/wms', 
                {
                    isBaseLayer: true,
                    layers: 'eko_merge'
                }, {
                    transitionEffect: 'resize'
                }), 
                new OpenLayers.Layer.WMS(
                'Blank', 
                'http://oceanviewer.ru/cache/service/wms', 
                {
                    isBaseLayer: true,
                    layers: 'eko_blank'
                }, {
                    transitionEffect: 'resize'
                })
                , */
                
                //new OpenLayers.Layer.Bing({
                //    name: "Bing Aerial",
                //    type: "AerialWithLabels",
                //    apiKey: ''
                //}),
                
                
new OpenLayers.Layer.XYZ(
    "MapBox Streets",
    [
    //"http://a.tiles.mapbox.com/v3/ru.map-47coodrk/\${z}/\${x}/\${y}.png",
    //"http://b.tiles.mapbox.com/v3/ru.map-47coodrk/\${z}/\${x}/\${y}.png",
    //"http://c.tiles.mapbox.com/v3/ru.map-47coodrk/\${z}/\${x}/\${y}.png",
    //"http://d.tiles.mapbox.com/v3/ru.map-47coodrk/\${z}/\${x}/\${y}.png"
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
),  
    
new OpenLayers.Layer.OSM( "OSM Map"),    
/*new OpenLayers.Layer.TMS(
    "MapBox World Light",
    [
        "http://a.tiles.mapbox.com/v1/mapbox.world-light/\${z}/\${y}/\${x}.png",
        "http://b.tiles.mapbox.com/v1/mapbox.world-light/\${z}/\${y}/\${x}.png",
        "http://c.tiles.mapbox.com/v1/mapbox.world-light/\${z}/\${y}/\${x}.png",
        "http://d.tiles.mapbox.com/v1/mapbox.world-light/\${z}/\${y}/\${x}.png"
    
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
),*/        
                
                new OpenLayers.Layer.Vector('Wells', {
                    psLayerType: 'point',
                    isBaseLayer: false,
                    defaultLabelField: 'NAME',
                    strategies: [new OpenLayers.Strategy.BBOX(), new OpenLayers.Strategy.Save()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: petroresConfig.vectorWfs,
                        featureType: "Wells_PRS",
                        featureNS: "http://gispro.ru/petrores",
                        geometryName: "GEOM"
                    })
                    ,defaultIdField: 'OBJECTID'
                    ,schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=petrores:Wells_PRS"
                    ,projection: new OpenLayers.Projection("EPSG:32639")
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
                }), 
                new OpenLayers.Layer.Vector('Other Licenses', {
                    psLayerType: 'poly',
                    isBaseLayer: false,
                    defaultLabelField: 'BLOCK_NAME',
                    defaultIdField: 'OBJECTID',
                    strategies: [new OpenLayers.Strategy.BBOX(), new OpenLayers.Strategy.Save()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: petroresConfig.vectorWfs,
                        featureType: "Other_Licenses",
                        featureNS: "http://gispro.ru/petrores",
                        geometryName: "GEOM"
                    }),
                    styleMap: new OpenLayers.StyleMap({
                        "default": new OpenLayers.Style({
                            name: "проверка",
                            fillColor: "#9555d4",
                            fillOpacity: 0,
                            strokeColor: "#9555d4",
                            strokeWidth: 0.4
                        }/*, {
                            rules: [
                                new OpenLayers.Rule({
                                    name: "проверка 3",
                                    
                                    fillColor: "#9555d4",
                                    fillOpacity: 0,
                                    strokeColor: "#9555d4",
                                    strokeWidth: 0.4
                                })
                            ]
                        }*/),
                        "select": new OpenLayers.Style({
                            name: "проверка2",
                            fillColor: "#9555d4",
                            fillOpacity: 0.4,
                            strokeColor: "#9555d4",
                            strokeWidth: 1.5
                        })
                    }),
                    schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=petrores:Other_Licenses"
                    ,projection: new OpenLayers.Projection("EPSG:32639")
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
                }), 
                new OpenLayers.Layer.Vector('Islands', {
                    psLayerType: 'poly',
                    isBaseLayer: false,
                    defaultLabelField: 'OBJECTID',
                    defaultIdField: 'OBJECTID',
                    strategies: [new OpenLayers.Strategy.BBOX(), new OpenLayers.Strategy.Save()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: petroresConfig.vectorWfs,
                        featureType: "Islands",
                        featureNS: "http://gispro.ru/petrores",
                        geometryName: "GEOM"
                    })
                    ,defaultIdField: 'OBJECTID'
                    ,schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=petrores:Wells_PRS"
                    ,projection: new OpenLayers.Projection("EPSG:32639")
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
                }), 
                new OpenLayers.Layer.Vector('Seismic 2D 4C 2011', {
                    psLayerType: 'line',
                    isBaseLayer: false,
                    //defaultLabelField: 'OBJECTID',
                    defaultIdField: 'OBJECTID',
                    strategies: [new OpenLayers.Strategy.BBOX(), new OpenLayers.Strategy.Save()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: petroresConfig.vectorWfs,
                        featureType: "Seismic_2D_4C_2011",
                        featureNS: "http://gispro.ru/petrores",
                        geometryName: "GEOM"
                    })
                    ,defaultIdField: 'OBJECTID'
                    ,schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=petrores:Wells_PRS"
                    ,projection: new OpenLayers.Projection("EPSG:32639")
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
                }), 
                new OpenLayers.Layer.Vector('Seismic 2D Lines Old', {
                    psLayerType: 'line',
                    isBaseLayer: false,
                    //defaultLabelField: 'OBJECTID',
                    defaultIdField: 'OBJECTID',
                    strategies: [new OpenLayers.Strategy.BBOX(), new OpenLayers.Strategy.Save()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: petroresConfig.vectorWfs,
                        featureType: "Seismic_2D_Lines_old",
                        featureNS: "http://gispro.ru/petrores",
                        geometryName: "GEOM"
                    })
                    ,defaultIdField: 'OBJECTID'
                    ,schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=petrores:Wells_PRS"
                    ,projection: new OpenLayers.Projection("EPSG:32639")
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
                }), 
                new OpenLayers.Layer.Vector('Seismic 3D 2009', {
                    psLayerType: 'line',
                    isBaseLayer: false,
                    defaultLabelField: 'OBJECTID',
                    defaultIdField: 'OBJECTID',
                    strategies: [new OpenLayers.Strategy.BBOX(), new OpenLayers.Strategy.Save()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: petroresConfig.vectorWfs,
                        featureType: "Seismic_3D_2009",
                        featureNS: "http://gispro.ru/petrores",
                        geometryName: "GEOM"
                    })
                    ,defaultIdField: 'OBJECTID'
                    ,schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=petrores:Wells_PRS"
                    ,projection: new OpenLayers.Projection("EPSG:32639")
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
                }), 
                new OpenLayers.Layer.Vector('Seismic 3D 2010', {
                    psLayerType: 'line',
                    isBaseLayer: false,
                    defaultLabelField: 'NAME',
                    defaultIdField: 'OBJECTID',
                    strategies: [new OpenLayers.Strategy.BBOX(), new OpenLayers.Strategy.Save()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: petroresConfig.vectorWfs,
                        featureType: "Seismic_3D_2010",
                        featureNS: "http://gispro.ru/petrores",
                        geometryName: "GEOM"
                    })
                    ,defaultIdField: 'OBJECTID'
                    ,schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=petrores:Wells_PRS"
                    ,projection: new OpenLayers.Projection("EPSG:32639")
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
                }), 
                new OpenLayers.Layer.Vector('Structures', {
                    psLayerType: 'poly',
                    isBaseLayer: false,
                    defaultLabelField: 'Name',
                    defaultIdField: 'OBJECTID',
                    strategies: [new OpenLayers.Strategy.BBOX(), new OpenLayers.Strategy.Save()],
                    protocol: new OpenLayers.Protocol.WFS({
                        url: petroresConfig.vectorWfs,
                        featureType: "Structures_PRS",
                        featureNS: "http://gispro.ru/petrores",
                        geometryName: "GEOM"
                    }),
                    styleMap: new OpenLayers.StyleMap({
                        "default": new OpenLayers.Style({
                            fillColor: "#ddf7d0",
                            fillOpacity: 0.5,
                            strokeColor: "#b3b3b3",
                            strokeWidth: 0.4
                        }),
                        "select": new OpenLayers.Style({
                        fillColor: "#ddf7d0",
                        fillOpacity: 0.8,
                        strokeColor: "#385827",
                        strokeOpacity: 0.7,
                        strokeWidth: 1.5
                        })
                    })
                    ,defaultIdField: 'OBJECTID'
                    ,schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=petrores:Wells_PRS"
                    ,projection: new OpenLayers.Projection("EPSG:32639")
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
                })
            ]};
            petroresConfig.hasDuplicates = function(array) {
                var valuesSoFar = {};
                for (var i = 0; i < array.length; ++i) {
                    var value = array[i];
                    if (Object.prototype.hasOwnProperty.call(valuesSoFar, value)) {
                        return true;
                    }
                    valuesSoFar[value] = true;
                }
                return false;
            }
        
        petroresConfig.makeAllIdsNumbers = function(obj){
            
            /*for(var field in obj){

                if(field==='id'){
                    obj[field] = parseInt(obj[field]);
                }else{
                    if(Ext.isObject(obj[field]) || Ext.isArray(obj[field])){
                        petroresConfig.makeAllIdsNumbers(obj[field])
                    }
                }
            }*/
        }
        
        
        //console.log('');
        
        </script>
        <!--<script type="text/javascript" src="lib/ext4/ext-all-debug.js"></script>-->
        <script type="text/javascript" src="lib/ext41/ext-all-debug.js"></script>
        <script type="text/javascript" src="lib/boxselect/Boxselect.js"></script>
        <link rel="stylesheet" type="text/css" href="lib/boxselect/boxselect.css"/>
        <!--<script type="text/javascript" src="lib/geoext2/src/GeoExt/GeoExt.js"></script>-->
        <script type="text/javascript">
            
            setInterval(function(){
                Ext.Ajax.request({
                    url: 'void.htm'
                });
            }, 1000 * 60 * 10);

        </script>
        <script type="text/javascript" src="app.js"></script>
        <title>Petroresource Documents System</title>
        
<style type="text/css">
/* Custom editing toolbar */
.customEditingToolbar {
float: right;
right: 0px;
height: 30px;
width: 300px;
}
.customEditingToolbar div {
float: right;
margin: 5px;
width: 24px;
height: 24px;
} 
.customEditingLayerToolbar {
float: right;
right: 280px;
height: 30px;
width: 270px;
}
.customFindDocToolbar {
float: right;
right: 580px;
height: 30px;
width: 50px;
}
.olControlNavigationItemActive {
background-image:
url("lib/openlayers212/theme/default/img/editing_tool_bar.png");
background-repeat: no-repeat;
background-position: -103px -23px;
}
.olControlNavigationItemInactive {
background-image:
url("lib/openlayers212/theme/default/img/editing_tool_bar.png");
background-repeat: no-repeat;
background-position: -103px -0px;
}
.olControlDrawFeaturePolygonItemInactive {
background-image:
url("lib/openlayers212/theme/default/img/editing_tool_bar.png");
background-repeat: no-repeat;
background-position: -26px 0px;
}
.olControlDrawFeaturePolygonItemActive {
background-image:
url("lib/openlayers212/theme/default/img/editing_tool_bar.png");
background-repeat: no-repeat;
background-position: -26px -23px;                                                            
}
.olControlDrawFeatureLineItemInactive {
background-image:
url("lib/openlayers212/theme/default/img/draw_line_off.png");
}
.olControlDrawFeatureLineItemActive {
background-image:
url("lib/openlayers212/theme/default/img/draw_line_on.png");
}
.olControlDrawFeaturePointItemInactive {
background-image:
url("lib/openlayers212/theme/default/img/draw_point_off.png");
}
.olControlDrawFeaturePointItemActive {
background-image:
url("lib/openlayers212/theme/default/img/draw_point_on.png");                                  
}
.olControlModifyFeatureItemActive {
background-image:
url(lib/openlayers212/theme/default/img/move_feature_on.png);
background-repeat: no-repeat;
background-position: 0px 1px;
}
.olControlModifyFeatureItemInactive {
background-image:
url(lib/openlayers212/theme/default/img/move_feature_off.png);
background-repeat: no-repeat;
background-position: 0px 1px;
}
.olControlDeleteFeatureItemActive {
background-image:
url(lib/openlayers212/theme/default/img/remove_point_on.png);
background-repeat: no-repeat;
background-position: 0px 1px;
}
.olControlDeleteFeatureItemInactive {
background-image:
url(lib/openlayers212/theme/default/img/remove_point_off.png);
background-repeat: no-repeat;
background-position: 0px 1px;
}
</style>        
        
    </head>

    <body>
        
        <!--< %
            if(request!=null && request.getUserPrincipal()!=null){
                Object ggg = request.getUserPrincipal();

                System.out.println("- -="+ggg+"==-");
            }
                
        % >
        
        < c:out value="$ {pageContext.request.userPrincipal.name}"/>-->
        
    </body>
</html>
