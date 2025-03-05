package org.imtp.desktop.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.imtp.common.packet.body.TokenInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.common.packet.body.UserSessionInfo;
import org.imtp.common.response.Result;
import org.imtp.desktop.component.OKHttpClientHelper;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.context.DefaultClientUserChannelContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @Description
 * @Author ys
 * @Date 2025/3/5 14:24
 */
public class ApiService {


    public static CompletableFuture<List<UserSessionInfo>> fetchUserSessions() {
        return CompletableFuture.supplyAsync(() -> {
            Result<List<UserSessionInfo>> result = OKHttpClientHelper.getInstance().doGet("/social/userSession/" + getUserId(), new TypeReference<Result<List<UserSessionInfo>>>() {
            });
            return result.getData();
        });
    }

    public static CompletableFuture<List<UserFriendInfo>> fetchUserFriends() {
        return CompletableFuture.supplyAsync(() -> {
            Result<List<UserFriendInfo>> result = OKHttpClientHelper.getInstance().doGet("/social/userFriend/" + getUserId(), new TypeReference<Result<List<UserFriendInfo>>>() {
            });
            return result.getData();
        });
    }

    public static CompletableFuture<List<UserGroupInfo>> fetchUserGroups() {
        return CompletableFuture.supplyAsync(() -> {
            Result<List<UserGroupInfo>> result = OKHttpClientHelper.getInstance().doGet("/social/userGroup/" + getUserId(), new TypeReference<Result<List<UserGroupInfo>>>() {
            });
            return result.getData();
        });
    }

    private static Long getUserId(){
        DefaultClientUserChannelContext clientContext = (DefaultClientUserChannelContext) ClientContextHolder.clientContext();
        TokenInfo tokenInfo = clientContext.getTokenInfo();
        return tokenInfo.getUserId();
    }


}
