Ext.define('PetroRes.view.DomainDocumentsAddPanel', {
    extend: 'Ext.panel.Panel',

    bodyPadding: 0,
    setTemplateDomain: function( record ){
      this.temlateDomain = record.raw;
      this.lblDomain.setText(' '+record.raw.name+'  ');
      this.tfAdd.setValue('');
      this.doLayout();
    },
    setTemplateDocument: function( record ){
      this.temlateObj = record.raw;
      this.tfAdd.setValue(this.temlateObj.fullTitle);
    },
    initComponent: function() {
        var me = this;

        me.lblDomain = Ext.create('Ext.form.Label',{
                region:'west',
                //resizable:true,
                text:'', height:24,
                cls:'ab_TextFieldLabel'
            });
        me.tfAdd = Ext.create('Ext.form.TextField',{
                //fieldLabel:' ',
                //labelAlign:'right',
                region:'center'
            });
        this.btnAddOpen = Ext.create('Ext.Button',
            {
                xtype:'button', iconCls:'ab_add', cls:'album-btn', width:23, height:24, region:'east',
                handler:function(){
                   var wnd = Ext.getCmp('MainWindow');
                        wnd.openPetroWindow('newDoc', {
                            closable: true,
                            title: 'New Document',
                            maximizable: true,
                            maximized: true,
                            height:wnd.getHeight()*0.8,
                            width:wnd.getWidth()*0.8,
                            layout: 'fit',
                            items: [
                            Ext.create('PetroRes.view.DocumentForm', {domain:me.temlateDomain})
                            ]
                        }); 
                }
            });
        
        Ext.applyIf(me, {
            listeners:{
                afterRender: function(thisForm, options){
                    me.keyNav= Ext.create('Ext.util.KeyNav', me.el, {                    
                        enter: function()
                        {
                            me.btnAddOpen.handler.call();
                        },
                        scope: this
                    });
                }
            },
            layout:'border',
            items: [ me.lblDomain, me.tfAdd, me.btnAddOpen]
        });

        me.callParent(arguments);
    }
});


