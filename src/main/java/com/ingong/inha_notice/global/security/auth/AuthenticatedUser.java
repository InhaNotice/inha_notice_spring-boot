/*
 * This is file of the project inha_notice
 * Licensed under the MIT License.
 * Copyright (c) 2025-2026 INGONG
 * For full license text, see the LICENSE file in the root directory or at
 * https://opensource.org/license/mit
 * Author: Junho Kim
 * Latest Updated Date: 2026-02-17
 */

package com.ingong.inha_notice.global.security.auth;

import com.ingong.inha_notice.domain.user.entity.User;
import com.ingong.inha_notice.domain.user.enums.UserRole;
import com.ingong.inha_notice.domain.user.enums.UserStatus;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class AuthenticatedUser implements UserDetails {

  private final String publicId;
  private final String password;
  private final UserStatus status;
  private final UserRole role;
  private final Collection<? extends GrantedAuthority> authorities;

  public static AuthenticatedUser from(User user) {
    UserRole role = (user.getRole() == null) ? UserRole.USER : user.getRole();

    Collection<? extends GrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));

    return new AuthenticatedUser(
        user.getPublicId(),
        user.getPassword(),
        user.getStatus(),
        role,
        authorities
    );
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getUsername() {
    return publicId;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return status != UserStatus.BANNED;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return status == UserStatus.ACTIVE;
  }

}
