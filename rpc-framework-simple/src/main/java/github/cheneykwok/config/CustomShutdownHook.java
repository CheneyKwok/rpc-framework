package github.cheneykwok.config;

import github.cheneykwok.registry.zk.util.CuratorUtils;
import github.cheneykwok.utils.threadpool.ThreadPoolFactoryUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * 注册关闭钩子
 */
public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook me() {
        return CUSTOM_SHUTDOWN_HOOK;
    }
    public void clearRegistry(int port) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            InetSocketAddress inetSocketAddress = null;
            try {
                inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), port);
                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
            } catch (UnknownHostException ignored) {
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }


}
