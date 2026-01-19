package com.transer.vortice.auth.domain.repository;

import com.transer.vortice.auth.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User.
 * Proporciona métodos de acceso a datos para usuarios.
 *
 * @author Vórtice Development Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su email.
     *
     * @param email email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su nombre de usuario o email.
     *
     * @param username nombre de usuario
     * @param email email del usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Verifica si existe un usuario con el nombre de usuario dado.
     *
     * @param username nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email email del usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario activo por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return Optional con el usuario si existe y está activo
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> findActiveUserByUsername(@Param("username") String username);

    /**
     * Busca un usuario activo por su email.
     *
     * @param email email del usuario
     * @return Optional con el usuario si existe y está activo
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.isActive = true")
    Optional<User> findActiveUserByEmail(@Param("email") String email);

    /**
     * Busca un usuario activo y no bloqueado por su nombre de usuario.
     *
     * @param username nombre de usuario
     * @return Optional con el usuario si existe, está activo y no está bloqueado
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true AND u.isLocked = false")
    Optional<User> findActiveAndUnlockedUserByUsername(@Param("username") String username);
}
