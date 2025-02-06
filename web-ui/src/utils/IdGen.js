import SnowflakeIdWorker from './SnowflakeIdWorker'

const snowflake = new SnowflakeIdWorker(1)

class IdGen{

    static nextId(){
        return snowflake.nextId().toString()
    }

}

export default IdGen

