package com.example.capsuletoy.service.notification;

import com.example.capsuletoy.domain.notification.SendEmailDomain;
import com.example.capsuletoy.model.Product;
import com.example.capsuletoy.model.User;
import com.example.capsuletoy.repository.UserRepository;
import com.example.capsuletoy.service.user.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private SendEmailDomain sendEmailDomain;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public void sendFinishedEmail(List<Product> allnewProducts){
        // スクレイピング完了後に通知メールを送信（新着0件でも送信）
        logger.info("通知メールを送信します（新着{}件）", allnewProducts.size());
        try {
            sendEmailDomain.notifyNewProducts(allnewProducts);
        } catch (Exception e) {
            logger.error("通知メール送信中にエラー: {}", e.getMessage(), e);
        }
    }

    /**
     * ユーザーの通知設定を切り替え
     */
    public User toggleNotification(Long userId) {
        
        User user = userService.findById(userId);
        user.setNotificationEnabled(!user.getNotificationEnabled());
        logger.info("通知設定変更: ユーザー={}, 有効={}", user.getUsername(), user.getNotificationEnabled());
        User returnUser = userRepository.save(user);
        return returnUser;
    }

    /**
     * テストメール送信
     * @throws Exception 
     */
    public void sendTestMail(String toAddress) throws Exception {
        try {
            sendEmailDomain.sendTestMail(toAddress);
        } catch (Exception e) {
            logger.error("テストメール送信失敗: {}", e.getMessage());
            throw new Exception("メール送信に失敗しました: " + e.getMessage());
        }
    }
}
