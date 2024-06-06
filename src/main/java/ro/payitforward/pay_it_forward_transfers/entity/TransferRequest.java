package ro.payitforward.pay_it_forward_transfers.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity(name = "TransferRequest")
@Table(name = "transfer_requests")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Instant createdAt;


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
    private Instant consentDate;
}
