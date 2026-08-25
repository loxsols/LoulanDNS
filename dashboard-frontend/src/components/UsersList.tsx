import React, { useEffect, useState } from 'react'
import UserForm from './UserForm'

type UserInfo = {
  userID?: number
  userName?: string
  administrator?: boolean
}

export default function UsersList() {
  const [users, setUsers] = useState<UserInfo[] | null>(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [editingUser, setEditingUser] = useState<string | null>(null)
  const [showCreate, setShowCreate] = useState(false)

  const fetchUsers = () => {
    setLoading(true)
    setError(null)
    fetch('/api/users')
      .then((r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`)
        return r.json()
      })
      .then((data) => setUsers(data))
      .catch((e) => setError(String(e)))
      .finally(() => setLoading(false))
  }

  useEffect(() => {
    fetchUsers()
  }, [])

  const handleDelete = async (userName?: string) => {
    if (!userName) return
    if (!confirm(`ユーザー ${userName} を削除してよいですか？`)) return
    try {
      const resp = await fetch(`/api/user/delete?UserName=${encodeURIComponent(userName)}`, { method: 'DELETE' })
      if (!resp.ok) throw new Error(`HTTP ${resp.status}`)
      fetchUsers()
    } catch (e: any) {
      setError(String(e))
    }
  }

  if (loading) return <div>読み込み中...</div>
  if (error) return <div className="error">エラー: {error}</div>

  return (
    <div>
      <div style={{ marginBottom: 8 }}>
        <button onClick={() => setShowCreate(true)}>新規ユーザー作成</button>
      </div>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>ユーザー名</th>
            <th>管理者</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          {(users || []).map((u) => (
            <tr key={u.userID ?? JSON.stringify(u)}>
              <td>{u.userID}</td>
              <td>{u.userName}</td>
              <td>{u.administrator ? 'Yes' : 'No'}</td>
              <td>
                <button onClick={() => setEditingUser(u.userName ?? null)}>編集</button>
                <button onClick={() => handleDelete(u.userName)} style={{ marginLeft: 8 }}>削除</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showCreate && (
        <UserForm
          create
          onClose={() => {
            setShowCreate(false)
            fetchUsers()
          }}
          onSaved={() => fetchUsers()}
        />
      )}

      {editingUser && (
        <UserForm
          initial={{ userName: editingUser }}
          onClose={() => {
            setEditingUser(null)
            fetchUsers()
          }}
          onSaved={() => fetchUsers()}
        />
      )}
    </div>
  )
}
