package ro.payitforward.pay_it_forward_transfers.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ro.payitforward.pay_it_forward_transfers.blockchain.SignatureService;
import ro.payitforward.pay_it_forward_transfers.dto.TransferActionDto;
import ro.payitforward.pay_it_forward_transfers.dto.enums.TransferActionEnum;
import ro.payitforward.pay_it_forward_transfers.entity.Transfer;
import ro.payitforward.pay_it_forward_transfers.entity.TransferRequest;
import ro.payitforward.pay_it_forward_transfers.model.TransferStatusEnum;
import ro.payitforward.pay_it_forward_transfers.repository.TransferRepository;

import java.security.SignatureException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class TransferService {

    private final TransferRepository transferRepository;

    public Page<Transfer> findByFromUserEqualsUserId(String userId, int page, int size) {
        return transferRepository.findByFromUserEquals(userId, PageRequest.of(page, size));
    }

    public Page<Transfer> findByToUserEqualsUserId(String userId, int page, int size) {
        return transferRepository.findByToUserEquals(userId, PageRequest.of(page, size));
    }

    public Transfer save(Transfer transfer) {
        return transferRepository.save(transfer);
    }

    public Transfer buildNew(TransferRequest transferRequest) {
        return Transfer.builder()
                .createdAt(Instant.now())
                .fromUser(transferRequest.getFromUser())
                .fromPublicId(transferRequest.getFromPublicId())
                .toUser(transferRequest.getToUser())
                .toPublicId(transferRequest.getToPublicId())
                .target(transferRequest.getTarget())
                .targetPublicId(transferRequest.getTargetPublicId())
                .status(TransferStatusEnum.PENDING.name())
                .build();
    }

    public Transfer findOrThrow(final UUID uuid) {
        Optional<Transfer> transfer = transferRepository.findById(uuid);
        if (transfer.isEmpty()) {
            log.error("Transfer with uuid: {} not found!", uuid);
            throw new IllegalArgumentException("Transfer with uuid: " + uuid.toString() + " not found!");
        }
        return transfer.get();
    }

    public Transfer execute(TransferActionEnum transferAction, UUID uuid, TransferActionDto data) throws SignatureException {
        final Transfer transfer = this.findOrThrow(uuid);

        return switch (transferAction) {
            case CANCEL -> cancelTransfer(transfer, data);
            case COMPLETE -> complteTransfer(transfer, data);
        };
    }

    private Transfer cancelTransfer(Transfer transfer, TransferActionDto data) {
//        assert (transfer.getToUser().equalsIgnoreCase(data.getCandidateId())
//                && transfer.getToPublicId().equalsIgnoreCase(data.getCandidatePublicId()))
//                ||
//                (transfer.getFromUser().equalsIgnoreCase(data.getCandidateId())
//                        && transfer.getFromPublicId().equalsIgnoreCase(data.getCandidatePublicId()));

        transfer.setStatus(TransferStatusEnum.CANCELED.name());
        transfer.setCancelledBy(data.getCandidateId());
        transfer.setCancelledByPublicId(data.getCandidatePublicId());
        transfer.setCancelledAt(Instant.now());

        final Transfer updated = transferRepository.save(transfer);
        log.info("Transfer {} canceled successfully!", transfer.getId());
        return updated;
    }

    private Transfer complteTransfer(Transfer transfer, TransferActionDto data) throws SignatureException {
//        assert isNotBlank(data.getSignature());
//        assert transfer.getFromUser().equalsIgnoreCase(data.getCandidateId())
//                && transfer.getFromPublicId().equalsIgnoreCase(data.getCandidatePublicId());

        validateSignature(transfer, data.getSignature());

        transfer.setStatus(TransferStatusEnum.COMPLETED.name());
        transfer.setSignature(data.getSignature());

        // TODO call book service to change the owner
        final Transfer updated = transferRepository.save(transfer);
        log.info("Transfer {} completed successfully!", transfer.getId());
        return updated;
    }

    public void validateSignature(UUID uuid, String signature) throws SignatureException {
        final Transfer transfer = this.findOrThrow(uuid);
        final String publicIdUsedToSign = SignatureService.getAddressUsedToSignHashedMessage(signature, transfer.getTargetPublicId());

//        assert transfer.getToPublicId().equals(publicIdUsedToSign);
    }

    public void validateSignature(Transfer transfer, String signature) throws SignatureException {
        final String publicIdUsedToSign = SignatureService.getAddressUsedToSignHashedMessage(signature, transfer.getTargetPublicId());
//        assert transfer.getToPublicId().equals(publicIdUsedToSign);
    }
}
