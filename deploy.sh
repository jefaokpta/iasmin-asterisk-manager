#!/usr/bin/env sh

# Export variables from .env if present
if [ -f .env ]; then
  set -a
  . ./.env
  set +a
else
  echo "Warning: .env nao encontrado"
  exit 1
fi

sh gradlew clean build

BUILD_EXIT_CODE=$?

if [ $BUILD_EXIT_CODE -ne 0 ]; then
    echo "❌ Build falhou"
    exit 1
fi

echo "🚚 Enviando jar para o servidor"
scp build/libs/iasmin-asterisk-ari-0.0.2.jar iasmin-pabx:/opt/iasmin-asterisk-ari/iasmin-asterisk-ari-0.0.2.jar

echo "🚀 Reiniciando serviço"
ssh iasmin-pabx "systemctl restart iasmin-asterisk-ari"