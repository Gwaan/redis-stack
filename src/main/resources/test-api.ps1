# test-api.ps1
$baseUrl = "http://localhost:8080"

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Tests API Dossiers" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

function Test-Endpoint {
    param([string]$name)
    Write-Host "- $name" -ForegroundColor Blue
    Write-Host ""
}

function Write-Success {
    param([string]$message)
    Write-Host "OK: $message" -ForegroundColor Green
    Write-Host ""
}

# 1. Initialiser les donnees de test
Test-Endpoint "1. Initialiser les donnees de test"
$response = Invoke-RestMethod -Uri "$baseUrl/api/dossiers/init-test-data" -Method Post -ContentType "application/json"
$response | ConvertTo-Json -Depth 10
Write-Success "Donnees initialisees"

# 2. Creer un nouveau dossier
Test-Endpoint "2. Creer un nouveau dossier"
$body = @{
    nom = "Dossier Test PowerShell"
    status = "VALIDE"
    montant = 3500.75
    description = "Cree depuis PowerShell"
} | ConvertTo-Json

$response = Invoke-RestMethod -Uri "$baseUrl/api/dossiers" -Method Post -Body $body -ContentType "application/json"
$response | ConvertTo-Json
Write-Success "Dossier cree"

# 3. Recuperer un dossier par ID
Test-Endpoint "3. Recuperer dossier par ID: 1"
$response = Invoke-RestMethod -Uri "$baseUrl/api/dossiers/1" -Method Get
$response | ConvertTo-Json
Write-Success "Dossier recupere"

# 4. Recuperer tous les dossiers
Test-Endpoint "4. Recuperer tous les dossiers (limit 5)"
$uri = "$baseUrl/api/dossiers" + "?offset=0" + "&limit=5"
$response = Invoke-RestMethod -Uri $uri -Method Get
$response | ConvertTo-Json -Depth 10
Write-Success "Liste recuperee"

# 5. Rechercher par status
Test-Endpoint "5. Rechercher dossiers VALIDE"
$response = Invoke-RestMethod -Uri "$baseUrl/api/dossiers/status/VALIDE" -Method Get
$response | ConvertTo-Json -Depth 10
Write-Success "Recherche par status OK"

# 6. Rechercher par nom
Test-Endpoint "6. Rechercher dossiers contenant Alpha"
$uri = "$baseUrl/api/dossiers/search" + "?nom=Alpha"
$response = Invoke-RestMethod -Uri $uri -Method Get
$response | ConvertTo-Json -Depth 10
Write-Success "Recherche par nom OK"

# 7. Rechercher par montant
Test-Endpoint "7. Rechercher dossiers entre 1000 et 3000"
$uri = "$baseUrl/api/dossiers/montant" + "?min=1000" + "&max=3000"
$response = Invoke-RestMethod -Uri $uri -Method Get
$response | ConvertTo-Json -Depth 10
Write-Success "Recherche par montant OK"

# 8. Recherche avancee
Test-Endpoint "8. Recherche avancee (status=VALIDE, montant>=2000)"
$uri = "$baseUrl/api/dossiers/search/advanced" + "?status=VALIDE" + "&minMontant=2000"
$response = Invoke-RestMethod -Uri $uri -Method Get
$response | ConvertTo-Json -Depth 10
Write-Success "Recherche avancee OK"

# 9. Compter les dossiers
Test-Endpoint "9. Compter tous les dossiers"
$response = Invoke-RestMethod -Uri "$baseUrl/api/dossiers/count" -Method Get
$response | ConvertTo-Json
Write-Success "Count OK"

# 10. Statistiques
Test-Endpoint "10. Statistiques"
$response = Invoke-RestMethod -Uri "$baseUrl/api/dossiers/stats" -Method Get
$response | ConvertTo-Json -Depth 10
Write-Success "Stats OK"

# 11. Verifier existence
Test-Endpoint "11. Verifier existence dossier 1"
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/api/dossiers/1" -Method Head -UseBasicParsing
    Write-Host "HTTP Status: $($response.StatusCode)" -ForegroundColor Yellow
    Write-Success "Existence verifiee"
} catch {
    Write-Host "Erreur: $_" -ForegroundColor Red
}

# 12. Supprimer un dossier
Test-Endpoint "12. Supprimer dossier 10"
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/dossiers/10" -Method Delete
    $response | ConvertTo-Json
    Write-Success "Dossier supprime"
} catch {
    Write-Host "Dossier 10 inexistant ou deja supprime" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "Tests termines" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan