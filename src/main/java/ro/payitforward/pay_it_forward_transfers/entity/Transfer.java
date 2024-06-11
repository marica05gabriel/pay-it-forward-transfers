package ro.payitforward.pay_it_forward_transfers.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "Transfer")
@Table(name = "transfers")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Instant createdAt;

    @Column(nullable = true)
    private String signature;
    @Column(nullable = true)
    private String publicTransactionHash;

    @Column(nullable = false)
    private String fromUser;
    @Column(nullable = false)
    private String fromPublicId;
    @Column(nullable = false)
    private String toUser;
    @Column(nullable = false)
    private String toPublicId;
    @Column(nullable = false)
    private String target;
    @Column(nullable = false)
    private String targetPublicId;

    @Column(nullable = false)
    private String status;
    @Column(nullable = true)
    private String cancelledBy;
    @Column(nullable = true)
    private String cancelledByPublicId;
    private Instant cancelledAt;
}
