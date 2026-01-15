import { useState, useEffect } from 'react';

function App() {
  // 1. 상태 관리: 입력값과 게시글 목록
  const [inputs, setInputs] = useState({ title: '', content: '' });
  const [posts, setPosts] = useState([]);
  const { title, content } = inputs;

  // 2. 초기 로딩: 앱이 시작될 때 DB에서 기존 데이터를 가져옴
  useEffect(() => {
    fetchPosts();
  }, []);

  const fetchPosts = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/posts');
      if (response.ok) {
        const data = await response.json();
        setPosts(data); // 서버에서 받은 배열로 상태 업데이트
      }
    } catch (error) {
      console.error("데이터 로딩 실패:", error);
    }
  };

  // 3. 입력창 변경 핸들러
  const onChange = (e) => {
    const { name, value } = e.target;
    setInputs({
      ...inputs,
      [name]: value
    });
  };

  // 4. 등록 핸들러: DB에 저장
  const onCreate = async () => {
    if (title === '' || content === '') return alert("내용을 입력해주세요!");

    const newPost = { title, content };

    try {
      const response = await fetch('http://localhost:8080/api/posts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newPost) // 객체를 JSON 문자열로 변환
      });

      if (response.ok) {
        const savedPost = await response.json();
        setPosts([...posts, savedPost]); // 기존 리스트에 서버가 준 새 데이터 추가
        setInputs({ title: '', content: '' }); // 입력창 초기화
        alert("DB 저장 성공!");
      }
    } catch (error) {
      alert("서버 연결 실패!");
    }
  };

  // 5. 삭제 핸들러: 화면에서 제거
  const onRemove = async (id) => {
    if(!window.confirm("정말로 삭제하겠습니까?")) return;

    try {
      // 1) 서버에 삭제 요청(DELETE)
      const response = await fetch(`http://localhost:8080/api/posts/${id}`, {
        method: 'DELETE',
      });

      if(reponse.ok) {
        // 2. 서버 삭제 성공 시, 화면에서도 해당 글을 제거
        setPosts(posts.filter(post => post.id !== id));
        alert("삭제되었습니다.");
      } else {
        alert("삭제 실패");
      }
    } catch(error) {
      console.error("삭제 중 에러 : ", error);
      alert("서버 연결 실패!");
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2>📝 리액트 x 스프링 부트 게시판</h2>
      
      {/* 입력 영역 */}
      <div style={{ marginBottom: '20px' }}>
        <input name="title" placeholder="제목" onChange={onChange} value={title} />
        <textarea 
          name="content" 
          placeholder="내용" 
          onChange={onChange} 
          value={content} 
          style={{ display: 'block', margin: '10px 0', width: '250px', height: '100px' }} 
        />
        <button onClick={onCreate}>등록하기</button>
      </div>

      <hr />

      {/* 목록 출력 영역 */}
      <div style={{ marginTop: '20px' }}>
        <h3>글 목록 ({posts.length}개)</h3>
        {posts.length === 0 ? <p>작성된 글이 없습니다.</p> : 
          posts.map(post => (
            <div key={post.id} style={{ border: '1px solid #ddd', padding: '15px', marginBottom: '10px', borderRadius: '8px' }}>
              <h4 style={{ margin: '0 0 10px 0' }}>{post.title}</h4>
              <p style={{ color: '#666' }}>{post.content}</p>
              <button onClick={() => onRemove(post.id)} style={{ color: 'red', cursor: 'pointer' }}>삭제</button>
            </div>
          ))
        }
      </div>
    </div>
  );
}

export default App;