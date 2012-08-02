Ext.define('PetroRes.view.DomainDocumentsAddPanel', {
    extend: 'Ext.panel.Panel',

    bodyPadding: 0,
    setTemplateDomain: function( record ){
      this.temlateDomain = record.raw;
      this.lblDomain.setText(' '+record.raw.name+'  ');
      this.tfAdd.setValue('');
      this.doLayout();
    },
    getTemplateDomain: function(){ 
        return this.temlateDomain; 
    },
    setTemplateDocument: function( record ){
      this.temlateObj = record.raw;
      this.tfAdd.setValue(this.temlateObj.fullTitle);
    },
    getTemplateDocument: function(){
      return this.temlateObj;
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
                xtype:'button', iconCls:'ab_add', cls:'album-btn', width:23, height:24, 
                
                handler:function(){
                   var df = Ext.create('PetroRes.view.DocumentForm', {domain:me.temlateDomain, fullTitle:me.tfAdd.getValue()});
                   df.addListener(  'documentadded', 
                                    function(p1, p2){
                                        me.fireEvent('documentadded', {form:df})},
                                    me);
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
                                df
                            ]
                        }); 
                }
            });
        this.btnDelete = Ext.create('Ext.Button',
            {
                xtype:'button', iconCls:'ab_delete', cls:'album-btn', width:23, height:24,
                handler:function(){
                    if( me.getTemplateDocument()){
                        Ext.Msg.confirm('Delete document', 'Delete "'+ me.getTemplateDocument().fullTitle +'" ?', 
                                         function(button) {
                                            if (button === 'yes') {
                                                Ext.Ajax.request({
                                                url: 'rest/documents',
                                                method: 'DELETE',
                                                jsonData: me.getTemplateDocument(),
                                                success: function() {
                                                    Ext.Msg.alert(
                                                        'Success', 
                                                        'Your document has been deleted.',
                                                        function(p1, p2){
                                                           me.fireEvent('documentdeleted', {domain:me.getTemplateDomain,  obj:me.getTemplateDocument()});
                                                        }
                                                    );
                                                },
                                                failure: function(fp, o){
                                                    Ext.Msg.alert('Failure', o.result.msg);
                                                }
                                            });     
                                            }
                                        });
                    }}
            });
        var pnlButtons = 
            Ext.create('Ext.panel.Panel',
                        { region:'east',
                          layout:{
                            type: 'hbox',
                            width:60, height:24,
                            align:'top'
                          },
                          items:[this.btnAddOpen, this.btnDelete]});

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
            items: [ me.lblDomain, me.tfAdd, pnlButtons]
        });

        me.callParent(arguments);
    }
});


