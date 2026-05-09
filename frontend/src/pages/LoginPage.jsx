import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ROLE_KEY, TOKEN_KEY, api, getRoleFromJwt } from '../api'

export default function LoginPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const { data } = await api.post('/auth/login', { email, password })
      localStorage.setItem(TOKEN_KEY, data.token)
      const role = data.role ?? getRoleFromJwt(data.token) ?? 'MEMBER'
      localStorage.setItem(ROLE_KEY, role)
      navigate(role === 'ADMIN' ? '/admin' : '/member', { replace: true })
    } catch (err) {
      const msg =
        err.response?.data?.error ??
        err.response?.data?.message ??
        err.message ??
        'Login failed'
      setError(typeof msg === 'string' ? msg : 'Login failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-black p-4">
      <div className="w-full max-w-md rounded-xl border border-zinc-800 bg-zinc-900 p-8 shadow-2xl shadow-black/80">
        <h1 className="mb-1 text-center text-2xl font-semibold tracking-tight text-zinc-100">
          Sign in
        </h1>
        <p className="mb-8 text-center text-sm text-zinc-500">Task manager</p>

        <form onSubmit={handleSubmit} className="space-y-5">
          {error ? (
            <p
              className="rounded-md border border-red-900/50 bg-red-950/40 px-3 py-2 text-sm text-red-300"
              role="alert"
            >
              {error}
            </p>
          ) : null}

          <div>
            <label
              htmlFor="login-email"
              className="mb-1.5 block text-xs font-medium uppercase tracking-wider text-zinc-500"
            >
              Email
            </label>
            <input
              id="login-email"
              type="email"
              autoComplete="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2.5 text-zinc-100 outline-none ring-yellow-600/30 placeholder:text-zinc-600 focus:border-yellow-600/50 focus:ring-2"
              placeholder="you@example.com"
            />
          </div>

          <div>
            <label
              htmlFor="login-password"
              className="mb-1.5 block text-xs font-medium uppercase tracking-wider text-zinc-500"
            >
              Password
            </label>
            <input
              id="login-password"
              type="password"
              autoComplete="current-password"
              required
              minLength={8}
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full rounded-lg border border-zinc-700 bg-zinc-950 px-3 py-2.5 text-zinc-100 outline-none ring-yellow-600/30 placeholder:text-zinc-600 focus:border-yellow-600/50 focus:ring-2"
              placeholder="••••••••"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full rounded-lg bg-yellow-600 py-2.5 text-sm font-semibold text-black shadow-lg shadow-yellow-900/20 transition hover:bg-yellow-500 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>

        <p className="mt-8 text-center text-sm text-zinc-500">
          No account?{' '}
          <Link
            to="/register"
            className="font-medium text-yellow-500 transition hover:text-yellow-400"
          >
            Register
          </Link>
        </p>
      </div>
    </div>
  )
}
