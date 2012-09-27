<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>PetroResource Document System</title>
        <link rel="stylesheet" type="text/css" href="lib/ext41/resources/css/ext-all.css"/>
        <script type="text/javascript" src="lib/ext41/ext-all-debug.js"></script>
        <script type="text/javascript">
            Ext.onReady(function(){
                var form = Ext.create('Ext.form.Panel', {
                    //width: 300,
                    //height: 150,
                    url: 'j_security_check',
                    method: 'POST',
                    standardSubmit: true,
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Login',
                            width: 250,
                            name: 'j_username'
                        },
                        {
                            xtype: 'textfield',
                            width: 250,
                            fieldLabel: 'Password',
                            inputType: 'password',
                            name: 'j_password'
                        },
                    ],
                    buttons: [
                        {
                            text: 'OK',
                            formBind: true,
                            handler: function(){
                                form.submit();
                            }
                        }
                    ]
                });

                var wnd = Ext.create('Ext.window.Window', {
                    //width: 300,
                    //height: 150,
                    title: 'Please Log In',
                    layout: 'fit',
                    resizable: false,
                    closable: false,
                    plain: true,
                    border: false,
                    items: [            
                        form
                    ]
                });
                //form.render('formDiv');//  Ext.getBody());
                wnd.render(Ext.getBody());
                wnd.show();
            });
        </script>
    </head>
    <body>
        
        <div id="formDiv" align="center" style="vertical-align: middle"></div>
        <!--<h1>Please Log In</h1>
        <form method="POST" action="j_security_check"/>
        Login: <input type="text" name="j_username"/><br/>
        Password: <input type="password" name="j_password"/><br/>
        <input type="submit"/>
        </form>        -->
    </body>

</html>
