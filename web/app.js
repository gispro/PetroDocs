Ext.Loader.setConfig({
    enabled: true,
    paths: {
        Ext: './lib/ext41',
        GeoExt: './lib/geoext2/src/GeoExt',
        PetroRes: './app'
    } 
});

Ext.require([
    //'GeoExt.*'
    
    
    'GeoExt.Action',
    //'Ext.container.Viewport',
    //'Ext.layout.container.Border',
    'GeoExt.tree.Panel',
    //'Ext.tree.plugin.TreeViewDragDrop',
    'GeoExt.panel.Map',
    'GeoExt.tree.OverlayLayerContainer',
    'GeoExt.tree.BaseLayerContainer',
    'GeoExt.data.LayerTreeModel',
    'GeoExt.tree.View',
    'GeoExt.tree.Column',
    'GeoExt.panel.Legend',
    'GeoExt.container.VectorLegend'
    //,'GeoExt.data.MapfishPrintProvider'
    //,'GeoExt.data.PrintPage'
]);


////// extjs 4.0 bug override
////// http://stackoverflow.com/questions/8653445/extjs-treestore-update-event-fire-instead-of-create/9228517#9228517


/*Ext.data.NodeInterface.oldGpv = Ext.data.NodeInterface.getPrototypeBody;
Ext.data.NodeInterface.getPrototypeBody = function(){
        var ret = Ext.data.NodeInterface.oldGpv.apply(this, arguments);

        ret.appendChild = function(node, suppressEvents, suppressNodeUpdate) {
            var me = this,
            i, ln,
            index,
            oldParent,
            ps;


            if (Ext.isArray(node)) {
                for (i = 0, ln = node.length; i < ln; i++) {
                    me.appendChild(node[i]);
                }
            } else {

                //node.store.load({node: node});

                node = me.createNode(node);
                

                if (suppressEvents !== true && me.fireEvent("beforeappend", me, node) === false) {
                    return false;
                }

                index = me.childNodes.length;
                oldParent = node.parentNode;

 
                if (oldParent) {
                    if (suppressEvents !== true && node.fireEvent("beforemove", node, oldParent, me, index) === false) {
                        return false;
                    }
                    oldParent.removeChild(node, null, false, true);
                }else{
                    node.phantom = true;
                }

                if(me.isLoaded()){
                    index = me.childNodes.length;
                    if (index === 0) {
                        me.setFirstChild(node);
                    }

                    me.childNodes.push(node);
                    node.parentNode = me;
                    node.nextSibling = null;

                    me.setLastChild(node);

                    ps = me.childNodes[index - 1];
                    if (ps) {
                        node.previousSibling = ps;
                        ps.nextSibling = node;
                        ps.updateInfo(suppressNodeUpdate);
                    } else {
                        node.previousSibling = null;
                    }
                    node.updateInfo(suppressNodeUpdate);
                }
                if (suppressEvents !== true) {
                    me.fireEvent("append", me, node, index);

                    if (oldParent) {
                        node.fireEvent("move", node, oldParent, me, index);
                    }
                }

                return node;
            }
        };
        return ret;
    }
;


Ext.override(Ext.view.AbstractView, {
    updateIndexes : function(startIndex, endIndex) {
        var ns = this.all.elements,
            records = this.store.getRange(),
            i;
            
        startIndex = startIndex || 0;
        endIndex = endIndex || ((endIndex === 0) ? 0 : (ns.length < records.length?(ns.length - 1):records.length-1) );
        for(i = startIndex; i <= endIndex; i++){
            ns[i].viewIndex = i;
            ns[i].viewRecordId = records[i].internalId;
            if (!ns[i].boundView) {
                ns[i].boundView = this.id;
            }
        }
    }    
});*/

// end bug override


// i dont know why ext4.1 doesnt find values anymore (ext4.0 did)
//Ext.form.field.ComboBox.findRecordByValue = function(value) {
//        if(value.id && Ext.isNumeric(value.id))
//            return this.findRecord('id', parseInt(value.id));
//        else
//            return this.findRecord(this.valueField, value);
//    }
//;
Ext.override(Ext.form.field.ComboBox, {findRecordByValue: function(value) {
        if(value.id && Ext.isNumeric(value.id))
            return this.findRecord('id', parseInt(value.id));
        else
            return this.findRecord(this.valueField, value);
    }
});


// from extjs 4.1
/*Ext.override(Ext.AbstractComponent, { addPropertyToState: function (state, propName, value) {
    var me = this,
        len = arguments.length;

    // If the property is inherited, it is a default and we don't want to save it to
    // the state, however if we explicitly specify a value, always save it
    if (len == 3 || me.hasOwnProperty(propName)) {
        if (len < 3) {
            value = me[propName];
        }

        // If the property has the same value as was initially configured, again, we
        // don't want to save it.
        if (value !== me.initialConfig[propName]) {
            (state || (state = {}))[propName] = value;
        }
    }

    return state;
}});*/

