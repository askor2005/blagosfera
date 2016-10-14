-- // voting batch failed template
-- Migration SQL that makes the change goes here.

INSERT INTO email_templates (id, body, send_from, subject, title)
  SELECT nextval('seq_email_templates'), '', 'Система Благосфера', 'Уведомлении о собрании', 'voting-batch-failed'
  WHERE NOT EXISTS (SELECT title FROM email_templates WHERE title='voting-batch-failed');

update email_templates
  set body = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
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
                                            <a href="${applicationUrl}/" target="_blank"><img src="${applicationUrl}/i/logo-50.png" alt="Система Благосфера™" style="display: block; margin: 0;" border="0" width="50" height="50" alt="Система Благосфера™" /></a>
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
                            Ув. ${receiver.officialName}, неудачно завершено собрание по теме "${batchVoting.subject}",

                            <br>

                            в рамках "${community.name}"

                            <br>

                            Причина неудачи - неудачное голосование по теме: ${failedVoting.subject}

                            <br><br>
                            <a href="${applicationUrl}/votingsystem/registrationInVoting.html?batchVotingId=${batchVoting.id}">Перейти к странице собрания</a>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </body>
</html>'
 where title='voting-batch-failed';

-- //@UNDO
-- SQL to undo the change goes here.


