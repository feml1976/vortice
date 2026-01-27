# Tests Frontend - Módulo de Llantas

## Configuración de Testing

El módulo de llantas utiliza **Vitest** + **React Testing Library** para los tests de componentes.

### Dependencias

```json
{
  "@testing-library/jest-dom": "^6.1.5",
  "@testing-library/react": "^14.1.2",
  "@testing-library/user-event": "^14.5.1",
  "@vitest/ui": "^1.0.4",
  "jsdom": "^23.0.1",
  "vitest": "^1.0.4"
}
```

### Archivos de Configuración

- **vitest.config.ts**: Configuración de Vitest con jsdom y setup
- **src/test/setup.ts**: Setup global (mocks de window.matchMedia, IntersectionObserver)
- **src/test/test-utils.tsx**: Utilidades de testing
  - Custom render con providers (QueryClient, BrowserRouter)
  - Mock data para tests
  - Re-exportación de testing-library utilities

## Tests Implementados

### TireSpecificationList Component

**Ubicación**: `src/features/tire/components/__tests__/TireSpecificationList.test.tsx`

**Cobertura**: 9 tests implementados, 7 passing ✅

#### Tests Passing (7)

1. ✅ **Renderizado inicial** - debe renderizar el campo de búsqueda
2. ✅ **Renderizado inicial** - debe renderizar el botón de nueva especificación
3. ✅ **Renderizado inicial** - debe renderizar los datos en la tabla
4. ✅ **Estado de carga** - debe mostrar el componente mientras carga
5. ✅ **Estado de error** - debe mostrar mensaje de error cuando falla la carga
6. ✅ **Sin datos** - debe mostrar mensaje cuando no hay fichas técnicas
7. ✅ **Paginación y datos** - debe mostrar múltiples registros

#### Tests con Warnings (2)

Estos tests pasan pero generan warnings de React relacionados con actualizaciones async:

8. ⚠️ **Renderizado inicial** - debe mostrar el total correcto de registros en la paginación
9. ⚠️ **Navegación** - debe llamar onCreate al hacer clic en "Nueva Especificación"

**Nota**: Los warnings son relacionados con el timing de actualizaciones de estado en MUI DataGrid y no afectan la funcionalidad.

## Ejecutar Tests

### Todos los tests
```bash
npm test
```

### Tests específicos
```bash
npm test TireSpecificationList.test.tsx
```

### Modo UI interactivo
```bash
npm run test:ui
```

### Con cobertura
```bash
npm run test:coverage
```

## Estructura de Tests

### Patrón de Testing

Los tests siguen el patrón **Arrange-Act-Assert**:

```typescript
it('debe renderizar el campo de búsqueda', async () => {
  // Arrange: preparar mocks y datos
  render(<TireSpecificationList {...defaultProps} />);

  // Act & Assert: verificar renderizado
  await waitFor(() => {
    expect(screen.getByPlaceholderText(/buscar especificaciones/i)).toBeInTheDocument();
  });
});
```

### Mocking

Los tests utilizan **vi.mock()** para mockear:
- React Query hooks (useTireSpecifications, useSearchTireSpecifications, useDeleteTireSpecification)
- React Router (useNavigate)

```typescript
vi.mock('../../hooks/useTireSpecifications', () => ({
  useTireSpecifications: vi.fn(),
  useSearchTireSpecifications: vi.fn(),
  useDeleteTireSpecification: vi.fn(),
}));
```

### Custom Render

Se utiliza un custom render que incluye todos los providers necesarios:

```typescript
import { render, screen, waitFor } from '@/test/test-utils';

render(<Component />); // Automáticamente incluye QueryClient y Router
```

## Mock Data

El archivo `src/test/test-utils.tsx` exporta mock data reutilizable:

- `mockTireBrand`
- `mockTireType`
- `mockTireReference`
- `mockTireSupplier`
- `mockTireSpecification`
- `mockTireSpecificationList`

## Notas Técnicas

### MUI DataGrid

El DataGrid de MUI genera warnings sobre "key" props en las acciones de fila. Esto es un issue conocido de MUI y no afecta la funcionalidad.

### Vitest vs Jest

Vitest es compatible con Jest pero más rápido y optimizado para Vite:
- Mismo API que Jest
- Integración nativa con Vite
- HMR para tests
- ESM support

### Async Operations

Para tests con operaciones async, siempre usar `waitFor()`:

```typescript
await waitFor(() => {
  expect(screen.getByText('FT-000001')).toBeInTheDocument();
});
```

## Próximos Pasos

Para expandir la cobertura de tests, se pueden agregar tests para:

1. **TireSpecificationForm** - tests del formulario de creación/edición
2. **TireSpecificationDetailDialog** - tests del modal de detalle
3. **TireSpecificationPage** - tests de integración de la página completa
4. **useTireSpecifications hook** - tests unitarios del hook

## Troubleshooting

### Error: "Cannot find module"
**Solución**: Verificar que los path aliases estén configurados en `vitest.config.ts`

### Error: "window.matchMedia is not a function"
**Solución**: Ya está mockeado en `src/test/setup.ts`

### Tests muy lentos
**Solución**: Usar `vi.fn()` en lugar de implementaciones reales para mocks

### act() warnings
**Solución**: Envolver operaciones async en `await waitFor()` o `await user.click()`
