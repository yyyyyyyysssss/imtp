package org.imtp.api.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ys
 * @Date 2024/7/9 16:13
 */
@Slf4j
public class RedisHelper {

    private RedisTemplate<String, Object> redisTemplate;
    private GenericJackson2JsonRedisSerializer serializer;
    private ObjectMapper objectMapper;

    public RedisHelper(RedisTemplate<String, Object> redisTemplate, GenericJackson2JsonRedisSerializer serializer, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.serializer = serializer;
        this.objectMapper = objectMapper;
    }

    /* ========================== key ========================== */

    /**
     * 判断 key 是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 删除一个或多个 key
     */
    public Boolean delete(String... keys) {

        return redisTemplate.delete(Arrays.asList(keys)) == keys.length;
    }

    /**
     * 设置 key 过期时间
     */
    public Boolean expire(String k, Duration duration) {
        return redisTemplate.expire(k, duration);
    }

    /**
     * 以秒为单位的 TTL
     */
    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 以毫秒为单位的 TTL
     */
    public long pTtl(String key) {

        return redisTemplate.getExpire(key, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * 移除 key 的过期时间，使其永久有效
     */
    public boolean persist(String key) {
        return Boolean.TRUE.equals(redisTemplate.persist(key));
    }

    /**
     * 正则匹配获取 keys（若 key 数量大建议使用 scan 而不是 keys）
     */
    public Set<String> keys(String pattern) {

        return redisTemplate.keys(pattern);
    }

    /**
     * 使用 SCAN 遍历全局 keys（非阻塞）
     */
    public Set<String> scan(String pattern) {
        Set<String> result = new HashSet<>();
        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(500)
                .build();

        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                cursor.forEachRemaining(item ->
                        result.add(new String(item, StandardCharsets.UTF_8))
                );
            } catch (Exception e) {
                log.error("RedisHelper scan error", e);
            }
            return null; // 执行结果不需要返回
        });

