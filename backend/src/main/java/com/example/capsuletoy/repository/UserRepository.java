package com.example.capsuletoy.repository;

import com.example.capsuletoy.model.User;
import com.example.capsuletoy.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ユーザー名で検索
    Optional<User> findByUsername(String username);

    // メールアドレスで検索
    Optional<User> findByEmail(String email);

    // ユーザー名の存在確認
    boolean existsByUsername(String username);

    // メールアドレスの存在確認
    boolean existsByEmail(String email);

    // 通知が有効なユーザーを取得
    List<User> findByNotificationEnabledTrue();

    // ロールでユーザーを取得
    List<User> findAllByRole(UserRole role);
}
