# Decisiones de arquitectura

Documento vivo. Cada sección explica una decisión, la alternativa que se descartó y el motivo.
Sirve de insumo para el entregable "Archivo de arquitectura" del plan de proyecto.

## 1. Por qué Spring Boot y no MERN

El sílabo del curso deja las dos puertas abiertas: la bibliografía obligatoria incluye
*Beginning Spring Boot 2* y *Pro MERN Stack*, y el Anexo D del ERS lo registra como el asunto
abierto A2. La decisión se tomó por tres razones concretas:

1. El equipo ya escribe Java para la app Android nativa. Un solo lenguaje significa que cualquiera
   puede entrar a cualquier parte del proyecto, que es lo que más pesa en un equipo de cinco
   personas con un solo ciclo de plazo.
2. IntelliJ IDEA aparece como herramienta del curso en el sílabo, junto a Android Studio. IntelliJ
   para un proyecto Node sería una elección rara; para Spring Boot es la elección natural.
3. La Unidad 5 del curso es "Introducción a los microservicios / Gateway y resiliencia /
   Despliegue de microservicios". Spring Cloud Gateway y Resilience4j son, literalmente, la
   implementación de referencia de esos tres temas. Sustentar el proyecto ante el jurado es más
   directo si el código usa los mismos nombres que las diapositivas.

Contraargumento honesto: MERN arranca más rápido y tiene más tutoriales en video. Si el equipo
tuviera experiencia previa en Node y ninguna en Spring, la decisión debería invertirse.

## 2. Por qué MongoDB Atlas y no Firestore

El RF-WTX-008 exige que este subsistema tenga base de datos propia e independiente de la que usa
la app móvil. Como la app móvil usa Firebase, usar Firestore aquí también haría confusa esa
separación ante el jefe de práctica, aunque técnicamente fueran proyectos distintos.

MongoDB además cierra la Unidad 3 del curso, que cubre NoSQL "con dos proveedores, MongoDB y
Firebase". Usar los dos en el mismo proyecto demuestra las dos rutas.

El tier M0 de Atlas da 512 MB y es gratis para siempre sin tarjeta, de sobra para un proyecto de
curso: el padrón de taxistas son decenas de documentos.

**Database per service.** `taxista-service` escribe en `taxi_registry` y `calificacion-service`
en `taxi_ratings`. Son dos bases lógicas dentro del mismo clúster M0 gratuito. Ninguno de los dos
servicios lee la base del otro; si necesitan un dato, se lo piden por HTTP. Es el patrón correcto
y no cuesta nada más.

## 3. Por qué este corte de microservicios

El subsistema tiene solo nueve requisitos funcionales. Partirlo en seis servicios sería
sobreingeniería evidente y el jurado lo notaría. Partirlo en cero servicios incumpliría la
restricción RES-10. El corte elegido son dos servicios de dominio más un gateway, y se justifica
por cómo crecen los datos:

- El **padrón de taxistas** es pequeño, estable y muy leído (cada solicitud de taxi lo consulta).
- Las **calificaciones** crecen sin límite, una por servicio prestado, y casi solo se escriben.

Son dos perfiles de carga distintos, que es la razón de libro para separar servicios. Separarlos
permite, por ejemplo, subir las instancias de `taxista-service` sin tocar el otro.

El `portal-web` es un cuarto deployable pero no es un microservicio de dominio: no tiene base de
datos y consume la misma API pública que la app Android. Eso es deliberado: si el portal funciona,
el contrato de la API está probado por dos clientes distintos y cualquier hueco aparece temprano.

## 3.1 Sincronizacion de valoracionPromedio

`Taxista.valoracionPromedio` es una copia de solo lectura, no la fuente de verdad (esa vive en
`taxi_ratings`, en `calificacion-service`). Cada `POST /api/calificaciones` recalcula el promedio
del taxista y lo empuja de inmediato con un `PATCH /api/taxistas/{id}/valoracion`, autenticado con
el rol `SERVICIO_INTERNO` de la seccion 5.

