-- Usuario
CREATE TABLE IF NOT EXISTS Usuario (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono INT NOT NULL,
    direccion VARCHAR(100) NOT NULL,
    ciudad VARCHAR(50) NOT NULL,
    codigoPostal INT NOT NULL,
    dni VARCHAR(9) UNIQUE NOT NULL,
    fechaCaducidadDni DATE NOT NULL,
    fechaCarnetConducir DATE NOT NULL,
    administrador BOOLEAN DEFAULT FALSE NOT NULL,
    esArrendador BOOLEAN DEFAULT FALSE NOT NULL,
    esArrendatario BOOLEAN DEFAULT FALSE NOT NULL,
    imagen VARCHAR(255) NULL
);

-- Cuenta
CREATE TABLE IF NOT EXISTS Cuenta (
    id SERIAL PRIMARY KEY,
    idUsuario INT REFERENCES Usuario(id) NOT NULL,
    numeroCuenta VARCHAR(50) UNIQUE NOT NULL,
    saldo DECIMAL(10,2) NOT NULL
);

-- Pago
CREATE TABLE IF NOT EXISTS Pago (
    id SERIAL PRIMARY KEY,
    titular VARCHAR(50) NOT NULL,
    numeroTarjeta VARCHAR(16) NOT NULL,
    idUsuario INT REFERENCES Usuario(id) NOT NULL
);

-- Marca
CREATE TABLE IF NOT EXISTS Marca (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL
);

-- Modelo
CREATE TABLE IF NOT EXISTS Modelo (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL,
    idMarca INT REFERENCES Marca(id) NOT NULL
);

-- Transmision
CREATE TABLE IF NOT EXISTS Transmision (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL
);

-- Categoria
CREATE TABLE IF NOT EXISTS Categoria (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL,
    descripcion VARCHAR(255) NULL
);

-- Color
CREATE TABLE IF NOT EXISTS Color (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) UNIQUE NOT NULL
);

-- Vehiculo
CREATE TABLE IF NOT EXISTS Vehiculo (
    id SERIAL PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    imagen VARCHAR(255) NOT NULL,
    matricula VARCHAR(7) UNIQUE NOT NULL,
    kilometraje INT NOT NULL,
    anyoFabricacion INT NOT NULL,
    capacidadPasajeros INT NOT NULL,
    capacidadMaletero INT NOT NULL,
    numeroPuertas INT NOT NULL,
    numeroMarchas INT NOT NULL,
    aireAcondicionado BOOLEAN DEFAULT FALSE NOT NULL,
    enMantenimiento BOOLEAN DEFAULT FALSE NOT NULL,
    oferta INT NULL,
    precioPorDia DECIMAL(10,2) NOT NULL,
    precioPorMedioDia DECIMAL(10,2) NOT NULL,
    precioCombustible DECIMAL(10,2) NOT NULL,
    idCategoria INT REFERENCES Categoria(id),
    idMarca INT REFERENCES Marca(id),
    idModelo INT REFERENCES Modelo(id),
    idTransmision INT REFERENCES Transmision(id),
    idColor INT REFERENCES Color(id),
    idUsuario INT REFERENCES Usuario(id)
);

-- Comentario
CREATE TABLE IF NOT EXISTS Comentario (
    id SERIAL PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    fechaCreacion DATE NOT NULL,
    idVehiculo INT REFERENCES Vehiculo(id) NOT NULL,
    idUsuario INT REFERENCES Usuario(id) NOT NULL
);

-- Alquiler
CREATE TABLE IF NOT EXISTS Alquiler (
    id SERIAL PRIMARY KEY,
    fechaCreacion DATE NOT NULL,
    fechaEntrega DATE NOT NULL,
    fechaDevolucion DATE NOT NULL,
    precioFinal DECIMAL(10,2) NOT NULL,
    litrosCombustible DECIMAL(10,2) NULL,
    idVehiculo INT REFERENCES Vehiculo(id) NOT NULL,
    idPago INT REFERENCES Pago(id) NOT NULL
);

-- Mensaje
CREATE TABLE IF NOT EXISTS Mensaje (
    id SERIAL PRIMARY KEY,
    remitente_id INT REFERENCES Usuario(id) NOT NULL,
    destinatario_id INT REFERENCES Usuario(id) NOT NULL,
    contenido TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL
);
