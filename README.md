# Tic_Tac_Toe_Online - Aplicación Cliente para Juego Multijugador

Este proyecto presenta una aplicación cliente diseñada para jugar al Tres en Raya con la capacidad de participar en partidas en línea con otros jugadores. Utilizando sockets UDP para la comunicación con un servidor central y sockets TCP para establecer conexiones directas entre los jugadores, la aplicación ofrece una experiencia de juego fluida y colaborativa.

## Características Destacadas

1. **Creación de Partidas:**
   - Facilita a un jugador la iniciación de una nueva partida con la opción de configurar el puerto para la conexión. El servidor central se encarga de asignar un puerto disponible de manera eficiente.

2. **Unirse a Partidas Existentes:**
   - Brinda la posibilidad a un jugador de unirse a una partida en curso proporcionando la dirección IP y el puerto de la misma.

3. **Juego Multijugador:**
   - Implementa un emocionante juego de Tres en Raya, donde dos jugadores realizan movimientos por turnos. La comunicación directa entre los clientes se logra mediante sockets TCP, asegurando una interactividad sin problemas.

4. **Gestión de Conexiones:**
   - Administra las conexiones de manera eficaz mediante el uso de sockets para la comunicación entre el cliente y el servidor central, así como entre los propios clientes.

## Instrucciones de Configuración:

1. **Requisitos Previos:**
   - Asegúrate de tener Java instalado en tu sistema.

2. **Clonar el Repositorio:**
   - Clona el proyecto desde el repositorio de GitHub: [enlace del repositorio].

3. **Explorar el Código:**
   - Examina detenidamente el código fuente en `AplicacionCliente.java`. Comprende cómo se establecen las conexiones, cómo se manejan los turnos y cómo se gestiona la lógica del juego.

4. **Ejecutar la Aplicación:**
   - Compila y ejecuta `AplicacionCliente.java` desde tu entorno de desarrollo preferido.

5. **Personalización y Mejoras:**
   - Anima a personalizar la aplicación agregando nuevas funcionalidades, mejorando la interfaz de usuario o implementando reglas adicionales para enriquecer la experiencia de juego.

## Notas Importantes:

- La dirección IP del servidor central es `127.0.0.1`, y utiliza el puerto `7879`.
- Los puertos para la conexión TCP se encuentran en el rango de `8000` a `8999`.

Tic_Tac_Toe_Online es un proyecto de aprendizaje explora las características y desarrolla con Tic_Tac_Toe_Online.
