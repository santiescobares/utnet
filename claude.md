# Contexto del Proyecto
Estás asistiendo en el desarrollo Full-Stack de **UTNet**, una plataforma web integral open-source para estudiantes universitarios. El sistema incluye foros de discusión, repositorio de apuntes, reseñas de profesores, información de carreras y cursos, y gestión de perfiles de usuario

- La información completa del proyecto está en `docs/info.txt`
- El stack completo que se utilizará está en `docs/stack.txt`
- La arquitectura de entidades y sus tablas esta en `docs/tablas.txt`

# Consideraciones del Stack
**Backend:**
- **Lenguaje:** Java 21+ (usar Records, pattern matching)
- **Framework:** Spring Boot 3.x (Web, Data JPA, Security)
- **Base de Datos:** PostgreSQL
- **Caché y Sesiones:** Redis
- **Almacenamiento:** Cloudflare R2 (AWS SDK v2)
- **Mapeo:** MapStruct (`componentModel = "spring"`)

**Frontend:**
- **Librería/Framework:** React con Vite
- **Lenguaje:** TypeScript (Tipado estricto obligatorio)
- **Estilos:** Tailwind CSS
- **Consumo de API:** Axios
- **Manejo de Estado:** React Query (TanStack Query) para datos del servidor, Zustand/Context para estado global local

# Reglas Arquitectónicas: Backend
1. **Capas Estrictas:** `Controller` (solo HTTP y validaciones) -> `Service` (lógica y `@Transactional`) -> `Repository` (Spring Data). Crear eventos de Spring Boot para CUD 
2. **Transferencia de Datos:** Nunca exponer Entidades. Usar `Records` para DTOs y MapStruct para conversiones.
3. **Inyección:** Usar `@RequiredArgsConstructor` de Lombok. Cero `@Autowired` en campos a menos que se requiera en Mapper
4. **Borrado Lógico:** No hacer `DELETE` físico. Usar campo `deleted_at` y `@SQLRestriction`
5. **IDs:** UUID o Long como clave primaria unívoca

# Reglas Arquitectónicas: Frontend
1. **Autenticación y Seguridad (¡CRÍTICO!):**
   - El backend utiliza JWT almacenado en **Cookies HttpOnly**. 
   - El frontend NUNCA debe intentar leer el token ni enviarlo en el header `Authorization`
   - Todas las peticiones a la API deben incluir `withCredentials: true` (en Axios)
2. **Estructura de Componentes:**
   - Usar Functional Components y Hooks
   - Separar componentes de UI puros (Dumb Components) de los componentes que traen datos (Smart Components).
3. **Tipado Estricto (TypeScript):**
   - Todo DTO del backend debe tener su correspondiente `interface` o `type` exacto en el frontend
   - No usar `any`

# Manejo de Errores
1. **Backend:** Lanzar excepciones personalizadas de dominio. Dejar que el `@ControllerAdvice` las formatee en el `ExceptionResponseDTO` estándar
2. **Frontend:**
   - Capturar siempre los errores de la API.
   - Leer el objeto `ExceptionResponseDTO` (que contiene `errorCode` y `message`) para mostrar *Toasts* o mensajes de error amigables al usuario
3. **Diccionario:** Generar/Agregar un errorDictionary que mapee todos los errorCodes del backend a frases amigables en español

# Diseño de Código
**Paso 1:** Por cada feature solicitada, analizar (si existe) y entender el flujo completo de relaciones, transacciones, comunicaciones y dependencias requeridas
**Paso 2:** Diseñar un plan de implementación robusto y escalable, creando componentes reutilizables, adaptativos y visualmente legibles
**Paso 3:** Asegurar que se utilizaron buenas practicas, alta cohesión, bajo acoplamiento. Comentar solo líneas de funcionalidades muy complejas
**Nota:** NUNCA modificar clases o archivos del backend que no hayan sido creados por ti sin previa autorización del usuario

# Estilo de Código General
- Diseño frontend "Mobile First" y soporte PWA
- Nombres de variables y funciones descriptivos en **inglés**
- Mantener orden de definición de variables entre entidades, services, DTOs y demás clases
- Funciones cortas con una única responsabilidad (SOLID)
- Respetar patrones de diseño estándar de la industria
- Extraer lógicas y componentes reutilizables en Custom Hooks, Cards, Modals (Frontend), Utility Classes o métodos privados (Backend)
- Mantener estética minimalista y estudiantil en el frontend, usar iconos, mantener consistencia de colores: azul eléctrico y blanco (o negro al 90% para Dark Mode). Usar lenguaje español argentino