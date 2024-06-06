package ro.payitforward.pay_it_forward_transfers.dto.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TransferActionEnum {
    CANCEL("CANCEL"),
    COMPLETE("COMPLETE");

    private final String code;

    TransferActionEnum(String code) {
        this.code = code;
    }

    public static Optional<TransferActionEnum> get(String codeToCheck) {
        return Arrays.stream(TransferActionEnum.values())
                .filter(action -> action.code.equalsIgnoreCase(codeToCheck))
                .findFirst();
    }

    public static TransferActionEnum getOrThrow(String codeToCheck) {
        return Arrays.stream(TransferActionEnum.values())
                .filter(action -> action.code.equalsIgnoreCase(codeToCheck))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Transfer consent: " + codeToCheck + " unknown!"));
    }
}
