# Multi-stage build para optimizar el tamaño de la imagen

# Etapa 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Copiar archivos de configuración de npm
COPY package*.json ./

# Instalar dependencias
RUN npm ci

# Copiar código fuente
COPY . .

# Build de producción
RUN npm run build

# Etapa 2: Runtime con Nginx
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Copiar build desde etapa anterior
COPY --from=build /app/dist .

# Copiar configuración de Nginx
COPY ../docker/nginx/nginx.conf /etc/nginx/conf.d/default.conf

# Exponer puerto
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s CMD wget --no-verbose --tries=1 --spider http://localhost/health || exit 1

# Nginx se ejecuta en foreground por defecto
CMD ["nginx", "-g", "daemon off;"]
