import { Navigate, Route, Routes } from 'react-router-dom'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import AdminDashboard from './pages/AdminDashboard.jsx'
import MemberDashboard from './pages/MemberDashboard.jsx'

function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/admin"
        element={
          <ProtectedRoute roles={['ADMIN']}>
            <AdminDashboard />
          </ProtectedRoute>
        }
      />
      <Route
        path="/member"
        element={
          <ProtectedRoute roles={['ADMIN', 'MEMBER']}>
            <MemberDashboard />
          </ProtectedRoute>
        }
      />
    </Routes>
  )
}

export default App
