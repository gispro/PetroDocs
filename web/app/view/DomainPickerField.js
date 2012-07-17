Ext.define("PetroRes.view.DomainPickerField", {
    extend : "Ext.form.field.Picker",
    requires : ["Ext.tree.Panel"],
    xtype: "domainpickerfield",
    initComponent : function() {
        var self = this;
        Ext.apply(self, {
            fieldLabel : self.fieldLabel,
            labelWidth : self.labelWidth     
        });
        self.callParent();
    },
    createPicker : function() {
        var self = this;
        var store = Ext.getStore('PetroRes.store.DomainsJsonTreeStore');
        self.picker = new Ext.tree.Panel({
            height : 300,
            autoScroll : true,
            floating : true,
            focusOnToFront : false,
            shadow : true,
            ownerCt : this.ownerCt,
            useArrows : true,
            store : store
            //rootVisible : false
        });
        self.picker.on({
            checkchange : function() {
                var records = self.picker.getView().getChecked(), names = [], values = [];
                Ext.Array.each(records, function(rec) {
                    names.push(rec.get('name'));
                    values.push(rec.get('id'));
                });
                self.setRawValue(values.join(';'));// 
                self.setValue(names.join(';'));// 
            }
        });
        return self.picker;
    },
    alignPicker : function() {
        var me = this, picker, isAbove, aboveSfx = '-above';
        if (this.isExpanded) {
            picker = me.getPicker();
            if (me.matchFieldWidth) {
                picker.setWidth(me.bodyEl.getWidth());
            }
            if (picker.isFloating()) {
                picker.alignTo(me.inputEl, "", me.pickerOffset);// ""->tl
                isAbove = picker.el.getY() < me.inputEl.getY();
                me.bodyEl[isAbove ? 'addCls' : 'removeCls'](me.openCls
                    + aboveSfx);
                picker.el[isAbove ? 'addCls' : 'removeCls'](picker.baseCls
                    + aboveSfx);
            }
        }
    }
});
