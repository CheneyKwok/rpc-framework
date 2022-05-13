package github.cheneykwok.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import github.cheneykwok.exception.SerializeException;
import github.cheneykwok.remoting.dto.RpcRequest;
import github.cheneykwok.remoting.dto.RpcResponse;
import github.cheneykwok.serialize.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Slf4j
public class KryoSerializer implements Serializer {

    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             Output output = new Output(out)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return out.toByteArray();
        } catch (Exception e) {
            throw new SerializeException("Serialization failed " + e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            Input input = new Input(in)) {
            Kryo kryo = kryoThreadLocal.get();
            T object = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(object);

        } catch (Exception e) {
            throw new SerializeException("DeSerialization failed " + e);
        }
    }
}
