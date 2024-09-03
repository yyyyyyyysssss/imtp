package org.imtp.web.service;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.imtp.web.domain.dto.EmailInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

/**
 * @Description
 * @Author ys
 * @Date 2024/8/13 9:24
 */
@Service
@Slf4j
@Async
public class EmailService {

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private SpringTemplateEngine springTemplateEngine;

    @Value("${spring.mail.from:''}")
    private String from;

    public void sendTextEmail(EmailInfo emailInfo){
        Assert.notNull(emailInfo,"emailInfo not null");
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject(emailInfo.getTitle());
        simpleMailMessage.setTo(emailInfo.getTo());
        simpleMailMessage.setText(emailInfo.getContent());
        if (from != null){
            simpleMailMessage.setFrom(from);
        }
        if (emailInfo.getCc() != null && emailInfo.getCc().length > 0){
            simpleMailMessage.setCc(emailInfo.getCc());
        }
        try {
            //发送
            javaMailSender.send(simpleMailMessage);
            log.info("sendTextEmail send succeed");
        }catch (Exception e){
            log.error("sendTextEmail error: ",e);
        }
    }

    public void sendHtmlEmail(EmailInfo emailInfo, String htmlTemplate, Map<String, Object> templateVariable){
        sendHtmlEmail(emailInfo,htmlTemplate,templateVariable,null);
    }

    public void sendHtmlEmail(EmailInfo emailInfo, String htmlTemplate, Map<String, Object> templateVariable,Map<String,byte[]> attachment){
        Assert.notNull(emailInfo,"emailInfo not null");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setSubject(emailInfo.getTitle());
            mimeMessageHelper.setTo(emailInfo.getTo());
            if (from != null){
                mimeMessageHelper.setFrom(from);
            }
            if (emailInfo.getCc() != null && emailInfo.getCc().length > 0){
                mimeMessageHelper.setCc(emailInfo.getCc());
            }
            //模板
            Context context = new Context();
            context.setVariables(templateVariable);
            String content = springTemplateEngine.process(htmlTemplate, context);
            mimeMessageHelper.setText(content,true);
            //附件
            if (attachment != null && !attachment.isEmpty()){
                for (String filename : attachment.keySet()){
                    byte[] bytes = attachment.get(filename);
                    ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
                    mimeMessageHelper.addAttachment(filename,byteArrayResource);
                }
            }
            //发送
            javaMailSender.send(mimeMessage);
            log.info("sendHtmlEmail send succeed");
        }catch (Exception e){
            log.error("sendHtmlEmail error: ",e);
        }

    }

}
