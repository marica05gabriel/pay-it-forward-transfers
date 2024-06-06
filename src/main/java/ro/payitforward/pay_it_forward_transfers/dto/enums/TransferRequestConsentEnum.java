package ro.payitforward.pay_it_forward_transfers.dto.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TransferRequestConsentEnum {
    CANCEL("cancel"),
    ACCEPT("accept"),
    REFUSE("refuse");

    private final String code;

    TransferRequestConsentEnum(String code) {
        this.code = code;
    }

    public static Optional<TransferRequestConsentEnum> get(String code) {
        return Arrays.stream(TransferRequestConsentEnum.values())
                .filter(consent -> consent.code.equalsIgnoreCase(code))
                .findFirst();
    }

    public static TransferRequestConsentEnum getOrThrow(String code) {
        return Arrays.stream(TransferRequestConsentEnum.values())
                .filter(consent -> consent.code.equalsIgnoreCase(code))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Transfer request consent: " + code + " unknown!"));
    }
}