# Faire fonctionner test containers avec WSL

Basé sur https://gist.github.com/sz763/3b0a5909a03bf2c9c5a057d032bd98b7

## Forcer le daemon docker WSL à écouter sur le port 2375 en ipv4

```
sudo mkdir -p /etc/systemd/system/docker.service.d
sudo vim /etc/systemd/system/docker.service.d/override.conf

# add the below to the override.conf file
[Service]
ExecStart=
ExecStart=/usr/bin/dockerd --host=tcp://127.0.0.1:2375 --host=unix:///var/run/docker.sock
```

## Ajouter ces propriétés en variables d'environnement au niveau du lancement du projet intellij

| Name | Value                                   |
|------|-----------------------------------------|
| `DOCKER_HOST` | `tcp://127.0.0.1:2375`                  |
| `DOCKER_TLS_VERIFY` | `0`                                     |
| `DOCKER_CERT_PATH` | `\\wsl$\Ubuntu\home\$USER_NAME\.docker` |

Remplacer $USER_NAME par le nom de l'utilisateur WSL