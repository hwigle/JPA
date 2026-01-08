// nav.js
async function loadHeader() {
    const headerDiv = document.getElementById('common-header');
    if (!headerDiv) return;

    // 1. header.html 파일을 가져와서 삽입
    const response = await fetch('/header.html');
    const headerHtml = await response.text();
    headerDiv.innerHTML = headerHtml;

    // 2. 로그인 상태 체크 (모든 페이지 공통)
    fetch('/api/members/me')
        .then(res => res.ok ? res.json() : Promise.reject())
        .then(member => {
            document.getElementById('logout-status').style.display = 'none';
            document.getElementById('login-status').style.display = 'block';
            document.getElementById('header-welcome-msg').innerText = `${member.nickname}님`;
            // 전역 변수로 유저 정보 저장 (필요한 페이지에서 사용)
            window.currentUser = member; 
        })
        .catch(() => {
            window.currentUser = null;
        });
}

// 페이지 로드 시 헤더 실행
document.addEventListener('DOMContentLoaded', loadHeader);