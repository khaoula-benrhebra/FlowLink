# Tests de Sécurité Postman - FlowLink

## Comptes utilisateurs

| Username | Password | Rôle | Authorization Header |
|----------|----------|------|---------------------|
| admin | admin123 | ADMIN | `Basic YWRtaW46YWRtaW4xMjM=` |
| gestionnaire_appro | appro123 | GESTIONNAIRE_APPROVISIONNEMENT | `Basic Z2VzdGlvbm5haXJlX2FwcHJvOmFwcHJvMTIz` |
| responsable_achats | achats123 | RESPONSABLE_ACHATS | `Basic cmVzcG9uc2FibGVfYWNoYXRzOmFjaGF0czEyMw==` |
| superviseur_logistique | logis123 | SUPERVISEUR_LOGISTIQUE | `Basic c3VwZXJ2aXNldXJfbG9naXN0aXF1ZTpsb2dpczEyMw==` |
| chef_production | prod123 | CHEF_PRODUCTION | `Basic Y2hlZl9wcm9kdWN0aW9uOnByb2QxMjM=` |
| superviseur_production | superprod123 | SUPERVISEUR_PRODUCTION | `Basic c3VwZXJ2aXNldXJfcHJvZHVjdGlvbjpzdXBlcnByb2QxMjM=` |
| gestionnaire_commercial | comm123 | GESTIONNAIRE_COMMERCIAL | `Basic Z2VzdGlvbm5haXJlX2NvbW1lcmNpYWw6Y29tbTEyMw==` |

## Tests par module

### 1. Module Clients (US30-US34)

#### ✅ Test réussi - GESTIONNAIRE_COMMERCIAL
```
Method: GET
URL: http://localhost:8080/api/customers
Headers:
  Authorization: Basic Z2VzdGlvbm5haXJlX2NvbW1lcmNpYWw6Y29tbTEyMw==
  Content-Type: application/json

Résultat attendu: 200 OK
```

#### ❌ Test échec - Mauvais rôle
```
Method: GET
URL: http://localhost:8080/api/customers
Headers:
  Authorization: Basic Z2VzdGlvbm5haXJlX2FwcHJvOmFwcHJvMTIz
  Content-Type: application/json

Résultat attendu: 403 Forbidden
```

### 2. Module Fournisseurs (US3-US7)

#### ✅ Créer fournisseur - GESTIONNAIRE_APPROVISIONNEMENT
```
Method: POST
URL: http://localhost:8080/api/suppliers
Headers:
  Authorization: Basic Z2VzdGlvbm5haXJlX2FwcHJvOmFwcHJvMTIz
  Content-Type: application/json

Body (raw JSON):
{
  "name": "Fournisseur Test",
  "email": "test@supplier.com",
  "phone": "0123456789"
}

Résultat attendu: 201 Created
```

#### ✅ Consulter fournisseurs - SUPERVISEUR_LOGISTIQUE
```
Method: GET
URL: http://localhost:8080/api/suppliers
Headers:
  Authorization: Basic c3VwZXJ2aXNldXJfbG9naXN0aXF1ZTpsb2dpczEyMw==
  Content-Type: application/json

Résultat attendu: 200 OK
```

### 3. Module Production (US18-US27)

#### ✅ Créer produit - CHEF_PRODUCTION
```
Method: POST
URL: http://localhost:8080/api/products
Headers:
  Authorization: Basic Y2hlZl9wcm9kdWN0aW9uOnByb2QxMjM=
  Content-Type: application/json

Body (raw JSON):
{
  "name": "Produit Test",
  "description": "Description test",
  "cost": 100.0
}

Résultat attendu: 201 Created
```

#### ✅ Consulter produits - SUPERVISEUR_PRODUCTION
```
Method: GET
URL: http://localhost:8080/api/products
Headers:
  Authorization: Basic c3VwZXJ2aXNldXJfcHJvZHVjdGlvbjpzdXBlcnByb2QxMjM=
  Content-Type: application/json

Résultat attendu: 200 OK
```

### 4. Gestion Utilisateurs (US1-US2)

#### ✅ Accès admin - ADMIN uniquement
```
Method: GET
URL: http://localhost:8080/api/users
Headers:
  Authorization: Basic YWRtaW46YWRtaW4xMjM=
  Content-Type: application/json

Résultat attendu: 200 OK
```

## Matrice de tests attendus

| Endpoint | ADMIN | GESTIONNAIRE_COMMERCIAL | GESTIONNAIRE_APPRO | SUPERVISEUR_LOGISTIQUE | Autres |
|----------|-------|------------------------|-------------------|----------------------|--------|
| GET /api/customers | ❌ 403 | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 403 |
| POST /api/suppliers | ❌ 403 | ❌ 403 | ✅ 201 | ❌ 403 | ❌ 403 |
| GET /api/suppliers | ❌ 403 | ❌ 403 | ❌ 403 | ✅ 200 | ❌ 403 |
| GET /api/users | ✅ 200 | ❌ 403 | ❌ 403 | ❌ 403 | ❌ 403 |

## Instructions de test

1. Démarrer l'application : `mvn spring-boot:run`
2. Ouvrir Postman
3. Créer une nouvelle collection "FlowLink Security Tests"
4. Tester chaque endpoint avec les différents rôles
5. Vérifier les codes de statut attendus (200, 401, 403)