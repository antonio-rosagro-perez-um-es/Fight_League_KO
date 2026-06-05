FROM node:22-alpine AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
COPY assets ../assets
RUN npm run build

FROM nginx:1.27-alpine
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/fight-league-ko-frontend/browser/ /usr/share/nginx/html
COPY assets /usr/share/nginx/html/assets
EXPOSE 80
