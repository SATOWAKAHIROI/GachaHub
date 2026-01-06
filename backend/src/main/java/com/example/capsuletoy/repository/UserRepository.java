package com.example.capsuletoy.repository;

import com.example.capsuletoy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
