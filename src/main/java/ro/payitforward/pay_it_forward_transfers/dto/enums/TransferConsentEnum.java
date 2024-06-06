package ro.payitforward.pay_it_forward_transfers.dto.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TransferConsentEnum {
    COMPLETE("complete"),
    CANCEL("cancel");

    private final String code;

    TransferConsentEnum(String code) {
        this.code = code;
    }

    public static Optional<TransferConsentEnum> get(String code) {
        return Arrays.stream(TransferConsentEnum.values())
                .filter(consent -> consent.code.equalsIgnoreCase(code))
                .findFirst();
    }

    public static TransferConsentEnum getOrThrow(String code) {
        return Arrays.stream(TransferConsentEnum.values())
                .filter(consent -> consent.code.equalsIgnoreCase(code))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Transfer consent: " + code + " unknown!"));
    }
}
