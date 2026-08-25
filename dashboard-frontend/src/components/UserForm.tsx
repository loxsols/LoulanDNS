import React, { useState } from 'react'

type Props = {
  initial?: { userName?: string; userPassword?: string; recordStatus?: number | string; memo?: string }
  onClose: () => void
  onSaved?: () => void
  create?: boolean
}

export default function UserForm({ initial = {}, onClose, onSaved, create = false }: Props) {
  const [userName, setUserName] = useState(initial.userName || '')
  const [userPassword, setUserPassword] = useState(initial.userPassword || '')
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
      params.set('UserName', userName)
      if (userPassword) params.set('UserPassword', userPassword)
      if (recordStatus) params.set('RecordStatus', recordStatus)
      if (memo) params.set('Memo', memo)

      const url = create ? `/api/user/create?${params.toString()}` : `/api/user/update?${params.toString()}`
      const resp = await fetch(url, { method: 'PUT' })
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
        <h3>{create ? '新規ユーザー作成' : 'ユーザー編集'}</h3>
        <label>
          ユーザー名
          <input value={userName} onChange={(e) => setUserName(e.target.value)} required disabled={!create && !!initial.userName} />
        </label>
        <label>
          パスワード
          <input value={userPassword} onChange={(e) => setUserPassword(e.target.value)} />
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
      <style>{`.modal{position:fixed;inset:0;background:rgba(0,0,0,0.3);display:flex;align-items:center;justify-content:center}.modal-content{background:#fff;padding:16px;border-radius:6px;min-width:320px} label{display:block;margin-top:8px} input{width:100%;padding:6px;margin-top:4px}`}</style>
    </div>
  )
}
