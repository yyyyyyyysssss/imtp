const weekdays = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
export const formatChatDate = (timestamp) => {
    if (!timestamp) {
        return '';
    }
    const date = new Date(timestamp);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    const now = new Date();
    const nowyear = now.getFullYear();
    const nowmonth = String(now.getMonth() + 1).padStart(2, '0');
    const nowday = String(now.getDate()).padStart(2, '0');
    if (year !== nowyear || month !== nowmonth || nowday - day > 7) {
        return `${year}/${month}/${day}`;
    } else {
        if (nowday === day) {
            const hours = String(date.getHours()).padStart(2, '0');
            const minutes = String(date.getMinutes()).padStart(2, '0');
            return `${hours}:${minutes}`;
        } else {
            if (nowday - day === 1) {
                const hours = String(date.getHours()).padStart(2, '0');
                const minutes = String(date.getMinutes()).padStart(2, '0');
                return `昨天  ${hours}:${minutes}`;
            }
            let gap = date.getDay();
            return weekdays[gap];
        }
    }
}