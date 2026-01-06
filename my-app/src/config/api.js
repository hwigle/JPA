// API 기본 URL 설정
// 환경 변수에서 가져오고, 없으면 기본값 사용
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080';

// API 엔드포인트 정의
export const API_ENDPOINTS = {
  // 인증 관련
  AUTH: {
    LOGIN: `${API_BASE_URL}/api/auth/login`,
    REGISTER: `${API_BASE_URL}/api/auth/register`,
    ME: `${API_BASE_URL}/api/auth/me`,
  },
  // 게시판 관련
  BOARD: {
    LIST: (page = 0) => `${API_BASE_URL}/api/board?page=${page}`,
    DETAIL: (id) => `${API_BASE_URL}/api/board/${id}`,
    CREATE: `${API_BASE_URL}/api/board`,
    UPDATE: (id) => `${API_BASE_URL}/api/board/${id}`,
    DELETE: (id) => `${API_BASE_URL}/api/board/${id}`,
  },
  // 댓글 관련
  COMMENT: {
    LIST: (boardId) => `${API_BASE_URL}/api/board/${boardId}/comments`,
    CREATE: (boardId) => `${API_BASE_URL}/api/board/${boardId}/comments`,
    DELETE: (commentId) => `${API_BASE_URL}/api/comments/${commentId}`,
  },
};

export default API_BASE_URL;
