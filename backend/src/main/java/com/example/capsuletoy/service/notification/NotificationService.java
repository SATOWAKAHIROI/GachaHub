package com.example.capsuletoy.service.notification;

import com.example.capsuletoy.domain.notification.CreateHtmlMainDomain;
import com.example.capsuletoy.domain.notification.NotificationEnabledDomain;
import com.example.capsuletoy.domain.notification.SendEmailDomain;
import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.User;
import com.example.capsuletoy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private SendEmailDomain sendEmailDomain;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationEnabledDomain notificationEnabledDomain;

    @Autowired
    private CreateHtmlMainDomain createHtmlMainDomain;

    public void sendFinishedEmail(List<Product> allnewProducts){
        // スクレイピング完了後に通知メールを送信（新着0件でも送信）
        logger.info("通知メールを送信します（新着{}件）", allnewProducts.size());
        try {
            notifyNewProducts(allnewProducts);
        } catch (Exception e) {
            logger.error("通知メール送信中にエラー: {}", e.getMessage(), e);
        }
    }

    /**
     * 新着商品を通知有効ユーザー全員にメール送信
     */
    private void notifyNewProducts(List<Product> newProducts) {
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
            sendEmailDomain.sendNewProductsEmail(user, newProducts, htmlContent);
        }
    }

    /**
     * ユーザーの通知設定を切り替え
     */
    public User toggleNotification(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: ID=" + userId));
        user.setNotificationEnabled(!user.getNotificationEnabled());
        logger.info("通知設定変更: ユーザー={}, 有効={}", user.getUsername(), user.getNotificationEnabled());
        return userRepository.save(user);
    }
}
