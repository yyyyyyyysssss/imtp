

export const urlParamParse = (urlParams) => {
    var regex = /(?:[?&]|^)([^=#?]+)=([^&#]*)/g,
        p = {},
        match;
    while ((match = regex.exec(decodeURIComponent(urlParams)))) {
        p[match[1]] = match[2];
    }
    return p;
}

export const dataURLtoFile = (dataurl, filename) => {
    var arr = dataurl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[arr.length - 1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, { type: mime });
}

export const formatFileSize = (size) => {
    if (size > 1048576) {
        size = (size / (1024 * 1024)).toFixed(1) + "M"
    } else if (size > 1024) {
        size = (size / 1024).toFixed(1) + "K"
    } else {
        size = size + "B";
    }
    return size;
}
const weekdays = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
export const formatChatDate = (timestamp) => {
    if (!timestamp) {
        return '';
    }
    const date = new Date(Number(timestamp));
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
                return '昨天';
            }
            let gap = date.getDay();
            return weekdays[gap];
        }
    }
}

export const getVideoDimensionsOfByFile = (file) => {
    return new Promise(resolve => {
        const url = URL.createObjectURL(file);
        getVideoDimensionsOf(url)
            .then(
                ({ height, width, duration }) => {
                    URL.revokeObjectURL(url);
                    resolve({ height, width, duration });
                }
            );
    });
}

export const getVideoDimensionsOf = (url) => {
    return new Promise(resolve => {
        const video = document.createElement('video');
        video.crossOrigin = 'anonymous'
        video.addEventListener("loadedmetadata", function () {
            const height = this.videoHeight;
            const width = this.videoWidth;
            const duration = video.duration;
            resolve({ height, width, duration });
        }, false);
        video.src = url;
    });
}

export const getVideoPoster = (url) => {
    return new Promise(resolve => {
        const video = document.createElement('video');
        video.crossOrigin = 'anonymous'
        video.currentTime = 1;
        video.addEventListener("loadeddata", function () {
            const height = this.videoHeight;
            const width = this.videoWidth;
            const canvas = document.createElement("canvas");
            canvas.width = width;
            canvas.height = height;
            canvas.getContext('2d').drawImage(video, 1, 1, canvas.width, canvas.height);
            const poster = canvas.toDataURL();
            resolve({ poster });
        }, false);
        video.src = url;
    });
}

export const createThumbnail = (file) => {
    const url = URL.createObjectURL(file)
    return getVideoPoster(url)
}


export const download = (url, filename) => {
    return new Promise((resolve, reject) => {
        fetch(url, { method: 'GET' })
            .then(res => {
                if (res.ok) {
                    return res.blob();
                } else {
                    return reject(res.statusText);
                }
            }).then(blob => {
                let a = document.createElement("a");
                a.href = URL.createObjectURL(blob);
                a.download = filename;
                a.style.display = "none";
                document.body.appendChild(a);
                a.click();
                URL.revokeObjectURL(a.href);
                document.body.removeChild(a);
                return resolve(true);
            }).catch(err => {
                return reject(err);
            });
    });
}


export const getBit = (num, n) => {
    let mask = 1 << n
    return (num & mask) >> n;
}


export const timeToSeconds = (time) => {
    const [hours, minutes, seconds] = time.split(':').map(Number)
    return hours * 3600 + minutes * 60 + seconds
}

export const formatTimeString = (time) => {
    const [hours, minutes, seconds] = time.split(':').map(Number)
    if(hours === 0){
        return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
    } else {
        return time
    }
}
