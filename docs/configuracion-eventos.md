Quiero refactorizar el flujo de registro de usuarios de este microservicio `identity-service` para que la actualización del Read Model de MongoDB se realice mediante eventos Kafka, respetando DDD, CQRS y arquitectura hexagonal.

## Contexto actual

Actualmente tengo un caso de uso similar a este:

```java
@Transactional
public AuthUserResponse execute(RegisterUserCommand command) {
    if (userRepository.findByEmail(command.email()).isPresent()) {
        throw new EmailAlreadyRegisteredException(command.email());
    }

    Password.assertValidRaw(command.password());

    Password hashedPassword =
            Password.fromHashed(passwordHasher.hash(command.password()));

    User user = User.create(
            command.email(),
            command.firstName(),
            command.lastName(),
            hashedPassword
    );

    User saved = userRepository.save(user);

    userProjectionPort.save(
        UserReadModels.from(saved)
    );

    return AuthUserResponse.from(saved);
}
```

El problema es que:

* `userRepository.save(user)` persiste el Write Model en PostgreSQL.
* `userProjectionPort.save(...)` persiste el Read Model en MongoDB.
* Todo ocurre dentro del mismo método `@Transactional`.
* MongoDB local está ejecutándose como standalone.
* Quarkus intenta involucrar MongoDB en la transacción y Mongo devuelve errores relacionados con `commitTransaction`.
* Arquitectónicamente tampoco quiero que el Command Handler actualice directamente MongoDB.

## Objetivo

Refactoriza este flujo para obtener esta arquitectura:

```text
RegisterUserCommand
        ↓
RegisterUserUseCase
        ↓
Domain User Aggregate
        ↓
PostgreSQL
        ↓
UserCreatedEvent
        ↓
Kafka
        ↓
UserCreatedConsumer
        ↓
UserProjectionPort
        ↓
MongoDB
```

PostgreSQL debe seguir siendo el Write Model.

MongoDB debe ser exclusivamente el Read Model.

Kafka debe desacoplar ambos modelos.

---

# Reglas arquitectónicas obligatorias

## 1. RegisterUserUseCase

`RegisterUserUseCase` NO debe llamar directamente a:

```java
userProjectionPort.save(...)
```

Debe encargarse únicamente de:

1. validar el command;
2. comprobar que el email no exista;
3. crear el aggregate `User`;
4. guardar el usuario mediante `UserRepository`;
5. publicar un evento `UserCreatedEvent`;
6. devolver `AuthUserResponse`.

Mantener `@Transactional` exclusivamente para las operaciones relacionadas con PostgreSQL.

No introducir dependencias de MongoDB en este caso de uso.

---

## 2. Crear un evento de aplicación/integración

Crear un evento:

```text
UserCreatedEvent
```

Debe contener únicamente los datos necesarios para construir el Read Model.

Por ejemplo:

```java
public record UserCreatedEvent(
    UUID userId,
    String email,
    String firstName,
    String lastName,
    Instant createdAt
) {
}
```

Ajustar los campos a los que realmente tenga actualmente el aggregate `User`.

NO incluir:

* password;
* passwordHash;
* información sensible innecesaria;
* entidades JPA;
* documentos Mongo;
* clases de infraestructura.

El evento debe ser serializable por Kafka/Jackson.

---

## 3. Puerto para publicar eventos

No quiero que `RegisterUserUseCase` conozca directamente Kafka.

Crear un puerto en application/domain, siguiendo la arquitectura existente, por ejemplo:

```java
public interface UserEventPublisher {
    void publish(UserCreatedEvent event);
}
```

o un nombre equivalente coherente con el proyecto.

El caso de uso debe depender únicamente de esta interfaz.

No importar:

```java
org.eclipse.microprofile.reactive.messaging.*
```

dentro de application/domain.

Kafka pertenece a infraestructura.

---

## 4. Kafka Producer Adapter

Crear una implementación del puerto en infraestructura.

Por ejemplo conceptualmente:

```text
infrastructure
└── messaging
    └── kafka
        ├── producer
        │   └── KafkaUserEventPublisher
        └── consumer
            └── UserCreatedConsumer
```

La implementación puede usar Quarkus SmallRye Reactive Messaging.

Ejemplo conceptual:

```java
@ApplicationScoped
public class KafkaUserEventPublisher implements UserEventPublisher {

    @Channel("user-created-out")
    Emitter<UserCreatedEvent> emitter;

    @Override
    public void publish(UserCreatedEvent event) {
        emitter.send(event);
    }
}
```

Adapta esto correctamente a las dependencias y convenciones que YA existen en el proyecto.

No agregues librerías redundantes si Quarkus ya tiene soporte Kafka instalado.

---

## 5. Kafka Consumer

Crear un consumer encargado exclusivamente de procesar `UserCreatedEvent`.

Conceptualmente:

```java
@ApplicationScoped
public class UserCreatedConsumer {

    private final UserProjectionPort userProjectionPort;

    public UserCreatedConsumer(UserProjectionPort userProjectionPort) {
        this.userProjectionPort = userProjectionPort;
    }

    @Incoming("user-created-in")
    public void consume(UserCreatedEvent event) {
        userProjectionPort.save(
            UserReadModels.from(event)
        );
    }
}
```

El consumer:

* recibe el evento;
* construye el Read Model;
* llama a `UserProjectionPort`;
* persiste en MongoDB.

NO debe modificar PostgreSQL.

NO debe ejecutar lógica propia del Command Handler.

---

## 6. UserReadModels

Actualmente existe algo similar a:

```java
UserReadModels.from(saved)
```

