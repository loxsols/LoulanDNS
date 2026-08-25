# LoulanDNS Dashboard Frontend

Minimal React + Vite frontend that calls the existing LoulanDNS admin APIs via Vite proxy.

Install and run:

```bash
cd dashboard-frontend
npm install
npm run dev
```

- Dev server: http://localhost:5173
- Proxy mappings (dev):
  - `/api/users` → `http://127.0.0.1:8080/admin/api/user/list/user`
  - `/api/dns/services` → `http://127.0.0.1:8080/admin/api/dns/service/list/dns-service-instance`

注: 本アプリは既存 LoulanDNS バックエンドを変更しません。バックエンドがローカルで `http://127.0.0.1:8080` で動作しているか、あるいはCORSが許可されていることを確認してください。
