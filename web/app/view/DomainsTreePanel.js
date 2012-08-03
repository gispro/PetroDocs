/*
 * File: app/view/DomainsTreePanel.js
 * Date: Tue Feb 07 2012 11:48:45 GMT+0400 (Ìîñêîâñêîå âðåìÿ (çèìà))
 *
 * This file was generated by Ext Designer version 1.2.2.
 * http://www.sencha.com/products/designer/
 *
 * This file will be generated the first time you export.
 *
 * You should implement event handling and custom methods in this
 * class.
 */

Ext.define('PetroRes.view.DomainsTreePanel', {
    extend: 'PetroRes.view.ui.DomainsTreePanel',
    
    /*listeners: {
        itemclick: function(th, record, item, index, e, eOpts ){
            console.log([th, record, item, index, e, eOpts]);
        }
    },*/
    
    initComponent: function() {
        var me = this;
        
        me.callParent(arguments);

        var proxy = me.store.getProxy();
        
        Ext.apply(proxy.actionMethods, {
            update: 'PUT',
            create: 'POST',
            destroy: 'DELETE'
        });
        
//        me.store.setRootNode({
//            expanded: true,
//            id: 9,
//            level: 0,
//            parentId: null
//        });
        if( me.editable)
            me.headerCt.insert(0, {
                xtype:'actioncolumn',
                flex:1,
                items: [{
                    icon: 'lib/ext41/examples/restful/images/delete.png',
                    //iconCls: 'remove',
                    tooltip: 'Delete',
                    handler: function(grid, rowIndex, colIndex) {
                        var node = grid.store.getAt(rowIndex);
                        Ext.MessageBox.confirm('Confirm', 
                        'Are you sure you want to delete ' + node.data.name + '?', 
                        function(yesNo){
                            //console.log(arguments);
                            if(yesNo==='yes')
                                node.destroy(false);
                        });
                    }
                },{
                    icon: 'lib/ext41/resources/themes/images/default/dd/drop-add.gif',
                    //iconCls: 'remove',
                    tooltip: 'Add',
                    handler: function(grid, rowIndex, colIndex) {
                        var domainForm = Ext.create('PetroRes.view.DomainForm');

                        var wnd = Ext.create('Ext.Window', {
                            closable: true,
                            //height:206,
                            width:400,
                            modal:true,
                            title: 'Edit Domain',
                            maximizable: false,
                            maximized: false,
                            layout: 'fit',
                            items: [
                                domainForm
                            ],
                            fbar: [
                            {
                                xtype: 'button',
                                itemId: 'selectButton',
                                text: 'Save',
                                listeners: {
                                    click: function(){

                                        wnd.close();

                                        var node = grid.store.getAt(rowIndex);
                                        node.expand(false, function(){
                                            var newDomain = domainForm.getValues();
                                            newDomain.parent = {id: node.data.id};
                                            if(newDomain.site === "" || newDomain.site === null){
                                                delete newDomain.site;
                                            }
                                            if(newDomain.well === "" || newDomain.well === null){
                                                delete newDomain.well;
                                            }
                                            if(newDomain.typeOfWork === "" || newDomain.typeOfWork === null){
                                                delete newDomain.typeOfWork;
                                            }
                                            if(newDomain.workProcess === "" || newDomain.workProcess === null){
                                                delete newDomain.workProcess;
                                            }
                                            //newDomain.parentNode = node;
                                            //console.log(newDomain);
                                            //grid.store.add(newDomain);
                                            var newNode = node.appendChild(newDomain);
                                            //console.log([node, newNode]);
                                            /////// ext4.0 fix : me.store.sync();
                                            //node.expand();
                                        });
                                    }                                    
                                }
                            },
                            {
                                xtype: 'button',
                                itemId: 'cancelButton',
                                text: 'Cancel',
                                listeners: {
                                    click: function(){
                                        wnd.close();
                                    }                                    
                                }
                            }
                            ]
                        }).show();
                    }
                }, {
                    icon: 'lib/ext41/examples/shared/icons/fam/cog_edit.png',
                    //iconCls: 'remove',
                    tooltip: 'Edit',
                    handler: function(grid, rowIndex, colIndex) {
                        
                        var node = grid.store.getAt(rowIndex);
                        
                        //console.log(node);
                        
                        
                        var domainForm = Ext.create('PetroRes.view.DomainForm');
                        domainForm.getForm().setValues({
                            site: node.data.site,
                            well: node.data.well,
                            typeOfWork: node.data.typeOfWork,
                            workProcess: node.data.workProcess,
                            name: node.data.name,
                            fullName: node.data.fullName,
                            pathPart: node.data.pathPart
                        })

                        var wnd = Ext.create('Ext.Window', {
                            closable: true,
                            //height:206,
                            width:400,
                            modal:true,
                            title: 'Edit Domain',
                            maximizable: false,
                            maximized: false,
                            layout: 'fit',
                            items: [
                                domainForm
                            ],
                            fbar: [
                            {
                                xtype: 'button',
                                itemId: 'selectButton',
                                text: 'Save',
                                listeners: {
                                    click: function(){

                                        wnd.close();

                                        var newDomain = domainForm.getValues();
                                        if(newDomain.site && newDomain.site!=="")
                                            node.set('site', newDomain.site);
                                        if(newDomain.well && newDomain.well!=="")
                                            node.set('well', newDomain.well);
                                        if(newDomain.typeOfWork && newDomain.typeOfWork!=="")
                                            node.set('typeOfWork', newDomain.typeOfWork);
                                        if(newDomain.workProcess && newDomain.workProcess!=="")
                                            node.set('workProcess', newDomain.workProcess);

                                        node.set('name', newDomain.name);
                                        node.set('fullName', newDomain.fullName);
                                        node.set('pathPart', newDomain.pathPart);
                                        //grid.store.sync();
                                    }                                    
                                }
                            },
                            {
                                xtype: 'button',
                                itemId: 'cancelButton',
                                text: 'Cancel',
                                listeners: {
                                    click: function(){
                                        wnd.close();
                                    }                                    
                                }
                            }
                            ]
                        }).show();
                    }
                }]
            });
    }
});
