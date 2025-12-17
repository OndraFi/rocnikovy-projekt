# Ročníkový projekt - redakční systém

---

## Spuštění v dockeru
První běh nutné spustit takto:
```
docker-compose up -d --build
```
následující lze pak takto:
```
docker-compose up -d
```

## open api generátor
Jak generovat api clienta pro frontend?
1. musí běžet spring aplikace na portu 8080
2. v terminálu přejít do složky backend
3. spustit příkaz .\generate-ts-client.cmd
4. api client se vytvoří ve složce frontend\api