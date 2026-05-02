# Notifications Providers & Test Guide

Recommended providers (MVP):
- SMS: Twilio (reliable, easy dev experience) or MessageBird (VN coverage).  
- Email: SendGrid or Mailgun (transactional email).  
- Push: Firebase Cloud Messaging (FCM) for mobile/web push.

Decision rationale:
- Twilio: global, good SDKs, supports SMS status callbacks for delivery reporting.
- SendGrid: simple transactional API and templates.
- FCM: free, integrates easily with mobile/web.

Quick test examples

1) Twilio (SMS) - curl
- Requirements: `TWILIO_ACCOUNT_SID`, `TWILIO_AUTH_TOKEN`, `TWILIO_FROM` (phone)

```bash
curl -X POST "https://api.twilio.com/2010-04-01/Accounts/$TWILIO_ACCOUNT_SID/Messages.json" \
--data-urlencode "To=+84XXXXXXXXX" \
--data-urlencode "From=$TWILIO_FROM" \
--data-urlencode "Body=Test message from Dental Clinic MVP" \
-u "$TWILIO_ACCOUNT_SID:$TWILIO_AUTH_TOKEN"
```

2) SendGrid (Email) - curl
- Requirements: `SENDGRID_API_KEY`

```bash
curl -X POST "https://api.sendgrid.com/v3/mail/send" \
-H "Authorization: Bearer $SENDGRID_API_KEY" \
-H "Content-Type: application/json" \
-d '{"personalizations":[{"to":[{"email":"test@example.com"}]}],"from":{"email":"no-reply@clinic.example.com"},"subject":"Test Email","content":[{"type":"text/plain","value":"Test message from Dental Clinic MVP"}] }'
```

3) FCM (Push) - Node.js snippet
- Use `firebase-admin` and server key; send a test notification to a device token.

Postman & automated tests
- Import `openapi.yaml` into Postman (or create a collection) and add environment variables for provider keys.
- Create a collection runner test that calls `/appointments` to create an appointment and then simulates calling the notification endpoint.

Delivery monitoring & fallbacks
- Use Twilio callbacks/webhooks to record send/delivery statuses into `Notification` entity.  
- If SMS fails, fall back to Email or show in-app push (if enabled).

Costs & quotas
- Twilio/SendGrid have free tiers but bill per message; estimate based on expected appointment volume.

Next steps to test in your environment
1. Create provider accounts (Twilio, SendGrid, Firebase).  
2. Store credentials in secrets manager (not in repo).  
3. Run the curl tests above to validate sending to a test number/email.  
4. Wire webhook callbacks to `/notifications/callback` to update `Notification` records.
