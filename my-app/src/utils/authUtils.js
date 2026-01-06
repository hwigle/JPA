import axios from 'axios';
import { API_ENDPOINTS } from '../config/api';

const TOKEN_KEY = 'jwtToken';
const SKIP_VERIFY_KEY = 'skipAuthVerifyOnce';
const storage = window.sessionStorage; // 개발 편의: 브라우저/탭 닫히면 자동 로그아웃

/**
 * JWT 토큰을 sessionStorage에서 가져옵니다.
 * @returns {string|null} 토큰 문자열 또는 null
 */
export const getToken = () => {
  return storage.getItem(TOKEN_KEY);
};

/**
 * JWT 토큰을 sessionStorage에 저장하고 axios 기본 헤더에 설정합니다.
 * @param {string} token - JWT 토큰
 */
export const setToken = (token) => {
  storage.setItem(TOKEN_KEY, token);
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
};

/**
 * 다음 앱 초기화 시 토큰 검증을 한 번 건너뛰도록 플래그를 설정합니다.
 * (로그인 직후 즉시 검증 호출로 인한 경고/로그아웃을 방지)
 */
export const setSkipVerifyOnce = () => {
  storage.setItem(SKIP_VERIFY_KEY, 'true');
};

/**
 * 스킵 플래그를 확인하고, 사용 후 제거합니다.
 */
const consumeSkipVerifyOnce = () => {
  const flag = storage.getItem(SKIP_VERIFY_KEY);
  if (flag) {
    storage.removeItem(SKIP_VERIFY_KEY);
    return true;
  }
  return false;
};

/**
 * JWT 토큰을 sessionStorage에서 제거하고 axios 기본 헤더에서도 삭제합니다.
 */
export const removeToken = () => {
  storage.removeItem(TOKEN_KEY);
  delete axios.defaults.headers.common['Authorization'];
};

/**
 * 현재 로그인 상태를 확인합니다.
 * @returns {boolean} 로그인 여부
 */
export const isLoggedIn = () => {
  return !!getToken();
};

/**
 * 현재 로그인한 사용자의 이름을 JWT 토큰에서 추출합니다.
 * @returns {Promise<string|null>} 사용자 이름 또는 null
 */
export const getCurrentUsername = async () => {
  const token = getToken();
  if (token) {
    try {
      // 'jwt-decode' 동적 import
      const { jwtDecode } = await import('jwt-decode');
      const decodedToken = jwtDecode(token);
      return decodedToken.sub;
    } catch (error) {
      console.error("토큰 디코드 실패:", error);
      return null;
    }
  }
  return null;
};

/**
 * 인증이 필요한 API 요청을 위한 헤더를 생성합니다.
 * @returns {object} Axios 요청 헤더 객체
 */
export const getAuthHeaders = () => {
  const token = getToken();
  if (token) {
    return { headers: { 'Authorization': `Bearer ${token}` } };
  }
  return {};
};

/**
 * 애플리케이션 초기화 시 토큰이 있으면 axios 헤더에 설정합니다.
 * index.js에서 사용합니다.
 */
export const initializeAuth = () => {
  const token = getToken();
  if (token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  }
};

/**
 * Axios 전역 응답 인터셉터를 설정합니다.
 * - 401 / 403 응답이 오면 토큰을 제거하고 로그인 페이지로 이동합니다.
 * - 다른 에러는 그대로 throw 합니다.
 *
 * index.js에서 애플리케이션 시작 시 한 번만 호출하면 됩니다.
 */
export const setupAxiosInterceptors = () => {
  axios.interceptors.response.use(
    (response) => response,
    (error) => {
      const url = error.config?.url || '';

      // 로그인/회원가입/토큰검증 요청은 전역 로그아웃 처리 대상에서 제외
      if (
        url.includes('/api/auth/login') ||
        url.includes('/api/auth/register') ||
        url.includes('/api/auth/me')
      ) {
        return Promise.reject(error);
      }

      const status = error.response?.status;
      const hasAuthHeader = !!error.config?.headers?.Authorization;
      const tokenExists = !!getToken();

      // Authorization 헤더가 없거나 토큰이 없는 상태의 401/403은 전역 로그아웃 처리하지 않음
      if (!hasAuthHeader || !tokenExists) {
        return Promise.reject(error);
      }

      if (status === 401 || status === 403) {
        // 토큰 만료 또는 인증 실패로 판단
        removeToken();
        // 사용자에게 안내
        alert('로그인이 만료되었거나 권한이 없습니다. 다시 로그인해주세요.');
        // 로그인 페이지로 이동
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );
};

/**
 * 앱 시작 시 토큰 검증을 수행합니다.
 * - 토큰이 없으면 아무 것도 하지 않음.
 * - 토큰이 있으나 서버가 401/403/404 등을 응답하면 토큰을 제거하고 로그인 페이지로 이동.
 */
export const verifyAuthOnStart = async () => {
  const token = getToken();
  if (!token) return;

  // 로그인 직후 한 번은 검증을 건너뜀 (서버 재기동/H2 초기화 등의 케이스 방지)
  if (consumeSkipVerifyOnce()) {
    return;
  }

  try {
    await axios.get(API_ENDPOINTS.AUTH.ME);
  } catch (error) {
    const status = error.response?.status;
    // 토큰 만료/권한 오류만 처리 (404 등은 조용히 무시)
    if (status === 401 || status === 403) {
      removeToken();
      if (window.location.pathname !== '/login') {
        alert('로그인이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요.');
        window.location.href = '/login';
      }
    } else {
      // 개발 편의를 위해 콘솔에만 남기고 사용자 알림은 생략
      console.warn('토큰 검증 실패(무시됨):', error);
    }
  }
};
