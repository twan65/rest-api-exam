package com.rest.api.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder // builderを使用できる。
@Entity // jpa entity
@Getter
@NoArgsConstructor // 引数がないコンストラクタを自動生成
@AllArgsConstructor // 引数を持つコンストラクタを自動生成
@Table(name = "user") // ‘user’テーブルにマッピングされることを指す
public class User {

    @Id // primaryKey
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long msrl;

    // uid columnを指す。必須でユニーク項目、長さは30
    @Column(nullable = false, unique = true, length = 30)
    private String uid;

    @Column(nullable = false, length = 100)
    private String name;
}
