# SensiPro

Android утилитаи таҳлили дастгоҳ ва тавсияи танзимоти сенситивӣ (Kotlin + Jetpack Compose).
Барномаи firibgar (cheat) нест — танҳо дастгоҳро таҳлил карда, тавсия медиҳад.

## Технология

- Kotlin, Jetpack Compose, Material 3
- MVVM + Repository pattern
- DataStore Preferences (танзимот ва таърих)
- Navigation Compose
- Забонҳо: Тоҷикӣ (пешфарз), Русӣ, Англисӣ

## Сохтор

```
app/src/main/java/com/sensipro/app/
  MainActivity.kt, SensiProApplication.kt
  device/        — таҳлили дастгоҳ (Build, DisplayMetrics, ActivityManager, StatFs)
  sensitivity/   — муҳаррики детерминистии тавсияи сенситивӣ
  settings/      — DataStore барои танзимот
  history/       — DataStore барои таърих
  data/          — контейнери DI
  navigation/    — NavGraph
  ui/            — экранҳо, тема, компонентҳо
```

## Сохтани APK дар GitHub Actions

Дар лоиҳа `gradle-wrapper.jar` бо мақсад дохил карда нашудааст (муҳити сохти ин лоиҳа ба интернет дастрасӣ надошт). Workflow (`.github/workflows/build-apk.yml`) онро худкор аз нав месозад пеш аз иҷрои `./gradlew`, бинобар ин ниёзе ба коре аз тарафи шумо нест — танҳо push кунед.

## Фармонҳои Termux барои боркунӣ ба GitHub

```bash
cd SensiPro
git init
git remote add origin https://github.com/<username>/<repo>.git
git add .
git commit -m "Initial SensiPro project"
git branch -M main
git push -u origin main
```

Пас аз push, ба саҳифаи "Actions"-и репозиторий гузаред — workflow "Build APK" худкор оғоз мешавад ва APK-ро ҳамчун artifact бо номи `SensiPro-debug` пешниҳод мекунад.
