import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter } from 'react-router-dom';
import { initializeAuth, setupAxiosInterceptors, verifyAuthOnStart } from './utils/authUtils';

// 애플리케이션 시작 시 인증 토큰 초기화
initializeAuth();
// Axios 전역 인터셉터 설정 (401/403 시 자동 로그아웃 및 리다이렉트)
setupAxiosInterceptors();
// 앱 시작 시 토큰 검증 (서버 재시작 등으로 토큰이 무효할 때 자동 로그아웃)
verifyAuthOnStart();

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
