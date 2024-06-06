package ro.payitforward.pay_it_forward_transfers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransferRequestCreationDto {
    private String from;
    private String fromPublicId;
    private String to;
    private String toPublicId;
    private String target;
    private String targetPublicId;
}
