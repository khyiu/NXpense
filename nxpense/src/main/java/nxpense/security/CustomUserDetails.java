package nxpense.security;

import nxpense.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private static final long serialVersionUID = -3060572330933644236L;

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.<GrantedAuthority>emptyList();
    }

    public User getUser() {
        return this.user;
    }

    public String getPassword() {
        return String.valueOf(this.user.getPassword());
    }

    public String getUsername() {
        return this.user.getEmail();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

}
