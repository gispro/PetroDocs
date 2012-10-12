Ext.define('PetroRes.view.DocumentsGridPanel', {
    extend: 'Ext.grid.Panel',
    
    id: 'DocumentsGridPanel',
    autoScroll: true,
    //store: 'AuthorsJsonStore',

    initComponent: function() {
        var me = this;
        
        Ext.applyIf(me, {
            viewConfig: {
                emptyText: 'No records',
                deferEmptyText: false
            },            
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'title',
                    flex: 10,
                    text: 'Title Rus'
                }, {
                    xtype: 'gridcolumn',
                    dataIndex: 'fullTitle',
                    flex: 5,
                    text: 'Title Eng'
                }, {
                    xtype: 'gridcolumn',
                    dataIndex: 'number',
                    flex: 7,
                    text: 'Number'
                }, {
                    xtype: 'templatecolumn',
                    dataIndex: 'files',
                    flex: 7,
                    text: 'Download',
                    tpl: Ext.create('Ext.XTemplate', 
                        '<tpl if="files">',
                        '<tpl if="Ext.isArray(files)">',
                        '<tpl for="files">',
                        '{[xindex > 1 ? "<br/>" : ""]}',
                        '<a target="_blank" href="form/file/{id}/{[this.getFileName(values.path)]}">{[this.getFileName(values.path)]}</a>',
                        '</tpl>',
                        '</tpl><tpl if="!Ext.isArray(files)">',
                        '<a target="_blank" href="form/file/{files.id}/{[this.getFileName(values.files.path)]}">{[this.getFileName(values.files.path)]}</a>',
                        '</tpl>',
                        '</tpl>',
                        {
                            getFileName: function(path){
                                return path.substr(path.lastIndexOf(petroresConfig.pathFolderSeparator) + 1);
                            }
                        }
                        )
                }
            ]
                
        });
        
        me.callParent(arguments);
        
    }
});
