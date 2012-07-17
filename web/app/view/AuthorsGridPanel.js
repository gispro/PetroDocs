Ext.define('PetroRes.view.AuthorsGridPanel', {
    extend: 'Ext.grid.Panel',
    
    id: 'AuthorsGridPanel',
    autoScroll: true,
    store: 'AuthorsJsonStore',

    initComponent: function() {
        var me = this;
        
        Ext.applyIf(me, {
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    editor: 'textfield',
                    flex: 10,
                    text: 'Name'
                }, {
                    xtype: 'gridcolumn',
                    dataIndex: 'shortName',
                    editor: 'textfield',
                    flex: 5,
                    text: 'Short Name'
                }, {
                    xtype: 'gridcolumn',
                    dataIndex: 'email',
                    editor: 'textfield',
                    flex: 7,
                    text: 'E-mails'
                }, {
                    xtype: 'gridcolumn',
                    dataIndex: 'phone',
                    editor: 'textfield',
                    flex: 7,
                    text: 'Phones'
                }, {
                    xtype: 'booleancolumn',
                    dataIndex: 'isInternal',
                    editor: 'checkbox',
                    flex: 2,
                    text: 'Int'
                }, {
                    xtype: 'gridcolumn',
                    dataIndex: 'login',
                    editor: 'textfield',
                    flex: 6,
                    text: 'Login'
                },
                {
                    xtype: 'templatecolumn',
                    tpl: Ext.create('Ext.XTemplate', 
                        '{organization.name}'
                    ),
                    dataIndex: 'organization',
                    getSortParam: function(){
                        return 'organization.name';
                    },
                    editor: {
                        xtype: 'combo',
                        store: 'OrganizationsJsonStore',
                        displayField: 'name',
                        valueField: 'id',
                        listeners: {
                            select: function(){
                                this.value=this.valueModels[
                                    0
                                ].raw;
                            }
                        }
                    },
                    flex: 10,
                    text: 'Organization'
                }
            ],
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    itemId: 'pagingToolBar',
                    displayInfo: true,
                    prependButtons: true,
                    store: 'AuthorsJsonStore',
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
                icon: 'lib/ext4/examples/restful/images/delete.png',
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
