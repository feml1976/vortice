# Instrucciones para Sincronizar con GitHub

Este documento contiene los pasos necesarios para crear un repositorio en GitHub y sincronizarlo con este proyecto local.

## Requisitos Previos

- Tener una cuenta en GitHub (https://github.com)
- Tener Git instalado en tu computadora
- Tener configurado Git con tu nombre y email:
  ```bash
  git config --global user.name "Tu Nombre"
  git config --global user.email "tu@email.com"
  ```

## Parte 1: Crear el Repositorio en GitHub

1. **Acceder a GitHub**
   - Ve a https://github.com
   - Inicia sesión con tu cuenta

2. **Crear un Nuevo Repositorio**
   - Haz clic en el botón "+" en la esquina superior derecha
   - Selecciona "New repository"

3. **Configurar el Repositorio**
   - **Repository name**: `vortice` (o el nombre que prefieras)
   - **Description**: (Opcional) Agrega una descripción de tu proyecto
   - **Visibility**: Selecciona "Public" o "Private" según tus necesidades
   - **NO marques** las opciones:
     - "Add a README file"
     - "Add .gitignore"
     - "Choose a license"
   (Ya que el repositorio local ya está inicializado)

4. **Crear el Repositorio**
   - Haz clic en "Create repository"
   - GitHub te mostrará una página con instrucciones

## Parte 2: Conectar el Repositorio Local con GitHub

Una vez creado el repositorio en GitHub, sigue estos pasos en tu terminal:

### Opción A: Usando HTTPS (Recomendado para principiantes)

```bash
# 1. Agregar el repositorio remoto (reemplaza USUARIO con tu nombre de usuario)
git remote add origin https://github.com/USUARIO/vortice.git

# 2. Verificar que se agregó correctamente
git remote -v

# 3. Cambiar la rama principal a 'main' (si es necesario)
git branch -M main

# 4. Subir los cambios al repositorio remoto
git push -u origin main
```

### Opción B: Usando SSH (Requiere configurar claves SSH)

```bash
# 1. Agregar el repositorio remoto (reemplaza USUARIO con tu nombre de usuario)
git remote add origin git@github.com:USUARIO/vortice.git

# 2. Verificar que se agregó correctamente
git remote -v

# 3. Cambiar la rama principal a 'main' (si es necesario)
git branch -M main

# 4. Subir los cambios al repositorio remoto
git push -u origin main
```

## Parte 3: Flujo de Trabajo Básico con Git

Una vez sincronizado, usa estos comandos para trabajar con tu repositorio:

### Ver el estado de los archivos
```bash
git status
```

### Agregar archivos al staging area
```bash
# Agregar un archivo específico
git add nombre_archivo.txt

# Agregar todos los archivos modificados
git add .
```

### Crear un commit
```bash
git commit -m "Descripción de los cambios realizados"
```

### Subir los cambios a GitHub
```bash
git push
```

### Descargar cambios desde GitHub
```bash
git pull
```

### Ver el historial de commits
```bash
git log
```

## Comandos Útiles Adicionales

### Ver las diferencias antes de hacer commit
```bash
git diff
```

### Ver las ramas disponibles
```bash
git branch
```

### Crear una nueva rama
```bash
git checkout -b nombre-nueva-rama
```

### Cambiar entre ramas
```bash
git checkout nombre-rama
```

### Clonar el repositorio en otra computadora
```bash
git clone https://github.com/USUARIO/vortice.git
```

## Solución de Problemas Comunes

### Error: "remote origin already exists"
```bash
# Eliminar el remoto existente
git remote remove origin

# Agregar el nuevo remoto
git remote add origin URL_DEL_REPOSITORIO
```

### Error al hacer push: "Updates were rejected"
```bash
# Descargar los cambios primero
git pull origin main --rebase

# Luego intenta hacer push nuevamente
git push
```

### Olvidé agregar un archivo al último commit
```bash
git add archivo_olvidado.txt
git commit --amend --no-edit
```

## Recursos Adicionales

- Documentación oficial de Git: https://git-scm.com/doc
- Guías de GitHub: https://docs.github.com/es
- Visualizar Git: https://git-school.github.io/visualizing-git/

## Notas Importantes

- Siempre haz `git pull` antes de comenzar a trabajar para obtener los últimos cambios
- Escribe mensajes de commit descriptivos y claros
- Haz commits frecuentes con cambios pequeños y relacionados
- Nunca subas archivos sensibles como contraseñas o claves API (usa `.gitignore`)
- Revisa los archivos con `git status` antes de hacer commit