Es una llamada best-effort a proposito (`TaxistaClient.actualizarValoracion` atrapa cualquier error
y solo lo registra en el log): si taxista-service estuviera caido en ese instante, la calificacion
ya quedo guardada en su propia base y no hay razon para fallar esa operacion por un problema de
sincronizacion de una copia. El padron del Superadmin puede mostrar un promedio desactualizado por
unos segundos hasta el siguiente evento; es el mismo trade-off de consistencia eventual que ya
describe la seccion 2 para database-per-service.

## 4. Por qué el frontend es server-side

Una SPA en React se vería más moderna, pero agrega un segundo despliegue, configuración de CORS,
manejo de tokens en el navegador y un build de JS que mantener. Para nueve requisitos que son
básicamente dos formularios y una tabla, Thymeleaf entrega lo mismo con una fracción del riesgo,
y el token vive en la sesión del servidor en lugar de quedar expuesto a scripts en la página.

El RNF-POR-004 solo pide que funcione en Chrome, Edge y Firefox. Bootstrap 5 lo resuelve.

## 5. Autenticación

`taxista-service` emite JWT firmados en HS256 con un secreto compartido. El gateway los valida en
el borde y cada microservicio los vuelve a validar: defensa en profundidad, para que exponer un
servicio por error no abra un hueco.

Cuatro roles viajan en el claim `roles`:

- `SUPERADMIN`: aprueba y rechaza solicitudes desde el portal.
- `TAXISTA`: ve y gestiona su propio registro.
- `APP_MOVIL`: credencial de servicio que usa la app Android. Así la app no consume la API sin
  autenticar, que sería el hueco más obvio del sistema.
- `SERVICIO_INTERNO`: lo usa exclusivamente `calificacion-service` para llamar a
  `PATCH /api/taxistas/{id}/valoracion` en `taxista-service` (RN-013, ver sección 3). No lo emite
  `taxista-service` para nadie más: `calificacion-service` firma su propio token de un solo uso con
  el mismo secreto compartido, vía `InternalTokenService`, cada vez que necesita empujar el
  promedio recalculado. Es la misma idea de "database per service, se piden los datos por HTTP"
  aplicada a una escritura en lugar de una lectura.

Las contraseñas se guardan con BCrypt, nunca en texto plano (requisito de calidad del plan de
proyecto).

**Mejora pendiente.** Lo más elegante sería que la app Android mande su token de Firebase Auth y
el gateway lo valide contra el JWKS público de Google, federando los dos sistemas de identidad.
Se dejó para después porque HS256 con credencial de servicio ya cumple el requisito y se entiende
en cinco minutos; la federación se puede agregar sin tocar los microservicios de dominio.

## 6. Resiliencia

El RF-TAX-018 y el RNF-FIA-001 piden que la caída de esta API no rompa las reservas de la app
móvil. Se resuelve en dos lugares:

- **Aquí**: Resilience4j en el gateway, con timeout de 4 s y circuito que se abre cuando falla más
  del 50 % de las últimas llamadas. Con el circuito abierto, el gateway responde un 503 con
  mensaje legible al instante, sin seguir golpeando el servicio caído.
- **En la app Android**: timeout en OkHttp y un mensaje al usuario cuando llega el 503. Sin esta
  mitad, el requisito no está cumplido aunque el gateway se comporte bien.

## 7. Riesgos abiertos

| Riesgo | Mitigación |
|---|---|
| Arranque en frío de Cloud Run: la JVM tarda unos segundos si el servicio escaló a cero | Poner `--min-instances=1` en el gateway el día de la demo y devolverlo a 0 después |
| El tier M0 de Atlas tiene 512 MB y 100 operaciones por segundo | Suficiente para el curso; queda documentado en el OPEX qué costaría el siguiente tier |
| El secreto JWT compartido entre cuatro servicios | Vive en Secret Manager, nunca en el repositorio; rotarlo obliga a redesplegar los cuatro |
| A2 del ERS sin confirmar por el coordinador | Esta decisión se toma y se documenta; si el profesor exige MERN, el corte de servicios y los contratos REST se mantienen y solo cambia la implementación |
