-- ============================================
-- Escape Math - Script de creacion de base de datos
-- Ejecutar una sola vez antes de usar el juego
-- ============================================

CREATE DATABASE IF NOT EXISTS escape_math CHARACTER SET utf8mb4;

USE escape_math;

CREATE TABLE IF NOT EXISTS puntuaciones (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    usuario            VARCHAR(50)  NOT NULL,
    puntaje            INT          NOT NULL,
    retos_completados  INT          NOT NULL,
    resultado          VARCHAR(20)  NOT NULL,   -- 'GANO' o 'PERDIO'
    fecha              DATETIME     DEFAULT CURRENT_TIMESTAMP
);

-- (Opcional) Consulta para ver el Top 3 manualmente:
-- SELECT usuario, puntaje, resultado, fecha
-- FROM puntuaciones
-- ORDER BY puntaje DESC, fecha ASC
-- LIMIT 3;
