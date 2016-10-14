-- // update_email_template_on_invite_accept
-- Migration SQL that makes the change goes here.

UPDATE email_templates SET BODY='<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <meta content="width = device-width" name="viewport">
</head>
<body>
<div id="mailsub" style="font-family: arial, sans-serif; width: 100%; color: #616266;">
    <table border="0" cellspacing="0" cellpadding="0" width="100%" style="font-family: arial, sans-serif; width: 100%; color: #616266;">
        <tr>
            <td width="100%">
                <div style="border-left: 1px solid #e7e7e7;; border-right: 1px solid #e7e7e7;; border-top: 1px solid #e7e7e7;; border-bottom: 1px solid #e7e7e7;">
                    <table border="0" cellspacing="0" cellpadding="0" width="100%" bgcolor="#f8f8f8">
                        <tr>
                            <td width="100%" height="10" colspan="3" bgcolor="#f8f8f8">
                                <div style="height: 10px; overflow: hidden; width: 100%; font-size: 0; line-height: 0;"></div>
                            </td>
                        </tr>
                        <tr>
                            <td width="20"><div style="height: 8px; overflow: hidden; width: 20px;"></div></td>
                            <td width="60">
                                <div style="width: 60px;">
                                    <a href="${applicationUrl}/" target="_blank"><img src="${applicationUrl}/i/logo-50.png" alt="Система RaMERA™" style="display: block; margin: 0;" border="0" width="50" height="50" alt="Система RaMERA™" /></a>
                                </div>
                            </td>
                            <td width="100%">
                                <div style="overflow: hidden; width: 100%;">
                                    <a href="${applicationUrl}/" target="_blank" style="text-decoration: none;"><h2 style="font-family:Arial; color: #616266; text-decoration: none; margin:0;">Система Благосфера</h2></a>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td width="100%" height="10" colspan="3" bgcolor="#f8f8f8"><div style="height: 10px; overflow: hidden; width: 100%; font-size: 0; line-height: 0;"></div></td>
                        </tr>
                    </table>
                </div>
                <div style="border-left: 1px solid #e7e7e7;; border-right: 1px solid #e7e7e7;; border-bottom: 1px solid #e7e7e7;;">
                    <table border="0" cellspacing="0" cellpadding="0" width="99%">
                        <tr>
                            <td colspan="3" width="99%" height="20"><img src="${applicationUrl}/i/_.gif" style="display: block; margin: 0;" border="0" width="99%" height="20" /></td>
                        </tr>
                        <tr>
                            <td width="20"><div style="height: 8px; overflow: hidden; width: 20px;"></div></td>
                            <td width="100%">
                                <h3 style="font-family:Arial; color: #616266; text-decoration: none; margin:0; margin-top: 10px;">Ваши данные для входа в систему БЛАГОСФЕРА</h3>
                                <hr/>
                                <p style="font-family:Arial; font-size:13px; padding: 5px 10px 0 10px; margin: 5px 0 0 0; color: #333333;">E-mail: ${login}</p>
                                <p style="font-family:Arial; font-size:13px; padding: 0px 10px 5px 10px; margin: 5px 0; color: #333333;">Проверочный код: ${password}</p>
                                <hr/>
                                <p style="font-family:Arial; font-size:13px; padding: 5px 10px; margin: 5px 0; color: #333333;">При входе в Систему БЛАГОСФЕРА введите в специально отведённые поля</br> свой e-mail и проверочный код, после чего следуйте инструкциям Системы.</p>
                                <p style="font-family:Arial; font-size:13px; padding: 5px 10px; margin: 5px 0; color: #333333;">Вы можете нажать по ссылке и перейти к смене пароля (тогда проверочный код уже будет там введен</br> в поле проверочный код и фокус будет установлен на новый пароль) или же вручную ввести данные</br> и проверочный код надо будет вбивать вручную.</p>
                                <p style="font-family:Arial; font-size:13px; padding: 5px 10px; margin: 5px 0; color: #333333; text-align: center;"><a href="${applicationUrl}/${inviteLogin}" style="text-decoration: initial; color: #3366FF; font-size: 18px;">Вход в Систему БЛАГОСФЕРА</a></p>
                            </td>
                            <td width="20"><div style="height: 8px; overflow: hidden; width: 20px;"></div></td>
                        </tr>
                    </table>
                </div>
            </td>
        </tr>
    </table>
</div>
</body>
</html>' WHERE TITLE='invite.email.on-invite-accept';

-- //@UNDO
-- SQL to undo the change goes here.


