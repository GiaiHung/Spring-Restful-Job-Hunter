package vn.giaihung.jobhunter.service;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final MailSender mailSender;
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(
            MailSender mailSender,
            JavaMailSender javaMailSender,
            SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    // Simple example
    public void sendEmail() {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("hunggiaitruong288@gmail.com");
        msg.setSubject("Testing from Spring Boot");
        msg.setText("Hello World from Spring Boot Email");
        mailSender.send(msg);
    }

    public void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }

    // With image
    public void sendEmailSync(
            String to,
            String subject,
            String content,
            boolean isMultipart,
            boolean isHtml,
            final String imageResourceName,
            final byte[] imageBytes,
            final String imageContentType) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content, isHtml);

            // Handling image
            final InputStreamSource imageSource = new ByteArrayResource(imageBytes);
            message.addInline(imageResourceName, imageSource, imageContentType);

            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            System.out.println("ERROR SEND EMAIL: " + e);
        }
    }

    public void sendEmailFromTemplateSync(String to,
            String subject,
            String templateName,
            String username,
            Object value) {
        Context context = new Context();

        context.setVariable("name", username);
        context.setVariable("jobs", value);
        String content = templateEngine.process(templateName, context);
        sendEmailSync(to, subject, content, false, true);
    }

    // With image
    public void sendEmailFromTemplateSync(String to,
            String subject,
            String templateName,
            String username,
            Object value,
            final String imageResourceName,
            final byte[] imageBytes,
            final String imageContentType) {
        Context context = new Context();

        context.setVariable("name", username);
        context.setVariable("jobs", value);
        context.setVariable("imageResourceName", imageResourceName);
        String content = templateEngine.process(templateName, context);
        sendEmailSync(to, subject, content, true, true, imageResourceName, imageBytes, imageContentType);
    }
}
