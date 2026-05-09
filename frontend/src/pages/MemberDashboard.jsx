import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ROLE_KEY, TOKEN_KEY, api } from '../api'

function logout() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ROLE_KEY)
}

const ORDER = ['PENDING', 'IN_PROGRESS', 'COMPLETED']

function nextStatus(current) {
  const i = ORDER.indexOf(current)
  if (i < 0 || i >= ORDER.length - 1) return current
  return ORDER[i + 1]
}

export default function MemberDashboard() {
  const [tasks, setTasks] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [updatingId, setUpdatingId] = useState(null)

  const load = useCallback(async () => {
    setError('')
    setLoading(true)
    try {
      // GET /api/tasks/my-tasks — shared `api` client adds Authorization: Bearer <JWT> (see ../api.js)
      const { data } = await api.get('/tasks/my-tasks')
      setTasks(data)
    } catch (e) {
      setError(e.response?.data?.error ?? e.message ?? 'Failed to load tasks')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  async function advanceStatus(task) {
    const n = nextStatus(task.status)
    if (n === task.status) return
    setUpdatingId(task.id)
    setError('')
    try {
      await api.patch(`/member/tasks/${task.id}/status`, { status: n })
      await load()
    } catch (e) {
      setError(e.response?.data?.error ?? e.message ?? 'Update failed')
    } finally {
      setUpdatingId(null)
    }
  }

  const role = localStorage.getItem(ROLE_KEY)

  return (
    <div className="min-h-screen bg-black px-4 py-8 text-zinc-100">
      <header className="mx-auto mb-10 flex max-w-3xl flex-wrap items-center justify-between gap-4 border-b border-zinc-800 pb-6">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight text-zinc-50">My tasks</h1>
          <p className="text-sm text-zinc-500">Assigned to you · advance status when ready</p>
        </div>
        <div className="flex gap-3">
          {role === 'ADMIN' ? (
            <Link
              to="/admin"
              className="rounded-lg border border-zinc-700 px-4 py-2 text-sm text-zinc-300 transition hover:border-zinc-500 hover:text-white"
            >
              Admin
            </Link>
          ) : null}
          <Link
            to="/login"
            onClick={logout}
            className="rounded-lg border border-zinc-800 px-4 py-2 text-sm text-zinc-400 transition hover:text-white"
          >
            Sign out
          </Link>
        </div>
      </header>

      <div className="mx-auto max-w-3xl space-y-6">
        {error ? (
          <p
            className="rounded-lg border border-red-900/50 bg-red-950/30 px-4 py-3 text-sm text-red-300"
            role="alert"
          >
            {error}
          </p>
        ) : null}

        <section className="rounded-xl border border-zinc-800 bg-zinc-900 p-6 shadow-xl shadow-black/50">
          {loading ? (
            <p className="text-sm text-zinc-500">Loading…</p>
          ) : tasks.length === 0 ? (
            <p className="text-sm text-zinc-500">No tasks assigned yet</p>
          ) : (
            <ul className="space-y-4">
              {tasks.map((t) => (
                <li
                  key={t.id}
                  className="rounded-lg border border-zinc-800 bg-zinc-950/60 px-4 py-4"
                >
                  <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                    <div>
                      <p className="font-medium text-zinc-100">{t.title}</p>
                      {t.description ? (
                        <p className="mt-1 text-sm text-zinc-500">{t.description}</p>
                      ) : null}
                      <p className="mt-2 text-xs text-zinc-600">
                        Project · {t.projectName ?? '—'}{' '}
                        <span className="text-zinc-700">· #{t.projectId}</span>
                      </p>
                    </div>
                    <div className="flex flex-wrap items-center gap-2">
                      <span className="rounded-md border border-yellow-900/40 bg-yellow-950/30 px-2 py-1 text-xs font-medium uppercase tracking-wide text-yellow-500/90">
                        {t.status.replace('_', ' ')}
                      </span>
                      {t.status !== 'COMPLETED' ? (
                        <button
                          type="button"
                          disabled={updatingId === t.id}
                          onClick={() => advanceStatus(t)}
                          className="rounded-lg bg-yellow-600 px-3 py-1.5 text-xs font-semibold text-black transition hover:bg-yellow-500 disabled:opacity-50"
                        >
                          {updatingId === t.id
                            ? '…'
                            : t.status === 'PENDING'
                              ? 'Start'
                              : 'Complete'}
                        </button>
                      ) : (
                        <span className="text-xs text-zinc-600">Done</span>
                      )}
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </section>
      </div>
    </div>
  )
}
