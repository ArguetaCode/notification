# notification-service

Microservicio de notificaciones de CampusEnroll HA. Consume eventos de pago publicados por `billing-service` en RabbitMQ, crea notificaciones en PostgreSQL y usa Redis para idempotencia de eventos.

## Stack

- Java 17
- Maven
- Spring Boot 3.3.5
- PostgreSQL
- RabbitMQ
- Redis
- Spring Boot Actuator
- Prometheus (Micrometer)
- Docker

## Ejecución local

1. Compilar:

```bash
mvn clean compile
```

2. Ejecutar:

```bash
mvn spring-boot:run
```

3. Servicio disponible en:

- `http://localhost:8084`

## Endpoints

- `GET /health`
- `GET /students/{studentId}/notifications`
- `PATCH /notifications/{id}/read`

### Ejemplos rápidos

```bash
curl http://localhost:8084/health
```

```bash
curl http://localhost:8084/students/1/notifications
```

```bash
curl -X PATCH http://localhost:8084/notifications/1/read
```

## RabbitMQ

- Exchange: `campusenroll.payments` (tipo `direct`)
- Queue: `campusenroll.notifications.payments`
- Routing keys consumidas:
- `payment.approved`
- `payment.failed`

El listener del servicio consume mensajes desde `campusenroll.notifications.payments` y crea notificaciones según el tipo de evento:

- `payment.approved` -> `PAYMENT_APPROVED`
- `payment.failed` -> `PAYMENT_FAILED`

## Redis e idempotencia

Para evitar reprocesar eventos duplicados se usa la key:

- `notification:event:{eventId}`

TTL configurado: 10 minutos. Si la key ya existe, el evento se ignora.

## Evidencia esperada

- El servicio inicia en puerto `8084`.
- Se crean/bindean exchange y cola en RabbitMQ.
- Al publicar `payment.approved` o `payment.failed`, se inserta una fila en `campusenroll.notifications`.
- Si se reenvía el mismo `eventId` dentro de 10 minutos, no se duplica la notificación.
- `GET /students/{studentId}/notifications` devuelve notificaciones ordenadas por `createdAt` descendente.
- `PATCH /notifications/{id}/read` actualiza estado a `READ`.

## Docker

Construir imagen:

```bash
docker build -t notification-service:local .
```

Ejecutar contenedor:

```bash
docker run --rm -p 8084:8084 --name notification-service \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:55432/campusenroll \
  -e SPRING_DATASOURCE_USERNAME=campus \
  -e SPRING_DATASOURCE_PASSWORD=campus123 \
  -e SPRING_RABBITMQ_HOST=host.docker.internal \
  -e SPRING_RABBITMQ_PORT=5672 \
  -e SPRING_RABBITMQ_USERNAME=campus \
  -e SPRING_RABBITMQ_PASSWORD=campus123 \
  -e SPRING_REDIS_HOST=host.docker.internal \
  -e SPRING_REDIS_PORT=6379 \
  notification-service:local
```
