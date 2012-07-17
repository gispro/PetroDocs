/*
 * File: app/view/ui/StagesGridPanel.js
 * Date: Thu Feb 09 2012 16:52:45 GMT+0400 (Ìîñêîâñêîå âðåìÿ (çèìà))
 *
 * This file was generated by Ext Designer version 1.2.2.
 * http://www.sencha.com/products/designer/
 *
 * This file will be auto-generated each and everytime you export.
 *
 * Do NOT hand edit this file.
 */

Ext.define('PetroRes.view.ui.StagesGridPanel', {
    extend: 'Ext.grid.Panel',

    id: 'StagesGridPanel',
    autoScroll: true,
    store: 'StagesJsonStore',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            viewConfig: {

            },
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
                    store: 'StagesJsonStore',
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
    }
});