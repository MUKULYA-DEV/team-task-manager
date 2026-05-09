import { Navigate } from 'react-router-dom'
import { ROLE_KEY, TOKEN_KEY, getRoleFromJwt } from '../api'

/**
 * Requires a JWT. Optionally restricts to `roles` (e.g. ['ADMIN']).
 */
export default function ProtectedRoute({ children, roles }) {
  const token = localStorage.getItem(TOKEN_KEY)
  if (!token) {
    return <Navigate to="/login" replace />
  }

  if (roles?.length) {
    let role = localStorage.getItem(ROLE_KEY)
    if (!role) {
      role = getRoleFromJwt(token)
    }
    if (!role || !roles.includes(role)) {
      if (role === 'ADMIN') {
        return <Navigate to="/admin" replace />
      }
      return <Navigate to="/member" replace />
    }
  }

  return children
}
