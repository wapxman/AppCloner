# App Cloner v2.0 - VirtualApp Edition

Полноценное клонирование Android-приложений с изоляцией данных.

## Быстрый старт

### Windows:
```
git clone https://github.com/wapxman/AppCloner.git
cd AppCloner
git checkout v2-virtualapp
setup.bat
```

### Linux/Mac:
```
git clone https://github.com/wapxman/AppCloner.git
cd AppCloner
git checkout v2-virtualapp
chmod +x setup.sh && ./setup.sh
```

Затем откройте проект в Android Studio и соберите APK.

## Как работает

Использует VirtualApp framework для создания виртуального контейнера.
Каждый клон получает:
- Изолированный процесс
- Собственные данные (аккаунт, кэш, настройки)
- Независимый жизненный цикл
- Свои уведомления

## Отличие от v1.0

| | v1.0 | v2.0 |
|---|---|---|
| Изоляция | Нет | Полная |
| Отдельные аккаунты | Нет | Да |
| Независимое закрытие | Нет | Да |
| Root | Не нужен | Не нужен |
