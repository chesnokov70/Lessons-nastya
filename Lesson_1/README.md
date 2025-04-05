Задача №1
Дано элементарное приложение на nodejs: https://github.com/AnastasiyaGapochkina01/node-app

Необходимо

Запустить его в docker
доступ к приложению должен осуществляться через nginx
С помощью terraform подготовить для него ВМ типа t3.small
С помощью ansible подготовить окружение на этой ВМ
установить docker
создать пользователя deployer с правами на запуск команд docker без sudo
установить контейнер с node-exporter
установить контейнер с cadvisor-exporter
подключить экспортеры к prometheus
Создать дашборды для метрик ВМ (node-exporter) и контейнеров (cadvisor)
Написать Jenkinsfile для деплоя приложения на созданную ВМ
сборка docker image и пуш его в docker hub или свой registry
деплой нового image
оповещение о результате сборки в telegram
Настроить сбор логов с контейнера приложения и nginx