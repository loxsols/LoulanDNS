import React from 'react'
import UsersList from './components/UsersList'
import DNSList from './components/DNSList'

export default function App() {
  return (
    <div className="container">
      <h1>LoulanDNS 管理ダッシュボード</h1>
      <section>
        <h2>ユーザー一覧</h2>
        <UsersList />
      </section>
      <section>
        <h2>DNSサービス一覧</h2>
        <DNSList />
      </section>
    </div>
  )
}
