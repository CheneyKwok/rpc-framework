package github.cheneykwok.remoting.transport.netty.client;


import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ChannelProvider {

    private final Map<String, Channel> channelMap;

    public ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress inetSocketAddress) {
        Channel channel = channelMap.get(inetSocketAddress.toString());
        if (channel != null && !channel.isActive()) {
            remove(inetSocketAddress);
        }
        return channel;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        channelMap.put(inetSocketAddress.toString(), channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        channelMap.remove(inetSocketAddress.toString());
        log.info("Channel map size: [{}]", channelMap.size());
    }
}
