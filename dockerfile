FROM openjdk:24-jdk-slim

WORKDIR /app

# Copia os arquivos compilados (.class)
COPY bin/ /app/bin/

# Copia os diretórios tmp/ e knownPeers/
COPY tmp/ /app/tmp/
COPY knownPeers/ /app/knownPeers/

# Expõe as portas dos peers
EXPOSE 5000 5001 5002

# Comando padrão (será sobrescrito pelo docker-compose)
CMD ["java", "-cp", "bin", "application.Main", "5000", "knownPeers/knownPeers1.txt", "tmp/peer1"]