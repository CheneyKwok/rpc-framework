package github.cheneykwok.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * zonde 分类：
 *
 * 持久节点：一旦创建就一直存在，即使 Zookeeper 集群宕机，直到将其删除
 * 临时节点：临时节点的生命周期是与客户端（session）绑定的，会话消失则节点消失。并且，临时节点只能做叶子节点，不能创建子节点
 * 持久顺序节点：除了具有持久节点的特性之外，子节点的名称还具有顺序性，比如 /node1/app0000000001、/node1/app000000000002
 * 临时顺序节点，除了具备临时节点之外的特性，子节点的名称还具有顺序性
 */
public class CuratorOp {

    public static CuratorFramework initZK() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .connectString("1.15.156.232:2181")
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        return zkClient;
    }

    public static void main(String[] args) throws Exception {

        CuratorFramework zk = initZK();
//        zk.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath("/node1/0001");
//        zk.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node1/0002");
//        zk.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/node2/0001");
        zk.setData().forPath("/node2", "java".getBytes());
        Run run = new Run();
        new Thread(run).start();

    }


}

class Run implements Runnable {
    @Override
    public void run() {
        String path = "/node1";
        PathChildrenCache childrenCache = new PathChildrenCache(CuratorOp.initZK(), path, true);
        PathChildrenCacheListener listener = ((curatorFramework, pathChildrenCacheEvent) -> {
            System.out.println("receive: " + pathChildrenCacheEvent.getType());
            System.out.println("receive data: " + pathChildrenCacheEvent.getData());
        });
        childrenCache.getListenable().addListener(listener);
        try {
            childrenCache.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
