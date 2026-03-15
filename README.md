# DistributedFileSystem

![Java](https://img.shields.io/badge/Java-11+-orange)\
![Docker](https://img.shields.io/badge/Docker-supported-blue)\
![Architecture](https://img.shields.io/badge/Architecture-Peer--to--Peer-green)\
![Status](https://img.shields.io/badge/Status-Experimental-yellow)

Peer-to-peer distributed file synchronization system where nodes (peers) exchange and synchronize files across the network.

The system supports both **local execution using Java** and **containerized execution using Docker**.

---

# Table of Contents

1\. [System Overview](#system-overview)\
2\. [Architecture](#architecture)\
3\. [Synchronization Workflow](#synchronization-workflow)\
4\. [Project Structure](#project-structure)\
5\. [Local Execution](#local-execution)\
6\. [Docker Execution](#docker-execution)\
7\. [Testing Synchronization](#testing-synchronization)\
8\. [Useful Commands](#useful-commands)\
9\. [Troubleshooting](#troubleshooting)

---

# System Overview

The **DistributedFileSystem** implements a decentralized peer-to-peer architecture where multiple nodes collaborate to synchronize files.

Each peer:

- Maintains a **local shared directory**\
- Connects to **known peers**\
- Propagates **file creation and modification events**\
- Synchronizes files across the network

The system ensures that changes in one peer are eventually replicated to others.

---

# Architecture

The system follows a **Peer-to-Peer (P2P)** architecture where each node acts both as a client and a server.

```mermaid\
graph LR

Peer1["Peer 1 (Port 5000)"]\
Peer2["Peer 2 (Port 5001)"]\
Peer3["Peer 3 (Port 5002)"]

Peer1 <--> Peer2\
Peer2 <--> Peer3\
Peer3 <--> Peer1

Each peer contains:

-   **Network port**

-   **Known peers configuration**

-   **Local synchronization directory**
```

* * * * *

Synchronization Workflow
========================

When a file is created or modified, the system propagates the change to other peers.

* * * * *

Project Structure
=================
```
DistributedFileSystem/\
│\
├── bin/                    # Compiled Java classes\
├── knownPeers/             # Peer configuration files\
│   ├── knownPeers1_local.txt\
│   ├── knownPeers2_local.txt\
│   ├── knownPeers3_local.txt\
│\
├── tmp/                    # Peer synchronization folders\
│   ├── peer1/\
│   ├── peer2/\
│   └── peer3/\
│\
├── docker-compose.yml\
└── README.md
```
* * * * *

Local Execution
===============

Prerequisites
-------------

Before running locally ensure you have:

-   **Java JDK 11 or higher**

-   Compiled classes in the `bin/` directory

Create the peer directories:

mkdir -Force tmp\peer1,tmp\peer2,tmp\peer3,tmp\peer4

* * * * *

Peer Configuration
------------------

Each peer maintains a file listing the addresses of the other peers.

### knownPeers1_local.txt
```
127.0.0.1:5001\
127.0.0.1:5002
```
### knownPeers2_local.txt
```
127.0.0.1:5000\
127.0.0.1:5002
```
### knownPeers3_local.txt
```
127.0.0.1:5000\
127.0.0.1:5001
```
* * * * *

Running the Peers
-----------------

Run each peer in **separate terminals**.

### Peer 1
```
java -cp bin application.Main 5000 knownPeers/knownPeers1_local.txt tmp/peer1
```
### Peer 2
```
java -cp bin application.Main 5001 knownPeers/knownPeers2_local.txt tmp/peer2
```
### Peer 3
```
java -cp bin application.Main 5002 knownPeers/knownPeers3_local.txt tmp/peer3
```
* * * * *

Local Peer Configuration
------------------------

| Peer | Port | Known Peers | Directory |
| --- | --- | --- | --- |
| Peer1 | 5000 | knownPeers1_local.txt | tmp/peer1 |
| Peer2 | 5001 | knownPeers2_local.txt | tmp/peer2 |
| Peer3 | 5002 | knownPeers3_local.txt | tmp/peer3 |

* * * * *

Docker Execution
================

The system can also be executed using **Docker containers**, allowing easier environment management.

* * * * *

Docker Commands
---------------

### Build and start containers
```
docker-compose up -d --build
```
### View logs
```
docker-compose logs -f
```
### Stop containers
```
docker-compose down
```
### Access container terminal
```
docker exec -it peer1 /bin/bash
```
* * * * *

Docker Peer Configuration
-------------------------

| Container | Port | Known Peers | Directory | Volume |
| --- | --- | --- | --- | --- |
| peer1 | 5000 | knownPeers1_docker.txt | tmp/peer1 | ./tmp/peer1:/app/tmp/peer1 |
| peer2 | 5001 | knownPeers2_docker.txt | tmp/peer2 | ./tmp/peer2:/app/tmp/peer2 |
| peer3 | 5002 | knownPeers3_docker.txt | tmp/peer3 | ./tmp/peer3:/app/tmp/peer3 |

* * * * *

### Docker knownPeers Files

#### knownPeers1_docker.txt
```
peer2:5001\
peer3:5002
```
#### knownPeers2_docker.txt
```
peer1:5000\
peer3:5002
```
#### knownPeers3_docker.txt
```
peer1:5000\
peer2:5001
```
* * * * *

Testing Synchronization
=======================

Create a file in one peer directory.

### Local test
```
echo "conteudo teste" > tmp/peer1/teste.txt
```
### Docker test
```
docker exec -it peer1 sh -c "echo 'conteudo teste' > /app/tmp/peer1/teste.txt"
```
If synchronization works correctly, the file should appear in the directories of the other peers.

* * * * *

Useful Commands
===============

### Enter container shell
```
docker exec -it peer1 /bin/bash
```
### Navigate to peer directory
```
cd /app/tmp/peer1
```
### Create or modify a file
```
echo "nova linha" > nome_arquivo.txt
```
* * * * *

Troubleshooting
===============

Peers cannot connect
--------------------

Verify:

-   Correct ports

-   knownPeers configuration

-   Firewall restrictions

* * * * *

Files are not synchronizing
---------------------------

Check:

-   Peer directories exist

-   Docker volumes are correctly mounted

-   All peers are running
