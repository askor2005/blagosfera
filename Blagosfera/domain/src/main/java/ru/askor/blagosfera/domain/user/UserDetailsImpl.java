package ru.askor.blagosfera.domain.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * Created by Maxim Nikitin on 16.03.2016.
 */
public class UserDetailsImpl implements UserDetails {

    private User user;
    private List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    private String password;
    private String username;

    public UserDetailsImpl() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public List<SimpleGrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean hasRole(String role) {
        for (SimpleGrantedAuthority authority : authorities) {
            if (authority.getAuthority().equals(role)) {
                return true;
            }
        }

        return false;
    }

    public boolean hasAnyRole(Collection<String> roles) {
        for (String role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDetailsImpl that = (UserDetailsImpl) o;

        return getUsername().equals(that.getUsername());

    }

    @Override
    public int hashCode() {
        return getUsername().hashCode();
    }
}
