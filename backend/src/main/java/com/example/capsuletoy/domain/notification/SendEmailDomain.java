package com.example.capsuletoy.domain.notification;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.User;
import com.example.capsuletoy.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class SendEmailDomain {
    private static final Logger logger = LoggerFactory.getLogger(SendEmailDomain.class);

    @Autowired
    private NotificationFromAddressDomain notificationFromAdressDomain;
    
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private NotificationEnabledDomain notificationEnabledDomain;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreateHtmlMainDomain createHtmlMainDomain;

    /**
     * 新着商品を通知有効ユーザー全員にメール送信
     */

    // メール本文作成
    public void notifyNewProducts(List<Product> newProducts) {
        if (!notificationEnabledDomain.isNotificationEnabled()) {
            logger.info("通知機能が無効のためスキップします");
            return;
        }

        List<User> enabledUsers = userRepository.findByNotificationEnabledTrue();
        if (enabledUsers.isEmpty()) {
            logger.info("通知有効なユーザーがいないためスキップします");
            return;
        }

        String htmlContent = createHtmlMainDomain.buildNewProductsHtml(newProducts);

        for (User user : enabledUsers) {
            sendNewProductsEmail(user, newProducts, htmlContent);
        }
    }


    // 件名を添えて送信
    private void sendNewProductsEmail(User user,List<Product> newProducts, String htmlContent) {
        try {
                sendHtmlMail(
                        user.getEmail(),
                        "【GachaHub】スクレイピング完了（新着" + newProducts.size() + "件）",
                        htmlContent
                );
                logger.info("通知メール送信成功: {}", user.getEmail());
            } catch (Exception e) {
                logger.error("通知メール送信失敗: {} - {}", user.getEmail(), e.getMessage());
            }
    }

    /**
     * HTMLメール送信
     */
    private void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(notificationFromAdressDomain.getFromAddress());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

     /**
     * テストメール送信
     */
    public void sendTestMail(String toAddress) throws MessagingException {
        String html = """
                <html>
                <body style="font-family: sans-serif; padding: 20px;">
                    <h2 style="color: #4F46E5;">GachaHub テストメール</h2>
                    <p>このメールはGachaHubの通知機能のテストメールです。</p>
                    <p>メール通知が正しく設定されています。</p>
                    <hr style="border: 1px solid #E5E7EB;">
                    <p style="color: #6B7280; font-size: 12px;">GachaHub - ガチャガチャ新着情報サービス</p>
                </body>
                </html>
                """;
        sendHtmlMail(toAddress, "【GachaHub】テストメール", html);
    }
}
