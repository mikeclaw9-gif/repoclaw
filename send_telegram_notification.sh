#!/bin/bash

# Send a Telegram notification about the laundry frontend API port update.

# Read environment variables
TELEGRAM_BOT_TOKEN="${TELEGRAM_BOT_TOKEN}"
TELEGRAM_CHAT_ID="${TELEGRAM_CHAT_ID}"

# Message to send
MESSAGE="Laundry frontend API port updated to 8085."

# Check if variables are set
if [[ -z "$TELEGRAM_BOT_TOKEN" || -z "$TELEGRAM_CHAT_ID" ]]; then
  echo "Error: TELEGRAM_BOT_TOKEN and TELEGRAM_CHAT_ID environment variables must be set." >&2
  exit 1
fi

# Telegram API URL
URL="https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage"

# Prepare JSON payload
PAYLOAD=$(printf '{"chat_id": "%s", "text": "%s"}' "$TELEGRAM_CHAT_ID" "$MESSAGE")

# Send the message
response=$(curl -s -X POST "$URL" -H "Content-Type: application/json" -d "$PAYLOAD")

# Check response
if echo "$response" | grep -q '"ok":true'; then
  echo "Notification sent successfully."
else
  echo "Failed to send notification. Response: $response" >&2
  exit 1
fi