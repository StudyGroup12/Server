import { Routes, Route, Link } from 'react-router-dom';
import LoginPage from './pages/auth/LoginPage';
import SignupPage from './pages/auth/SignupPage';
import { useAuth } from './hooks/useAuth';
import './App.css';

function App() {
  const { user, logout } = useAuth();

  return (
    <div className="app-layout">
      <header className="app-header">
        <nav className="nav-container">
          <Link to="/" className="logo">StudyGroup</Link>
          <div className="nav-links">
            {user ? (
              <>
                <span className="user-nickname">{user.nickname}님 환영합니다!</span>
                <button onClick={logout} className="logout-btn">로그아웃</button>
              </>
            ) : (
              <>
                <Link to="/login" className="nav-link">로그인</Link>
                <Link to="/signup" className="nav-link">회원가입</Link>
              </>
            )}
          </div>
        </nav>
      </header>

      <main className="app-main">
        <Routes>
          <Route path="/" element={
            <div className="home-content">
              <h1>스터디 그룹 관리 서비스</h1>
              <p>함께 공부하고, 성장하세요.</p>
              {!user && (
                <div className="hero-buttons">
                  <Link to="/signup" className="hero-btn primary">시작하기</Link>
                  <Link to="/login" className="hero-btn secondary">로그인</Link>
                </div>
              )}
            </div>
          } />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignupPage />} />
        </Routes>
      </main>

      <footer className="app-footer">
        <p>&copy; 2024 StudyGroup. All rights reserved.</p>
      </footer>
    </div>
  );
}

export default App;