/*
 *
 *dont remember
 *
 *Ext.override(Ext.data.TreeStore, {
   onUpdateRecords: function(records, operation, success){
        if (success) {
            var me = this,
                i = 0,
                length = records.length,
                data = me.data,
                original,
                parentNode,
                record;

            for (; i < length; ++i) {
                record = records[i];
                original = me.tree.getNodeById(record.getId());
                if(original){parentNode = original.parentNode;
                    if (parentNode) {
                        original.isReplace = true;
                        parentNode.replaceChild(record, original);
                        original.isReplace = false;
                    }
                }
            }
        }
    } 
});
 *
 *
 *
 *
 */


// a bug in tree grid is the same as 
//http://www.sencha.com/forum/showthread.php?202939-4.1.0-Ext.view.Table-onUpdate-of-initially-hidden-GridPanel-causes-JavaScript-error

Ext.override(Ext.view.Table, {
    onUpdate : function(store, record, operation, changedFieldNames) {
        var me = this,
            index,
            newRow, oldRow,
            oldCells, newCells, len, i,
            columns, overItemCls,
            isHovered, row;
            
        if (me.rendered && me.all.elements.length > 0) {
            
            index = me.store.indexOf(record);
            columns = me.headerCt.getGridColumns();
            overItemCls = me.overItemCls;

            
            
            if (columns.length && index > -1) {
                newRow = me.bufferRender([record], index)[0];
                oldRow = me.all.item(index);
                isHovered = oldRow.hasCls(overItemCls);
                oldRow.dom.className = newRow.className;
                if(isHovered) {
                    oldRow.addCls(overItemCls);
                }

                
                oldCells = oldRow.query(this.cellSelector);
                newCells = Ext.fly(newRow).query(this.cellSelector);
                len = newCells.length;
                
                row = oldCells[0].parentNode;
                for (i = 0; i < len; i++) {
                    
                    if (me.shouldUpdateCell(columns[i], changedFieldNames)) {
                        row.insertBefore(newCells[i], oldCells[i]);
                        row.removeChild(oldCells[i]);
                    }
                }

                
                
                me.selModel.refresh();
                me.doStripeRows(index, index);
                me.fireEvent('itemupdate', record, index, newRow);
            }
        }

    }    
});




Ext.application({
    name: 'PetroRes',
    appFolder: './app',
    stores: [
    'GeoObjJsonStore',
    'WordsJsonStore',
    'GeoTypesJsonStore',
    'SitesJsonStore',
    'FilesJsonStore',
    'AuthorsJsonStore',
    'StagesJsonStore',
    //'DomainsJsonStore',
    'WellsJsonStore',
    'SuperTypesJsonStore',
    'ExtsJsonStore',
    'TypesJsonStore',
    'ClassificationsJsonStore',
    'PeriodicitiesJsonStore',
    'DomainsJsonTreeStore',
    'WorkProcessesJsonStore',
    'TypesOfWorkJsonStore',
    'ProjectionsJsonStore',
    'OrganizationsJsonStore',
    'DomainDocumentsJsonStore',
    'DocumentsJsonStore'
    ],
                
    launch: function() {
        Ext.QuickTips.init();
        
        var titleStart = 'Petroresource Documents System';
                    
        var wnd = Ext.create('PetroRes.view.MainWindow', {
            title: titleStart + (petroresConfig.loggedInAs?' :: '+petroresConfig.loggedInAs:''),
            renderTo: Ext.getBody()
        });
        wnd.show();
        
        var userStore = Ext.getStore('AuthorsJsonStore');
        userStore.load({
            url: 'rest/authors/'+petroresConfig.loggedInAs, 
            callback: function(records){
                if(records.length < 1){
                    records = userStore.add({login: petroresConfig.loggedInAs});
                }
                var curUserRec = records[0];
                //console.log(curUser);
                
                if(!curUserRec.data.name || curUserRec.data.name.length==0){
                    // window to introduse oneself
                    
                    var userForm = Ext.create('PetroRes.view.AuthorForm');
                    
                    wnd.openPetroWindow('editUser', {
                        closable: true,
                        title: 'Edit User ' + petroresConfig.loggedInAs,
                        maximizable: true,
                        maximized: false,
                        height:wnd.getHeight()*0.5,
                        width:wnd.getWidth()*0.8,
                        layout: 'fit',
                        items: [
                            userForm
                        ]
                    });
                    
                    userForm.getForm().loadRecord(curUserRec);
                    
                }else{
                    wnd.setTitle(titleStart + ' :: ' + curUserRec.data.name);
                }
            }
        });
    }
                
});