Modificarlo o agregar una sobrecarga para que pueda construir la proyección desde:

```java
UserCreatedEvent
```

Por ejemplo:

```java
UserReadModels.from(event)
```

El consumer no debería necesitar cargar nuevamente el usuario desde PostgreSQL.

El evento debe contener la información suficiente para crear la proyección.

---

## 7. Kafka Configuration

Revisar el `application.properties` o `application.yml` actual y agregar solamente la configuración necesaria.

Utilizar un topic claro, por ejemplo:

```text
identity.user.created
```

Configurar canales equivalentes a:

```properties
mp.messaging.outgoing.user-created-out.connector=smallrye-kafka
mp.messaging.outgoing.user-created-out.topic=identity.user.created

mp.messaging.incoming.user-created-in.connector=smallrye-kafka
mp.messaging.incoming.user-created-in.topic=identity.user.created
mp.messaging.incoming.user-created-in.group.id=identity-user-projection
```

Agregar serializers/deserializers únicamente si son necesarios con la versión actual de Quarkus.

Antes de modificar dependencias, revisar el `pom.xml`.

---

# Muy importante: NO romper la arquitectura existente

El proyecto utiliza:

* Java 21
* Quarkus
* DDD
* CQRS
* arquitectura hexagonal / Ports & Adapters
* PostgreSQL + Hibernate/Panache para Write Model
* MongoDB + Panache para Read Model
* Kafka para comunicación por eventos

Respeta la estructura existente del repositorio.

Antes de crear nuevas carpetas/clases:

1. inspecciona la estructura actual;
2. identifica los paquetes de:

   * domain;
   * application;
   * infrastructure;
   * ports;
   * adapters;
3. reutiliza las convenciones existentes.

NO reestructures todo el proyecto.

Haz cambios mínimos y coherentes.

---

# Evitar este antipatrón

NO hacer:

```java
@Transactional
public AuthUserResponse execute(...) {
    User saved = userRepository.save(user);

    userProjectionPort.save(...);

    return ...;
}
```

Tampoco simplemente llamar un nuevo servicio Mongo desde el mismo método transaccional.

Quiero desacoplamiento real mediante Kafka.

---

# Transacciones

La transacción de PostgreSQL debe cubrir únicamente el Write Model.

MongoDB debe ejecutarse cuando el consumer reciba el evento.

No intentar compartir una transacción entre:

```text
PostgreSQL + MongoDB
```

No crear transacciones Mongo innecesarias.

---

# Consistencia eventual

Aceptar explícitamente consistencia eventual.

Después de:

```text
POST /auth/register
```

puede existir una ventana muy pequeña donde:

```text
PostgreSQL = usuario existente
MongoDB    = proyección todavía no creada
```

Eso es esperado en esta arquitectura.

No introducir llamadas síncronas Mongo para intentar evitarlo.

---

# Idempotencia

El consumer debe ser preparado para recibir potencialmente el mismo evento más de una vez.

Antes de insertar ciegamente, analizar el `UserProjectionPort` actual y hacer que la operación sea idempotente.

Idealmente:

```text
userId
```

debe ser el identificador estable del documento/proyección.

Si el documento ya existe, actualizarlo/upsert en lugar de crear duplicados.

No crear duplicados de usuarios en MongoDB por eventos repetidos.

---

# Manejo de errores

No ocultar errores con:

```java
try {
   ...
} catch(Exception ignored) {
}
```

Si el consumer falla guardando Mongo:

* Kafka debe poder reintentar el procesamiento según la configuración;
* no perder silenciosamente el evento;
* preparar el código para posteriormente implementar DLQ si fuese necesario.

No implementar infraestructura excesiva todavía si el proyecto no la necesita.

---

# Fase actual

En esta primera implementación NO quiero todavía implementar Transactional Outbox.

Quiero primero:

```text
PostgreSQL
    ↓
Kafka
    ↓
MongoDB
```

Pero deja una separación limpia para poder evolucionar posteriormente hacia:

```text
PostgreSQL
    ↓
Transactional Outbox
    ↓
Kafka
    ↓
MongoDB
```

No mezclar la implementación actual con Outbox todavía.

---

# Resultado esperado

Después del refactor quiero poder seguir haciendo:

```http
POST /auth/register
```

y que ocurra:

```text
1. RegisterUserCommand
2. RegisterUserUseCase
3. PostgreSQL guarda User
4. UserCreatedEvent se publica en Kafka
5. la transacción PostgreSQL termina
6. Kafka entrega UserCreatedEvent
7. UserCreatedConsumer procesa el evento
8. UserProjectionPort guarda/upsertea el Read Model
9. MongoDB contiene la proyección
```

El endpoint debe continuar devolviendo:

```text
201 Created
```

sin esperar que MongoDB termine de construir la proyección.

---

# Antes de modificar código

Primero analiza:

* `RegisterUserUseCase`
* `UserRepository`
* `UserProjectionPort`
* implementación Mongo de `UserProjectionPort`
* `UserReadModels`
* configuración Kafka actual
* `pom.xml`
* estructura de paquetes
* cualquier producer/consumer Kafka ya existente

Después dime brevemente qué archivos vas a crear/modificar.

Luego realiza la implementación.

Finalmente:

1. compila el proyecto;
2. ejecuta los tests existentes;
3. corrige errores de compilación;
4. verifica configuración Kafka;
5. comprueba que `RegisterUserUseCase` ya no dependa de MongoDB;
6. comprueba que Mongo solo sea actualizado por el consumer;
7. no elimines funcionalidad existente que no esté relacionada con este cambio.
