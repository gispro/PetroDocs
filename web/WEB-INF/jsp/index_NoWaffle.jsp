<%@page import="org.apache.log4j.Level"%>
<%@page import="org.apache.log4j.Priority"%>
<%@page import="java.util.Enumeration"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="java.io.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="ru.gispro.petrores.doc.util.UserSessions"%>
<%@page import="org.apache.log4j.Logger"%> 
<%@page import="org.apache.log4j.MDC"%> 
<%
String sLogin = request.getRemoteUser(), 
       sSessionID = session.getId();
    if( UserSessions.registerUserSession(sLogin, session.getId())){
        UserSessions.info("ru.gispro.petrores.doc.util.UserSessions", request.getRemoteUser(), 
                         "LOGIN", "Login", null, true, "Login is successful");
    }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="stylesheet" type="text/css" href="lib/ext41/resources/css/ext-all.css"/>
        <link rel="stylesheet" type="text/css"  href="css/buttons.css"/>
        <!--<script src="lib/openlayers212/OpenLayers.debug.js"></script>-->
        <script src="lib/openlayers212/OpenLayers.js"></script>
        <script src="lib/proj4js-combined.js"></script>
        <script type="text/javascript">
            var petrodoc_sid = '<%=sSessionID%>';
            OpenLayers.ProxyHost= "form/proxy?url=";
            OpenLayers.IMAGE_RELOAD_ATTEMPTS=5;
            Proj4js.defs["EPSG:32639"] = "+proj=utm +zone=39 +a=6378137.0 +b=6356752.31424518 +ellps=WGS84 +datum=WGS84 +units=m +no_defs";
            //Proj4js.defs["EPSG:4326"] = "+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs";
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
            petroresConfig.vectorWfs = '${initParam.vectorWfs}';
            petroresConfig.domainRootId = ${initParam.domainRootId};
            
            petroresConfig.defaultMap = '${initParam.defaultMap}';
            
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

            petroresConfig.proj4326 = new OpenLayers.Projection('EPSG:4326');
            petroresConfig.proj32639 = new OpenLayers.Projection('EPSG:32639');
            petroresConfig.projGoog = new OpenLayers.Projection('EPSG:3857');//900913');
            petroresConfig.mapfishUrl = '${initParam.mapfishUrl}'
            
            petroresConfig.defaultWfsLabelField = '${initParam.defaultWfsLabelField}' // Name
            petroresConfig.defaultWfsIdField = '${initParam.defaultWfsIdField}' // OBJECTID
            petroresConfig.defaultWfsFeatureNS = '${initParam.defaultWfsFeatureNS}' // http://petroresurs.com/geoportal
            petroresConfig.defaultWfsFeatureNSShort = '${initParam.defaultWfsFeatureNSShort}' // PetroResurs
            
            petroresConfig.showFeatureViewer = function(layer, features){
                            var wnd = Ext.getCmp('MainWindow');
                            for(var featNum in features){
                                if(layer.schemaLoaded){
                                    var form = Ext.create('Ext.panel.Panel', {
                                        autoScroll: true,
                                        items: [
                                            {
                                                xtype: 'fieldset',
                                                layout: 'anchor',
                                                defaults: {
                                                    anchor: '100%'
                                                },
                                                title: 'Features',
                                                items: layer.schemaLoaded.featureTypes[0].fields
                                            }
                                        ],
                                        buttons: [
                                            {
                                                text: 'Close',
                                                handler: function(){
                                                    wnd.openPetroWindows.attrWndView.close();
                                                }
                                            }                                            
                                            
                                        ]
                                    });

                                    wnd.openPetroWindow('attrWndView', {
                                        closable: true,
                                        title: 'Specify Attributes',
                                        maximizable: false,
                                        maximized: false,
                                        width: 350,
                                        //autoScroll: true,
                                        /*layout: 'anchor',
                                        defaults: {
                                            anchor: '100%'
                                        },*/
                                        layout: 'fit',
                                        items: [
                                            form
                                        ]
                                    });
                                }
                            }
            }

            petroresConfig.showFeatureSearcher = function(layer, selectControl){
                var wnd = Ext.getCmp('MainWindow');
                if(layer.schemaLoaded){
                    var form = Ext.create('Ext.form.Panel', {
                        autoScroll: true,
                        items: [
                            {
                                xtype: 'textfield',
                                name: 'OneFieldSearch',
                                fieldLabel: 'Any Field'
                            },
                            {
                                xtype: 'fieldset',
                                layout: 'anchor',
                                defaults: {
                                    anchor: '100%'
                                },
                                title: 'Features',
                                items: layer.schemaLoaded.featureTypes[0].fields
                            }
                        ],
                        buttons: [
                            {
                                text: 'Search',
                                handler: function(){
                                    if(form.getForm().isValid()){
                                        var attrs = form.getForm().getFieldValues(true);
                                        for(var attr in attrs){
                                            if(!attrs[attr] || attrs[attr]==''){
                                                delete attrs[attr];
                                            }else{
                                                if(attrs[attr].toLowerCase){
                                                    attrs[attr] = attrs[attr].toLowerCase();
                                                }
                                            }
                                        }
                                        
                                        var oneFieldSearch = attrs.OneFieldSearch;
                                        delete attrs.OneFieldSearch;
                                        
                                        var bounds = new OpenLayers.Bounds();
                                        var foundSome = false;
                                        
                                        
                                        bigLoop:
                                        for(var featr in layer.features){
                                            if(oneFieldSearch){
                                                for(var attr in layer.features[featr].attributes){
                                                    var comp = layer.features[featr].attributes[attr];
                                                    if(comp.toLowerCase){
                                                        comp = comp.toLowerCase();
                                                    }
                                                    if(comp.indexOf(oneFieldSearch)>=0){
                                                        selectControl.select(layer.features[featr]);
                                                        bounds.extend(layer.features[featr].geometry.getBounds());
                                                        foundSome = true;
                                                        continue bigLoop;
                                                    }
                                                }
                                            }
                                            {
                                                var reallyDo = false;
                                                for(var attr in attrs){
                                                    reallyDo = true;
                                                    var comp = layer.features[featr].attributes[attr];
                                                    if(!comp){
                                                        continue bigLoop;
                                                    }
                                                    if(comp.toLowerCase){
                                                        comp = comp.toLowerCase();
                                                    }
                                                    if(comp.indexOf(attrs[attr])<0){
                                                        continue bigLoop;
                                                    }
                                                }
                                                if(reallyDo){
                                                    selectControl.select(layer.features[featr]);
                                                    bounds.extend(layer.features[featr].geometry.getBounds());
                                                    foundSome = true;
                                                }
                                            }
                                        }
                                        if(foundSome){
                                            layer.map.zoomToExtent(bounds);
                                        }
                                        
                                        wnd.openPetroWindows.attrWnd.close();
                                        
                                    }
                                }
                            }                                            

                        ]
                    });
                    wnd.openPetroWindow('attrWnd', {
                        closable: true,
                        title: 'Find By Attributes',
                        maximizable: false,
                        maximized: false,
                        width: 350,
                        layout: 'fit',
                        items: [
                            form
                        ]
                    });
                }
            }


            petroresConfig.showFeatureEditor = function(layer, features, readOnly){
                //console.log('feature editer showed', features[0].state);
                            var wnd = Ext.getCmp('MainWindow');
                            if(layer.schemaLoaded){
                                for(var featNum in features){
                                    var feature = features[featNum];
                                    if(feature.state){

                                        var vertices = feature.geometry.getVertices();
                                        var editVericesFields = [];
                                        for(var vertI in vertices){
                                            
                                            // from meters to degrees
                                            var tmpVertex = 
                                                new OpenLayers.LonLat(vertices[vertI].x, vertices[vertI].y);
                                            //vertices[vertI].transform(petroresConfig.projGoog, petroresConfig.proj4326);
                                            tmpVertex.transform(petroresConfig.projGoog, petroresConfig.proj4326);
                                            
                                            editVericesFields.push({
                                                xtype: 'fieldset',
                                                title: 'Point ' + vertI,
                                                layout: 'anchor',
                                                defaults: {
                                                    anchor: '100%'
                                                },
                                                items: [
                                                    {
                                                        xtype: 'numberfield',
                                                        fieldLabel: 'X',
                                                        name: 'petroPointX' + vertI,
                                                        //value: vertices[vertI].x
                                                        value: tmpVertex.lon,
                                                        decimalPrecision: 4,
                                                        isModifiedPS: false,
                                                        listeners: {
                                                            change: function(th){
                                                                th.isModifiedPS = true;
                                                            }
                                                        }
                                                    }, 
                                                    {
                                                        xtype: 'numberfield',
                                                        name: 'petroPointY' + vertI,
                                                        fieldLabel: 'Y',
                                                        value: tmpVertex.lat,
                                                        decimalPrecision: 4,
                                                        isModifiedPS: false,
                                                        listeners: {
                                                            change: function(th){
                                                                th.isModifiedPS = true;
                                                            }
                                                        }
                                                    }
                                                ]
                                            });
                                        }


                                        var form = Ext.create('Ext.form.Panel', {
                                            autoScroll: true,
                                            items: [
                                                {
                                                    xtype: 'fieldset',
                                                    layout: 'anchor',
                                                    defaults: {
                                                        anchor: '100%'
                                                    },
                                                    title: 'Features',
                                                    items: layer.schemaLoaded.featureTypes[0].fields
                                                },
                                                {
                                                    xtype: 'fieldset',
                                                    title: 'Coordinates',
                                                    layout: 'anchor',
                                                    defaults: {
                                                        anchor: '100%'
                                                    },
                                                    items: editVericesFields
                                                }
                                            ],
                                            buttons: [
                                                {
                                                    text: 'OK',
                                                    handler: function(){
                                                        if(form.getForm().isValid()){
                                                            // distinguish coordinates from attributes
                                                            var attrs = form.getForm().getFieldValues(true);
                                                            var tmpVerticesX = [];
                                                            var tmpVerticesY = [];
                                                            for(var attr in attrs){
                                                                if(attr.indexOf('petroPoint')===0){
                                                                    vertI = attr.substr('petroPointX'.length);
                                                                    if(attr.substr('petroPoint'.length, 1) === 'X'){
                                                                        tmpVerticesX[parseInt(vertI)] = attrs[attr];
                                                                        //vertices[vertI].move(attrs[attr] - vertices[vertI].x, 0);
                                                                    }else{
                                                                        tmpVerticesY[parseInt(vertI)] = attrs[attr];
                                                                        //vertices[vertI].move(0, attrs[attr] - vertices[vertI].y);
                                                                    }
                                                                    delete attrs[attr];
                                                                }
                                                            }
                                                            feature.attributes = attrs; // form.getForm().getFieldValues(true);
                                                            wnd.openPetroWindows.attrWnd.close();
                                                            
                                                            // back to meters
                                                            //feature.geometry.transform(petroresConfig.proj4326, petroresConfig.projGoog);
                                                            for(vertI in tmpVerticesX){
                                                                tmpVertex = 
                                                                    new OpenLayers.LonLat(tmpVerticesX[vertI], tmpVerticesY[vertI]);
                                                                tmpVertex.transform(petroresConfig.proj4326, petroresConfig.projGoog);
                                                                if(form.getForm().findField('petroPointX' + vertI).isModifiedPS
                                                                || form.getForm().findField('petroPointY' + vertI).isModifiedPS
                                                                ){
                                                                    vertices[vertI].move(tmpVertex.lon - vertices[vertI].x, 
                                                                    tmpVertex.lat - vertices[vertI].y);
                                                                    //console.log('MOVED');
                                                                }else{
                                                                    //console.log('NOT MOVED');
                                                                }
                                                                //vertices[vertI].move(tmpVertex);
                                                                //vertices[vertI] = tmpVertex;
                                                            }
                                                            
                                                            // what the hack?
                                                            // one
                                                            //feature.geometry.transform(petroresConfig.projGoog, petroresConfig.proj4326);
                                                            // two
                                                            //feature.geometry.transform(petroresConfig.proj32639, petroresConfig.projGoog);
                                                            // profit
                                                            //layer.saveStrategy.save([feature]);
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
                                                            if(feature.state===OpenLayers.State.INSERT){
                                                                feature.state = OpenLayers.State.DELETE;
                                                                layer.events.triggerEvent("afterfeaturemodified",
                                                                {feature: feature});
                                                                feature.renderIntent = "select";
                                                                layer.drawFeature(feature);
                                                            }
                                                            //layer.saveStrategy.save([feature]);
                                                        }
                                                    }
                                                }                                            

                                            ]
                                        });

                                        if(feature.state!=='Insert'){
                                            //feature.state = OpenLayers.State.UPDATE;
                                            form.getForm().setValues(feature.attributes);
                                        }

                                        if(readOnly){
                                            form.query('.field').forEach(function(c){c.setDisabled(false);});
                                            //form.disable();
                                        }

                                        wnd.openPetroWindow('attrWnd', {
                                            closable: true,
                                            title: 'Specify Attributes',
                                            maximizable: false,
                                            maximized: false,
                                            width: 350,
                                            //autoScroll: true,
                                            /*layout: 'anchor',
                                            defaults: {
                                                anchor: '100%'
                                            },*/
                                            layout: 'fit',
                                            items: [
                                                form
                                            ]
                                        });
                                    }
                                }
                            }
            };
            
            petroresConfig.createEditingPanel = function(layer){
                if(layer.editingPanel)
                    return;
                
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
                        //if(edit.feature) {
                        var modified = [];
                        for(var ft in layer.saveStrategy.layer.features){
                            var ftt = layer.saveStrategy.layer.features[ft];
                            if(ftt.state){
                                // what the hack?
//                                // one
//                                ftt.geometry.transform(petroresConfig.projGoog, petroresConfig.proj4326);
//                                // two
//                                ftt.geometry.transform(petroresConfig.proj32639, petroresConfig.projGoog);
//                                // profit
                                edit.selectControl.unselectAll();
                                
                                modified.push(ftt);
                            }
                        }
                        
                        layer.saveStrategy.save();
                        
                        //backHack
                        for(ft in modified){
                                //layer.drawFeature(modified[ft]);
//                                // two
//                                modified[ft].geometry.transform(petroresConfig.projGoog, petroresConfig.proj32639);
//                                // one
//                                modified[ft].geometry.transform(petroresConfig.proj4326, petroresConfig.projGoog);
                                
                                layer.drawFeature(modified[ft]);
                        }
                        
                        //for(ft in modified){
                        //        layer.drawFeature(modified[ft]);
                        //}

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
            };

            petroresConfig.layersSaver = function(map){ 
                var conf = {
                    overlays: [],
                    baseLayers: []
                }
                var layer;
                for(var l in map.layers){
                    layer = map.layers[l];
                    if(layer.isBaseLayer){
                        conf.baseLayers.unshift({
                            label: layer.name,
                            url: layer.url,
                            visibility: layer.visibility,
                            params: {
                                layers: layer.params.LAYERS
                            },
                            options: {
                                transitionEffect: layer.options.transitionEffect,
                                projection: layer.options.projection.projCode
                            }
                        });
                    }else if(layer.displayInLayerSwitcher == true){
                        var styleMap = Ext.JSON.encode(layer.styleMap.styles);
                        styleMap = Ext.JSON.decode(styleMap);
                        for(var stName in styleMap){
                            styleMap[stName] = styleMap[stName].defaultStyle;
                        }
                        
                        conf.overlays.unshift({
                            label: layer.name,
                            featureType: layer.protocol.featureType,
                            typeName: layer.schema.substr(layer.schema.lastIndexOf('=')+1),
                            vectorType: layer.psLayerType,
                            opacity: layer.opacity,
                            visibility: layer.visibility,
                            defaultLabelField: layer.defaultLabelField,
                            styleMap: styleMap
                        });
                    }
                }
                
                //console.log(conf);
                conf.extent = map.getExtent().toArray();
                return conf;
            };


            petroresConfig.layersCreator = function(mapName){ 
                
                var layersConfig;
                if(mapName){
                    layersConfig = petroresConfig.mapConfigs[mapName];
                }else{
                    layersConfig = petroresConfig.layersConfig;
                }
                
                var ret = [];
                var layer;
                // visible
                for(var l in layersConfig.baseLayers){
                    layer = layersConfig.baseLayers[l];
                    var opts = layer.options;
                    opts.visibility = layer.visibility;
                    if(opts.visibility)
                        continue;
                    var created = new OpenLayers.Layer.WMS(
                        layer.label, 
                        layer.url, 
                        layer.params, 
                        opts);
                    ret.unshift(created);
                }
                for(var l in layersConfig.baseLayers){
                    layer = layersConfig.baseLayers[l];
                    var opts = layer.options;
                    opts.visibility = layer.visibility;
                    if(!opts.visibility)
                        continue;
                    var created = new OpenLayers.Layer.WMS(
                        layer.label, 
                        layer.url, 
                        layer.params, 
                        opts);
                    ret.unshift(created);
                }
                for(l in layersConfig.overlays){
                    layer = layersConfig.overlays[l];
                    layer = new OpenLayers.Layer.Vector(layer.label, {
                        psLayerType: layer.vectorType,
                        isBaseLayer: false,
                        visibility: layer.visibility,
                        defaultLabelField: layer.defaultLabelField,
                        defaultIdField: petroresConfig.defaultWfsIdField,
                        strategies: [new OpenLayers.Strategy.Fixed(), petroresConfig.makeSaveStrategy()],
                        protocol: new OpenLayers.Protocol.WFS({
                            url: petroresConfig.vectorWfs,
                            featureType: layer.featureType,
                            featureNS: petroresConfig.defaultWfsFeatureNS,
                            geometryName: "GEOM"
                        }),
                        styleMap: layer.styleMap?new OpenLayers.StyleMap(layer.styleMap):undefined
                        ,schema: petroresConfig.vectorWfs + "/DescribeFeatureType?version=1.1.0&typename=" + layer.typeName
                        //,projection: new OpenLayers.Projection("EPSG:32639")
                        ,projection: petroresConfig.proj4326
                        ,version: "1.1.0"
                        , eventListeners: {
                            beforefeaturesadded: function(obj){
                                petroresConfig.showFeatureEditor(obj.object, obj.features);
                            },
                            beforefeaturemodified: function(obj){
                                //console.log(obj);
                                obj.feature.state = OpenLayers.State.UPDATE;
                                petroresConfig.showFeatureEditor(obj.object, [obj.feature]);
                            },
                            loadend: function(eventsObj){
                                petroresConfig.loadWfsSchema(eventsObj.object);
                                petroresConfig.createEditingPanel(eventsObj.object)
                            }
                        }
                    });
                    layer.setOpacity(layersConfig.overlays[l].opacity);
                    ret.unshift(layer);
                }
                
                ret = {
                    layers: ret,
                    extent: 
                    layersConfig.extent
                    ?new OpenLayers.Bounds(layersConfig.extent)
                    :new OpenLayers.Bounds(                                        
                        5082754.0816867,
                        5417407.5350582,
                        5806765.6135031,
                        5857073.3216934
                    )
                }
                
                return ret;
            }

            
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
        };
        
        petroresConfig.getMapMenus = function(){
            var ret = [
                Ext.create('Ext.menu.Item', 
                    {
                        id: 'MainMapMenuItem',
                        text: 'Caspian Sea',
                        handler: function(){
                            Ext.getCmp('MainWindow').openMap();
                        }
                    }),
                Ext.create('Ext.menu.Separator')
            ];
            
            for(var mconf in petroresConfig.mapConfigs){
                if(mconf!='main'){
                    ret.push(
                        Ext.create('Ext.menu.Item', {
                            text: mconf,
                            handler: function(){
                                Ext.getCmp('MainWindow').openMap(this.text);
                            }
                        }));
                }
            }
            
            
            return ret;
        };
    

        petroresConfig.makeSaveStrategy = function(){
            var st = new OpenLayers.Strategy.Save();
            st.events.register('success', {type:'save success', strategy:st}, 
                                function(env, p2, p3, p4){
                                    var layer = env.object.layer,
                                        resp = env.response,
                                        features = resp.reqFeatures ? resp.reqFeatures : {};
                                    
                                    for(var fname in features){
                                        var f = features[fname];
                                        var opCode, opName, mess;
                                        if( f.state){
                                            if(f.state == 'Insert'){
                                                opCode = 'INSERT_GEO_OBJ';
                                                opName = 'Insert GEO object';
                                                mess ='Insert into layer '+'\"'+layer.name+'\"'+' GEO object \"'+f.attributes.Name/*[layer.defaultLabelField]*/+'\"';
                                            } else if( f.state =='Update'){
                                                opCode = 'UPDATE_GEO_OBJ';
                                                opName = 'Update GEO object';
                                                mess ='Update from layer '+'\"'+layer.name+'\"'+' GEO object \"'+f.attributes.Name/*[layer.defaultLabelField]*/+'\"';
                                            } else if( f.state =='Delete'){
                                                opCode = 'DELETE_GEO_OBJ';
                                                opName = 'Delete GEO object';
                                                mess ='Delete from layer '+'\"'+layer.name+'\"'+' GEO object \"'+f.attributes.Name/*[layer.defaultLabelField]*/+'\"';
                                            }
                                        }
                                    }
                                    Ext.Ajax.request({
                                                url: 'form/log/info/ru.gispro.petrores.doc.geoobject',
                                                params: {
                                                    opCode: opCode,
                                                    opName: opName,
                                                    //docID:'docIDTest',
                                                    ok:'ok',
                                                    mess:mess
                                                }
                                            });
                                }    
            );
                
            return st;
        }
        
        //console.log('');
        
        </script>
        <!--<script type="text/javascript" src="lib/ext41/ext-all-debug.js"></script>-->
        <script type="text/javascript" src="lib/ext41/ext-all.js"></script>
        <script type="text/javascript" src="lib/boxselect/Boxselect.js"></script>
        <script type="text/javascript" src="config/helpInfo.js"></script>
        <link rel="stylesheet" type="text/css" href="lib/boxselect/boxselect.css"/>
        <script type="text/javascript">
            
            setInterval(function(){
                Ext.Ajax.request({
                    url: 'void.htm'
                });
            }, 1000 * 60 * 10);

            Ext.onReady(function(){
            // loads map config
            Ext.Ajax.request({
                url: 'form/maps/' + petroresConfig.defaultMap,
                success: function(res){
                    petroresConfig.layersConfig = Ext.JSON.decode(res.responseText);
                }
            });
            
            petroresConfig.windowsXY = Ext.util.Cookies.get('windowsXY');
            if(!petroresConfig.windowsXY || petroresConfig.windowsXY==='[object Object]'){
                petroresConfig.windowsXY = {};
            }else{
                petroresConfig.windowsXY = decodeURI(petroresConfig.windowsXY);
                petroresConfig.windowsXY = Ext.JSON.decode(petroresConfig.windowsXY, true);
                if(!petroresConfig.windowsXY){
                    petroresConfig.windowsXY = {};
                }
            }
            
            // loads maps list
            petroresConfig.mapConfigs = {};
            Ext.Ajax.request({
                url: 'form/maps',
                success: function(res){
                    var mcs = res.responseText.split(/\r?\n/);
                    for(var mc in mcs){
                        var mpcfg = mcs[mc];
                        mpcfg = /*decodeURIComponent(*/mpcfg/*)*/;
                        if(mpcfg.length>0){
                            Ext.Ajax.request({
                                url: 'form/maps/' + mpcfg,
                                success: function(res1){
                                    var u = res1.request.options.url;
                                    mpcfg = u.substring(u.lastIndexOf('/')+1, u.lastIndexOf('.'));
                                    petroresConfig.mapConfigs[mpcfg]
                                        = Ext.JSON.decode(res1.responseText);
                                }
                            });
                        }
                    }
                }
            });
            });
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
url("lib/openlayers212/theme/default/img/editing_tool_bar.png");
background-repeat: no-repeat;
background-position: -78px 0px;
}
.olControlDrawFeaturePointItemActive {
background-image:
url("lib/openlayers212/theme/default/img/editing_tool_bar.png");
background-repeat: no-repeat;
background-position: -78px -23px;                                                            
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
.olControlEditFeaturePencilItemInactive {
background-image:
url("lib/openlayers212/theme/default/img/draw_point_off.png");
}
.olControlEditFeaturePencilItemActive {
background-image:
url("lib/openlayers212/theme/default/img/draw_point_on.png");                                  
}


.petroButtonMapPan {
background-image:
url("images/pan.png");                                  
}
.petroButtonMapEdit {
background-image:
url("images/edit.png");                                  
}
.petroButtonMapSelect {
background-image:
url("images/select.png");                                  
}
.petroButtonMapInfo {
background-image:
url("images/information.png");                                  
}
.petroButtonMapSaveConf {
background-image:
url("images/save.png");                                  
}
.petroButtonMapFindDocs {
background-image:
url("images/finddocs.png");                                  
}
.petroButtonMapDistance {
background-image:
url("images/distance.png");                                  
}
.petroButtonMapArea {
background-image:
url("images/area.png");                                  
}
.petroButtonMapPdf {
background-image:
url("images/pdf.png");                                  
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
