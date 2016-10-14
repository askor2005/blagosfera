package ru.radom.kabinet.services;

import org.apache.commons.io.IOUtils;
import org.apache.el.ExpressionFactoryImpl;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import ru.askor.blagosfera.core.settings.SettingsManager;
import ru.askor.blagosfera.domain.user.User;
import ru.radom.kabinet.dao.EmailTemplateDao;
import ru.radom.kabinet.dao.SmtpServerDao;
import ru.radom.kabinet.model.EmailTemplate;
import ru.radom.kabinet.model.SmtpServer;
import ru.radom.kabinet.security.SecurityUtils;
import ru.radom.kabinet.security.context.RequestContext;
import ru.radom.kabinet.utils.StringUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Сервис для отправки сообщений по почте
 * @author dfilinberg
 */
@Service
public class EmailService {
	private final static Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private EmailTemplateDao emailTemplateDao;

	@Autowired
	private SmtpServerDao smtpServerDao;

	@Autowired
	private SettingsManager settingsManager;

	@Autowired
	private SystemSettingsService systemSettingsService;

	@Autowired
	private RequestContext radomRequestContext;

    private final ExpressionFactoryImpl factory = new ExpressionFactoryImpl();

	// Общий шаблон для системы общих уведомлений по почте
	private static final String COMMON_EMAIL_TEMPLATE = "common.notify.email";

    public void deleteTemplate(Long id) {
		emailTemplateDao.delete(id);
	}

	public EmailTemplate getTemplateById(Long id) {
		return id == null ? null : emailTemplateDao.getById(id);
	}

	public List<EmailTemplate> findTemplates() {
		return emailTemplateDao.find(Order.asc("title"));
	}

	public void editTemplate(EmailTemplate template) {
		emailTemplateDao.saveOrUpdate(template);
	}

