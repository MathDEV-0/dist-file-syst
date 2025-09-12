FROM openjdk:24-jdk-slim

WORKDIR /app

# Copia APENAS os arquivos compilados
COPY bin/ /app/bin/

# Expõe as portas dos peers
EXPOSE 5000 5001 5002

# Comando padrão (será sobrescrito pelo docker-compose)
CMD ["java", "-cp", "bin", "application.Main", "5000", "knownPeers/knownPeers1_docker.txt", "tmp/peer1"]