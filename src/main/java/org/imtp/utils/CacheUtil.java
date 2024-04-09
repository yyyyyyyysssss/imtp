package org.imtp.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.netty.channel.Channel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @Description
 * @Author ys
 * @Date 2024/4/9 16:58
 */
public class CacheUtil {

    private static final LoadingCache<Long, Channel> channelCache = CacheBuilder
            .newBuilder()
            .initialCapacity(100)
            .maximumSize(1000)
            .build(new CacheLoader<>() {
                @Override
                public Channel load(Long key) {
                    return null;
                }
            });

    public static void putChannel(Long k,Channel c){
        channelCache.put(k,c);
    }

    public static Channel getChannel(Long k){
        try {
            return channelCache.get(k);
        } catch (ExecutionException e) {
            return null;
        }
    }

}
