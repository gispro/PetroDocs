Ext.define('PetroRes.view.DocumentSimpleSearchPanel', {
    extend: 'Ext.panel.Panel',

    bodyPadding: 0,

    initComponent: function() {
        var me = this;

        var layers = petroresConfig.layersCreator();
        var vectorLayers = [];
        var fun = function() {
            var str = '';
            me.selectedFeatures = [];
            for(var vact in vectorLayers){
                var ths = vectorLayers[vact];

                for(var feat in ths.selectedFeatures){
                    me.selectedFeatures.push(ths.selectedFeatures[feat]);

                    var data = ths.selectedFeatures[feat].data;
                    var met = ths.defaultLabelField;
                    if(!data[met])
                        met = undefined;
                    if(!met){
                        for(var name in data){
                            if(name.indexOf('nam')!=-1){
                                met = name;
                                break;
                            }
                        }
                    }
                    if(!met){
                        for(var name2 in data){
                            if(Ext.isString(data[name2])){
                                met = name2;
                                break;
                            }
                        }
                    }
                    if(!met){
                        str = str + ', ' + ths.selectedFeatures[feat].fid.replace(/\s+$/,'');
                    }else{
                        str = str + ', ' + data[met].replace(/\s+$/,'');
                    }
                }
            }

            str = str.substring(2);
            me.getForm().findField('geoObjects').setValue(str);

        };
        for(var l in layers){
            if(layers[l].CLASS_NAME == 'OpenLayers.Layer.Vector'){
                vectorLayers.push(layers[l]);

                layers[l].events.on({
                    featureselected: function() {
                        return fun(this);
                    },
                    featureunselected: function() {
                        return fun(this);
                    }
                });
            };
        }
        me.theMap = new OpenLayers.Map({
            layers: layers,
            allOverlays: false
            ,displayProjection: new OpenLayers.Projection("EPSG:4326")
            ,projection: new OpenLayers.Projection("EPSG:4326")
        });
        var mapPanel = 
                Ext.create('GeoExt.panel.Map', {
                    //id: 'bigGextMapPanel',
                    map: me.theMap,
                    region: 'center',
                    //extent: new OpenLayers.Bounds(45.00, 36.18, 55, 47.50)
                    extent: new OpenLayers.Bounds(
                        5082754.0816867,
                        5417407.5350582,
                        5806765.6135031,
                        5857073.3216934
                    )
                });
        var layerStore = Ext.create('Ext.data.TreeStore', {
            model: 'GeoExt.data.LayerTreeModel',
            //storeId: 'NewDocMapLayers',
            root: {
                expanded: true,
                children: [
                    {
                        plugins: [
                            {
                                ptype: 'gx_baselayercontainer'
                                //,store: layers
                                ,loader: {store: mapPanel.layers}
                            }
                        ],
                        expanded: true,
                        text: "Base Maps"
                    }, {
                        plugins: [
                            {
                                ptype: 'gx_overlaylayercontainer'
                                ,loader: {store: mapPanel.layers}
                                //,store: layers
                            }
                        ],
                        expanded: true
                    }
                ]
            }
        });

        var layerTree = Ext.create('GeoExt.tree.Panel', {
            border: true,
            region: "west",
            title: "Layers",
            width: 200,
            split: true,
            collapsible: true,
            collapseMode: "mini",
            autoScroll: true,
            store: layerStore,
            rootVisible: false,
            lines: false,
            listeners:{
                afterrender: function(){
                    Ext.getCmp('advancedSearchFieldSetDSF').collapse();
                }
            }
        });


        var control = new OpenLayers.Control.SelectFeature(vectorLayers, {
                    clickout: false, toggle: true,
                    multiple: false, hover: false,
                    toggleKey: "ctrlKey", // ctrl key removes from selection
                    multipleKey: "shiftKey", // shift key adds to selection
                    box: false
                });
        me.theMap.addControl(control);
        control.activate();
        
        var tfSearch = Ext.create('Ext.form.TextField',{
                region:'center'
            }),
            btnSearchStart = Ext.create('Ext.Button',
            {
                xtype:'button', iconCls:'ab_search', cls:'album-btn', width:23, height:24, region:'east',
                handler:function(){
                        Ext.Ajax.request({
                            headers: {
                                Accept: 'application/json'
                            },
                            url: 'rest/documents/find',
                            jsonData: {simpleSearch: tfSearch.value},
                            success: function(resp, opts){
                                var ret = Ext.decode(resp.responseText);
                                petroresConfig.makeAllIdsNumbers(ret);
                                console.log(["вот, получилось", ret])

                                var wnd = Ext.getCmp('MainWindow');

                                var stor = Ext.create('Ext.data.Store', {
                                    model: Ext.getStore('DocumentsJsonStore').getProxy().getModel(),
                                    data: ret,
                                    proxy: {
                                        type: 'memory',
                                        reader: {
                                            type: 'json',
                                            root: 'documents'
                                        }
                                    }
                                });

                                var grid = Ext.create(
                                    'PetroRes.view.DocumentsGridPanel', 
                                    {
                                        store: stor,
                                        listeners:{
                                            itemdblclick: function(ths, rec){

                                                var editForm = Ext.create(
                                                        'PetroRes.view.DocumentFormEdit'
                                                    );
                                                wnd.openPetroWindow('editDoc', {
                                                    closable: true,
                                                    width:wnd.getWidth()*0.9,
                                                    title: 'Edit Document',
                                                    maximizable: true,
                                                    maximized: true,
                                                    layout: 'fit',
                                                    items: [
                                                        editForm
                                                    ]                                    
                                                });
                                                editForm.loadRecord(rec);
                                            }
                                        }
                                    }
                                );
                                if(wnd.openPetroWindows.searchres){
                                    wnd.openPetroWindows.searchres.close();
                                }
                                wnd.openPetroWindow('searchres', {
                                    closable: true,
                                    width:wnd.getWidth()*0.9,
                                    title: 'Found Documents',
                                    maximizable: true,
                                    maximized: false,
                                    layout: 'fit',
                                    items: [
                                        grid
                                    ]                                    
                                });

                            }
                        });
                    }
            });
        
        Ext.applyIf(me, {
            listeners:{
                afterRender: function(thisForm, options){
                    me.keyNav= Ext.create('Ext.util.KeyNav', me.el, {                    
                        enter: function()
                        {
                            btnSearchStart.handler.call();
                        },
                        scope: this
                    });
                }
            },
            layout:'border',
            items: [ tfSearch, btnSearchStart]
        });

        me.callParent(arguments);
    }
});


