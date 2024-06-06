package ro.payitforward.pay_it_forward_transfers.model;

import java.util.Arrays;
import java.util.Optional;

public enum TransferRequestStatusEnum {
    PENDING("PENDING"),
    CANCELED("CANCELED"),
    ACCEPTED("ACCEPTED"),
    REFUSED("REFUSED");
    private final String code;

    TransferRequestStatusEnum(String code) {
        this.code = code;
    }

    public static Optional<TransferRequestStatusEnum> get(String code) {
        return Arrays.stream(TransferRequestStatusEnum.values())
                .filter(status -> status.code.equalsIgnoreCase(code))
                .findFirst();
    }

    public static TransferRequestStatusEnum getOrThrow(String code) {
        return Arrays.stream(TransferRequestStatusEnum.values())
                .filter(status -> status.code.equalsIgnoreCase(code))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Transfer request status: " + code + " unknown!"));
    }
}
