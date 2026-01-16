# 🧪 Tests Postman - RawMaterial & Supplier

Guide de test pour la relation many-to-many entre matières premières et fournisseurs.

---

## 📋 Prérequis

- **Application démarrée** : `mvn spring-boot:run` ou Docker
- **URL de base** : `http://localhost:8080/api`
- **Authentification** : Token JWT avec le rôle `GESTIONNAIRE_APPROVISIONNEMENT`

### Headers communs

| Header          | Value                    |
|-----------------|--------------------------|
| Content-Type    | application/json         |
| Authorization   | Bearer `<votre_token>`   |

---

## 📝 User Stories à Tester

### US1 : Créer des Fournisseurs (Prérequis)

**Endpoint :** `POST /api/suppliers`

```json
{
    "name": "Fournisseur Alpha",
    "contact": "Jean Dupont",
    "email": "alpha@supplier.com",
    "phone": "0612345678",
    "rating": 4.5,
    "leadTime": 7
}
```

```json
{
    "name": "Fournisseur Beta",
    "contact": "Marie Martin",
    "email": "beta@supplier.com",
    "phone": "0698765432",
    "rating": 4.0,
    "leadTime": 10
}
```

```json
{
    "name": "Fournisseur Gamma",
    "contact": "Pierre Blanc",
    "email": "gamma@supplier.com",
    "phone": "0654321098",
    "rating": 3.8,
    "leadTime": 14
}
```

> 📌 **Notez les IDs retournés** (ex: 1, 2, 3)

---

### US2 : Créer une Matière Première AVEC Suppliers

**Endpoint :** `POST /api/raw-materials`

```json
{
    "name": "Acier Inoxydable",
    "stock": 500,
    "stockMin": 50,
    "unit": "kg",
    "supplierIds": [1, 2]
}
```

**✅ Résultat attendu :**
- Status : `201 Created`
- La réponse contient la liste `suppliers` avec les détails des fournisseurs 1 et 2

---

### US3 : Mettre à Jour les Suppliers d'une Matière Première

**Endpoint :** `PUT /api/raw-materials/1`

```json
{
    "name": "Acier Inoxydable Premium",
    "stock": 600,
    "stockMin": 60,
    "unit": "kg",
    "supplierIds": [2, 3]
}
```

**✅ Résultat attendu :**
- Status : `200 OK`
- Les suppliers passent de [1, 2] à [2, 3]

---

### US4 : Récupérer une Matière Première avec ses Suppliers

**Endpoint :** `GET /api/raw-materials/1`

**✅ Résultat attendu :**
```json
{
    "status": 200,
    "message": "...",
    "data": {
        "idMaterial": 1,
        "name": "Acier Inoxydable Premium",
        "stock": 600,
        "stockMin": 60,
        "unit": "kg",
        "suppliers": [
            {
                "idSupplier": 2,
                "name": "Fournisseur Beta",
                "contact": "Marie Martin",
                "email": "beta@supplier.com",
                "phone": "0698765432",
                "rating": 4.0,
                "leadTime": 10
            },
            {
                "idSupplier": 3,
                "name": "Fournisseur Gamma",
                ...
            }
        ]
    }
}
```

---

### US5 : Supprimer un Supplier - RawMaterial NON affecté

**Étape 1 :** Supprimer le supplier

**Endpoint :** `DELETE /api/suppliers/2`

**✅ Résultat attendu :** Status `200 OK`

---

**Étape 2 :** Vérifier que la matière première existe toujours

**Endpoint :** `GET /api/raw-materials/1`

**✅ Résultat attendu :**
- La matière première existe toujours
- Le supplier 2 n'est plus dans la liste `suppliers`

---

### US6 : Supprimer une Matière Première - Supplier NON affecté

**Étape 1 :** Supprimer la matière première

**Endpoint :** `DELETE /api/raw-materials/1`

**✅ Résultat attendu :** Status `200 OK`

---

**Étape 2 :** Vérifier que le supplier existe toujours

**Endpoint :** `GET /api/suppliers/3`

**✅ Résultat attendu :**
- Le supplier existe toujours avec toutes ses données

---

### US7 : Créer une Matière Première SANS Suppliers

**Endpoint :** `POST /api/raw-materials`

```json
{
    "name": "Cuivre",
    "stock": 200,
    "stockMin": 20,
    "unit": "kg"
}
```

**✅ Résultat attendu :**
- Status : `201 Created`
- `suppliers` est une liste vide `[]`

---

### US8 : Ajouter des Suppliers à une Matière existante

**Endpoint :** `PUT /api/raw-materials/2`

```json
{
    "name": "Cuivre",
    "stock": 200,
    "stockMin": 20,
    "unit": "kg",
    "supplierIds": [1, 3]
}
```

**✅ Résultat attendu :**
- Status : `200 OK`
- La matière première a maintenant des suppliers

---

## 🔍 Vérification en Base de Données

Pour vérifier la table pivot `material_suppliers` :

```sql
SELECT * FROM material_suppliers;
```

| material_id | supplier_id |
|-------------|-------------|
| 2           | 1           |
| 2           | 3           |

---

## ⚠️ Codes d'Erreur Possibles

| Code | Message                          | Cause                                      |
|------|----------------------------------|--------------------------------------------|
| 400  | Validation error                 | Champs obligatoires manquants              |
| 401  | Unauthorized                     | Token JWT manquant ou expiré               |
| 403  | Forbidden                        | Rôle insuffisant                           |
| 404  | RawMaterial not found            | ID de matière première inexistant          |
| 409  | RawMaterial already exists       | Nom de matière première déjà utilisé       |

---

## 📊 Résumé des Tests

| # | User Story                                    | Méthode | Endpoint              |
|---|-----------------------------------------------|---------|----------------------|
| 1 | Créer des fournisseurs                        | POST    | /api/suppliers       |
| 2 | Créer matière première avec suppliers         | POST    | /api/raw-materials   |
| 3 | Mettre à jour les suppliers                   | PUT     | /api/raw-materials/1 |
| 4 | Récupérer matière avec suppliers              | GET     | /api/raw-materials/1 |
| 5 | Supprimer supplier sans affecter matière      | DELETE  | /api/suppliers/2     |
| 6 | Supprimer matière sans affecter supplier      | DELETE  | /api/raw-materials/1 |
| 7 | Créer matière sans suppliers                  | POST    | /api/raw-materials   |
| 8 | Ajouter suppliers à matière existante         | PUT     | /api/raw-materials/2 |
