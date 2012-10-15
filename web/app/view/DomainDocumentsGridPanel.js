/* 
 * Панель, осуществляющая управление To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

Ext.define('PetroRes.view.DomainDocumentsGridPanel', {
    extend: 'Ext.grid.Panel',
    
    id: 'DomainDocumentsGridPanel',
    autoScroll: true,
    //store: 'AuthorsJsonStore',
    initComponent: function() {
        var me = this;
        Ext.create(  'PetroRes.store.DomainDocumentsJsonStore', 
                     {storeId:'DomainDocumentsJsonStore'});
        
        Ext.applyIf(me, {
            title:'Documents',
            store: 'DomainDocumentsJsonStore',
            tools:[/*{ type:'plus',
                    tooltip: 'Add Document',
                    handler: function(event, toolEl, panel){}                                         
            },{ type:'close',
                    tooltip: 'Delete Doqument',
                    handler: function(event, toolEl, panel){}                                         
            },{ type: 'search',
                    tooltip: 'Search',
                    handler: function(event, target, owner, tool){
                    // do search
                }
            },{ type:'help',
                    tooltip: 'Help',
                    // hidden:true,
                    handler: function(event, toolEl, panel){
                    // refresh logic
                    }                                         
            }*/],
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'title',
                    flex: 7,
                    text: 'Title Rus'
                }, {
                    xtype: 'gridcolumn',
                    dataIndex: 'fullTitle',
                    flex: 7,
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
                    sortable:false,
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
                ,{xtype: 'gridcolumn',   dataIndex: 'id',  flex: 5,  text: 'Id', hidden: true}
                ,{xtype: 'gridcolumn',   dataIndex: 'year',  flex: 5,  text: 'Year', hidden: true}
                ,{xtype: 'gridcolumn',   dataIndex: 'type',  flex: 5,  text: 'Type', hidden: true, sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'comment',  flex: 5,  text: 'Comment', hidden: true}
                ,{xtype: 'gridcolumn',   dataIndex: 'originationDetails', hidden: true,  flex: 5,  text: 'Origination Details'}
                ,{xtype: 'gridcolumn',   dataIndex: 'limitationDetails', hidden: true,  flex: 5,  text: 'Limitation Details'}
                ,{xtype: 'gridcolumn',   dataIndex: 'originationDate', hidden: true,  flex: 5,  text: 'Origination Date'}
                ,{xtype: 'gridcolumn',   dataIndex: 'approvalDate', hidden: true,  flex: 5,  text: 'Approval Date'}
                ,{xtype: 'gridcolumn',   dataIndex: 'registrationDate', hidden: true,  flex: 5,  text: 'Registration Date'}
                ,{xtype: 'gridcolumn',   dataIndex: 'placementDate', hidden: true,  flex: 5,  text: 'Placement Date'}
                ,{xtype: 'gridcolumn',   dataIndex: 'placer', hidden: true,  flex: 5,  text: 'Placer', sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'updateDate', hidden: true,  flex: 5,  text: 'Update Date'}
                ,{xtype: 'gridcolumn',   dataIndex: 'pageCount', hidden: true,  flex: 5,  text: 'Page Count'}
                ,{xtype: 'gridcolumn',   dataIndex: 'geometryType', hidden: true,  flex: 5,  text: 'Geometry Type', sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'scale', hidden: true,  flex: 5,  text: 'Scale'}
                ,{xtype: 'gridcolumn',   dataIndex: 'resolution', hidden: true,  flex: 5,  text: 'Resolution'}
                ,{xtype: 'gridcolumn',   dataIndex: 'projection', hidden: true,  flex: 5,  text: 'Projection', sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'periodicity', hidden: true,  flex: 5,  text: 'Periodicity', sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'classification', hidden: true,  flex: 5,  text: 'Classification', sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'site',  flex: 5, hidden: true,  text: 'Site', sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'stage', hidden: true,  flex: 5,  text: 'Stage', sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'words',  flex: 5,  text: 'Words', renderer: function(value, meta){
                                                                                                    var str = '';
                                                                                                    if(value && value instanceof Array && value.length> 0){
                                                                                                      for( var i = 0; i < value.length; i++)
                                                                                                          str +=(i>0? ', ': '')+value[i].word;
                                                                                                    }
                                                                                                    return str;
                                                                                                }, hidden: true, sortable:false}
                ,{xtype: 'gridcolumn',   dataIndex: 'authors',  flex: 5,  text: 'Authors', renderer: function(value, meta){
                                                                                                    var str = '';
                                                                                                    if(value && value instanceof Array && value.length> 0){
                                                                                                      for( var i = 0; i < value.length; i++)
                                                                                                          str +=(i>0? ', ': '')+value[i].shortName;
                                                                                                    }
                                                                                                    return str;
                                                                                                }, hidden: true, sortable:false}
                ,{xtype: 'gridcolumn',   dataIndex: 'geoObjects',  flex: 5,  text: 'Geo Objects',renderer: function(value, meta){
                                                                                                    var str = '';
                                                                                                    if(value && value instanceof Array && value.length> 0){
                                                                                                      for( var i = 0; i < value.length; i++)
                                                                                                          str +=(i>0? ', ': '')+value[i].id;
                                                                                                    }
                                                                                                    return str;
                                                                                                }, hidden: true, sortable:false}
                ,{xtype: 'gridcolumn',   dataIndex: 'domain',  flex: 5,  text: 'Domain', hidden: true, sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'typeOfWork',  flex: 5,  text: 'Type Of Work', hidden: true, sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
                ,{xtype: 'gridcolumn',   dataIndex: 'workProcess',  flex: 5,  text: 'Work Process', hidden: true, sortable:false, renderer: function(value, meta){ return value ? value.name : value}}
            ],
            listeners:{
                itemdblclick: function(ths, rec){
                    ths.editForm(rec);
                }
            }
        });
        
        me.callParent(arguments);
        
    }, 
    editForm: function(rec){
        var editForm = Ext.create(
                'PetroRes.view.DocumentFormEdit'
            ),
            wnd = Ext.getCmp('MainWindow');

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
});

