

//生成min到max之间的随机数  包含min 不包含max
export const randomInt = (min, max) => {

    return Math.floor(Math.random() * (max - min) + min)
}