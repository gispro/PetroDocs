Ext.define('PetroRes.view.DocumentSearchForm', {
    extend: 'Ext.form.Panel',

    bodyPadding: 10,

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

        Ext.applyIf(me, {
            listeners:{
                afterRender: function(thisForm, options){
                    me.keyNav= Ext.create('Ext.util.KeyNav', me.el, {                    
                        enter: function()
                        {
                            var but = Ext.getCmp('searchButtonDSF');
                            but.handler.call(but);
                        },
                        scope: this
                    });
                }
            },
            autoScroll: true,
            items: [
            {
                xtype: 'textfield',
                fieldLabel: 'Simple Search Terms',
                name: 'simpleSearch',
                //maxLength: 255,
                msgTarget: 'side',
                anchor: '100%'
            },
            /*{
                xtype: 'textfield',
                fieldLabel: 'File Content Search',
                name: 'contentSearch',
                //maxLength: 255,
                msgTarget: 'side',
                anchor: '100%'
            },*/
            {
                xtype: 'fieldset',
                title: 'Advanced Search',
                id: 'advancedSearchFieldSetDSF',
                collapsible: true,
                collapsed: true,
                defaults: {
                    anchor: '100%'
                },
                items: [                
                    {
                        xtype: 'fieldset',
                        title: 'General',
                        collapsible: false,
                        defaults: {
                            anchor: '100%'
                        },
                        items: [                
                            {
                                xtype: 'textfield',
                                fieldLabel: 'Short Title',
                                name: 'title',
                                maxLength: 255,
                                msgTarget: 'side',
                                anchor: '100%'
                            },
                            {
                                xtype: 'textfield',
                                fieldLabel: 'Full Title',
                                name: 'fullTitle',
                                maxLength: 512,
                                msgTarget: 'side',
                                anchor: '100%'
                            },
                            {
                                xtype: 'textfield',
                                fieldLabel: 'Keywords',
                                name: 'words',
                                msgTarget: 'side',
                                anchor: '100%',
                                getSubmitValue: function(){
                                    return this.value ? this.value.split(/\s*,\s*/) : null;
                                }
                            },
                            {
                                xtype: 'textfield',
                                fieldLabel: 'Comment',
                                name: 'comment',
                                msgTarget: 'side',
                                anchor: '100%'
                            }
                        ]
                    }, 
                    {
                        xtype: 'container',
                        layout: 'column',
                        anchor: '100%',
                        items:[
                            {
                                columnWidth: .5,
                                border: 0,
                                layout: 'anchor',
                                margin: '0, 5, 0, 0',
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        title: 'Numbers',
                                        collapsible: false,
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        items: [
                                            {
                                                xtype: 'numberfield',
                                                fieldLabel: 'Year From',
                                                minValue: 1900,
                                                maxValue: 2100,
                                                name: 'yearFrom',
                                                msgTarget: 'side'
                                            },
                                            {
                                                xtype: 'numberfield',
                                                fieldLabel: 'Year To',
                                                minValue: 1900,
                                                maxValue: 2100,
                                                name: 'yearTo',
                                                msgTarget: 'side',
                                                allowBlank: true
                                            },
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: 'Number',
                                                maxLength: 50,
                                                name: 'number',
                                                msgTarget: 'side'
                                            },
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: 'Archive Number',
                                                maxLength: 50,
                                                name: 'archiveNumber',
                                                msgTarget: 'side',
                                                margins:'0 0 0 0',
                                                allowBlank: true
                                            }
                                        ]
                                    }
                                ]
                            },                    
                            {
                                columnWidth: .5,
                                border: 0,
                                margin: '0, 0, 0, 5',
                                layout: 'anchor',
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        title: 'Dates',
                                        collapsible: false,
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        items: [
                                            {
                                                xtype: 'datefield',
                                                fieldLabel: 'Origination From',
                                                name: 'originationDateFrom',
                                                maxValue: new Date (2100,1,1),
                                                minValue: new Date (1900,1,1),
                                                msgTarget: 'side',
                                                getSubmitValue: function(){
                                                    if(Ext.isDate(this.value))
                                                        return Ext.Date.format(this.value, 'Y-m-d');
                                                    return undefined;
                                                }
                                            },
                                            {
                                                xtype: 'datefield',
                                                fieldLabel: 'Origination To',
                                                name: 'originationDateTo',
                                                maxValue: new Date (2100,1,1),
                                                minValue: new Date (1900,1,1),
                                                msgTarget: 'side',
                                                getSubmitValue: function(){
                                                    if(Ext.isDate(this.value))
                                                        return Ext.Date.format(this.value, 'Y-m-d');
                                                    return undefined;
                                                }
                                            },
                                            {
                                                xtype: 'datefield',
                                                fieldLabel: 'Approval From',
                                                name: 'approvalDateFrom',
                                                maxValue: new Date (2100,1,1),
                                                minValue: new Date (1900,1,1),
                                                msgTarget: 'side',
                                                allowBlank: true,
                                                getSubmitValue: function(){
                                                    if(Ext.isDate(this.value))
                                                        return Ext.Date.format(this.value, 'Y-m-d');
                                                    return undefined;
                                                }
                                            },
                                            {
                                                xtype: 'datefield',
                                                fieldLabel: 'Approval To',
                                                name: 'approvalDateTo',
                                                maxValue: new Date (2100,1,1),
                                                minValue: new Date (1900,1,1),
                                                msgTarget: 'side',
                                                allowBlank: true,
                                                getSubmitValue: function(){
                                                    if(Ext.isDate(this.value))
                                                        return Ext.Date.format(this.value, 'Y-m-d');
                                                    return undefined;
                                                }
                                            },
                                            {
                                                xtype: 'datefield',
                                                fieldLabel: 'Registration From',
                                                name: 'registrationDateFrom',
                                                maxValue: new Date (2100,1,1),
                                                minValue: new Date (1900,1,1),
                                                msgTarget: 'side',
                                                //value: new Date(),
                                                getSubmitValue: function(){
                                                    if(Ext.isDate(this.value))
                                                        return Ext.Date.format(this.value, 'Y-m-d');
                                                    return undefined;
                                                }
                                            },
                                            {
                                                xtype: 'datefield',
                                                fieldLabel: 'Registration To',
                                                name: 'registrationDateTo',
                                                maxValue: new Date (2100,1,1),
                                                minValue: new Date (1900,1,1),
                                                msgTarget: 'side',
                                                getSubmitValue: function(){
                                                    if(Ext.isDate(this.value))
                                                        return Ext.Date.format(this.value, 'Y-m-d');
                                                    return undefined;
                                                }
                                            },
                                            {
                                                xtype: 'datefield',
                                                fieldLabel: 'Placement From',
                                                name: 'placementDateFrom',
                                                maxValue: new Date (2100,1,1),
                                                minValue: new Date (1900,1,1),
                                                msgTarget: 'side',
                                                getSubmitValue: function(){
                                                    if(Ext.isDate(this.value))
                                                        return Ext.Date.format(this.value, 'Y-m-d');
                                                    return undefined;
                                                }
                                            },
                                            {
                                                xtype: 'datefield',
                                                fieldLabel: 'Placement To',
                                                name: 'placementDateTo',
                                                maxValue: new Date (2100,1,1),
                                                minValue: new Date (1900,1,1),
                                                msgTarget: 'side',
                                                getSubmitValue: function(){
                                                    if(Ext.isDate(this.value))
                                                        return Ext.Date.format(this.value, 'Y-m-d');
                                                    return undefined;
                                                }
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    },

                    {
                        xtype: 'container',
                        layout: 'column',
                        anchor: '100%',
                        items:[
                            {
                                columnWidth: .5,
                                border: 0,
                                layout: 'anchor',
                                margin: '0, 5, 0, 0',
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        title: 'Domain',
                                        collapsible: false,
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        items: [
                                            {
                                                xtype: 'pickerfield',
                                                fieldLabel: 'Domain Path',
                                                name: 'domain',
                                                matchFieldWidth: false,
                                                anchor: '100%',
                                                msgTarget: 'side',
                                                editable: false,

                                                valueToRaw: function(){  
                                                    if(this.selectedDomain){
                                                        //this.selectedDomain = data;
                                                        var data = this.selectedDomain;
                                                        var ret = data.name;
                                                        var oldParent;
                                                        while(data.parent && data.parent.id!=oldParent){
                                                            ret = data.parent.name + " :: " + ret;
                                                            oldParent = data.parent.id;
                                                            data = data.parent;
                                                        }
                                                        ret = ret.substring((data.name + " :: ").length);
                                                        return ret;
                                                    }
                                                }, 
                                                rawToValue: function(arg){
                                                    if(this.selectedDomain)
                                                        return this.selectedDomain;
                                                },
                                                getValue: function(){
                                                    if(this.selectedDomain)
                                                        return this.selectedDomain;
                                                },
                                                getRawValue: function(){
                                                    if(this.selectedDomain)
                                                        return this.selectedDomain;
                                                    return '';
                                                },
                                                getSubmitValue: function(){
                                                    if(this.selectedDomain)
                                                        return this.selectedDomain;
                                                    return undefined;
                                                },
                                                createPicker: function() {
                                                    return Ext.create("Ext.Window",{
                                                        pickerField: this,
                                                        preventHeader: true,
                                                        border: 0,
                                                        closable : false,                           
                                                        modal : true,
                                                        layout: 'fit',
                                                        width: this.getWidth()*.8,
                                                        height: this.ownerCt.getHeight()*.5, 
                                                        items: [
                                                        {
                                                            xtype: 'treepanel',
                                                            store: 'DomainsJsonTreeStore',
                                                            //height: 250,
                                                            hideHeaders: true,
                                                            columns: [
                                                            {
                                                                xtype: 'treecolumn',
                                                                dataIndex: 'name',
                                                                flex: 15,
                                                                text: 'Domain'
                                                            }
                                                            ],
                                                            listeners: {
                                                                itemclick: function(th, record){
                                                                    th.ownerCt.ownerCt.pickerField.selectedDomain = record.raw;
                                                                    th.ownerCt.ownerCt.pickerField.setValue(record.raw);
                                                                    th.ownerCt.ownerCt.pickerField.collapse();
                                                                    th.ownerCt.ownerCt.pickerField.fireEvent(
                                                                        'select', th.ownerCt.ownerCt.pickerField, record.raw);
                                                                }
                                                            }
                                                        }]
                                                    });
                                                }
                                            }, 
                                            {
                                                xtype: 'combo',
                                                multiSelect: true,
                                                fieldLabel: 'Project Stage',
                                                hideLabel: false,
                                                name: 'stage',
                                                store: 'StagesJsonStore',
                                                displayField: 'name',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                typeAhead: 'true',
                                                msgTarget: 'side',
                                                //id: 'StageCombo',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }
                                                    }
                                                }//,
                                                //getSubmitValue: function(){
                                                //    return Ext.encode(this.value);
                                            //}
                                            }, 
                                            {
                                                xtype: 'combo',
                                                multiSelect: true,
                                                fieldLabel: 'Project',
                                                hideLabel: false,
                                                name: 'site',
                                                store: 'SitesJsonStore',
                                                displayField: 'name',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                typeAhead: 'true',
                                                msgTarget: 'side',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }
                                                    }
                                                }//,
                                                //getSubmitValue: function(){
                                                //    return Ext.encode(this.value);
                                                //}
                                            }, {
                                                xtype: 'combo',
                                                multiSelect: true,
                                                fieldLabel: 'Type Of Work',
                                                hideLabel: false,
                                                name: 'typeOfWork',
                                                store: 'TypesOfWorkJsonStore',
                                                displayField: 'name',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                typeAhead: 'true',
                                                msgTarget: 'side',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }
                                                    }
                                                }//,
                                                //getSubmitValue: function(){
                                                //    return Ext.encode(this.value);
                                                //}
                                            }, {
                                                xtype: 'combo',
                                                multiSelect: true,
                                                fieldLabel: 'Work Process',
                                                hideLabel: false,
                                                name: 'workProcess',
                                                store: 'WorkProcessesJsonStore',
                                                displayField: 'name',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                typeAhead: 'true',
                                                msgTarget: 'side',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }
                                                    }
                                                }//,
                                                //getSubmitValue: function(){
                                                //    return Ext.encode(this.value);
                                                //}
                                            }          
                                        ]
                                    },
                                    {
                                        xtype: 'fieldset',
                                        title: 'Classified',
                                        collapsible: false,
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        items: [
                                            {
                                                xtype: 'combo',
                                                multiSelect: true,
                                                fieldLabel: 'Classification',
                                                hideLabel: false,
                                                name: 'classification',
                                                store: 'ClassificationsJsonStore',
                                                displayField: 'name',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                multiSelect: true,
                                                typeAhead: 'true',
                                                msgTarget: 'side',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }
                                                    }
                                                }//,
                                                //getSubmitValue: function(){
                                                //    return Ext.encode(this.value);
                                                //}
                                            },                                    
                                            {
                                                xtype: 'textfield',
                                                fieldLabel: 'Limitation Details',
                                                name: 'limitationDetails',
                                                msgTarget: 'side',
                                                anchor: '100%'
                                            }                                    
                                        ]
                                    }
                                ]
                            },                    
                            {
                                columnWidth: .5,
                                border: 0,
                                margin: '0, 0, 0, 5',
                                layout: 'anchor',
                                items: [
                                    {
                                        xtype: 'fieldset',
                                        title: 'Authors',
                                        collapsible: false,
                                        items: [
                                            {
                                                xtype: 'combo',
                                                fieldLabel: 'Authors',
                                                multiSelect: true,
                                                name: 'author',
                                                store: 'AuthorsJsonStore',
                                                displayField: 'shortName',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                anchor: '100%',
                                                typeAhead: 'true',
                                                msgTarget: 'side',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }
                                                    }
                                                }
                                            },                                             {
                                                xtype: 'combo',
                                                fieldLabel: 'Placer',
                                                multiSelect: true,
                                                name: 'placer',
                                                store: 'AuthorsJsonStore',
                                                displayField: 'shortName',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                anchor: '100%',
                                                typeAhead: 'true',
                                                msgTarget: 'side',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }
                                                    }
                                                }
                                            }
                                        ]
                                    }, {
                                        xtype: 'fieldset',
                                        title: 'Document Type',
                                        collapsible: false,
                                        defaults: {
                                            anchor: '100%'
                                        },
                                        items: [
                                            {
                                                xtype: 'combo',
                                                multiSelect: true,
                                                fieldLabel: 'Generic',
                                                name: 'superType',
                                                store: 'SuperTypesJsonStore',
                                                displayField: 'name',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                typeAhead: 'true',
                                                listeners: {
                                                    select: function(){
                                                        this.value = []; 
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }

                                                        var val = this.value;
                                                        var store = me.getForm().findField('type').getStore();
                                                        store.filterBy(function(record){
                                                            var supTypeId = record.get('superType').id;
                                                            for(key in val){
                                                                if(supTypeId === val[key].id){
                                                                    return true;
                                                                }
                                                            }
                                                            return false;
                                                        });
                                                    }
                                                },
                                                //getSubmitValue: function(){
                                                //    return Ext.encode(this.value);
                                                //},
                                                msgTarget: 'side'   
                                            }, {
                                                xtype: 'combo',
                                                queryMode: 'local',
                                                multiSelect: true,
                                                typeAhead: 'true',
                                                fieldLabel: 'Type',
                                                lastQuery: '',
                                                //hideLabel: true,
                                                name: 'type',
                                                store: Ext.getStore('TypesJsonStore').copyStore(),
                                                displayField: 'name',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        var isGeo = false;
                                                        for(var key in this.valueModels){
                                                            var typ = this.valueModels[key].data;
                                                            if(!Ext.isArray(typ.typeExt)){
                                                                typ.typeExt = [typ.typeExt];
                                                            }                                               
                                                            this.value.push(typ);
                                                            if(typ.isGeo){
                                                                isGeo = true;
                                                            }
                                                        }
                                                        if(isGeo){
                                                            Ext.getCmp('geoTypeFieldSetDSF').expand();
                                                        }else{
                                                            Ext.getCmp('geoTypeFieldSetDSF').collapse();
                                                        }
                                                    }
                                                },
                                                //getSubmitValue: function(){
                                                //    return Ext.encode(this.value);
                                                //},
                                                msgTarget: 'side'
                                            },
                                            {
                                                xtype: 'textarea',
                                                fieldLabel: 'Origination Details',
                                                name: 'originationDetails',
                                                msgTarget: 'side',
                                                anchor: '100%'
                                            },
                                            {
                                                xtype: 'combo',
                                                multiSelect: true,
                                                fieldLabel: 'Update Cycle',
                                                hideLabel: false,
                                                name: 'periodicity',
                                                store: 'PeriodicitiesJsonStore',
                                                displayField: 'name',
                                                queryMode: 'local',
                                                lastQuery: '',
                                                typeAhead: 'true',
                                                msgTarget: 'side',
                                                listeners: {
                                                    select: function(){
                                                        this.value = [];
                                                        for(var key in this.valueModels){
                                                            this.value.push(this.valueModels[key].raw);
                                                        }
                                                    }
                                                }//,
                                                //getSubmitValue: function(){
                                                //    return Ext.encode(this.value);
                                                //}
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }, 
                    {
                        xtype: 'fieldset',
                        id: 'geoTypeFieldSetDSF',
                        title: 'Geo Data attributes',
                        anchor: '100%',
                        layout: {
                            type: 'hbox'
                        },
                        defaults: {
                            margins:'0 15 0 0',
                            //anchor: '100%'
                            flex: 1
                        },
                        collapsible: true,
                        collapsed: true,
                        items: [
                            {
                                xtype: 'combo',
                                multiSelect: true,
                                fieldLabel: 'Geo Type',
                                hideLabel: false,
                                name: 'geometryType',
                                store: 'GeoTypesJsonStore',
                                displayField: 'name',
                                queryMode: 'local',
                                lastQuery: '',
                                typeAhead: 'true',
                                msgTarget: 'side',
                                listeners: {
                                    select: function(){
                                        this.value = [];
                                        for(var key in this.valueModels){
                                            this.value.push(this.valueModels[key].raw);
                                        }

                                        for(key in this.value){
                                            if(this.value[key].name === 'raster'){
                                                Ext.getCmp('resolutionFieldFromDSF').enable();
                                                Ext.getCmp('resolutionFieldToDSF').enable();
                                                return;
                                            }
                                        }

                                        Ext.getCmp('resolutionFieldToDSF').disable();
                                        Ext.getCmp('resolutionFieldFromDSF').disable();
                                    }
                                }//,
                                //getSubmitValue: function(){
                                //    return Ext.encode(this.value);
                                //}
                            }, {
                                xtype: 'numberfield',
                                id: 'scaleFieldFrom',
                                fieldLabel: 'Scale From',
                                maxLength: 50,
                                name: 'scale',
                                msgTarget: 'side'
                            },{
                                xtype: 'numberfield',
                                id: 'scaleFieldTo',
                                fieldLabel: 'Scale To',
                                maxLength: 50,
                                name: 'scale',
                                msgTarget: 'side'
                            },
                            {
                                xtype: 'numberfield',
                                id: 'resolutionFieldFrom',
                                fieldLabel: 'Resolution From',
                                maxLength: 50,
                                name: 'resolution',
                                msgTarget: 'side'
                            },{
                                xtype: 'numberfield',
                                id: 'resolutionFieldTo',
                                fieldLabel: 'Resolution To',
                                maxLength: 50,
                                name: 'resolution',
                                msgTarget: 'side'
                            },
                            {
                                xtype: 'textfield',
                                fieldLabel: 'Projection',
                                maxLength: 50,
                                name: 'projectionCode',
                                msgTarget: 'side',
                                margins:'0 0 0 0'
                            }
                        ]
                    },            
                    {
                        xtype: 'fieldset',
                        //id: 'fileFieldSet',
                        title: 'File(s)',
                        anchor: '100%',
                        collapsible: false,
                        items: [
                            {
                                xtype: 'textfield',
                                fieldLabel: 'File Name',
                                maxLength: 50,
                                anchor: '100%',
                                name: 'fileNames',
                                msgTarget: 'side',
                                margins:'0 0 0 0'//,
                                //getSubmitValue: function(){
                                //    return this.value ? Ext.encode(this.value.split(/\s*,\s*/)) : null;
                                //}
                            }                    
                        ]
                    }, 

                    {
                        xtype: 'fieldset',
                        title: 'Geo Reference',
                        collapsible: false,
                        defaults: {
                            anchor: '100%'
                        },
                        items: [                
                            {
                                xtype: 'textfield',
                                name: 'geoObjects',
                                anchor: '100%',
                                fieldLabel: 'Selected',
                                readOnly: true,
                                getSubmitValue: function(){
                                    var obj = [];
                                    for(var feat in me.selectedFeatures){
                                        var oneFeatArr = me.selectedFeatures[feat].fid.split('.');
                                        obj.push({
                                            idInTable: oneFeatArr[1],
                                            tableName: oneFeatArr[0]
                                        });
                                    }
                                    return obj;// Ext.encode(obj);
                                }

                            }, {
                                xtype: 'panel',
                                height: 250,
                                layout: 'border',
                                items:[
                                    mapPanel,
                                    layerTree
                                ]
                                /*listeners: {
                                    afterrender: function(th){
                                        var renderer = OpenLayers.Util.getParameters(window.location.href).renderer;
                                        renderer = (renderer) ? [renderer] : OpenLayers.Layer.Vector.prototype.renderers;

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
                                                                //if(data[name2] instanceof String){
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

                                        var leftLoad;
                                        var biggestExtent = new OpenLayers.Bounds();

                                        for(var l in layers){
                                            if(layers[l].CLASS_NAME == 'OpenLayers.Layer.Vector'){
                                                vectorLayers.push(layers[l]);

                                                layers[l].events.on({
                                                    featureselected: function() {
                                                        return fun(this);
                                                    },
                                                    featureunselected: function() {
                                                        return fun(this);
                                                    }//, cool jumping to map, customer didnt want
                                                    //loadend: function(){
                                                    //    biggestExtent.extend(this.getDataExtent());
                                                    //    leftLoad--;
                                                    //    if(leftLoad<=0){
                                                    //        th.map.zoomToExtent(biggestExtent);
                                                    //    }
                                                    //}
                                                });
                                            };
                                        }

                                        leftLoad = vectorLayers.length;

                                        th.map = new OpenLayers.Map(th.body.dom.id, {
                                            controls: [
                                                new OpenLayers.Control.Navigation({zoomBoxEnabled: false}),
                                                new OpenLayers.Control.PanZoom(),
                                                new OpenLayers.Control.LayerSwitcher(),
                                                new OpenLayers.Control.ArgParser(),
                                                new OpenLayers.Control.Attribution()
                                            ],
                                            layers: layers
                                        });
                                        me.theMap = th.map;
                                        var control = new OpenLayers.Control.SelectFeature(vectorLayers, {
                                                    clickout: false, toggle: true,
                                                    multiple: false, hover: false,
                                                    toggleKey: "ctrlKey", // ctrl key removes from selection
                                                    multipleKey: "shiftKey", // shift key adds to selection
                                                    box: true
                                                });

                                        th.map.addControl(control);
                                        control.activate();
                                        th.map.zoomToExtent(new OpenLayers.Bounds(45.00, 36.18, 48, 47.50));
                                        //th.map.zoomToMaxExtent();
                                    }
                                }*/
                            }
                        ]
                    }
                ]
            }
            ],
            buttons: [{
                text: 'Search',
                id: 'searchButtonDSF',
                handler: function() {
                    var form = this.up('form').getForm();
                    //if(form.isValid())
                    {
                        
                        Ext.Ajax.request({
                            headers: {
                                Accept: 'application/json'
                            },
                            url: 'rest/documents/find',
                            jsonData: form.getValues(),
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
                                
                                /*var stor = Ext.create('Ext.data.Store', {
                                    fields: [
                                        {
                                            name: 'id',
                                            type: 'int'
                                        },
                                        {
                                            name: 'year'
                                        },
                                        {
                                            name: 'number'
                                        },
                                        {
                                            name: 'archiveNumber'
                                        },
                                        {
                                            name: 'title'
                                        },
                                        {
                                            name: 'fullTitle'
                                        },
                                        {
                                            name: 'type'
                                        },
                                        {
                                            name: 'comment'
                                        },
                                        {
                                            name: 'originationDetails'
                                        },
                                        {
                                            name: 'limitationDetails'
                                        },
                                        {
                                            name: 'originationDate',
                                            type: 'date'
                                        },
                                        {
                                            name: 'approvalDate',
                                            type: 'date'
                                        },
                                        {
                                            name: 'registrationDate',
                                            type: 'date'
                                        },
                                        {
                                            name: 'placementDate',
                                            type: 'date'
                                        },
                                        {
                                            name: 'placer'
                                        },
                                        {
                                            name: 'updateDate',
                                            type: 'date'
                                        },
                                        {
                                            name: 'pageCount'
                                        },
                                        {
                                            name: 'geometryType'
                                        },
                                        {
                                            name: 'scale'
                                        },
                                        {
                                            name: 'resolution'
                                        },
                                        {
                                            name: 'projection'
                                        },
                                        {
                                            name: 'periodicity'
                                        },
                                        {
                                            name: 'classification'
                                        },
                {
                    name: 'site'
                },
                {
                    name: 'stage'
                },
                {
                    name: 'words'
                },
                {
                    name: 'authors'
                },
                {
                    name: 'geoObjects'
                },
                {
                    name: 'domain'
                },
                {
                    name: 'files'
                },
                {
                    name: 'typeOfWork'
                },
                {
                    name: 'workProcess'
                }
                                    ],
                                    data: ret,
                                    proxy: {
                                        type: 'memory',
                                        reader: {
                                            type: 'json',
                                            root: 'documents'
                                        }
                                    }
                                });*/
                                
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
                        
                        
                        /*form.submit({
                            url: 'form/findDocument',
                            waitMsg: 'Uploading new document...',
                            success: function(fp, o) {
                                //console.log(o);
                                Ext.Msg.alert(
                                    'Success', 
                                    'Your document "' + o.result.documents[0].title + '" has been uploaded.',
                                    function(){
                                        me.theMap.destroy();
                                        me.up().close();
                                    }
                                );
                            },
                            failure: function(fp, o){
                                Ext.Msg.alert('Failure', o.result.msg);
                            }
                        });*/
                    }
                }
            }]
        });

        me.callParent(arguments);
    }
});