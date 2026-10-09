package pe.edu.upc.rafishdar_back.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pe.edu.upc.rafishdar_back.entities.User;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSecurity
        implements UserDetails {

    private User user;

    @Override
    public Collection<? extends GrantedAuthority>
    getAuthorities() {

        return user.getAuthorities()
                .stream()
                .map(AuthoritySecurity::new)
                .toList();
    }

    @Override
    public String getPassword() {

        return user.getPassword();
    }

    @Override
    public String getUsername() {

        return user.getCorreo();
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

        return user.getEstado()
                .equalsIgnoreCase("Activo");
    }
}