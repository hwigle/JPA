// 필요한 도구 불러오기(라이브러리 쓸 때)
import { useState } from 'react'; // 리액트에게 '상태' 기능을 빌려오겠다고 선언

// 컴포넌트(함수) 정의하기
function App() {
  // 여러 입력값을 하나의 객체로 묶어서 관리
  const [inputs, setInputs] = useState({
    title: '',
    content: ''
  });

  // 비구조화 할당을 통해 값들을 추출
  const { title, content} = inputs;

  const onChange = (e) => {
    // e.target에서 name과 value 가져오기
    const { name, value } = e.target;

    // 기존 객체를 복사하고 특정 값만 덮어쓰기
    setInputs({
      ...inputs,      // 기존의 inputs 객체를 복사(Spread 연산자)
      [name]: value   // name 키를 가진 값을 value로 변경
    });
  };

  const onReset = () => {
    setInputs({
      title: '',
      content: ''
    });
  };

  return (
    <div style={{ padding: '20px '}}>
      <h2>게시글 작성 실습</h2>
      <div>
        <input
          name="title"
          placeholder='제목을 입력하세요'
          onChange={onChange}
          value={title}
        />
      </div>
      <div style={{ marginTop: '10px'}}>
        <textarea
          name="content"
          placeholder='내용을 입력하세요'
          onChange={onChange}
          value={content}
        />
      </div>

      <div style={{ marginTop: '20px', borderTop: '1px solid #ccc', paddingTop: '10px' }}>
        <p><strong>입력된 제목:</strong> {title}</p>
        <p><strong>입력된 내용:</strong> {content}</p>
        <button onClick={onReset}>초기화</button>
      </div>
    </div>
  )
}

// 이 컴포넌트를 다른 곳(main.jsx)에서 쓸 수 있게 내보내기
export default App;