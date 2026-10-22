# syntax=docker/dockerfile:1

# ---- build ----
FROM mcr.microsoft.com/azurelinux/base/nodejs:24 AS build
WORKDIR /workspace

# 依存関係の取得を先に行い、ソースが変わってもキャッシュを再利用する
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# ---- runtime ----
FROM mcr.microsoft.com/azurelinux/base/nginx:1.30
LABEL org.opencontainers.image.source="https://github.com/k-kosugi/fizzbuzz" \
      org.opencontainers.image.title="fizzbuzz-frontend"

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /workspace/dist /usr/share/nginx/html

# Kubernetes の runAsNonRoot で検証できるよう、ユーザーは数値で指定する (ベースイメージの nginx ユーザー)
USER 999
EXPOSE 8080

# /api は含まない。Ingress などで backend に振り分ける
CMD ["nginx", "-g", "daemon off;"]
