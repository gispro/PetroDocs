/*
 * File: app/store/ExtsJsonStore.js
 * Date: Thu Feb 09 2012 16:52:45 GMT+0400 (Ìîñêîâñêîå âðåìÿ (çèìà))
 *
 * This file was generated by Ext Designer version 1.2.2.
 * http://www.sencha.com/products/designer/
 *
 * This file will be auto-generated each and everytime you export.
 *
 * Do NOT hand edit this file.
 */

Ext.define('PetroRes.store.ExtsJsonStore', {
    extend: 'Ext.data.Store',

    constructor: function(cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            autoLoad: true,
            autoSync: true,
            storeId: 'ExtsJsonStore',
            //buffered: true,
            pageSize: 65535,
            remoteSort: true,
            proxy: {
                type: 'ajax',
                url: 'rest/exts',
                headers: {
                    Accept: 'application/json'
                },
                reader: {
                    type: 'json',
                    idProperty: 'ext',
                    root: 'exts'
                }
            },
            fields: [
                {
                    name: 'ext'
                },
                {
                    name: 'mimeTypeDef'
                }
            ]
        }, cfg)]);
    }
});