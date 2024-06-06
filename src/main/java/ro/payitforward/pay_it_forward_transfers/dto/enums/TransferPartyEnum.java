package ro.payitforward.pay_it_forward_transfers.dto.enums;

import java.util.Arrays;
import java.util.Optional;

public enum TransferPartyEnum {
    TRANSFEROR("transferor"),
    TRANSFEREE("transferee");

    private final String code;

    TransferPartyEnum(String code) {
        this.code = code;
    }

    public static Optional<TransferPartyEnum> get(String party) {
        return Arrays.stream(TransferPartyEnum.values())
                .filter(transferParty -> transferParty.code.equalsIgnoreCase(party))
                .findFirst();
    }

    public static TransferPartyEnum getOrThrow(String party) {
        return Arrays.stream(TransferPartyEnum.values())
                .filter(transferParty -> transferParty.code.equalsIgnoreCase(party))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Transfer party: " + party + " unknown!"));
    }
}
