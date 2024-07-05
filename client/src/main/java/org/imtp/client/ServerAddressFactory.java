package org.imtp.client;

import org.imtp.common.enums.ServerModel;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/5 14:16
 */
public class ServerAddressFactory {

    public static ServerAddress getServerAddress(){
        Config config = Config.getInstance();
        return getServerAddress(config.getModel());
    }

    public static ServerAddress getServerAddress(ServerModel model){
        switch (model){
            case HOST :
                return HostServerAddress.getInstance();
            case CLUSTER:
                return ClusterServerAddress.getInstance();
            default:
                throw new UnsupportedOperationException("未知的操作类型：" + model);
        }
    }

}
