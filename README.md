# EscapeMath

Juego educativo de plataformas 2D, desarrollado en Java (Swing), donde el jugador debe escapar de un laboratorio matemático resolviendo ejercicios de distintas áreas de las matemáticas.

## 📖 Descripción

Escape Math combina la mecánica de un juego de plataformas sencillo (movimiento y salto) con retos matemáticos interactivos. El jugador avanza a través de 5 salas temáticas en orden, cada una con 5 ejercicios distintos, recolectando fragmentos de una llave para completar cada sala y avanzar a la siguiente.

## ✨ Características

- **Sistema de cuentas**: registro, inicio de sesión y modo invitado.
- **5 salas temáticas**, con 5 ejercicios cada una:
  - Lógica proposicional
  - Sistemas numéricos
  - Álgebra básica
  - Expresiones algebraicas
  - Fracciones
- **Sistema de vidas e intentos** por ejercicio, con temporizador.
- **Sistema de puntaje**, con Top 3 de mejores puntajes guardado en base de datos.
- **Cinemática de introducción** y narrativa de escape del laboratorio.
- **Persistencia en MySQL**: usuarios y puntuaciones.

## 🛠️ Tecnologías utilizadas

- **Java** (Swing para la interfaz gráfica y el motor 2D del juego)
- **MySQL** (persistencia de usuarios y puntuaciones)
- JDBC (conector oficial `mysql-connector-j`)

## 🚀 Cómo ejecutarlo

### Requisitos
- JDK instalado (para compilar y ejecutar)
- MySQL Server en ejecución
- MySQL Workbench (opcional, para administrar la base de datos)

### Pasos
1. Clona este repositorio.
2. Ejecuta el script `escape_math.sql` en tu servidor MySQL para crear la base de datos y las tablas.
3. Edita `db.properties` con tus credenciales de conexión a MySQL.
4. Ejecuta `AbrirJuego.bat` (Windows) para compilar y abrir el juego automáticamente.

## 🎯 Controles

- Mover: `A` / `D` o flechas izquierda/derecha
- Saltar: `W`, flecha arriba o barra espaciadora

## 📄 Licencia

Proyecto educativo de uso libre para fines de aprendizaje.
