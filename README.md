=======
# Guia de Execução do DistributedFileSystem

## 📋 Índice
1. [Execução Local com Java](#execução-local-com-java)
2. [Execução com Docker](#execução-com-docker)
3. [Testes e Comandos Úteis](#testes-e-comandos-úteis)
4. [Estrutura de Arquivos](#estrutura-de-arquivos)
5. [Solução de Problemas](#solução-de-problemas)

---

## 🖥️ Execução Local com Java

### Pré-requisitos
- Java JDK 11 ou superior
- Arquivos compilados na pasta `bin/`

### Comandos para executar os peers (em terminais separados):

**Peer 1:**
java -cp bin application.Main 5000 knownPeers/knownPeers1_local.txt tmp/peer1

**Peer 2:**
java -cp bin application.Main 5001 knownPeers/knownPeers2_local.txt tmp/peer2

**Peer 3:**
java -cp bin application.Main 5002 knownPeers/knownPeers3_local.txt tmp/peer3

### Configuração dos Peers Locais

| Peer | Porta | Arquivo de Peers | Diretório |
| --- | --- | --- | --- |
| Peer1 | 5000 | `knownPeers1_local.txt` | `tmp/peer1` |
| Peer2 | 5001 | `knownPeers2_local.txt` | `tmp/peer2` |
| Peer3 | 5002 | `knownPeers3_local.txt` | `tmp/peer3` |

### Conteúdo dos arquivos knownPeers:
***
knownPeers1_local.txt:
---

127.0.0.1:5001

127.0.0.1:5002

***
knownPeers2_local.txt:
---


127.0.0.1:5000

127.0.0.1:5002

***
knownPeers3_local.txt:
---

127.0.0.1:5000

127.0.0.1:5001

* * * * *

🐳 Execução com Docker
----------------------

### Comandos Docker:

# Build e execução dos containers
docker-compose up -d --build

# Ver logs em tempo real
docker-compose logs -f

# Parar e remover containers
docker-compose down

# Acessar um container específico
docker exec -it peer1 /bin/bash

### Configuração Docker dos Peers

| Container | Porta | Arquivo de Peers | Diretório | Volume |
| --- | --- | --- | --- | --- |
| peer1 | 5000 | `knownPeers1_docker.txt` | `tmp/peer1` | `./tmp/peer1:/app/tmp/peer1` |
| peer2 | 5001 | `knownPeers2_docker.txt` | `tmp/peer2` | `./tmp/peer2:/app/tmp/peer2` |
| peer3 | 5002 | `knownPeers3_docker.txt` | `tmp/peer3` | `./tmp/peer3:/app/tmp/peer3` |

### Conteúdo dos arquivos knownPeers para Docker:

knownPeers1_docker.txt:

peer2:5001

peer3:5002


knownPeers2_docker.txt:

peer1:5000

peer3:5002


knownPeers3_docker.txt:

peer1:5000

peer2:5001

* * * * *

🧪 Testes e Comandos Úteis
--------------------------

### Testando a sincronização:

Criar arquivo:

# Localmente
echo "conteúdo teste" > tmp/peer1/teste.txt

# No Docker
docker exec -it peer1 sh -c "echo 'conteúdo teste' > /app/tmp/peer1/teste.txt"

ou

docker exec -it peer1 /bin/bash
ls
cd ..
cd tmp
cd peerN

### Criar/modificar arquivo:

echo "nova linha" > nome_arquivo.txt


