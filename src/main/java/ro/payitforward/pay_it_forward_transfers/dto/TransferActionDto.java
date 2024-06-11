package ro.payitforward.pay_it_forward_transfers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferActionDto {
    private String candidateId;
    private String candidatePublicId;
    private String publicTransactionHash;
    private String signature;
}
