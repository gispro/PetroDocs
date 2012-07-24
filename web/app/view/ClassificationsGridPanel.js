Ext.define('PetroRes.view.ClassificationsGridPanel', {
    extend: 'Ext.grid.Panel',
    
    id: 'ClassificationsGridPanel',
    autoScroll: true,
    store: 'ClassificationsJsonStore',

    initComponent: function() {
        var me = this;
        
        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    editor: 'textfield',
                    flex: 20,
                    text: 'Name'
                },
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'descr',
                    editor: 'textfield',
                    flex: 40,
                    text: 'Description'
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    itemId: 'pagingToolBar',
                    displayInfo: true,
                    prependButtons: true,
                    store: 'ClassificationsJsonStore',
                    dock: 'bottom',
                    items: [
                        {
                            xtype: 'button',
                            itemId: 'addButton',
                            text: 'Add'
                        }
                    ]
                }
            ],
            plugins: [
                Ext.create('Ext.grid.plugin.RowEditing', {

                })
            ]
        });
        
        me.callParent(arguments);
        
        var proxy = me.store.getProxy();
        
        Ext.apply(proxy.actionMethods, {
            update: 'PUT',
            create: 'POST',
            destroy: 'DELETE'
        });
        
        me.addButton = me.dockedItems.getByKey('pagingToolBar').items.getByKey('addButton')
        
        me.headerCt.insert(0, {
            xtype:'actioncolumn',
            flex:1,
            items: [{
                icon: 'lib/ext41/examples/restful/images/delete.png',
                tooltip: 'Delete',
                handler: function(grid, rowIndex, colIndex) {
                    grid.store.removeAt(rowIndex);
                }
            }]
        });

        for (var i in me.plugins){
            if(me.plugins[i]['$className']==='Ext.grid.plugin.RowEditing'){
                me.editorPlugin = me.plugins[i];
                me.editorPlugin.on('canceledit', function(){
                    if(me.editorPlugin.startedPetroObjectAdd == true){
                        me.store.autoSync = false;
                        me.editorPlugin.startedPetroObjectAdd = false;
                        me.store.removeAt(0);
                        me.store.autoSync = true;
                    }
                });
                me.addButton.on('click', function(){
                    me.editorPlugin.cancelEdit();
                    me.store.autoSync = false;

                    me.store.insert(0, {});
                    me.editorPlugin.startEdit(0, 0);            
                    me.editorPlugin.startedPetroObjectAdd = true;            
                    me.store.autoSync = true;
                });
                
                break;
            }
        }
    }
});
