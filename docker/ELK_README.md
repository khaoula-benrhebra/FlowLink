# 🔍 Stack ELK - FlowLink

## Architecture

```
Application Spring Boot
        ↓ (TCP:5000)
    Logstash
        ↓
  Elasticsearch (Port 9200)
        ↓
    Kibana (Port 5601)
```

## Services

| Service | Port | URL |
|---------|------|-----|
| Elasticsearch | 9200 | http://localhost:9200 |
| Logstash | 5000 | TCP uniquement |
| Kibana | 5601 | http://localhost:5601 |

## Démarrage rapide

```bash
# Démarrer la stack ELK
docker-compose up -d elasticsearch logstash kibana

# Vérifier les services
docker-compose ps

# Voir les logs
docker-compose logs -f logstash
```

## Configuration

### Logback (Application)
- Fichier : `src/main/resources/logback-spring.xml`
- Envoie les logs vers Logstash (TCP:5000)
- Masque automatiquement les données sensibles

### Logstash
- Fichier : `logstash/pipeline/logstash.conf`
- Reçoit les logs (TCP:5000)
- Filtre les données sensibles
- Indexe dans Elasticsearch

### Elasticsearch
- Index : `flowlink-logs-YYYY.MM.dd`
- Rotation quotidienne automatique

## Utilisation

Voir le guide complet : [docs/KIBANA_GUIDE.md](../docs/KIBANA_GUIDE.md)

## Troubleshooting

### Elasticsearch ne démarre pas
```bash
# Vérifier les logs
docker-compose logs elasticsearch

# Augmenter la mémoire si nécessaire
# Modifier ES_JAVA_OPTS dans docker-compose.yml
```

### Les logs n'apparaissent pas dans Kibana
```bash
# Vérifier que Logstash reçoit les logs
docker-compose logs logstash

# Vérifier les index Elasticsearch
curl http://localhost:9200/_cat/indices?v

# Vérifier la connexion de l'application
# L'application doit pouvoir atteindre logstash:5000
```

### Kibana ne se connecte pas à Elasticsearch
```bash
# Vérifier la santé d'Elasticsearch
curl http://localhost:9200/_cluster/health

# Redémarrer Kibana
docker-compose restart kibana
```
