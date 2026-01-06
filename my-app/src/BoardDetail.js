import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link as RouterLink, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { API_ENDPOINTS } from './config/api';
import { getCurrentUsername, getAuthHeaders } from './utils/authUtils';
import { formatTimestamp } from './utils/dateUtils';

// --- MUI 컴포넌트 import ---
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Divider from '@mui/material/Divider';
import CircularProgress from '@mui/material/CircularProgress';
import Alert from '@mui/material/Alert';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import TextField from '@mui/material/TextField';
import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Delete';
// --- MUI import 끝 ---


function BoardDetail() {
  const { boardId } = useParams();
  
  // ❗️ NOTE: useNavigate() 훅은 React Router의 <BrowserRouter>
  // 컨텍스트 내에서만 사용할 수 있습니다.
  const navigate = useNavigate();

  // (모든 state 선언)
  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthor, setIsAuthor] = useState(false);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState("");
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentUsername, setCurrentUsername] = useState(null);

  // --- 댓글 목록 불러오는 함수 (useCallback으로 최적화) ---
  const fetchComments = useCallback(async () => {
    try {
      // (로그인 안 해도 댓글은 보이도록 GET 요청은 헤더 없이 보냄)
      const response = await axios.get(
        API_ENDPOINTS.COMMENT.LIST(boardId)
      );
      setComments(response.data);
    } catch (error) {
      console.error("댓글 로딩 실패:", error);
      setComments([]); 
    }
  }, [boardId]); 

  // --- 게시글 & 사용자 정보 불러오는 useEffect ---
  useEffect(() => {
    const fetchPostAndUser = async () => {
      setLoading(true);
      setIsAuthor(false);

      const username = await getCurrentUsername();
      setCurrentUsername(username);
      setIsLoggedIn(!!username);

      try {
        // (게시글 읽기도 인증이 필요 없음 - permitAll)
        const postResponse = await axios.get(
          API_ENDPOINTS.BOARD.DETAIL(boardId)
        );
        const fetchedPost = postResponse.data;
        setPost(fetchedPost);

        // 작성자 비교 (현재 로그인 사용자 vs 글 작성자)
        if (username && fetchedPost && fetchedPost.authorUsername === username) {
          setIsAuthor(true);
        }

        // 댓글 정보 가져오기
        await fetchComments(); 

      } catch (e) {
        console.error("상세 데이터 로딩 실패: ", e);
        setPost(null);
      }
      setLoading(false);
    };

    fetchPostAndUser();
  }, [boardId, fetchComments]); 

  
  // (게시글 삭제 핸들러)
  const handleDeletePost = async () => {
    if (window.confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
      try {
        await axios.delete(
          API_ENDPOINTS.BOARD.DELETE(boardId),
          getAuthHeaders() // (삭제는 인증 필요)
        );
        alert("게시글이 성공적으로 삭제되었습니다.");
        navigate("/list");
      } catch (error) {
        // ... (에러 처리) ...
      }
    }
  };

  // (댓글 작성 핸들러)
  const handleCommentSubmit = async (e) => {
    e.preventDefault(); 
    if (!newComment.trim()) {
      alert("댓글 내용을 입력하세요.");
      return;
    }
    try {
      await axios.post(
        API_ENDPOINTS.COMMENT.CREATE(boardId), 
        { content: newComment }, 
        getAuthHeaders() // (댓글 작성은 인증 필요)
      );
      setNewComment(""); 
      await fetchComments(); // 댓글 목록 새로고침
    } catch (error) {
      // ... (에러 처리) ...
    }
  };

  // (댓글 삭제 핸들러)
  const handleCommentDelete = async (commentId) => {
    if (window.confirm("이 댓글을 삭제하시겠습니까?")) {
      try {
        await axios.delete(
          API_ENDPOINTS.COMMENT.DELETE(commentId),
          getAuthHeaders() // (댓글 삭제는 인증 필요)
        );
        alert("댓글이 삭제되었습니다.");
        await fetchComments(); // 댓글 목록 새로고침
      } catch (error) {
        // ... (에러 처리) ...
      }
    }
  };

  // (로딩 중 UI)
  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  // (게시글 없음 UI)
  if (!post) {
    return <Alert severity="error">해당 게시글을 찾을 수 없습니다.</Alert>;
  }

  // (메인 UI)
  return (
    <React.Fragment> 
      {/* 1. 게시글 본문 (Paper) */}
      <Paper elevation={3} sx={{ p: { xs: 2, md: 4 }, mt: 2 }}>
        {/* ... (게시글 제목, 부가 정보, 내용, 버튼 영역) ... */}
        <Typography variant="h4" component="h1" gutterBottom>
          {post.title}
        </Typography>
        <Typography variant="subtitle1" color="text.secondary" gutterBottom>
          게시글 ID: {post.id} | 작성자: {post.authorUsername || '알 수 없음'}
        </Typography>
        <Divider sx={{ my: 2 }} />
        <Typography variant="body1" sx={{ minHeight: '150px', whiteSpace: 'pre-wrap' }}>
          {post.content}
        </Typography>
        <Divider sx={{ my: 2 }} />
        <Box sx={{ display: 'flex', justifyContent: 'space-between', mt: 2 }}>
          <Button variant="outlined" component={RouterLink} to="/list">
            목록
          </Button>
          {isAuthor && (
            <Box sx={{ display: 'flex', gap: 1 }}>
              <Button variant="contained" color="secondary" component={RouterLink} to={`/update/${boardId}`}>
                수정
              </Button>
              <Button variant="contained" color="error" onClick={handleDeletePost}>
                삭제
              </Button>
            </Box>
          )}
        </Box>
      </Paper>
      
      {/* 2. 댓글 영역 (Paper) */}
      <Paper elevation={0} sx={{ p: { xs: 2, md: 4 }, mt: 4 }}>
        <Typography variant="h6" gutterBottom>
          댓글 ({comments.length})
        </Typography>

        {/* 3. 댓글 작성 폼 (로그인 시에만) */}
        {isLoggedIn && (
          <Box 
            component="form" 
            onSubmit={handleCommentSubmit} 
            sx={{ display: 'flex', gap: 1, mb: 3 }}
          >
            <TextField
              label="댓글을 입력하세요"
              variant="outlined"
              fullWidth
              size="small"
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
            />
            <Button type="submit" variant="contained">작성</Button>
          </Box>
        )}

        {/* 4. 댓글 목록 */}
        <List>
          {comments.length > 0 ? (
            comments.map((comment) => (
              <React.Fragment key={comment.id}>
                <ListItem 
                  alignItems="flex-start"
                  secondaryAction={
                    // (댓글 작성자 본인일 경우에만 삭제 버튼 표시)
                    isLoggedIn && currentUsername === comment.authorUsername && (
                      <IconButton edge="end" aria-label="delete" onClick={() => handleCommentDelete(comment.id)}>
                        <DeleteIcon />
                      </IconButton>
                    )
                  }
                >
                  <ListItemText
                    // [수정됨] primary: 작성자 | 시간 (포맷팅 적용)
                    primary={
                      <React.Fragment>
                        <Typography
                          component="span"
                          variant="body2"
                          color="text.primary"
                          sx={{ fontWeight: 'bold', mr: 1 }}
                        >
                          {comment.authorUsername}
                        </Typography>
                        <Typography component="span" variant="caption" color="text.secondary">
                          {formatTimestamp(comment.createdAt)}
                        </Typography>
                      </React.Fragment>
                    }
                    // [수정됨] secondary: 댓글 내용
                    secondary={
                      <Typography
                        component="span"
                        variant="body1"
                        color="text.primary"
                        sx={{ mt: 1, display: 'block' }}
                      >
                        {comment.content}
                      </Typography>
                    }
                  />
                </ListItem>
                <Divider variant="inset" component="li" />
              </React.Fragment>
            ))
          ) : (
            <Typography color="text.secondary">
              작성된 댓글이 없습니다.
            </Typography>
          )}
        </List>
      </Paper>
    </React.Fragment> 
  );
}

export default BoardDetail;

