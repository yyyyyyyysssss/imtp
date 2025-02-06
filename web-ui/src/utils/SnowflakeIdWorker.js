
class SnowflakeIdWorker {
  constructor(workId) {
    this.workId = workId;
    this.sequence = 0;
    this.lastTimestamp = -1;
    this.epoch = 1640995200000; // 自定义起始时间戳
    this.workerIdBits = 10
    this.sequenceBits = 12
    this.maxWorkerId = ~(-1 << this.workerIdBits)
    this.workerIdShift = this.sequenceBits
    this.timestampLeftShift = this.sequenceBits + this.workerIdBits
    this.sequenceMask = ~(-1 << this.sequenceBits)
  }

  nextId() {
    let timestamp = this.currentTimestamp()

    if (timestamp < this.lastTimestamp) {
      throw new Error("Clock moved backwards. Refusing to generate id.");
    }

    if (timestamp === this.lastTimestamp) {
      this.sequence = (this.sequence + 1) & this.sequenceMask; // 序列号最多12位
      if (this.sequence === 0) {
        timestamp = this.getNextMill()
      }
    } else {
      this.sequence = 0;
    }

    this.lastTimestamp = timestamp;
    
    return (BigInt(timestamp  - this.epoch)) << BigInt(this.timestampLeftShift) | BigInt(this.workId) << BigInt(this.workerIdShift) | BigInt(this.sequence)
  }

  getNextMill() {
    let mill = this.currentTimestamp()
    while (mill <= this.lastTimestamp) {
      mill = this.currentTimestamp();
    }
    return mill;
  }

  currentTimestamp() {

    return new Date().getTime()
  }

}

export default SnowflakeIdWorker;