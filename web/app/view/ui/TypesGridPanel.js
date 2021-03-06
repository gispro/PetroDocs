/*
 * File: app/view/ui/TypesGridPanel.js
 * Date: Thu Feb 09 2012 16:52:45 GMT+0400 (Ìîñêîâñêîå âðåìÿ (çèìà))
 *
 * This file was generated by Ext Designer version 1.2.2.
 * http://www.sencha.com/products/designer/
 *
 * This file will be auto-generated each and everytime you export.
 *
 * Do NOT hand edit this file.
 */

Ext.define('PetroRes.view.ui.TypesGridPanel', {
    extend: 'Ext.grid.Panel',

    id: 'TypesGridPanel',
    autoScroll: true,
    store: 'TypesJsonStore',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            viewConfig: {

            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    itemId: 'pagingToolBar',
                    displayInfo: true,
                    prependButtons: true,
                    store: 'TypesJsonStore',
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
            ],
            columns: [
                {
                    xtype: 'gridcolumn',
                    dataIndex: 'name',
                    editor: 'textfield',
                    flex: 20,
                    text: 'Name'
                },
                {
                    xtype: 'booleancolumn',
                    dataIndex: 'isMultiFile',
                    editor: 'checkbox',
                    flex: 5,
                    text: 'Multi'
                },
                {
                    xtype: 'booleancolumn',
                    dataIndex: 'isGeo',
                    editor: 'checkbox',
                    flex: 5,
                    text: 'Is Geo'
                },
                {
                    xtype: 'templatecolumn',
                    tpl: Ext.create('Ext.XTemplate', 
                        '{superType.name}'
                    ),
                    dataIndex: 'superType',
                    editor: {
                        xtype: 'combo',
                        store: 'SuperTypesJsonStore',
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
                    text: 'Supertype'
                }
            ]
        });

        me.callParent(arguments);
    }
});