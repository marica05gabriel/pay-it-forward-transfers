package ro.payitforward.pay_it_forward_transfers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferDto {
    private String id;
    private Instant createdAt;

    private String publicTransactionHash;

    private String from;
    private String fromPublicId;
    private String to;
    private String toPublicId;
    private String target;
    private String targetPublicId;

    private String status;
}
