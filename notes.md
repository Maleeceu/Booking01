# Booking projesi — kurulum notlari

## 1. Git kurulumu

Proje klasorunde (pom.xml'in yaninda):

```bash
ls                      # pom.xml ve src goruyor muyum? dogru klasor mu?
git init                # bu klasorde .git olustur, depo baslat
git branch -m main      # branch adini main yap (-m = rename)
git status              # working dir / staging / repo durumu
```

Ilk commit:

```bash
git add .                                      # calisma alanindan staging'e
git commit -m "Initialize Spring Boot project" # snapshot kaydet, emir kipi
```

GitHub'a baglama:

```bash
git remote add origin https://github.com/Maleeceu/<repo>.git   # takma ad ver, gonderme yok
git push -u origin main                                        # gonder, -u upstream kurar
```

Bundan sonra sadece `git push` yeter.

### Gunluk akis
```bash
git switch -c feature/seat-map    # yeni branch ac ve gec
git add .
git commit -m "Add seat map endpoint"
git push -u origin feature/seat-map
# GitHub'da PR ac, squash merge
git switch main
git pull
```

**Kural:** main'e dogrudan commit atma. Push ettikten sonra gecmisi degistirme (`revert` kullan).

---

## 2. docker-compose.yml

Proje kokunde, pom.xml'in yaninda:

```yaml
services:
  postgres:
    image: postgres:16
    container_name: booking-db
    environment:
      POSTGRES_DB: booking
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 1234
    ports:
      - "5434:5432"
    volumes:
      - booking-data:/var/lib/postgresql/data

volumes:
  booking-data:
```

| Satir | Ne yapiyor |
|---|---|
| `image: postgres:16` | Docker Hub'dan hangi imaj. `latest` yazma, surum sabitle |
| `container_name` | `docker exec` yazarken lazim |
| `environment` | Container ilk acilista bu degiskenlerle db kuruyor |
| `ports: 5434:5432` | Sol = benim makinem, sag = container ici |
| `volumes` | Container silinse bile veri kalir |

**Port neden 5434:** 5432'de eski `blog-db-1`, 5433'te `pg-sql-dersi` var.

### Docker komutlari

```bash
docker compose up -d      # imaji indir, container olustur, baslat (-d arka planda)
docker ps                 # calisan container'lari listele
docker compose down       # container'i durdur ve sil (volume kalir)
docker compose logs -f    # loglari izle

docker exec -it booking-db psql -U postgres -d booking   # veritabanina bagla
```

psql icinde: `\l` veritabanlari, `\dt` tablolar, `\d tablo` yapi, `\q` cikis, `\! clear` ekran temizle.

### Kavramlar
- **Imaj** = salt okunur sablon (sinif gibi)
- **Container** = imajdan uretilmis calisan kopya (nesne gibi)
- Container kendi dosya sistemi ve agina sahip, makineden yalitilmis
- Sanal makineden farki: kernel'i paylasiyor, bu yuzden hafif ve hizli

---

## 3. application.properties

`src/main/resources/application.properties`:

```properties
spring.application.name=booking

spring.datasource.url=jdbc:postgresql://localhost:5434/booking
spring.datasource.username=postgres
spring.datasource.password=1234

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.open-in-view=false

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

### URL anatomisi
```
jdbc:postgresql://localhost:5434/booking
 |       |            |       |     |
 |       |            |       |     +-- veritabani adi
 |       |            |       +-------- port (DIS port, compose'un sol tarafi)
 |       |            +---------------- makine adresi
 |       +----------------------------- surucu
 +------------------------------------- protokol
```

Spring makinede calisiyor, container icinde degil. O yuzden `localhost:5434`.
(Spring de container'a girerse: `jdbc:postgresql://postgres:5432/booking` — servis adi + ic port.)

### Kritik iki ayar

**`ddl-auto=validate`**
Hibernate semayi kurmaz, sadece entity'lerle karsilastirir. Uyusmazlik varsa uygulama acilmaz.
Sema Flyway'in isi. (`update` kullanma — sessizce yanlis sey yapiyor, FK hatasi verip devam ediyor.)

**`open-in-view=false`**
Hibernate session'i HTTP istegi boyunca acik tutan varsayilani kapatir.
Transaction sinirini `@Transactional` ile SEN cizeceksin.
Acik birakilirsa: db baglantisi bosuna tutulur + N+1 sorunlari gizlenir.

---

## 4. pom.xml — Spring Boot 4.0.8

Bagimliliklar: data-jpa, flyway (+ `flyway-database-postgresql`), validation, webmvc, postgresql, lombok.

Not: Boot 4'te starter isimleri modulerlesti (`starter-web` degil `starter-webmvc`).
Internetteki orneklerin cogu 3.x icin — arama yaparken dikkat.

Security bilerek eklenmedi; sonra eklenecek.

---

## 5. Sirada

- [ ] `V1__create_venue_and_seat.sql` migration
- [ ] Entity'ler (iliskiler LAZY, `@Builder.Default`)
- [ ] Ilk endpoint + test

### Domain
```
VENUE  1--N SEAT           salon ve koltuklari (sabit dunya)
VENUE  1--N EVENT          salonda gecen etkinlikler
EVENT  1--N BOOKING        etkinlige yapilan rezervasyonlar
USERS  1--N BOOKING        kullanicinin rezervasyonlari
BOOKING 1--N BOOKING_SEAT  rezervasyondaki koltuklar
SEAT   1--N BOOKING_SEAT   koltugun rezervasyonlari
```

`BOOKING_SEAT` ara tablo — bir rezervasyonda cok koltuk oldugu icin.
`event_id` orada da var, cunku su kisit lazim:

```sql
UNIQUE (event_id, seat_id)
```

Ayni etkinlikte ayni koltuk iki kez satilamaz. Yaris kosuluna karsi son savunma hatti.

### "Bos koltuklar" sorgusu
```sql
SELECT s.* FROM seat s
LEFT JOIN booking_seat bs ON s.id = bs.seat_id AND bs.event_id = 1
WHERE bs.id IS NULL;
```

`bs.event_id = 1` kosulu **ON'da**, WHERE'de degil.
WHERE'e koyarsan kurtarilan NULL satirlar elenir, INNER JOIN'e doner, bos koltuklar kaybolur.