# 📱 Datos Precargados para App Móvil

## ✅ ¿Qué son los datos precargados?

Los datos precargados son información (juegos, categorías, géneros) que se cargan automáticamente en la base de datos cuando inicias los microservicios. Esto permite que tu app móvil tenga contenido desde el primer momento.

## 🎮 Datos Incluidos

### Juegos de Ejemplo (12 juegos)
- The Witcher 3: Wild Hunt
- Cyberpunk 2077
- FIFA 24
- Call of Duty: Modern Warfare
- Assassin's Creed Valhalla
- Minecraft
- Grand Theft Auto V
- The Legend of Zelda: Breath of the Wild
- Counter-Strike 2
- Red Dead Redemption 2
- Elden Ring
- God of War

### Categorías (5 categorías)
- Acción
- Aventura
- RPG
- Deportes
- Estrategia

### Géneros (5 géneros)
- Acción
- Aventura
- RPG
- Deportes
- Estrategia

## 📡 Cómo Consumir los Datos desde la App Móvil

### 1. Obtener Todos los Juegos

**Endpoint:** `GET http://localhost:3002/api/games`

**Ejemplo en Flutter/Dart:**
```dart
Future<List<Game>> getGames() async {
  final response = await http.get(
    Uri.parse('http://localhost:3002/api/games'),
    headers: {'Content-Type': 'application/json'},
  );
  
  if (response.statusCode == 200) {
    final data = json.decode(response.body);
    final gamesList = data['_embedded']['gameResponseList'] as List;
    return gamesList.map((game) => Game.fromJson(game)).toList();
  } else {
    throw Exception('Error al cargar juegos');
  }
}
```

**Ejemplo en React Native/JavaScript:**
```javascript
const getGames = async () => {
  try {
    const response = await fetch('http://localhost:3002/api/games');
    const data = await response.json();
    return data._embedded.gameResponseList;
  } catch (error) {
    console.error('Error:', error);
  }
};
```

### 2. Obtener Juego por ID

**Endpoint:** `GET http://localhost:3002/api/games/{id}`

**Ejemplo:**
```dart
Future<Game> getGameById(int id) async {
  final response = await http.get(
    Uri.parse('http://localhost:3002/api/games/$id'),
  );
  
  if (response.statusCode == 200) {
    return Game.fromJson(json.decode(response.body));
  } else {
    throw Exception('Juego no encontrado');
  }
}
```

### 3. Filtrar Juegos

**Filtros disponibles:**
- Por categoría: `GET /api/games?categoria=1`
- Por género: `GET /api/games?genero=1`
- Con descuento: `GET /api/games?descuento=true`
- Buscar por nombre: `GET /api/games?search=Witcher`

**Ejemplo:**
```dart
Future<List<Game>> getGamesWithDiscount() async {
  final response = await http.get(
    Uri.parse('http://localhost:3002/api/games?descuento=true'),
  );
  // ... procesar respuesta
}
```

### 4. Obtener Categorías

**Endpoint:** `GET http://localhost:3002/api/categories`

```dart
Future<List<Category>> getCategories() async {
  final response = await http.get(
    Uri.parse('http://localhost:3002/api/categories'),
  );
  // ... procesar respuesta
}
```

### 5. Obtener Géneros

**Endpoint:** `GET http://localhost:3002/api/genres`

```dart
Future<List<Genre>> getGenres() async {
  final response = await http.get(
    Uri.parse('http://localhost:3002/api/genres'),
  );
  // ... procesar respuesta
}
```

## 🔄 Estructura de Respuesta JSON

### Respuesta de Juegos
```json
{
  "_embedded": {
    "gameResponseList": [
      {
        "id": 1,
        "nombre": "The Witcher 3: Wild Hunt",
        "descripcion": "Un RPG de mundo abierto épico...",
        "precio": 29990.00,
        "stock": 50,
        "imagenUrl": "https://images.unsplash.com/...",
        "desarrollador": "CD Projekt RED",
        "fechaLanzamiento": "2015",
        "categoria": {
          "id": 3,
          "nombre": "RPG"
        },
        "genero": {
          "id": 3,
          "nombre": "RPG"
        },
        "activo": true,
        "descuento": 0
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:3002/api/games"
    }
  }
}
```

## 🚀 Cómo Cargar los Datos

### ✅ Automático (Ya Configurado)
**Los datos se cargan automáticamente la primera vez que inicias el servicio.**

Cuando ejecutas el servicio `game-catalog-service`:
1. **Primera vez:** Si la base de datos está vacía, se cargan automáticamente 12 juegos de ejemplo
2. **Siguientes veces:** Si ya hay datos, no se vuelven a cargar (evita duplicados)

**No necesitas hacer nada manualmente.** Solo:
1. Asegúrate de que las bases de datos estén creadas (ejecuta `setup-databases.sql` en phpMyAdmin)
2. Inicia el servicio `game-catalog-service`
3. Los datos se cargarán automáticamente si la tabla `juegos` está vacía

### 🔄 Recargar Datos (Si es necesario)
Si necesitas recargar los datos desde cero:
1. Abre phpMyAdmin: http://localhost/phpmyadmin
2. Selecciona la base de datos `games_db`
3. Ve a la pestaña **SQL**
4. Ejecuta: `TRUNCATE TABLE juegos;` (esto borra todos los juegos)
5. Reinicia el servicio `game-catalog-service`
6. Los datos se cargarán automáticamente nuevamente

## 📝 Notas Importantes

1. **URL Base:** Cambia `localhost` por la IP de tu servidor cuando despliegues en producción
2. **CORS:** Los servicios ya tienen CORS configurado para aceptar peticiones desde cualquier origen
3. **Imágenes:** Las URLs de imágenes son de ejemplo (Unsplash). Reemplázalas con tus propias imágenes
4. **Precios:** Los precios están en pesos chilenos (CLP)

## 🔧 Agregar Más Juegos

Para agregar más juegos precargados:

1. Edita `setup-databases.sql` y agrega más INSERT statements
2. O edita `game-catalog-service/src/main/resources/data.sql`
3. Reinicia el servicio

Ejemplo:
```sql
INSERT INTO juegos (nombre, descripcion, precio, stock, imagen_url, desarrollador, fecha_lanzamiento, categoria_id, genero_id, activo, descuento) VALUES
('Nuevo Juego', 'Descripción del juego', 19990, 50, 'https://ejemplo.com/imagen.jpg', 'Desarrollador', '2024', 1, 1, TRUE, 0);
```

## ✅ Verificar que los Datos Están Cargados

1. Abre Swagger: http://localhost:3002/swagger-ui.html
2. Prueba el endpoint `GET /api/games`
3. Deberías ver los 12 juegos en la respuesta

O desde la app móvil:
```dart
final games = await getGames();
print('Juegos cargados: ${games.length}'); // Debería mostrar 12
```

