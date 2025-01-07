

// 获取指定比特位的值
export const getBitAtPosition = (num, n) => {
    const mask = 1 << n
    return (num & mask) >> n; 
}