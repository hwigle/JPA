/**
 * 시간을 한국어 형식으로 포맷팅합니다.
 * @param {string|number|Date} timestamp - 타임스탬프
 * @returns {string} 포맷팅된 시간 문자열 (예: "2025. 10. 31. 오후 4:32")
 */
export const formatTimestamp = (timestamp) => {
  if (!timestamp) return "";
  const date = new Date(timestamp);
  
  // 'ko-KR' 로케일을 사용하고, '초'를 제외한 옵션을 지정
  const options = {
    year: 'numeric',
    month: '2-digit', // "10"
    day: '2-digit',   // "31"
    hour: '2-digit',  // "04"
    minute: '2-digit',// "32"
    hour12: true // '오전/오후' 사용 (false로 하면 24시간제)
  };
  
  // e.g., "2025. 10. 31. 오후 4:32"
  return date.toLocaleString('ko-KR', options);
};
