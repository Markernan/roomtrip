# taxi-service

App web de gestión de taxistas + API REST que consume la app móvil.
Persistencia propia en **MongoDB**, separada de Firebase.

> Esta carpeta es un marcador. El stack todavía **no** está decidido.

## Decisión pendiente

Node/Express o Spring Boot. La elección condiciona el proveedor de despliegue en la nube,
que es uno de los entregables finales del proyecto.

## Responsabilidades

- Autoregistro del taxista: nombres, apellidos, tipo y número de documento, fecha de
  nacimiento, correo, teléfono, domicilio, foto, placa del auto y foto del vehículo.
- Habilitación del taxista por parte del Superadmin.
- Exponer por API REST la información de taxistas que necesita la app móvil.

## Restricción dura

La app móvil **nunca** accede directamente a esta base de datos. Toda información de
taxistas pasa por la API REST.

## Contrato de la API

Por definir. Documentar aquí los endpoints, sus códigos de respuesta y el formato de
error, porque `TaxiRepository` en la app móvil depende de ellos.