	public void sendTo(Collection<?> to, EmailTemplate template, Map<String, Object> variables) {
		if (to.isEmpty() || template == null) {
			return;
		}

		variables.put("applicationUrl", systemSettingsService.getApplicationUrl());
		EmailTemplateContext context = new EmailTemplateContext(variables, factory);

		for (final Object sharer : to) {
			try {
				getSender().send(createMessage(context, sharer, template));
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	/**
	 * Отправить сообщение на почту с помощью общего шаблона
	 * @param receiver
	 * @param subject
	 * @param content
	 * @param variables
	 */
	public void sendTo(User receiver, String subject, String content, Map<String, Object> variables) {
		EmailTemplate commonTemplate = emailTemplateDao.findByTitle(COMMON_EMAIL_TEMPLATE);

		EmailTemplate fakeTemplate = new EmailTemplate();
		fakeTemplate.setTitle(commonTemplate.getTitle());
		fakeTemplate.setFrom(commonTemplate.getFrom());
		// Меняем в общем шаблоне тему и контент сообщения
		String body = commonTemplate.getBody();
		String title = commonTemplate.getSubject();

		variables.put("content", content);
		variables.put("subject", subject);
		variables.put("applicationUrl", systemSettingsService.getApplicationUrl());
		EmailTemplateContext context = new EmailTemplateContext(variables, factory);
		fakeTemplate.setBody((String)factory.createValueExpression(context, body, String.class).getValue(context));
		fakeTemplate.setSubject((String) factory.createValueExpression(context, title, String.class).getValue(context));

		sendTo(Collections.singletonList(receiver), fakeTemplate, variables);
	}

	/**
	 * Простая реализация отправки почты
	 * @param receiver получатель
	 * @param subject тема письма
	 * @param content сообщение письма
	 * @param from от кого
	 * @throws Exception
	 */
	public void sendTo(User receiver, String subject, String content, String from) {
		try {
			MimeMessage message = getSender().createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED, "utf-8");
			helper.setTo(new InternetAddress(receiver.getEmail(), receiver.getFullName(), "UTF-8"));
			helper.setSubject(subject);
			helper.setText(content, true);
			helper.setFrom(new InternetAddress(getSender().getUsername(), from, "UTF-8"));

			getSender().send(message);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	public void sendVcard(User user,User receiver, String subject, String content, String vcard) {
		try {
			MimeMessage message = getSender().createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED, "utf-8");
			helper.setTo(new InternetAddress(receiver.getEmail(), receiver.getFullName(), "UTF-8"));
			helper.setSubject("Контакт: " + user.getFullName());
			helper.setText(content, true);
			helper.setFrom(new InternetAddress(getSender().getUsername(), "Система благосфера", "UTF-8"));
			helper.addAttachment("contact_windows.vcf", new InputStreamSource() {
				@Override
				public InputStream getInputStream() throws IOException {
					return IOUtils.toInputStream(vcard, Charset.forName("cp1251"));
				}
			}, "text/vcard");
			helper.addAttachment("contact.vcf", new InputStreamSource() {
				@Override
				public InputStream getInputStream() throws IOException {
					return IOUtils.toInputStream(vcard, Charset.forName("utf-8"));
				}
			}, "text/vcard");
			getSender().send(message);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private MimeMessage createMessage(EmailTemplateContext context, Object to, EmailTemplate template) throws Exception {
		context.getVariableMapper().setVariable("to", factory.createValueExpression(to, User.class));
		MimeMessage message = getSender().createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED, "utf-8");

		if (to instanceof User) {
			User sharerTo = (User) to;
			helper.setTo(new InternetAddress(sharerTo.getEmail(), sharerTo.getFullName(), "UTF-8"));
		} else if (to instanceof String) {
			helper.setTo(new InternetAddress((String) to, null, "UTF-8"));
		}

		helper.setSubject((String) factory.createValueExpression(context, template.getSubject(), String.class).getValue(context));
		helper.setText((String) factory.createValueExpression(context, template.getBody(), String.class).getValue(context), true);
		helper.setFrom(new InternetAddress(getSender().getUsername(), template.getFrom(), "UTF-8"));
		return message;
	}

	private JavaMailSenderImpl getSender() {
		SmtpServer smtp = smtpServerDao.findActual();
		if (smtp == null) {
			throw new IllegalStateException("SMTP server is not configured");
		}
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setDefaultEncoding("UTF-8");
		sender.setHost(smtp.getHost());
		sender.setPort(smtp.getPort());
		sender.setUsername(smtp.getUsername());
		sender.setPassword(smtp.getPassword());
		sender.setProtocol(smtp.getProtocol());
		sender.getSession().setDebug(smtp.isDebug());
		sender.getSession().getProperties().setProperty("mail.mime.charset", "UTF-8");
		return sender;
	}

	/*private void onActivate(User sharer) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("sharer", sharer);
		sendTo(Collections.singletonList(sharer), emailTemplateDao.findByTitle("notify.email.on-activate"), variables);
	}

	private void onRegister(User sharer) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("sharer", sharer);
		sendTo(Collections.singletonList(sharer), emailTemplateDao.findByTitle("notify.email.on-register"), variables);
	}

	private void onChangePassword(User sharer) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("sharer", sharer);
		sendTo(Collections.singletonList(sharer), emailTemplateDao.findByTitle("notify.email.on-change-password"), variables);
	}

	private void onInitChangeEmail(User sharer) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("sharer", sharer);
		sendTo(Collections.singletonList(sharer), emailTemplateDao.findByTitle("notify.email.on-init-change-email"), variables);
	}

	private void onCompleteChangeEmail(User sharer) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("sharer", sharer);
		sendTo(Collections.singletonList(sharer), emailTemplateDao.findByTitle("notify.email.on-complete-change-email"), variables);
	}

	private void onInitPasswordRecovery(User sharer) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("sharer", sharer);
		sendTo(Collections.singletonList(sharer), emailTemplateDao.findByTitle("notify.email.on-init-password-recovery"), variables);
	}
	
	private void onCompletePasswordRecovery(User sharer) {
		Map<String, Object> variables = new HashMap<>();
		variables.put("sharer", sharer);
		sendTo(Collections.singletonList(sharer), emailTemplateDao.findByTitle("notify.email.on-complete-password-recovery"), variables);
	}*/

    public void sendError(final HttpServletRequest req, final Throwable exception, final String profile) {
        try {
            final String supportEmail = settingsManager.getSystemSetting("support.email");
            if(StringUtils.isEmpty(supportEmail)) return;
            final JavaMailSenderImpl sender = getSender();
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED, "utf-8");
            helper.setTo(new InternetAddress(supportEmail, "RADOM SUPPORT", "UTF-8"));
            helper.setSubject(String.format("ERROR (%s): %s", profile.toUpperCase(), exception.getLocalizedMessage()));
            final StringWriter sw = new StringWriter();
            exception.printStackTrace(new PrintWriter(sw));
            final StringBuilder sb = new StringBuilder();
            final User user = SecurityUtils.getUser();

            if (user != null) {
                sb.append("USER NAME: ").append(user.getFullName()).append("\n");
                sb.append("USER IKP: ").append(user.getIkp()).append("\n");
            }
            if (req != null) {
                //sb.append("VERIFICATION_REQUEST URL: ").append(req.getRequestURL()).append("\n");
                final Enumeration headerNames = req.getHeaderNames();
                if(headerNames.hasMoreElements()){
                    sb.append("VERIFICATION_REQUEST HEADERS: ").append("\n");
                    while(headerNames.hasMoreElements()){
                        final String headerName = (String)headerNames.nextElement();
                        sb.append("  ").append(headerName).append(": ").append(req.getHeader(headerName)).append("\n");
                    }
                }

                final Enumeration paramNames = req.getParameterNames();
                if(paramNames.hasMoreElements()){
                    sb.append("VERIFICATION_REQUEST PARAMETERS: ").append("\n");
                    while(paramNames.hasMoreElements()){
                        final String paramName = (String)paramNames.nextElement();
                        sb.append("  ").append(paramName).append(": ").append(req.getParameter(paramName)).append("\n");
                    }
                }
            }

            sb.append("STACK TRACE: ").append("\n").append(sw.toString());
            helper.setText(sb.toString());
            helper.setFrom(new InternetAddress(sender.getUsername(), profile.toUpperCase() + " APPLICATION ERROR", "UTF-8"));
            sender.send(message);
        } catch (Exception e) {
            logger.error("sent to support error");
        }
    }

}
