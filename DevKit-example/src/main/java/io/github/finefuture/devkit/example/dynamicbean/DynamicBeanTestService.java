package io.github.finefuture.devkit.example.dynamicbean;

import com.google.common.collect.Lists;
import org.redisson.api.ClusterNode;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author longqiang
 * @version 1.0
 */
@Service
public class DynamicBeanTestService {

    @Autowired
    private RedissonClient redissonClient;

    public List<String> getNewRedissonAddress() {
        Collection<ClusterNode> nodes = redissonClient.getClusterNodesGroup().getNodes();
        List<String> newRedissonAddresses = Lists.newArrayList();
        nodes.forEach(node -> newRedissonAddresses.add(node.getAddr().toString()));
        return newRedissonAddresses;
    }

}
