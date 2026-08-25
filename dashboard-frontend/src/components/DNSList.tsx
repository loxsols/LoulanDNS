import React, { useEffect, useState } from 'react'
import DNSForm from './DNSForm'

type DNSService = {
  dnsServiceInstanceID?: number
  dnsServiceInstanceName?: string
  dnsserviceInstanceExplain?: string
}

export default function DNSList() {
  const [list, setList] = useState<DNSService[] | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [showCreate, setShowCreate] = useState(false)
  const [editingInitial, setEditingInitial] = useState<any | null>(null)

  const fetchList = () => {
    setLoading(true)
    setError(null)
    fetch('/api/dns/services')
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`)
        return r.json()
      })
      .then((data) => setList(data))
      .catch((e) => setError(String(e)))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    fetchList()
  }, [])

  const handleEdit = async (id?: number) => {
    if (!id) return
    setLoading(true)
    try {
      const resp = await fetch(`/api/dns/service/${id}`)
      if (!resp.ok) throw new Error(`HTTP ${resp.status}`)
      const data = await resp.json()
      setEditingInitial(data)
      setEditingId(id)
    } catch (e: any) {
      setError(String(e))
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async (id?: number) => {
    if (!id) return
    if (!confirm(`サービス ${id} を削除してよいですか？`)) return
    try {
      const resp = await fetch(`/api/dns/service/delete/${id}`, { method: 'DELETE' })
      if (!resp.ok) throw new Error(`HTTP ${resp.status}`)
      fetchList()
    } catch (e: any) {
      setError(String(e))
    }
  }

  if (loading) return <div>読み込み中...</div>
  if (error) return <div className="error">エラー: {error}</div>
  if (!list) return <div>サービスが見つかりません</div>

  return (
    <div>
      <div style={{ marginBottom: 8 }}>
        <button onClick={() => setShowCreate(true)}>新規サービス作成</button>
      </div>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>サービス名</th>
            <th>説明</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {list.map((s) => (
            <tr key={s.dnsServiceInstanceID ?? JSON.stringify(s)}>
              <td>{s.dnsServiceInstanceID}</td>
              <td>{s.dnsServiceInstanceName}</td>
              <td>{s.dnsserviceInstanceExplain}</td>
              <td>
                <button onClick={() => handleEdit(s.dnsServiceInstanceID)}>編集</button>
                <button onClick={() => handleDelete(s.dnsServiceInstanceID)} style={{ marginLeft: 8 }}>削除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showCreate && (
        <DNSForm
          onClose={() => {
            setShowCreate(false)
            fetchList()
          }}
          onSaved={() => fetchList()}
        />
      )}

      {editingId && editingInitial && (
        <DNSForm
          initial={editingInitial}
          onClose={() => {
            setEditingId(null)
            setEditingInitial(null)
            fetchList()
          }}
          onSaved={() => fetchList()}
        />
      )}
    </div>
  )
}
