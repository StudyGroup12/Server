import { FormEvent, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import './Auth.css';

const SignupPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [nickname, setNickname] = useState('');
  const { signup } = useAuth();

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    signup({ email, password, nickname });
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>회원가입</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="signup-email">이메일</label>
            <input
              id="signup-email"
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
              placeholder="email@example.com"
            />
          </div>
          <div className="form-group">
            <label htmlFor="signup-password">비밀번호</label>
            <input
              id="signup-password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
              placeholder="8자 이상 입력하세요"
              minLength={8}
            />
          </div>
          <div className="form-group">
            <label htmlFor="signup-nickname">닉네임</label>
            <input
              id="signup-nickname"
              type="text"
              value={nickname}
              onChange={(event) => setNickname(event.target.value)}
              required
              placeholder="닉네임을 입력하세요"
            />
          </div>
          <button type="submit" className="auth-button">
            회원가입
          </button>
        </form>
        <p className="auth-link">
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  );
};

export default SignupPage;
