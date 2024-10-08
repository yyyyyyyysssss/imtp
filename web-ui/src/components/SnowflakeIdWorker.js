
class SnowflakeIdWorker{
    constructor(workId){
        this.workId = workId;
        this.sequence = 0;
        this.lastTimestamp = -1;
        this.epoch = 1640995200000; // 自定义起始时间戳
    }

    nextId() {
        const timestamp = Date.now();

        if (timestamp === this.lastTimestamp) {
          this.sequence = (this.sequence + 1) & 0xFFF; // 序列号最多12位
        } else {
          this.sequence = 0;
        }
    
        this.lastTimestamp = timestamp;
    
        const id = ((timestamp - this.epoch) << 22) | (this.workId << 12) | this.sequence;
        return id;
    }

}

export default SnowflakeIdWorker;