package com.example.capsuletoy.service;

import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.User;
import com.example.capsuletoy.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Value("${notification.from-address}")
    private String fromAddress;

    @Value("${notification.enabled}")
    private boolean notificationEnabled;

    /**
     * 新着商品を通知有効ユーザー全員にメール送信
     */
    public void notifyNewProducts(List<Product> newProducts) {
        if (!notificationEnabled) {
            logger.info("通知機能が無効のためスキップします");
            return;
        }

        List<User> enabledUsers = userRepository.findByNotificationEnabledTrue();
        if (enabledUsers.isEmpty()) {
            logger.info("通知有効なユーザーがいないためスキップします");
            return;
        }

        String htmlContent = buildNewProductsHtml(newProducts);

        for (User user : enabledUsers) {
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

    /**
     * ユーザーの通知設定を切り替え
     */
    public User toggleNotification(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: ID=" + userId));
        user.setNotificationEnabled(!user.getNotificationEnabled());
        logger.info("通知設定変更: ユーザー={}, 有効={}", user.getUsername(), user.getNotificationEnabled());
        return userRepository.save(user);
    }

    /**
     * HTMLメール送信
     */
    private void sendHtmlMail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromAddress);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    /**
     * 新着商品通知用のHTML生成
     */
    private String buildNewProductsHtml(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                <html>
                <body style="font-family: sans-serif; padding: 20px; background-color: #F9FAFB;">
                    <div style="max-width: 600px; margin: 0 auto; background: white; border-radius: 8px; padding: 24px;">
                        <h2 style="color: #4F46E5; margin-bottom: 16px;">🎪 スクレイピング完了通知</h2>
                        <p style="color: #374151;">新着商品: <strong>%d件</strong></p>
                        <hr style="border: 1px solid #E5E7EB;">
                """.formatted(products.size()));

        if (products.isEmpty()) {
            sb.append("<p style=\"color: #6B7280; text-align: center; padding: 20px 0;\">新着商品はありませんでした。</p>");
        } else {
            for (Product product : products) {
                sb.append("<div style=\"padding: 12px 0; border-bottom: 1px solid #F3F4F6;\">");
                sb.append("<h3 style=\"color: #1F2937; margin: 0 0 4px 0;\">").append(escapeHtml(product.getProductName())).append("</h3>");

                if (product.getManufacturer() != null) {
                    String displayName = "BANDAI".equals(product.getManufacturer()) ? "バンダイ" : "タカラトミーアーツ";
                    sb.append("<span style=\"color: #6B7280; font-size: 13px;\">メーカー: ").append(displayName).append("</span><br>");
                }
                if (product.getPrice() != null) {
                    sb.append("<span style=\"color: #4F46E5; font-weight: bold;\">").append(product.getPrice()).append("円</span><br>");
                }
                if (product.getReleaseDate() != null) {
                    sb.append("<span style=\"color: #6B7280; font-size: 13px;\">発売日: ").append(product.getReleaseDate()).append("</span><br>");
                }
                if (product.getSourceUrl() != null) {
                    sb.append("<a href=\"").append(escapeHtml(product.getSourceUrl())).append("\" style=\"color: #4F46E5; font-size: 13px;\">詳細を見る →</a>");
                }
                sb.append("</div>");
            }
        }

        sb.append("""
                        <hr style="border: 1px solid #E5E7EB; margin-top: 16px;">
                        <p style="color: #9CA3AF; font-size: 12px; text-align: center;">
                            GachaHub - ガチャガチャ新着情報サービス<br>
                            通知を停止するにはアカウント設定から通知をOFFにしてください。
                        </p>
                    </div>
                </body>
                </html>
                """);

        return sb.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
