// 멀티셀렉트 토글 유틸
export function toggleField(data, setData, field, val) {
  const cur = data[field] || [];
  setData({
    ...data,
    [field]: cur.includes(val) ? cur.filter((x) => x !== val) : [...cur, val],
  });
}