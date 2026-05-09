import axios from 'axios'

/** localStorage key for the JWT returned by /auth/login and /auth/register */
export const TOKEN_KEY = 'token'

/** Role string from login/register response: ADMIN | MEMBER */
export const ROLE_KEY = 'role'

export const api = axios.create({
  baseURL: 'team-task-manager-production-0fef.up.railway.app',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY)
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

/** Read role claim from JWT payload (unverified; use after backend trusts token). */
export function getRoleFromJwt(token) {
  if (!token) return null
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.role ?? null
  } catch {
    return null
  }
}
