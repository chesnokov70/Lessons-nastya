#!/bin/sh
chmod 644 /etc/loki/loki-config.yaml
exec "$@"
