# Lessons-nastya
#----------------
# С помощью terraform создать машину для мониторинга
# С помощью ansible установить docker
# С помощью ansible и docker запустить на машине мониторинга prometheus + grafana # + loki + alertmanager. Проверить, что все компоненты работоспособны
# 
# Access Services
# 
# chmod 644 loki-config.yaml
#
# Prometheus → http://<server-ip>:9090
# Grafana → http://<server-ip>:3000 (Login: admin / admin)
# Loki → http://<server-ip>:3100
# Alertmanager → http://<server-ip>:9093
#
# sudo /bin/systemctl daemon-reload
# sudo /bin/systemctl enable grafana-server

#
# С помощью terraform создать машину для jenkins
# С помощью ansible установить jenkins
# Поднять свой secure docker registry
#
# -------------------------------------------
# aws ec2 describe-subnets --query "Subnets[*].{ID:SubnetId,CIDR:CidrBlock}" --output table
