package github.cheneykwok.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public enum RpcResponseCodeEnum {

    SUCCESS(200, "The remote call is successful"),

    FAIL(500, "The remote call is fail");

    private final int code;

    private final String message;
}
