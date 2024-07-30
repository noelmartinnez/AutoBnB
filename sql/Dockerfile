FROM postgres:latest
ENV POSTGRES_DB=Autobnb
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=noel
COPY migrations.sql /docker-entrypoint-initdb.d/
COPY seeders.sql /docker-entrypoint-initdb.d/
