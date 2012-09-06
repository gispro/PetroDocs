Ext.define('PetroRes.view.DocumentFormEdit', {
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
            me.getForm().findField('geoObjects').oldSetValue(str);

        };
        for(var l in layers){
            if(layers[l].CLASS_NAME == 'OpenLayers.Layer.Vector'){
                vectorLayers.push(layers[l]);
                
                //var ppp = layers[l];
                layers[l].events.on({
                    featureselected: function() {
                        return fun(this);
                    },
                    featureunselected: function() {
                        return fun(this);
                    }
                    , loadend: function(){
                        me.loadSelectedFeatures(this);
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
                        plugins: [{
                            ptype: 'gx_overlaylayercontainer'
                            //,store: mapPanel.layers
                            ,loader: {store: mapPanel.layers}
                        }],
                        expanded: true
                    },
                    {
                        plugins: [{
                            ptype: 'gx_baselayercontainer'
                            //,store: mapPanel.layers
                            ,loader: {store: mapPanel.layers}
                        }],
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
            collapseMode: "header",
            autoScroll: true,
            store: layerStore,
            rootVisible: false,
            lines: false
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

        me.featureSelector = control;


        Ext.applyIf(me, {
            autoScroll: true,

            loadSelectedFeatures: function(layer){
                if(me.featuresToSelect){
                    for(var feat in me.featuresToSelect){
                        var fid = me.featuresToSelect[feat].tableName + '.' + me.featuresToSelect[feat].idInTable;
                        var feature = layer.getFeatureByFid(fid);
                        if(feature){
                            me.featureSelector.select(feature);
                        }
                    }
                }
            },
            
            listeners: {
                afterrender: function(){
                    var frm = me.getForm();
                    var oldLoadRecord = frm.loadRecord;
                    frm.loadRecord = function(){
                        //console.log(arguments);

                        if(!Ext.isArray(arguments[0].data.geoObjects))
                            arguments[0].set('geoObjects', [arguments[0].data.geoObjects]);

                        var authors = arguments[0].data.authors;
                        if(!Ext.isArray(authors))
                            authors = [authors];
                        var cont = Ext.getCmp('authorsContainerDFE');
                        for(var i = 1; i < authors.length; i++){
                            cont.items.getAt(0).handler.call(cont.items.getAt(0), cont.items.getAt(0));
                        }
                        
                        for(i = 0; i < authors.length; i++){
                            var shit;
                            if(i==0){
                                shit = cont.items.getAt(2).items.getAt(0).items.getAt(0).items.getAt(0);
                                //cont.items.getAt(2).items.getAt(0).items.getAt(0).items.getAt(0).setValue(authors[i]);
                            }else{
                                shit = cont.items.getAt(i+2).items.getAt(0);
                            }
                            //var rec = shit.findRecord('id', parseInt(authors[i].id));
                            //shit.setValue(rec);
                            shit.setValue(authors[i]);
                        }
                        //alert('shit!');
                        
                        
                        var files = arguments[0].data.files;
                        {
                            if(files){
                                if(!Ext.isArray(files))
                                    files = [files];
                                me.fileObjects = files;
                            }else{
                                me.fileObjects = [];
                                files = [];
                            }
                            for(i=0;i<files.length;i++){
                                if(me.fileObjects[i].mimeType!=='directory'){
                                    me.fileObjects[i].ext = files[i].path.substr(files[i].path.lastIndexOf('.') + 1);
                                }
                            }
                            var filesCont = Ext.getCmp('loadedFileFieldSetDFE');
                            for(i = 0; i < files.length; i++){

                                if(files[i].mimeType==='directory'){
                                    filesCont.add({
                                        xtype: 'label',
                                        html: '<a target="_blank" href="' + files[i].path +'">'+files[i].path+'</a> '
                                        //text: files[i].path
                                    });
                                }else{

                                    var nm = files[i].path.substr(files[i].path.lastIndexOf(petroresConfig.pathFolderSeparator) + 1);
                                    filesCont.add({
                                        xtype: 'label',
                                        html: '<a target="_blank" href="form/file/' + files[i].id + '/'+nm+'">'+nm+'</a> '
                                        //text: files[i].path
                                    });

                                }

                            }
                        }
                        
                        var type = arguments[0].data.type;
                        Ext.getCmp('superTypeComboDFE').setValue(type.superType);
                        
                        var ret = frm.oldLoadRecord.apply(frm, arguments);

                        var typeCombo = Ext.getCmp('typeComboDFE');
                        typeCombo.fireEvent('select', typeCombo, [{data: type}]);
                        
                        for(i=0;i<files.length;i++){
                            var fs = Ext.getCmp('fileFieldSetDFE');
                            fs.removeAll();
                            fs.add({
                                xtype: 'textfield',
                                layout: 'column',   
                                anchor: '100%',
                                fieldLabel: 'Directory',
                                value: files[i].path,
                                combineErrors: false,
                                msgTarget: 'under',
                                name: 'directory'
                            });
                        }
                        

                        var domainPicker = Ext.getCmp('domainPickerDFE');
                        domainPicker.fireEvent('select', domainPicker, domainPicker.selectedDomain);

                        var placer = arguments[0].data.placer;
                        if(placer)
                            Ext.getCmp('posterLabelDFE').setValue(placer.shortName);

                        return ret;
                    }
                    frm.oldLoadRecord = oldLoadRecord;
                }
            },
            
            
            // the file field block ============
            fileFieldPattern: {
                xtype: 'fieldcontainer',
                maxAllowedFiles: 3,
                minAllowedFiles: 1,
                alreadyAddedFiles: 0,
                startSeekForShowFile: 0,
                layout: 'column',   
                anchor: '100%',
                fieldLabel: 'Document',
                //labelWidth: 60,
                combineErrors: false,
                msgTarget: 'under',
                listeners: {
                    afterrender: function() {
                        this.showAllFileFields();
                    }
                },
                showAllFileFields: function(){
                    
                    //count loaded files of this ext
                    var thisExtCount = 0;
                    for(var fileII = 0; fileII<me.fileObjects.length; fileII++){
                        var f = me.fileObjects[fileII];
                        for(var xxx in this.typExts){
                            if(f.ext === this.typExts[xxx]){
                                thisExtCount++;
                                break;
                            }
                        }
                    }
                    
                        var ii;

                        var maxToAdd = 
                            this.minAllowedFiles > thisExtCount ? 
                            this.minAllowedFiles :
                            thisExtCount;
                        
                        for(ii = 0; ii < maxToAdd; ii++){
                            
                            /*if(fileII<me.fileObjects.length){
                                var f = me.fileObjects[fileII];
                                var good = false;
                                for(var xxx in this.typExts){
                                    if(f.ext === this.typExts[xxx]){
                                        good = true;
                                        break;
                                    }
                                }
                                fileII++;
                                if(!good){
                                    ii--;
                                    f = undefined;
                                    continue;
                                }
                            } */                          
                            
                            this.addFileField();
                        }
                },
                addFileField: function(){
                    this.alreadyAddedFiles++;
                    var superCont = this;
                    
                    // seek for showfile
                    var fileObj;
                    var fileII;
                    var found = 0;
                    var typpExxts = this.typExts;
                    seek:
                    for(fileII = 0; fileII<me.fileObjects.length; fileII++){
                        var f = me.fileObjects[fileII];
                        for(var xxx in this.typExts){
                            if(f.ext === this.typExts[xxx]){
                                found++;
                                if(found>=this.alreadyAddedFiles){
                                    fileObj = f;
                                    break seek;
                                }
                            }
                        }
                    }
                    
                    if(fileObj)
                        var nm = fileObj.path.substr(fileObj.path.lastIndexOf(petroresConfig.pathFolderSeparator) + 1);
                    
                    this.items.getAt(1).add(
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            anchor: '100%',
                            
                            items: [
                                {
                                    xtype: 'filefield',
                                    fieldLabel: 
                                        this.alreadyAddedFiles,
                                    flex: 1,
                                    name: 'content',
                                    msgTarget: 'side',
                                    allowBlank: (fileObj?true:false),
                                    buttonText: 'Select File',
                                    fileObject: fileObj,
                                    listeners: {
                                        beforerender: function(that) {
                                            if(!that.oldGetErrors){
                                                that.oldGetErrors = that.getErrors;
                                            
                                                that.getErrors = function() {
                                                    var ret = that.oldGetErrors.apply(that, arguments);
                                                    if(that.fileExistsCheck){
                                                        if(ret){
                                                            if(Ext.isArray(ret) && that.fileExistsCheck){
                                                                ret.push(that.fileExistsCheck)
                                                            }else{
                                                                ret = [ret, that.fileExistsCheck]
                                                            }
                                                        }else{
                                                                ret = [that.fileExistsCheck]
                                                        }
                                                    }
                                                    return ret;
                                                };
                                            }
                                        },
                                        change: function(that, fileName){
                                            var path = me.getForm().findField('path').getValue();
                                            if(path){
                                                fileName = fileName.substring(fileName.lastIndexOf('\\')+ 1);
                                                fileName = path + '\\' + fileName;
                                                
                                                if(fileObj){
                                                    if(fileObj.path === fileName){
                                                        delete that.fileExistsCheck;
                                                    }else{
                                                        var shittt = fileObj.path.substring(fileObj.path.lastIndexOf('\\')+ 1);
                                                        that.fileExistsCheck = 'Choose file with the name '+shittt+' here';
                                                    }
                                                }else {
                                                    var nowExt = fileName.substring(fileName.lastIndexOf('.')+ 1);
                                                    var matched = false;
                                                    
                                                    for(var xxx in typpExxts){
                                                        if(nowExt.toUpperCase() === typpExxts[xxx].toUpperCase()){
                                                            matched = true;
                                                            break;
                                                        }
                                                    }
                                                    if( !matched ){
                                                        that.fileExistsCheck = 'Choose file with the right extention here';
                                                    }else{
                                                        delete that.fileExistsCheck;
                                                        // check if exists
                                                        Ext.Ajax.request({
                                                            url: 'rest/files',
                                                            method: 'GET',
                                                            params: {
                                                                path: fileName
                                                            },
                                                            headers: {
                                                                Accept: 'application/json'
                                                            },
                                                            success: function(response){
                                                                var obj = Ext.decode(response.responseText);
                                                                if(obj.total>=1){
                                                                    that.fileExistsCheck = 'File with the name '+fileName+' is already registered';
                                                                    //that.markInvalid('File with this name is already registered');
                                                                }else{
                                                                    delete that.fileExistsCheck;
                                                                }
                                                            }
                                                        });                                                
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }, 
                                {
                                    xtype: 'label',
                                    html: (fileObj?'<a target="_blank" href="form/file/' + fileObj.id + '/'+nm+'">'+nm+'</a> ':'No File')                                    
                                },
                                {
                                    xtype:'hidden',                       
                                    name:'unchangedFiles',
                                    getSubmitValue: function(){
                                        if(fileObj){
                                            var tmpExt = fileObj.ext;
                                            delete fileObj['ext'];
                                            var ret = Ext.encode(fileObj);
                                            fileObj.ext = tmpExt;
                                            return ret;
                                        }else
                                            return null;
                                    }
                                },
                                {
                                    xtype: 'button',
                                    //text: '-',
                                    //flex: 1,
                                    icon: 'lib/ext41/examples/restful/images/delete.png',
                                    listeners: {
                                        click: function(){
                                            //console.log([this.ownerCt, this.ownerCt.ownerCt.items.getAt(this.ownerCt.fileFieldNumber)]);
                                            this.ownerCt.ownerCt.remove(this.ownerCt);
                                            superCont.alreadyAddedFiles--;
                                            var lll = 1
                                            superCont.items.getAt(1).items.each(function(item){
                                                item.items.getAt(0).labelEl.dom.innerText = lll;
                                                lll++;
                                            });
                                            superCont.items.getAt(0).items.getAt(0).setDisabled(false);
                                        }
                                    }
                                    
                                }
                            ]
                        }
                    );
                    if(this.alreadyAddedFiles>=this.maxAllowedFiles){
                        this.items.getAt(0).items.getAt(0).setDisabled(true);
                    }
                },
                items: [
                    {
                        xtype: 'container',
                        width: 30,
                        items: [
                            {
                                xtype: 'button',
                                //text: '+',
                                icon: 'lib/ext41/resources/themes/images/default/dd/drop-add.gif',
                                listeners: {
                                    beforerender: function(){
                                        this.fileFieldMe = this.up("[xtype='fieldcontainer']");
                                        return true;
                                    },
                                    click: function(){
                                        this.fileFieldMe.addFileField();
                                    }
                                }
                            }                        
                        ]
                    },
                    {
                        xtype: 'panel',
                        border: 0,
                        columnWidth: 1,
                        layout: {
                            type: 'anchor'
                        },
                        items: []
                    } 
                ]
            },
            
            // END the file field block ============
            
            
            
            
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
                        xtype:'hidden',                       
                        name:'id'
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Short Title',
                        name: 'title',
                        maxLength: 255,
                        msgTarget: 'side',
                        allowBlank: false,
                        anchor: '100%'
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Full Title',
                        name: 'fullTitle',
                        maxLength: 512,
                        msgTarget: 'side',
                        allowBlank: false,
                        anchor: '100%'
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Keywords',
                        name: 'words',
                        msgTarget: 'side',
                        anchor: '100%',
                        getSubmitValue: function(){
                            return this.value ? Ext.encode(this.value.split(/\s*,\s*/)) : null;
                        },
                        valueToRaw: function(value){
                            if(value){
                                if(!Ext.isArray(value)){
                                    value = [value];
                                }
                                var ret = '';
                                for(var i in value){
                                    ret = ret + (i>0?', ':'') + value[i].word;
                                }
                                
                                return ret;
                            }
                            return '';
                        }
                    },
                    {
                        xtype: 'textarea',
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
                                        fieldLabel: 'Year',
                                        minValue: 1900,
                                        maxValue: 2100,
                                        name: 'year',
                                        msgTarget: 'side',
                                        allowBlank: true
                                    },
                                    {
                                        xtype: 'textfield',
                                        fieldLabel: 'Number',
                                        maxLength: 50,
                                        name: 'number',
                                        msgTarget: 'side',
                                        allowBlank: false
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
                                        fieldLabel: 'Origination',
                                        name: 'originationDate',
                                        maxValue: new Date (2100,1,1),
                                        minValue: new Date (1900,1,1),
                                        msgTarget: 'side',
                                        allowBlank: false
                                    },
                                    {
                                        xtype: 'datefield',
                                        fieldLabel: 'Approval',
                                        name: 'approvalDate',
                                        maxValue: new Date (2100,1,1),
                                        minValue: new Date (1900,1,1),
                                        msgTarget: 'side',
                                        allowBlank: true
                                    },
                                    {
                                        xtype: 'datefield',
                                        fieldLabel: 'Registration',
                                        name: 'registrationDate',
                                        maxValue: new Date (2100,1,1),
                                        minValue: new Date (1900,1,1),
                                        msgTarget: 'side',
                                        value: new Date(),
                                        allowBlank: false
                                    }/*,
                                    {
                                        xtype: 'datefield',
                                        fieldLabel: 'Placement',
                                        name: 'placementDate',
                                        maxValue: new Date (2100,1,1),
                                        minValue: new Date (1900,1,1),
                                        msgTarget: 'side',
                                        allowBlank: true
                                    }*/
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
                                        id: 'domainPickerDFE',
                                        matchFieldWidth: false,
                                        anchor: '100%',
                                        msgTarget: 'side',
                                        editable: false,
                                        listeners: {
                                            select: function(that, domain){
                                                var oldDomain = domain;
                                                var path = domain.pathPart;
                                                while(domain.parent){
                                                    domain = domain.parent;
                                                    path = domain.pathPart + petroresConfig.pathFolderSeparator + path;
                                                }
                                                me.getForm().findField('path').setValue(path);
                                                
                                                // get stage and site
                                                var curDomain = oldDomain;
                                                var site, stage, nextDomain;
                                                var tow, wp;
                                                while(true){
                                                    if(curDomain.site){
                                                        site = curDomain.site;
                                                    }
                                                    if(curDomain.typeOfWork){
                                                        tow = curDomain.typeOfWork;
                                                    }
                                                    if(curDomain.workProcess){
                                                        wp = curDomain.workProcess;
                                                    }
                                                    
                                                    if(curDomain.name=='New Ventures'){
                                                        stage = 1;
                                                    }else if(curDomain.name=='Exploration'){
                                                        stage = 2;
                                                    }else if (curDomain.name=='Production'){
                                                        stage = 3;
                                                    }
                                                        
                                                    if(site && stage && wp && tow){
                                                        break;
                                                    }
                                                    
                                                    nextDomain = curDomain.parent;
                                                    if(!nextDomain || nextDomain.id==curDomain.id){
                                                        break;
                                                    }
                                                    
                                                    curDomain = nextDomain;
                                                }
                                                
                                                //console.log([site, stage, wp, tow]);
                                                
                                                var stageCombo = me.getForm().
                                                    findField('stage');
                                                var siteCombo = me.getForm().
                                                    findField('site');
                                                var towCombo = me.getForm().
                                                    findField('typeOfWork');
                                                var wpCombo = me.getForm().
                                                    findField('workProcess');
                                                
                                                if(stage){
                                                    stageCombo.select(stageCombo.getStore().getById(stage));
                                                    stageCombo.fireEvent('select', stageCombo);
                                                }
                                                if(site){
                                                    siteCombo.setValue(site);
                                                    //siteCombo.select(siteCombo.getStore().getById(parseInt(site.id)));
                                                    siteCombo.fireEvent('select', siteCombo);
                                                }
                                                if(tow){
                                                    towCombo.select(towCombo.getStore().getById(parseInt(tow.id)));
                                                    towCombo.fireEvent('select', towCombo);
                                                }
                                                if(wp){
                                                    wpCombo.select(wpCombo.getStore().getById(parseInt(wp.id)));
                                                    wpCombo.fireEvent('select', wpCombo);
                                                }
                                            }
                                        },
                                        //setValue: function(valll){
                                        //    //var me = this;
                                        //    this.selectedValue = valll;
                                        //    //me.callParent(arguments);
                                        //    Ext.get Cmp('pickerInnerTree').getSelectionModel().select(valll);
                                        //},
                                        valueToRaw: function(valll){  
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
                                            }else if(valll){
                                                this.selectedDomain = valll;
                                                return this.valueToRaw();
                                            }else{
                                                return '';
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
                                        //isValid: function(){
                                        //    return (this.selectedDomain?true:false);
                                        //},
                                        getRawValue: function(){
                                            if(this.selectedDomain)
                                                return this.selectedDomain;
                                            return '';
                                        },
                                        getSubmitValue: function(){
                                            return Ext.encode(this.selectedDomain);
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
                                                    id: 'pickerInnerTree',
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
                                        },
                                        validator: function(o){
                                            if(!o){
                                                //this.value='';
                                                return 'Please select the document path (domain)';
                                            }
                                            return true;
                                        }
                                    }, 
                                    {
                                        xtype: 'combo',
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
                                                var val = this.valueModels[0].raw;
                                                this.value = val;
                                            }
                                        },
                                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                                        },
                                        validator: function(o){
                                            if(!o)
                                                return 'Please select value';
                                            return true;
                                        }
                                    }, 
                                    {
                                        xtype: 'combo',
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
                                                var val = this.valueModels[0].raw;
                                                this.value = val;
                                            }
                                        },
                                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                                        },
                                        validator: function(o){
                                            if(!o)
                                                return 'Please select value';
                                            return true;
                                        }
                                    }, 
                                    {
                                        xtype: 'combo',
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
                                                var val = this.valueModels[0].raw;
                                                //this.value = val.id;
                                                this.value = val;
                                            }
                                        },
                                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                                        }
                                    }, 
                                    {
                                        xtype: 'combo',
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
                                                var val = this.valueModels[0].raw;
                                                this.value = val;
                                            }
                                        },
                                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                                        },
                                        validator: function(o){
                                            if(!o)
                                                return 'Please select value';
                                            return true;
                                        }
                                    },
                                    {
                                        xtype: 'textfield',
                                        name: 'path',
                                        disabled: true,
                                        fieldLabel: 'File Path',
                                        allowBlank: false,
                                        anchor: '100%'
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
                                        fieldLabel: 'Classification',
                                        hideLabel: false,
                                        name: 'classification',
                                        store: 'ClassificationsJsonStore',
                                        displayField: 'name',
                                        queryMode: 'local',
                                        lastQuery: '',
                                        typeAhead: 'true',
                                        msgTarget: 'side',
                                        listeners: {
                                            select: function(){
                                                var val = this.valueModels[0].raw;
                                                //this.value = val.id;
                                                this.value = val;
                                            }
                                        },
                                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                                        }
                                    },                                    
                                    {
                                        xtype: 'textarea',
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
                                //style: 'z-index: 12',
                                collapsible: false,
                                id: 'authorsContainerDFE',
                                /*defaults: {
                                    anchor: '100%'
                                },*/
                                items: [
                                    {
                                        xtype: 'button',
                                        style: 'position: absolute; top: 0px; right: 40px; padding: 0px;',
                                        //text: '+',
                                        icon: 'lib/ext41/resources/themes/images/default/dd/drop-add.gif',
                                        handler: function(th){
                                            //console.log(th.ownerCt.ownerCt.ownerCt.items.getAt(0));
                                            th.ownerCt.add(
                                                {
                                                    xtype: 'container',
                                                    layout: 'hbox',
                                                    items: [
                                                        {
                                                            flex: 1,
                                                            xtype: 'combo',
                                                            fieldLabel: 'Author',
                                                            hideLabel: true,
                                                            name: 'author',
                                                            store: 'AuthorsJsonStore',
                                                            displayField: 'shortName',
                                                            queryMode: 'local',
                                                            lastQuery: '',
                                                            typeAhead: 'true',
                                                            msgTarget: 'side',
                                                            listeners: {
                                                                select: function(){
                                                                    var val = this.valueModels[0].raw;
                                                                    this.value = val;
                                                                }
                                                            },
                                                            getSubmitValue: function(){
                                                                if(this.valueModels&&this.valueModels[0])
                                                                    return Ext.encode(this.valueModels[0].raw);
                                                                return '';
                                                            },
                                                            validator: function(o){
                                                                if(!o)
                                                                    return 'Please select value';
                                                                var formVals = me.getForm().getValues();
                                                                var authh = formVals.author;
                                                                if(Ext.isArray(authh)){
                                                                    var metOnce;
                                                                    for(var i in authh){
                                                                        if(authh[i] === this.getSubmitValue())
                                                                                if(metOnce)
                                                                                    return 'Duplicate Authors';
                                                                                else
                                                                                    metOnce = true;
                                                                    }
                                                                    //if(petroresConfig.hasDuplicates(authh)){
                                                                    //    return 'Duplicate Authors';
                                                                    //}
                                                                }
                                                                return true;
                                                            }
                                                        }
                                                    ]

                                                }

                                            );
                                        }
                                    }, {
                                        style: 'position: absolute; top: 0px; right: 20px; padding: 0px;',
                                        xtype: 'button',
                                        //text: '-',
                                        icon: 'lib/ext41/examples/restful/images/delete.png',
                                        handler: function(th){
                                            //console.log(th.ownerCt.ownerCt.ownerCt.items.getAt(0));
                                            th.ownerCt.remove(th.ownerCt.items.getAt(3));
                                        }
                                    },                                     
                                    {
                                        xtype: 'container',
                                        layout: 'column',
                                        items:[
                                            {
                                                columnWidth: 1,
                                                border: 0,
                                                layout: 'anchor',
                                                defaults: {
                                                    anchor: '100%'
                                                },
                                                items:[
                                                    {
                                                        xtype: 'container',
                                                        layout: 'hbox',
                                                        items: [
                                                            {
                                                                flex: 1,
                                                                xtype: 'combo',
                                                                fieldLabel: 'Author',
                                                                hideLabel: true,
                                                                name: 'author',
                                                                store: 'AuthorsJsonStore',
                                                                displayField: 'shortName',
                                                                queryMode: 'local',
                                                                lastQuery: '',
                                                                typeAhead: 'true',
                                                                msgTarget: 'side',
                                                                listeners: {
                                                                    select: function(){
                                                                        var val = this.valueModels[0].raw;
                                                                        this.value = val;
                                                                    }
                                                                },
                                                                getSubmitValue: function(){
                                                                    if(this.valueModels&&this.valueModels[0])
                                                                        return Ext.encode(this.valueModels[0].raw);
                                                                    return '';
                                                                },
                                                                validator: function(o){
                                                                    if(!o)
                                                                        return 'Please select value';
                                                                    var formVals = me.getForm().getValues();
                                                                    var authh = formVals.author;
                                                                    if(Ext.isArray(authh)){
                                                                        var metOnce = false;
                                                                        for(var i in authh){
                                                                            if(authh[i] === this.getSubmitValue())
                                                                                if(metOnce)
                                                                                    return 'Duplicate Authors';
                                                                                else
                                                                                    metOnce = true;
                                                                        }
                                                                        //if(petroresConfig.hasDuplicates(authh)){
                                                                        //    return 'Duplicate Authors';
                                                                        //}
                                                                    }
                                                                    return true;
                                                                }
                                                            }
                                                        ]

                                                    }
                                                ]

                                            }
                                        ]
                                    }
                                ]
                            }, 
                            {
                                xtype: 'textfield',
                                fieldLabel: 'Added By',
                                id: 'posterLabelDFE',
                                anchor: '100%'
                            }, 
                            {
                                xtype: 'fieldset',
                                title: 'Document Type',
                                collapsible: false,
                                defaults: {
                                    anchor: '100%'
                                },
                                items: [
                                    {
                                        xtype: 'combo',
                                        fieldLabel: 'General Types',
                                        name: 'superType',
                                        id: 'superTypeComboDFE',
                                        store: 'SuperTypesJsonStore',
                                        displayField: 'name',
                                        queryMode: 'local',
                                        lastQuery: '',
                                        typeAhead: 'true',
                                        listeners: {
                                            select: function(){
                                                var val = this.valueModels[0].raw;
                                                this.value = val;
                                                var store = me.getForm().findField('type').getStore();
                                                store.filterBy(function(record){
                                                    return record.get('superType').id === val.id;
                                                });
                                            }
                                        },
                                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                                        },
                                        msgTarget: 'side',
                                        validator: function(o){
                                            if(!o)
                                                return 'Please select value';
                                            return true;
                                        }                    
                                    }, {
                                        xtype: 'combo',
                                        queryMode: 'local',
                                        typeAhead: 'true',
                                        fieldLabel: 'Type',
                                        lastQuery: '',
                                        //hideLabel: true,
                                        name: 'type',
                                        id: 'typeComboDFE',
                                        store: Ext.getStore('TypesJsonStore').copyStore(),
                                        displayField: 'name',
                                        listeners: {
                                            select: function(th, records){
                                                //console.log(records);
                                                var typ = records[0].data;
                                                
                                                if(!Ext.isArray(typ.typeExt)){
                                                    typ.typeExt = [typ.typeExt];
                                                }                                               
                                                
                                                if(typ.typeExt.length == 1 && (!typ.typeExt[0] || typ.typeExt[0]==="" )){
                                                    var fs = Ext.getCmp('fileFieldSetDFE');
                                                    fs.removeAll();
                                                    fs.add({
                                                        xtype: 'textfield',
                                                        layout: 'column',   
                                                        anchor: '100%',
                                                        fieldLabel: 'Directory',
                                                        combineErrors: false,
                                                        msgTarget: 'under',
                                                        name: 'directory'
                                                    });
                                                    typ.typeExt = [];
                                                    fs.setTitle(typ.name);
                                                    this.value = typ;
                                                    return;
                                                }                                                
                                                
                                                //this.value = typ.id; //!!!
                                                this.value = typ; //!!!

                                                //console.log(typ);

                                                if(typ.isGeo){
                                                    Ext.getCmp('geoTypeFieldSetDFE').expand();
                                                }else{
                                                    Ext.getCmp('geoTypeFieldSetDFE').collapse();
                                                }

                                                var fs = Ext.getCmp('fileFieldSetDFE');
                                                var fileField, i;
                                                fs.removeAll();
                                                if(typ.isMultiFile!=='true'){
                                                    fs.setTitle(typ.name + ' file');
                                                    fileField = Ext.clone(me.fileFieldPattern);
                                                    var label = "";
                                                    var typExts = [];
                                                    if(Ext.isArray(typ.typeExt)){
                                                        for(i in typ.typeExt){
                                                            label = label + ' or ' + typ.typeExt[i].ext.ext;
                                                            typExts.push(typ.typeExt[i].ext.ext);
                                                        }
                                                        label = label.substring(4);
                                                    }else{
                                                        label = typ.typeExt.ext.ext;
                                                        typExts.push(typ.typeExt.ext.ext);
                                                    }
                                                    fileField.typExts = typExts;
                                                    fileField.fieldLabel = label;
                                                    fileField.maxAllowedFiles = typ.typeExt.maxCount;
                                                    fileField.minAllowedFiles = typ.typeExt.minCount;
                                                    fs.add(fileField);
                                                }else{
                                                    fs.setTitle(typ.name + ' files');

                                                    var typeExt = typ.typeExt;
                                                    if(!Ext.isArray(typeExt)){
                                                        typeExt = [typeExt];
                                                    }
                                                    for(i in typeExt){
                                                        fileField = Ext.clone(me.fileFieldPattern);
                                                        fileField.typExts = [typeExt[i].ext.ext];
                                                        fileField.fieldLabel = typeExt[i].ext.ext;
                                                        fileField.maxAllowedFiles = typ.typeExt[i].maxCount;
                                                        fileField.minAllowedFiles = typ.typeExt[i].minCount;
                                                        fs.add(Ext.clone(fileField));
                                                    }
                                                }
                                            }
                                        },
                                        getSubmitValue: function(){
                                            return Ext.encode(this.value);
                                            //if(this.valueModels&&this.valueModels[0])
                                            //    return Ext.encode(this.valueModels[0].raw);
                                            //return null;
                                        },
                                        msgTarget: 'side',
                                        validator: function(o){
                                            if(!o)
                                                return 'Please select value';
                                            return true;
                                        }
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
                                                var val = this.valueModels[0].raw;
                                                //this.value = val.id;
                                                this.value = val;
                                            }
                                        },
                                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                                        }
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }, 
            {
                xtype: 'fieldset',
                id: 'geoTypeFieldSetDFE',
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
                                var val = this.valueModels[0].raw;
                                this.value = val;
                                
                                if(val.name==='raster')
                                    Ext.getCmp('resolutionFieldDFE').enable();
                                else
                                    Ext.getCmp('resolutionFieldDFE').disable();
                            }
                        },
                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                        }
                    }, {
                        xtype: 'numberfield',
                        //id: 'scaleField',
                        fieldLabel: 'Scale',
                        maxLength: 50,
                        name: 'scale',
                        msgTarget: 'side',
                        allowBlank: true
                    },
                    {
                        xtype: 'numberfield',
                        id: 'resolutionFieldDFE',
                        fieldLabel: 'Resolution',
                        maxLength: 50,
                        name: 'resolution',
                        msgTarget: 'side',
                        allowBlank: true
                    },
                    {
                        xtype: 'combo',
                        fieldLabel: 'Projection',
                        hideLabel: false,
                        name: 'projection',
                        store: 'ProjectionsJsonStore',
                        displayField: 'code',
                        queryMode: 'local',
                        lastQuery: '',
                        typeAhead: 'true',
                        msgTarget: 'side',
                        listeners: {
                            select: function(){
                                var val = this.valueModels[0].raw;
                                this.value = val;
                            }
                        },
                        getSubmitValue: function(){
                                            if(this.valueModels&&this.valueModels[0])
                                                return Ext.encode(this.valueModels[0].raw);
                                            return null;
                        }
                    }
                ]
            },            
            {
                xtype: 'fieldset',
                id: 'loadedFileFieldSetDFE',
                title: 'Loaded File(s)',
                anchor: '100%',
                collapsible: false,
                items: []
            }, 
            {
                xtype: 'fieldset',
                id: 'fileFieldSetDFE',
                title: 'File(s)',
                anchor: '100%',
                collapsible: false,
                items: []
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
                            return Ext.encode(obj);
                        },
                        listeners: {
                            beforerender: function(thefield){
                                if(!thefield.oldSetValue){
                                    var oldSetValue = thefield.setValue;
                                    thefield.setValue = function(){
                                        me.featuresToSelect = arguments[0];
                                        return thefield.oldSetValue.apply(thefield, arguments);
                                    }
                                    
                                    thefield.oldSetValue = oldSetValue;
                                }
                            }
                        }
                    }, {
                        xtype: 'panel',
                        height: 250,
                        layout: 'border',
                        items: [
                            mapPanel,
                            layerTree
                        ]//,
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
                                                if(!met || !data[met]){
                                                    str = str + ', ' + ths.selectedFeatures[feat].fid.replace(/\s+$/,'');
                                                }else{
                                                    str = str + ', ' + data[met].replace(/\s+$/,'');
                                                }
                                            }
                                        }

                                        str = str.substring(2);
                                        me.getForm().findField('geoObjects').oldSetValue(str);

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
                                            , loadend: function(){
                                                me.loadSelectedFeatures(this);
                                            }
                                        });
                                    };
                                }

                                leftLoad = vectorLayers.length;

                                th.map = new OpenLayers.Map(th.body.dom.id, {
                                    controls: [
                                        new OpenLayers.Control.Navigation(),
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
                                            box: false
                                        });

                                th.map.addControl(control);
                                control.activate();
                                th.map.zoomToExtent(new OpenLayers.Bounds(45.00, 36.18, 55, 47.50));
                                
                                me.featureSelector = control;
                                
                                //th.map.zoomToMaxExtent();
                            }
                        }*/
                    }
                ]
            }
            ],
            buttons: [{
                text: 'Save',
                handler: function() {
                    var form = this.up('form').getForm();
                    if(form.isValid()){
                        form.submit({
                            url: 'form/editDocument',
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
                        });
                    }
                }
            }, {
                text: 'Delete',
                handler: function() {
                    
                    var form = this.up('form').getForm();
                    //var docId = form.getRecord().get('id');
                    
                    //console.log(docId);
                    
                    Ext.Ajax.request({
                        url: 'rest/documents',
                        method: 'DELETE',
                        jsonData: form.getValues(),
                        success: function() {
                            Ext.Msg.alert(
                                'Success', 
                                'Your document has been deleted.',
                                function(){
                                    me.theMap.destroy();
                                    me.up().close();
                                }
                            );
                        },
                        failure: function(fp, o){
                            Ext.Msg.alert('Failure', o.result.msg);
                        }
                    });                    
                }
            }]
        });

        me.callParent(arguments);
    }
});