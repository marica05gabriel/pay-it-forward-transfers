package ro.payitforward.pay_it_forward_transfers.model;

import java.util.Arrays;
import java.util.Optional;

public enum TransferStatusEnum {
    PENDING("PENDING"),
    COMPLETED("COMPLETED"),
    CANCELED("CANCELED");

    TransferStatusEnum(String code) {
        this.code = code;
    }

    private final String code;

    public static Optional<TransferStatusEnum> get(String code) {
        return Arrays.stream(TransferStatusEnum.values())
                .filter(status -> status.code.equalsIgnoreCase(code))
                .findFirst();
    }

    public static TransferStatusEnum getOrThrow(String code) {
        return Arrays.stream(TransferStatusEnum.values())
                .filter(status -> status.code.equalsIgnoreCase(code))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Transfer status: " + code + " unknown!"));
    }

}
