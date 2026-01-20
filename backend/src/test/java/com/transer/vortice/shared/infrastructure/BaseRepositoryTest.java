package com.transer.vortice.shared.infrastructure;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Clase base para tests de repositorios usando Testcontainers con PostgreSQL.
 *
 * Esta clase configura un contenedor PostgreSQL compartido de forma singleton para todos los tests,
 * permitiendo ejecutar tests de repositorios contra una base de datos PostgreSQL real
 * en lugar de H2, lo cual garantiza que las migraciones de Flyway y las características
 * específicas de PostgreSQL funcionen correctamente.
 *
 * <p>El contenedor se inicia una vez y se mantiene vivo durante toda la ejecución de tests,
 * evitando el overhead de iniciar/detener contenedores Docker entre test classes.
 *
 * <p>Uso:
 * <pre>
 * {@code
 * @DisplayName("User Repository Tests")
 * class UserRepositoryTest extends BaseRepositoryTest {
 *     @Autowired
 *     private UserRepository userRepository;
 *
 *     @Test
 *     void testFindByUsername() {
 *         // test implementation
 *     }
 * }
 * }
 * </pre>
 *
 * @author Vórtice Development Team
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/cleanup-test-data.sql")
public abstract class BaseRepositoryTest {

    /**
     * Contenedor PostgreSQL compartido como singleton entre todos los tests.
     *
     * Este contenedor se inicia una vez en la primera ejecución y se mantiene vivo
     * para todos los tests subsecuentes, mejorando significativamente el rendimiento.
     *
     * Se usa un patrón singleton manualmente iniciado para evitar que Testcontainers
     * detenga el contenedor entre test classes cuando se usa @DataJpaTest.
     */
    protected static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("vortice_test")
            .withUsername("test")
            .withPassword("test");
        postgresContainer.start();
    }

    /**
     * Configura dinámicamente las propiedades de conexión a la base de datos
     * para que Spring Boot use el contenedor PostgreSQL de Testcontainers.
     *
     * @param registry el registro de propiedades dinámicas de Spring
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        // Configurar Flyway para usar el mismo datasource
        registry.add("spring.flyway.url", postgresContainer::getJdbcUrl);
        registry.add("spring.flyway.user", postgresContainer::getUsername);
        registry.add("spring.flyway.password", postgresContainer::getPassword);
    }
}
