package github.cheneykwok.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "kyro"),

    NULL(Byte.MIN_VALUE, "");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        return Arrays.stream(values()).filter(e -> e.code == code).findAny().orElse(NULL).name;
    }
}
