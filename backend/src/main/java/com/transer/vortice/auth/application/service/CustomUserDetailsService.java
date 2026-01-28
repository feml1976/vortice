package com.transer.vortice.auth.application.service;

import com.transer.vortice.auth.domain.model.User;
import com.transer.vortice.auth.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación personalizada de UserDetailsService para Spring Security.
 * Carga los detalles del usuario desde la base de datos.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Carga un usuario por su nombre de usuario para autenticación.
     *
     * @param username nombre de usuario o email
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Cargando usuario por username: {}", username);

        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });

        log.debug("Usuario encontrado: {} (ID: {})", user.getUsername(), user.getId());

        return buildUserDetails(user);
    }

    /**
     * Construye un objeto UserDetails de Spring Security desde un User del dominio.
     *
     * @param user usuario del dominio
     * @return UserDetails para Spring Security
     */
    private UserDetails buildUserDetails(User user) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(user.getIsLocked())
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }

    /**
     * Convierte los roles y permisos del usuario a GrantedAuthorities de Spring Security.
     * Incluye tanto los roles (con prefijo ROLE_) como los permisos individuales.
     *
     * @param user usuario del dominio
     * @return colección de authorities
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Agregar autoridades basadas en roles y sus permisos
        user.getRoles().forEach(role -> {
            // Agregar el rol con prefijo ROLE_
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));

            // Agregar permisos del rol
            role.getPermissions().forEach(permission -> {
                authorities.add(new SimpleGrantedAuthority(permission.getName()));
            });
        });

        log.debug("Authorities cargadas para usuario {}: {}", user.getUsername(),
                  authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", ")));

        return authorities;
    }
}
