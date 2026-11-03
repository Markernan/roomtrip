# Manual de usuario — RoomTrip

> Funcionalidades disponibles al Lab 6. Las capturas de pantalla se agregan al cerrar el proyecto.

## Índice

1. [Cliente](#cliente)
2. [Administrador de hotel](#administrador-de-hotel)
3. [Taxista](#taxista)
4. [Superadmin](#superadmin)

---

## Acceso (todos los roles)

Desde el Lab 6 hay **un solo inicio de sesión**: *Iniciar sesión* abre la pantalla de Firebase
(correo y contraseña, o Google). Con correo, la primera vez hay que abrir el enlace de
verificación que llega al buzón y tocar *Ya verifiqué*. La app lee el rol de la cuenta y abre el
módulo que corresponde. Si la cuenta es nueva pregunta *Soy cliente* o *Soy taxista*. Los
administradores de hotel entran con el correo que el Superadmin indicó al registrar el hotel, y el
Superadmin se crea desde la consola de Firebase. La sesión queda abierta hasta tocar *Cerrar sesión*.

## Cliente

- **Registro:** *Regístrate* en el login (todos los campos son obligatorios; la foto es opcional).
  Con Google, elegir *Soy cliente* y completar el formulario.
- **Búsqueda de hoteles:** el inicio y la búsqueda muestran los hoteles activos. En el detalle se
  ven habitaciones, servicios (con el taxi gratuito y su monto mínimo) y opiniones.
- **Reserva:** elegir habitación y fechas; la app indica si están libres. *Reservar* lleva al pago
  con tarjeta **simulada** (solo se guardan los 4 últimos dígitos). No se puede reservar una
  habitación ocupada esas noches ni tener dos reservas activas que se crucen.
- **Mis reservas:** historial en tiempo real. *Contactar* abre el chat con el hotel mientras la
  reserva esté activa.
- **Checkout:** desde el día de entrada. Valoración (1 a 5 estrellas) y observaciones obligatorias.
  Si el total alcanza el monto mínimo del hotel, se puede pedir el **taxi gratis** al aeropuerto.
  El cobro final lo confirma el hotel y llega una notificación.
- **Taxi:** *Ver mi taxi* muestra el estado y el taxista asignado; al llegar, *Mostrar QR* para
  que el taxista cierre el servicio. El mapa en tiempo real llega en el Lab 7.
- **Perfil:** editar datos personales (el correo no se cambia) y cerrar sesión.

## Administrador de hotel

- **Acceso:** con el correo que el Superadmin registró para el hotel (ver *Acceso*).
- **Configuración del hotel:** ubicación, lugares históricos cercanos y monto mínimo para el taxi
  gratis. Las fotos son, por ahora, imágenes de demostración incluidas en la app (subir fotos
  propias requiere Firebase Storage).
- **Inventario:** registrar habitaciones (tipo, capacidad, área, precio) y servicios.
- **Traslados:** estado en tiempo real de los taxis que salen del hotel.
- **Checkouts:** cobrar los checkouts pendientes, con cobro adicional por daños (monto, motivo y
  observación obligatorios). El cliente recibe la notificación del cobro.
- **Chats:** conversaciones con los clientes de reservas activas.
- **Valoraciones y reportes:** opiniones de los clientes e ingresos del hotel.

## Taxista

- Autoregistro en la app web y habilitación por el Superadmin
- **Acceso a la app móvil (Lab 6):** iniciar sesión (ver *Acceso*) y, si la cuenta es nueva,
  elegir *Soy taxista*. Hasta que el Superadmin apruebe la solicitud, la app lo indica y no deja entrar.
- **Consulta y aceptación de pedidos:** la lista de *Pasajeros solicitando viaje* se actualiza sola.
  Hay que tener activado *Disponible*. Si otro taxista acepta el pedido primero, la app lo avisa.
- **Actualización de estados del servicio:** *Iniciar ruta al hotel* (En camino) y *Llegué al
  encuentro* (En traslado). Si se cierra la app, al volver se retoma el servicio en curso.
- **Cierre del servicio por escaneo del código QR:** *Escanear QR* registra el servicio como
  Finalizado (la lectura con cámara todavía es simulada).
- **Perfil:** editar datos personales y la placa; ver el historial de servicios finalizados.

## Superadmin

- **Panel:** totales de usuarios, hoteles y taxistas por aprobar, y *Cargar datos de demostración* (3 hoteles con habitaciones y
  servicios, solo si aún no hay hoteles).
- **Gestión de usuarios:** activar o desactivar cuentas. Una cuenta desactivada no puede entrar.
- **Registro de hoteles:** datos del hotel y correo de su administrador. La app crea una invitación:
  cuando esa persona inicia sesión con ese correo, queda como administrador del hotel.
  En *Hoteles* se activa o desactiva cada hotel (los inactivos no aparecen al cliente).
- **Aprobación de taxistas:** aprobar o rechazar las solicitudes pendientes.
- **Reportes:** reservas y montos por hotel.
- **Auditoría:** eventos registrados por todos los roles (colección `logs`).