        return result;
    }

    /* ========================== String ========================== */

    /**
     * 设置字符串 key 对应的值
     */
    public void setValue(String key, Object object) {
        setValue(key, object, null);
    }

    /**
     * 设置字符串 key 对应的值 支持过期时间。
     */
    public void setValue(String key, Object object, @Nullable Duration duration) {
        if (duration != null) {
            redisTemplate.opsForValue().set(key, object, duration);
        } else {
            redisTemplate.opsForValue().set(key, object);
        }
    }

    /**
     * 获取字符串 key 对应的值
     */
    public Object getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public <T> T getValue(String key, Class<T> clazz) {
        Object obj = getValue(key);
        return deserialize(obj, clazz);
    }

    /**
     * 批量获取字符串 key 对应的值
     */
    public List<Object> multiGetValue(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        return redisTemplate.opsForValue().multiGet(keys);
    }

    public <T> List<T> multiGetValue(Collection<String> keys, Class<T> clazz) {
        List<Object> objects = multiGetValue(keys);
        if (objects == null) {
            return Collections.emptyList();
        }
        return objects.stream().filter(Objects::nonNull).map(m -> deserialize(m, clazz)).collect(Collectors.toList());
    }

    /**
     * 将 key 对应的整数自增1
     */
    public Long incr(String key) {
        return incr(key, 1);
    }

    /**
     * 将 key 对应的整数增加指定 delta
     */
    public Long incr(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 将 key 对应的整数自减1
     */
    public Long decr(String key) {
        return decr(key, 1);
    }

    /**
     * 将 key 对应的整数减少指定 delta
     */
    public Long decr(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /* ========================== Hash ========================== */

    /**
     * 向 Hash 写入一个字段
     */
    public void addHash(String k, String field, Object value) {
        addHash(k, field, value, null);
    }

    /**
     * 向 Hash 写入一个字段并设置过期时间
     * 注意：Redis Hash 不支持 field 级别的 TTL，此处设置的过期时间作用于整个 key
     */
    public void addHash(String k, String field, Object value, Duration duration) {
        Map<String, Object> kv = new HashMap<>();
        kv.put(field, value);
        addHash(k, kv, duration);
    }

    /**
     * 向 Hash 批量写入多个字段
     */
    public void addHash(String k, Map<String, Object> kv) {
        addHash(k, kv, null);
    }

    /**
     * 向 Hash 批量写入多个字段，可设置 Hash 整体 TTL
     * 注意：如果 key 已存在并设置过 TTL，此处重新设置 TTL 会刷新整个 Hash 的过期时间
     */
    public void addHash(String k, Map<String, Object> kv, Duration duration) {
        HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(k, kv);
        if (duration != null) {
            expire(k, duration);
        }
    }

    /**
     * 获取 Hash 中某个 field 的值
     */
    public Object getHash(String k, String field) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.get(k, field);
    }

    /**
     * 获取 Hash 中某个 field，并反序列化为指定类型
     */
    public <T> T getHash(String k, String field, Class<T> clazz) {
        Object object = redisTemplate.opsForHash().get(k, field);
        return deserialize(object, clazz);
    }

    /**
     * 获取整个 Hash 的所有字段和值
     */
    public Map<String, Object> getHashAll(String k) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.entries(k);
    }

    /**
     * 获取整个 Hash 并转换为指定类型的值 Map
     */
    public <T> Map<String, T> getHashAll(String k, Class<T> clazz) {
        Map<String, Object> raw = getHashAll(k);
        if (raw == null || raw.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, T> result = new LinkedHashMap<>(raw.size());
        for (Map.Entry<String, Object> e : raw.entrySet()) {
            T value = deserialize(e.getValue(), clazz);
            result.put(e.getKey(), value);
        }
        return result;
    }

    /**
     * 对 Hash 中指定 field 进行自增操作
     * 若 field 不存在则初始化为 delta
     */
    public Long incrHash(String k, String field) {
        return incrHash(k, field, 1);
    }

    /**
     * 对 Hash 中指定 field 自增 delta（必须为整数类型）
     */
    public Long incrHash(String k, String field, long delta) {
        HashOperations<String, String, Object> hashOperations = redisTemplate.opsForHash();
        return hashOperations.increment(k, field, delta);
    }

    /**
     * 对 Hash 中指定 field 进行自减操作
     * 若 field 不存在则初始化为 delta
     */
    public Long decrHash(String key, String field) {
        return decrHash(key, field, 1);
    }

    /**
     * 对 Hash 中指定 field 自减 delta（必须为整数类型）
     */
    public Long decrHash(String key, String field, long delta) {
        return redisTemplate.opsForHash().increment(key, field, -delta);
    }

    /* ========================== Set ========================== */

    /**
     * 向 Set 中添加一个或多个元素
     */
    public Long addSet(String key, Object... values) {
        return addSet(key, null, values);
    }

    /**
     * 向 Set 中添加元素并设置过期时间
     */
    public Long addSet(String key, Duration duration, Object... values) {
        Long count = redisTemplate.opsForSet().add(key, values);
        if (duration != null) {
            expire(key, duration);
        }
        return count;
    }

    /**
     * 获取 Set 中所有元素
     */
    public Set<Object> getSetMembers(String key) {

        return redisTemplate.opsForSet().members(key);
    }

    public <T> Set<T> getSetMembers(String key, Class<T> clazz) {
        Set<Object> raw = getSetMembers(key);
        Set<T> result = new LinkedHashSet<>(raw.size());
        for (Object obj : raw) {
            result.add(deserialize(obj, clazz));
        }
        return result;
    }

    /**
     * 判断某值是否存在于 Set 中
     */
    public Boolean setIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 删除 Set 中的一个或多个元素
     */
    public Long removeSet(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /* ========================== ZSet ========================== */

    /**
     * 向 ZSet 添加一个元素
     */
    public Boolean addZSet(String key, Object value, double score) {
        return addZSet(key, value, score, null);
    }

    /**
     * 随机获取一个元素（不移除）
     */
    public Object setSetRandomMember(String key) {
        return redisTemplate.opsForSet().randomMember(key);
    }

    public <T> T setSetRandomMember(String key, Class<T> clazz) {
        Object raw = setSetRandomMember(key);
        return deserialize(raw, clazz);
    }

    /**
     * 随机获取多个元素（不移除）
     */
    public List<Object> setRandomMembers(String key, long count) {

        return redisTemplate.opsForSet().randomMembers(key, count);
    }

    /**
     * 随机获取多个元素（反序列化）
     */
    public <T> List<T> setRandomMembers(String key, long count, Class<T> clazz) {
        List<Object> raw = setRandomMembers(key, count);
        List<T> result = new ArrayList<>(raw.size());
        for (Object obj : raw) {
            result.add(deserialize(obj, clazz));
        }
        return result;
    }

    /**
     * 随机弹出一个元素（移除）
     */
    public Object setPop(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    public <T> T setPop(String key, Class<T> clazz) {
        Object raw = setPop(key);
        return deserialize(raw, clazz);
    }

    /**
     * 并集：key ∪ otherKeys
     */
    public Set<Object> setUnion(String key, Collection<String> otherKeys) {

        return redisTemplate.opsForSet().union(key, otherKeys);
    }

    public <T> Set<T> setUnion(String key, Collection<String> otherKeys, Class<T> clazz) {
        Set<Object> raw = setUnion(key, otherKeys);
        Set<T> result = new LinkedHashSet<>(raw.size());
        for (Object obj : raw) {
            result.add(deserialize(obj, clazz));
        }
        return result;
    }

    /**
     * 交集：key ∩ otherKeys
     */
    public Set<Object> setIntersect(String key, Collection<String> otherKeys) {

        return redisTemplate.opsForSet().intersect(key, otherKeys);
    }

    public <T> Set<T> setIntersect(String key, Collection<String> otherKeys, Class<T> clazz) {
        Set<Object> raw = setIntersect(key, otherKeys);
        Set<T> result = new LinkedHashSet<>(raw.size());
        for (Object obj : raw) {
            result.add(deserialize(obj, clazz));
        }
        return result;
    }

    /**
     * 差集：key - otherKeys
     */
    public Set<Object> setDiff(String key, Collection<String> otherKeys) {

        return redisTemplate.opsForSet().difference(key, otherKeys);
    }

    public <T> Set<T> setDiff(String key, Collection<String> otherKeys, Class<T> clazz) {
        Set<Object> raw = setDiff(key, otherKeys);
        Set<T> result = new LinkedHashSet<>(raw.size());
        for (Object obj : raw) {
            result.add(deserialize(obj, clazz));
        }
        return result;
    }


    /**
     * 向 ZSet 添加一个元素 并可设置过期时间
     */
    public Boolean addZSet(String key, Object value, double score, Duration duration) {
        Boolean b = redisTemplate.opsForZSet().add(key, value, score);
        if (duration != null) {
            expire(key, duration);
        }
        return b;
    }

    /**
     * 按区间（start/end）从 ZSet 查询元素（按 score 升序）
     */
    public List<Object> rangeZSet(String key, long start, long end) {
        Set<Object> set = redisTemplate.opsForZSet().range(key, start, end);
        if (set.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(set);
    }

    public <T> List<T> rangeZSet(String key, long start, long end, Class<T> clazz) {
        List<Object> objects = rangeZSet(key, start, end);
        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }
        return objects.stream().filter(Objects::nonNull).map(m -> deserialize(m, clazz)).collect(Collectors.toList());
    }

    /**
     * 获取 ZSet 的全部数据
     */
    public List<Object> rangeAllZSet(String key) {

        return rangeZSet(key, 0, -1);
    }

    public <T> List<T> rangeAllZSet(String key, Class<T> clazz) {

        return rangeZSet(key, 0, -1, clazz);
    }

    /**
     * 根据 score 区间获取 ZSet 元素
     */
    public List<Object> rangeByScoreZSet(String key, double min, double max) {
        Set<Object> set = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        if (set.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(set);
    }

    public <T> List<T> rangeByScoreZSet(String key, double min, double max, Class<T> clazz) {
        List<Object> objects = rangeByScoreZSet(key, min, max);
        if (objects == null || objects.isEmpty()) {
            return Collections.emptyList();
        }
        return objects.stream().filter(Objects::nonNull).map(m -> deserialize(m, clazz)).toList();
    }

    /**
     * 按区间获取 ZSet 元素，包含 score（即 TypedTuple）
     */
    public List<ZSetOperations.TypedTuple<Object>> rangeScoreZSet(String key, long start, long end) {
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        if (typedTuples.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(typedTuples);
    }

    public <T> List<ZSetOperations.TypedTuple<T>> rangeScoreZSet(String key, long start, long end, Class<T> clazz) {
        List<ZSetOperations.TypedTuple<Object>> typedTuples = rangeScoreZSet(key, start, end);
        if (typedTuples == null || typedTuples.isEmpty()) {
            return Collections.emptyList();
        }
        List<ZSetOperations.TypedTuple<T>> result = new ArrayList<>();
        for (ZSetOperations.TypedTuple<Object> tuple : typedTuples) {
            T value = deserialize(tuple.getValue(), clazz);
            Double score = tuple.getScore();
            result.add(new DefaultTypedTuple<>(value, score));
        }
        return result;
    }

    /**
     * 获取 ZSet 的元素总数
     */
    public Long countZSet(String key) {
        return redisTemplate.opsForZSet().zCard(key);
    }

    /**
     * 移除 ZSet 中的指定元素
     */
    public Long removeZSet(String key, Object... values) {

        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 按 score 区间移除 ZSet 元素
     */
    public Long removeZSetByScore(String key, double min, double max) {

        return redisTemplate.opsForZSet().removeRangeByScore(key, min, max);
    }

    /* ========================== List ========================== */

    /**
     * 从左侧（头部）推入一个元素
     */
    public Long listLeftPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 从左侧批量推入元素
     */
    public Long listLeftPushAll(String key, Collection<?> values) {
        if (values == null || values.isEmpty()) return 0L;
        return redisTemplate.opsForList().leftPushAll(key, values.toArray());
    }

    /**
     * 从右侧（尾部）推入一个元素
     */
    public Long listRightPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * 从右侧批量推入元素
     */
    public Long listRightPushAll(String key, Collection<?> values) {
        if (values == null || values.isEmpty()) return 0L;
        return redisTemplate.opsForList().rightPushAll(key, values.toArray());
    }

    /**
     * 左侧弹出一个元素
     */
    public Object listLeftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    public <T> T listLeftPop(String key, Class<T> clazz) {
        Object obj = listLeftPop(key);
        return deserialize(obj, clazz);
    }

    /**
     * 右侧弹出一个元素
     */
    public Object listRightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public <T> T listRightPop(String key, Class<T> clazz) {
        Object obj = listRightPop(key);
        return deserialize(obj, clazz);
    }

    /**
     * 获取指定范围内的元素
     */
    public List<Object> listRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    public <T> List<T> listRange(String key, long start, long end, Class<T> clazz) {
        List<Object> raw = listRange(key, start, end);
        if (raw == null || raw.isEmpty()){
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(raw.size());
        for (Object o : raw) {
            result.add(deserialize(o, clazz));
        }
        return result;
    }

    /**
     * 获取 List 的所有元素，相当于 range(key, 0, -1)
     */
    public List<Object> listRangeAll(String key) {
        List<Object> raw = listRange(key, 0, -1);
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }
        return raw;
    }

    /**
     * 获取 List 的所有元素并反序列化为指定类型
     */
    public <T> List<T> listRangeAll(String key, Class<T> clazz) {
        return listRange(key,0,-1,clazz);
    }

    /**
     * 根据下标获取元素
     */
    public Object listIndex(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    public <T> T listIndex(String key, long index, Class<T> clazz) {
        Object obj = listIndex(key, index);
        return deserialize(obj, clazz);
    }

    /**
     * 获取 List 长度
     */
    public Long listSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /* ========================== GEO ========================== */

    /**
     * 向 GEO 中添加一个位置（value → 经度/纬度）
     */
    public Long geoAdd(String key, double longitude, double latitude, Object value) {
        return geoAdd(key, longitude, latitude, value, null);
    }

    /**
     * 向 GEO 中添加位置并设置过期时间
     */
    public Long geoAdd(String key, double longitude, double latitude, Object value, Duration duration) {
        Long count = redisTemplate.opsForGeo()
                .add(key, new Point(longitude, latitude), value);
        if (duration != null) {
            expire(key, duration);
        }
        return count;
    }

    /**
     * 批量添加 GEO 位置
     */
    public Long geoAdd(String key, Map<Object, Point> memberLocations) {
        return geoAdd(key, memberLocations, null);
    }

    /**
     * 批量添加 GEO 位置并设置过期时间
     */
    public Long geoAdd(String key, Map<Object, Point> memberLocations, Duration duration) {
        Set<RedisGeoCommands.GeoLocation<Object>> geoLocations = new HashSet<>();
        for (Map.Entry<Object, Point> entry : memberLocations.entrySet()) {
            geoLocations.add(new RedisGeoCommands.GeoLocation<>(entry.getKey(), entry.getValue()));
        }
        Long count = redisTemplate.opsForGeo().add(key, geoLocations);
        if (duration != null) {
            expire(key, duration);
        }
        return count;
    }

    /**
     * 获取一个或多个 member 的坐标位置
     */
    public List<Point> geoPos(String key, Object... members) {
        return redisTemplate.opsForGeo().position(key, members);
    }

    public <T> Map<T, Point> geoPos(String key, Class<T> clazz, Object... members) {
        List<Point> points = geoPos(key, members);
        Map<T, Point> result = new LinkedHashMap<>();
        for (int i = 0; i < members.length; i++) {
            T val = deserialize(members[i], clazz);
            result.put(val, points.get(i));
        }
        return result;
    }

    /**
     * 计算两个 member 之间的距离
     */
    public Distance geoDist(String key, Object member1, Object member2) {
        return redisTemplate.opsForGeo().distance(key, member1, member2);
    }

    /**
     * 计算两个 member 之间的距离（指定单位）
     */
    public Distance geoDist(String key, Object member1, Object member2, Metric metric) {
        return redisTemplate.opsForGeo().distance(key, member1, member2, metric);
    }

    /**
     * 获取一个或多个 member 的 GEO Hash 值
     */
    public List<String> geoHash(String key, Object... members) {
        return redisTemplate.opsForGeo().hash(key, members);
    }

    /**
     * 按给定经纬度为中心，搜索范围内的地理位置元素 默认米
     *
     * @param key Redis Key
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离数值
     * @param limit 返回最大数量（可用于分页）
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoRadius(
            String key,
            double longitude,
            double latitude,
            double distance,
            long limit
    ) {
        // 米 → 千米
        double distanceKm = distance / 1000.0;
        return geoRadius(key,longitude,latitude,distanceKm,Metrics.KILOMETERS,limit);
    }

    public <T> GeoResults<RedisGeoCommands.GeoLocation<T>> geoRadius(
            String key,
            double longitude,
            double latitude,
            double distance,
            long limit,
            Class<T> clazz
    ) {
        double distanceKm = distance / 1000.0;
        return geoRadius(key,longitude,latitude,distanceKm,Metrics.KILOMETERS,limit,clazz);
    }

    /**
     * 按给定经纬度为中心，搜索范围内的地理位置元素
     *
     * @param key Redis Key
     * @param longitude 经度
     * @param latitude 纬度
     * @param distance 距离数值
     * @param metrics 距离单位
     * @param limit 返回最大数量（可用于分页）
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoRadius(
            String key,
            double longitude,
            double latitude,
            double distance,
            Metrics metrics,
            long limit
    ) {
        Circle circle = new Circle(new Point(longitude, latitude), new Distance(distance, metrics));

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates()  // 返回坐标
                .includeDistance()     // 返回距离
                .sortAscending()       // 按距离正序排序
                .limit(limit);         // 返回数量限制

        return redisTemplate.opsForGeo().radius(key, circle, args);
    }

    public <T> GeoResults<RedisGeoCommands.GeoLocation<T>> geoRadius(
            String key,
            double longitude,
            double latitude,
            double distance,
            Metrics metrics,
            long limit,
            Class<T> clazz
    ) {
        GeoResults<RedisGeoCommands.GeoLocation<Object>> results = geoRadius(key,longitude,latitude,distance,metrics,limit);
        if (results == null || results.getContent().isEmpty()) {
            return new GeoResults<>(Collections.emptyList());
        }

        // 转换泛型
        List<GeoResult<RedisGeoCommands.GeoLocation<T>>> mapped = new ArrayList<>();
        for (GeoResult<RedisGeoCommands.GeoLocation<Object>> item : results) {
            RedisGeoCommands.GeoLocation<Object> location = item.getContent();
            T val = deserialize(location.getName(), clazz);  // 反序列化泛型对象
            RedisGeoCommands.GeoLocation<T> newLoc = new RedisGeoCommands.GeoLocation<>(val, location.getPoint());
            mapped.add(new GeoResult<>(newLoc, item.getDistance()));
        }
        return new GeoResults<>(mapped, results.getAverageDistance());
    }

    /**
     * 按 member 搜索附近元素（默认返回全部，无 limit）
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoRadiusByMemberAll(
            String key,
            Object member,
            double distanceMeters
    ) {
        double distanceKm = distanceMeters / 1000.0;
        return geoRadiusByMember(key, member, distanceKm,Metrics.KILOMETERS,Long.MAX_VALUE);
    }

    /**
     * 按 member 搜索附近元素（默认返回全部，无 limit）
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoRadiusByMemberAll(
            String key,
            Object member,
            double distanceMeters,
            Metrics metrics
    ) {
        return geoRadiusByMember(key, member, distanceMeters,metrics,Long.MAX_VALUE);
    }

    /**
     * 按指定成员（member）的位置为中心，搜索范围内的地理位置元素
     *
     * @param key Redis Key
     * @param member 成员名称（之前通过 geoAdd 保存）
     * @param distance 距离数值
     * @param metrics 距离单位
     * @param limit 返回最大数量
     */
    public GeoResults<RedisGeoCommands.GeoLocation<Object>> geoRadiusByMember(
            String key,
            Object member,
            double distance,
            Metrics metrics,
            long limit
    ) {
        Distance dis = new Distance(distance, metrics);

        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates()
                .includeDistance()
                .sortAscending()
                .limit(limit);

        return redisTemplate.opsForGeo().radius(key, member, dis, args);
    }

    /**
     * 删除 GEO 中的某个元素
     */
    public Long geoRemove(String key, Object... members) {
        return redisTemplate.opsForZSet().remove(key, members); // GEO 底层就是 ZSet
    }

    /* ========================== BITMAP ========================== */

    /**
     * 设置 bitmap 指定位为 true/false
     *
     * @param key Redis key
     * @param offset 位下标（从 0 开始）
     * @param value true = 1, false = 0
     * @return 设置前的旧值（true/false）
     */
    public Boolean bitmapSet(String key, long offset, boolean value) {
        return redisTemplate.opsForValue().setBit(key, offset, value);
    }

    /**
     * 获取 bitmap 指定位的值
     */
    public Boolean bitmapGet(String key, long offset) {
        return redisTemplate.opsForValue().getBit(key, offset);
    }

    /**
     * 统计整个 bitmap 中为 1 的位数量
     *
     * @param key Redis key
     * @return 1 的位数量（若 key 不存在返回 0）
     */
    public Long bitmapCount(String key) {
        if (key == null) return 0L;
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        return redisTemplate.execute((RedisConnection connection) -> connection.stringCommands().bitCount(keyBytes));
    }

    /**
     * 统计 bitmap 指定字节区间内为 1 的位数量（start/end 单位为字节）
     *
     * @param key   Redis key
     * @param start 起始字节索引（包含），可为 0
     * @param end   结束字节索引（包含），可为 -1 表示到末尾
     * @return 指定位区间内 1 的数量
     */
    public Long bitmapCount(String key, long start, long end) {
        if (key == null) return 0L;
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        return redisTemplate.execute((RedisConnection connection) -> connection.stringCommands().bitCount(keyBytes, start, end));
    }

    /**
     * 对一个或多个 Bitmap 执行位操作（如 AND / OR / XOR / NOT）
     *
     * @param op      BitOperation.AND / OR / XOR / NOT
     * @param destKey 结果写入的 key
     * @param keys    参与运算的 key 列表
     * @return 写入 destKey 的 bitmap 的字节长度
     */
    public Long bitmapOp(RedisStringCommands.BitOperation op, String destKey, String... keys) {
        if (destKey == null || keys == null || keys.length == 0) {
            return 0L;
        }

        byte[] dest = destKey.getBytes(StandardCharsets.UTF_8);

        byte[][] sources = Arrays.stream(keys)
                .map(k -> k.getBytes(StandardCharsets.UTF_8))
                .toArray(byte[][]::new);

        return redisTemplate.execute((RedisConnection connection) ->
                // 直接调用 RedisConnection 的 bitOp，更稳、更兼容
                connection.stringCommands().bitOp(op, dest, sources)
        );
    }

    /**
     * 获取 Bitmap 的长度（单位：bit）
     */
    public Long bitmapLength(String key) {
        Long byteLength = redisTemplate.execute((RedisConnection connection) ->
                connection.stringCommands().strLen(key.getBytes(StandardCharsets.UTF_8))
        );
        return byteLength * 8;
    }


    @SuppressWarnings("unchecked")
    private <T> T deserialize(Object obj, Class<T> clazz) {
        if (obj == null) return null;
        if (clazz.isInstance(obj)) {
            return (T) obj;
        }
        if (obj instanceof byte[]) {
            return serializer.deserialize((byte[]) obj, clazz);
        }
        if (obj instanceof String) {
            return serializer.deserialize(((String) obj).getBytes(StandardCharsets.UTF_8), clazz);
        }
        if (obj instanceof Map) {
            // 使用 ObjectMapper
            return objectMapper.convertValue(obj, clazz);
        }
        throw new IllegalArgumentException("Unsupported redis value type: " + obj.getClass());
    }

}
