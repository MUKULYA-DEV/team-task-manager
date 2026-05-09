import { useCallback, useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ROLE_KEY, TOKEN_KEY, api } from '../api'

function logout() {
  localStorage.removeItem(TOKEN_KEY)
  localStorage.removeItem(ROLE_KEY)
}

export default function AdminDashboard() {
  const [projects, setProjects] = useState([])
  const [members, setMembers] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [projName, setProjName] = useState('')
  const [projDesc, setProjDesc] = useState('')
  const [ownerEmail, setOwnerEmail] = useState('')
  const [taskProjectId, setTaskProjectId] = useState('')
  const [taskTitle, setTaskTitle] = useState('')
  const [taskDesc, setTaskDesc] = useState('')
  const [assigneeId, setAssigneeId] = useState('')
  const [savingProject, setSavingProject] = useState(false)
  const [savingTask, setSavingTask] = useState(false)

  const load = useCallback(async () => {
    setError('')
    setLoading(true)
    try {
      const [pRes, mRes] = await Promise.all([
        api.get('/admin/projects'),
        api.get('/admin/members'),
      ])
      setProjects(pRes.data)
      setMembers(mRes.data)
    } catch (e) {
      setError(e.response?.data?.error ?? e.message ?? 'Failed to load')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    load()
  }, [load])

  useEffect(() => {
    if (!members.length) return
    setOwnerEmail((e) => e || members[0].email)
    setAssigneeId((id) => id || String(members[0].id))
  }, [members])

  useEffect(() => {
    if (!projects.length) return
    setTaskProjectId((id) => id || String(projects[0].id))
  }, [projects])

  async function handleCreateProject(e) {
    e.preventDefault()
    setSavingProject(true)
    setError('')
    try {
      await api.post('/admin/projects', {
        name: projName,
        description: projDesc || undefined,
        ownerEmail,
      })
      setProjName('')
      setProjDesc('')
      await load()
    } catch (err) {
      setError(err.response?.data?.error ?? err.message ?? 'Could not create project')
    } finally {
      setSavingProject(false)
    }
  }

  async function handleCreateTask(e) {
    e.preventDefault()
    const selectedAssigneeId =
      assigneeId !== '' && assigneeId != null ? Number(assigneeId) : NaN
    if (!Number.isFinite(selectedAssigneeId) || selectedAssigneeId <= 0) {
      setError('Please select a member to assign the task to.')
      return
    }
    setSavingTask(true)
    setError('')
    try {
      // api baseURL ends with /api → POST /api/admin/tasks
      await api.post('/admin/tasks', {
        projectId: Number(taskProjectId),
        title: taskTitle,
        description: taskDesc || undefined,
        assigneeId: selectedAssigneeId,
      })
      setTaskTitle('')
      setTaskDesc('')
      await load()
    } catch (err) {
      setError(err.response?.data?.error ?? err.message ?? 'Could not create task')
    } finally {
      setSavingTask(false)
    }
  }

  return (
    <div className="min-h-screen bg-black px-4 py-8 text-zinc-100">
      <header className="mx-auto mb-10 flex max-w-5xl flex-wrap items-center justify-between gap-4 border-b border-zinc-800 pb-6">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight text-zinc-50">
            Command center
          </h1>
          <p className="text-sm text-zinc-500">Projects & task assignments</p>
        </div>
        <div className="flex gap-3">
          <Link
            to="/member"
            className="rounded-lg border border-zinc-700 px-4 py-2 text-sm text-zinc-300 transition hover:border-zinc-500 hover:text-white"
          >
            Member view
          </Link>
          <Link
            to="/login"
            onClick={logout}
            className="rounded-lg border border-zinc-800 px-4 py-2 text-sm text-zinc-400 transition hover:text-white"
          >
            Sign out
          </Link>
        </div>
      </header>

      <div className="mx-auto max-w-5xl space-y-10">
        {error ? (
          <p
            className="rounded-lg border border-red-900/50 bg-red-950/30 px-4 py-3 text-sm text-red-300"
            role="alert"
          >
            {error}
          </p>
        ) : null}

        <section className="rounded-xl border border-zinc-800 bg-zinc-900/80 p-6 shadow-xl shadow-black/50">
          <h2 className="mb-4 text-lg font-medium text-yellow-500/90">Projects</h2>
          {loading ? (
            <p className="text-sm text-zinc-500">Loading…</p>
          ) : (
            <ul className="space-y-3">
              {projects.length === 0 ? (
                <li className="text-sm text-zinc-500">No projects yet.</li>
              ) : (
                projects.map((p) => (
                  <li
                    key={p.id}
                    className="flex flex-col gap-1 rounded-lg border border-zinc-800 bg-zinc-950/50 px-4 py-3 sm:flex-row sm:items-center sm:justify-between"
                  >
                    <div>
                      <span className="font-medium text-zinc-100">{p.name}</span>
                      {p.description ? (
                        <p className="text-sm text-zinc-500">{p.description}</p>
                      ) : null}
                    </div>
                    <span className="text-xs uppercase tracking-wider text-zinc-600">
                      Owner · {p.ownerEmail}
                    </span>
                  </li>
                ))
              )}
            </ul>
          )}
        </section>

        <div className="grid gap-8 lg:grid-cols-2">
          <section className="rounded-xl border border-zinc-800 bg-zinc-900 p-6 shadow-xl">
            <h3 className="mb-4 text-sm font-semibold uppercase tracking-widest text-zinc-500">
              New project
            </h3>
            <form onSubmit={handleCreateProject} className="space-y-4">
              <div>
                <label className="mb-1 block text-xs text-zinc-500">Name</label>
                <input
                  required
                  value={projName}
                  onChange={(e) => setProjName(e.target.value)}
                  className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2 text-sm text-zinc-100 outline-none focus:border-yellow-600/50 focus:ring-1 focus:ring-yellow-600/40"
                />
              </div>
              <div>
                <label className="mb-1 block text-xs text-zinc-500">Description</label>
                <textarea
                  value={projDesc}
                  onChange={(e) => setProjDesc(e.target.value)}
                  rows={2}
                  className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2 text-sm text-zinc-100 outline-none focus:border-yellow-600/50"
                />
              </div>
              <div>
                <label className="mb-1 block text-xs text-zinc-500">Owner (member email)</label>
                <select
                  required
                  value={ownerEmail}
                  onChange={(e) => setOwnerEmail(e.target.value)}
                  className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2 text-sm text-zinc-100 outline-none focus:border-yellow-600/50"
                >
                  {members.length === 0 ? (
                    <option value="">No members — register a member account first</option>
                  ) : (
                    members.map((m) => (
                      <option key={m.id} value={m.email}>
                        {m.email}
                      </option>
                    ))
                  )}
                </select>
              </div>
              <button
                type="submit"
                disabled={savingProject || !members.length}
                className="w-full rounded-lg bg-yellow-600 py-2.5 text-sm font-semibold text-black transition hover:bg-yellow-500 disabled:opacity-50"
              >
                {savingProject ? 'Creating…' : 'Create project'}
              </button>
            </form>
          </section>

          <section className="rounded-xl border border-zinc-800 bg-zinc-900 p-6 shadow-xl">
            <h3 className="mb-4 text-sm font-semibold uppercase tracking-widest text-zinc-500">
              Assign task
            </h3>
            <form onSubmit={handleCreateTask} className="space-y-4">
              <div>
                <label className="mb-1 block text-xs text-zinc-500">Project</label>
                <select
                  required
                  value={taskProjectId}
                  onChange={(e) => setTaskProjectId(e.target.value)}
                  className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2 text-sm text-zinc-100 outline-none focus:border-yellow-600/50"
                >
                  {projects.length === 0 ? (
                    <option value="">Create a project first</option>
                  ) : (
                    projects.map((p) => (
                      <option key={p.id} value={p.id}>
                        {p.name}
                      </option>
                    ))
                  )}
                </select>
              </div>
              <div>
                <label className="mb-1 block text-xs text-zinc-500">Title</label>
                <input
                  required
                  value={taskTitle}
                  onChange={(e) => setTaskTitle(e.target.value)}
                  className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2 text-sm text-zinc-100 outline-none focus:border-yellow-600/50"
                />
              </div>
              <div>
                <label className="mb-1 block text-xs text-zinc-500">Description</label>
                <textarea
                  value={taskDesc}
                  onChange={(e) => setTaskDesc(e.target.value)}
                  rows={2}
                  className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2 text-sm text-zinc-100 outline-none focus:border-yellow-600/50"
                />
              </div>
              <div>
                <label className="mb-1 block text-xs text-zinc-500">Assign to member</label>
                <select
                  required
                  value={assigneeId}
                  onChange={(e) => setAssigneeId(e.target.value)}
                  className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2 text-sm text-zinc-100 outline-none focus:border-yellow-600/50"
                >
                  {members.length === 0 ? (
                    <option value="">No members</option>
                  ) : (
                    members.map((m) => (
                      <option key={m.id} value={m.id}>
                        {m.email}
                      </option>
                    ))
                  )}
                </select>
              </div>
              <button
                type="submit"
                disabled={savingTask || !projects.length || !members.length}
                className="w-full rounded-lg bg-yellow-600 py-2.5 text-sm font-semibold text-black transition hover:bg-yellow-500 disabled:opacity-50"
              >
                {savingTask ? 'Assigning…' : 'Create task'}
              </button>
            </form>
          </section>
        </div>
      </div>
    </div>
  )
}
