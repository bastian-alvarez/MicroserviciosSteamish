# 🔧 Corrección de Configuración Docker en Render

## ❌ Problemas Detectados

### 1. Dockerfile Path
**Actual**: `api-gateway/ api-gateway/Dockerfile`  
**Problema**: Tiene espacios extra o formato incorrecto

**✅ Correcto**: `api-gateway/Dockerfile`

### 2. Docker Build Context Directory
**Actual**: `api-gateway/ .`  
**Problema**: Tiene espacios extra y está apuntando a `api-gateway/` cuando debería ser la raíz

**✅ Correcto**: `.` (punto) - **RAÍZ del repositorio**

**⚠️ IMPORTANTE**: El Build Context debe ser la **raíz** porque el Dockerfile necesita:
- El `pom.xml` padre (en la raíz)
- Acceso a `api-gateway/` como subdirectorio

### 3. Pre-Deploy Command
**Actual**: `api-gateway/ $`  
**Problema**: Tiene contenido extraño

**✅ Correcto**: **(vacío)**

---

## ✅ Configuración Correcta

### Dockerfile Path
```
api-gateway/Dockerfile
```
(Sin espacios, sin barras al final)

### Docker Build Context Directory
```
.
```
(Solo un punto - significa raíz del repositorio)

### Docker Command
```
(vacío)
```
(Render usará el ENTRYPOINT del Dockerfile)

### Pre-Deploy Command
```
(vacío)
```
(No necesitas pre-deploy para este caso)

---

## 📋 Pasos para Corregir

1. **Dockerfile Path**:
   - Click en "Edit"
   - Borra todo el contenido
   - Escribe: `api-gateway/Dockerfile`
   - Guarda

2. **Docker Build Context Directory**:
   - Click en "Edit"
   - Borra todo el contenido
   - Escribe: `.` (solo un punto)
   - Guarda

3. **Pre-Deploy Command**:
   - Click en "Edit"
   - Borra todo el contenido
   - Déjalo vacío
   - Guarda

---

## 🔍 Por Qué el Build Context Debe Ser `.`

El Dockerfile hace esto:
```dockerfile
COPY pom.xml* ./                    # Necesita pom.xml de la RAÍZ
COPY api-gateway/pom.xml* ./api-gateway/  # Necesita api-gateway/ como subdirectorio
COPY api-gateway/src ./api-gateway/src
```

Si el Build Context es `api-gateway/`, entonces:
- ❌ No puede acceder al `pom.xml` padre (está fuera del contexto)
- ❌ No puede copiar correctamente los archivos

Con Build Context = `.` (raíz):
- ✅ Puede acceder a `pom.xml` (raíz)
- ✅ Puede acceder a `api-gateway/` (subdirectorio)
- ✅ Todo funciona correctamente

---

## ✅ Configuración Final Esperada

```
Dockerfile Path:              api-gateway/Dockerfile
Docker Build Context:        .
Docker Command:               (vacío)
Pre-Deploy Command:           (vacío)
```

---

## 🚀 Después de Corregir

1. **Guarda todos los cambios**
2. **Haz un deploy manual** o espera auto-deploy
3. **Revisa los logs** - deberías ver el build exitoso
4. **Verifica** que el puerto se detecte correctamente

---

## 📸 Resumen Visual

**ANTES (Incorrecto)**:
```
Dockerfile Path:              api-gateway/ api-gateway/Dockerfile
Docker Build Context:         api-gateway/ .
Pre-Deploy Command:           api-gateway/ $
```

**DESPUÉS (Correcto)**:
```
Dockerfile Path:              api-gateway/Dockerfile
Docker Build Context:         .
Docker Command:               (vacío)
Pre-Deploy Command:           (vacío)
```

---

## 🎯 Checklist

- [ ] Dockerfile Path = `api-gateway/Dockerfile` (sin espacios)
- [ ] Docker Build Context = `.` (solo punto)
- [ ] Docker Command = (vacío)
- [ ] Pre-Deploy Command = (vacío)
- [ ] Variables de entorno configuradas (PORT=8080, etc.)
- [ ] Guardar cambios
- [ ] Deploy

