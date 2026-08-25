import React, { useState } from 'react'

type Props = {
  initial?: {
    dnsServiceInstanceID?: number
    dnsServiceInstanceName?: string
    dnsServiceInstanceExplain?: string
    dnsServiceTypeCode?: number
    dnsResolverInstanceID?: number
    recordStatus?: number
    memo?: string
  }
  onClose: () => void
  onSaved?: () => void
}

export default function DNSForm({ initial = {}, onClose, onSaved }: Props) {
  const [id] = useState<number | undefined>(initial.dnsServiceInstanceID)
  const [userName, setUserName] = useState<string>('')
  const [name, setName] = useState(initial.dnsServiceInstanceName || '')
  const [explain, setExplain] = useState(initial.dnsServiceInstanceExplain || '')
  const [typeCode, setTypeCode] = useState<string>(String(initial.dnsServiceTypeCode ?? '0'))
  const [resolverId, setResolverId] = useState<string>(String(initial.dnsResolverInstanceID ?? '0'))
  const [recordStatus, setRecordStatus] = useState<string>(String(initial.recordStatus ?? '101'))
  const [memo, setMemo] = useState(initial.memo || '')
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setSaving(true)
    setError(null)
    try {
      const params = new URLSearchParams()
      // UserName is required for these APIs; ask user to fill manually in name field
      params.set('UserName', userName)
      if (name) params.set('DNSServiceInstanceName', name)
      if (explain) params.set('DNSServiceInstanceExplain', explain)
      if (typeCode) params.set('DNSServiceTypeCode', typeCode)
      if (resolverId) params.set('DNSResolverInstanceID', resolverId)
      if (recordStatus) params.set('RecordStatus', recordStatus)
      if (memo) params.set('Memo', memo)

      let url: string
      let method = 'PUT'
      if (id) {
        // update by id
        url = `/api/dns/service/update/${id}?${params.toString()}`
      } else {
        // create
        url = `/api/dns/service/create?${params.toString()}`
      }

      const resp = await fetch(url, { method })
      if (!resp.ok) throw new Error(`HTTP ${resp.status}`)
      onSaved && onSaved()
      onClose()
    } catch (e: any) {
      setError(String(e))
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal">
      <form onSubmit={handleSubmit} className="modal-content">
        <h3>{id ? 'DNSサービス編集' : 'DNSサービス作成'}</h3>
        <label>
          UserName (必須)
          <input placeholder="ユーザー名を入力" value={userName} onChange={(e) => setUserName(e.target.value)} required />
        </label>
        <label>
          サービス名
          <input value={name} onChange={(e) => setName(e.target.value)} />
        </label>
        <label>
          説明
          <input value={explain} onChange={(e) => setExplain(e.target.value)} />
        </label>
        <label>
          TypeCode
          <input value={typeCode} onChange={(e) => setTypeCode(e.target.value)} />
        </label>
        <label>
          Resolver ID
          <input value={resolverId} onChange={(e) => setResolverId(e.target.value)} />
        </label>
        <label>
          RecordStatus
          <input value={recordStatus} onChange={(e) => setRecordStatus(e.target.value)} />
        </label>
        <label>
          Memo
          <input value={memo} onChange={(e) => setMemo(e.target.value)} />
        </label>
        {error && <div className="error">エラー: {error}</div>}
        <div style={{ marginTop: 8 }}>
          <button type="submit" disabled={saving}>{saving ? '保存中...' : '保存'}</button>
          <button type="button" onClick={onClose} style={{ marginLeft: 8 }}>キャンセル</button>
        </div>
      </form>
      <style>{`.modal{position:fixed;inset:0;background:rgba(0,0,0,0.3);display:flex;align-items:center;justify-content:center}.modal-content{background:#fff;padding:16px;border-radius:6px;min-width:360px} label{display:block;margin-top:8px} input{width:100%;padding:6px;margin-top:4px}`}</style>
    </div>
  )
}
