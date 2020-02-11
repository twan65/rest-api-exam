package com.rest.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder // builderを使用できる。
@Entity // jpa entity
@Getter
@NoArgsConstructor // 引数がないコンストラクタを自動生成
@AllArgsConstructor // 引数を持つコンストラクタを自動生成
@Table(name = "user") // ‘user’テーブルにマッピングされることを指す
public class User implements UserDetails {

    @Id // primaryKey
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long msrl;

    // uid columnを指す。必須でユニーク項目、長さは30
    @Column(nullable = false, unique = true, length = 30)
    private String uid;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;

    /*
    基本権限がセットされる。
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    /*
    権限はメンバーごとに複数になる可能性もあるのでCollectionを利用
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    /*
    isAccountNonExpired : アカウントが満了となったのか
    isAccountNonLocked : アカウントがロックされてないのか
    isCredentialsNonExpired : アカウントのパスワードが満了されてないのか
    isEnabled : アカウントを利用できるか
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return this.uid;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }
}
