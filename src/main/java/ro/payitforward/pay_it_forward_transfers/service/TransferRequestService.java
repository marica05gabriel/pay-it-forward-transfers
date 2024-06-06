package ro.payitforward.pay_it_forward_transfers.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ro.payitforward.pay_it_forward_transfers.dto.TransferRequestConsentDto;
import ro.payitforward.pay_it_forward_transfers.dto.TransferRequestCreationDto;
import ro.payitforward.pay_it_forward_transfers.dto.enums.TransferRequestConsentEnum;
import ro.payitforward.pay_it_forward_transfers.entity.Transfer;
import ro.payitforward.pay_it_forward_transfers.entity.TransferRequest;
import ro.payitforward.pay_it_forward_transfers.model.TransferRequestStatusEnum;
import ro.payitforward.pay_it_forward_transfers.repository.TransferRequestRepository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class TransferRequestService {

    private final TransferRequestRepository transferRequestRepository;
    private final TransferService transferService;

    public TransferRequest findOrThrow(final UUID uuid) {
        Optional<TransferRequest> transferRequest = transferRequestRepository.findById(uuid);
        if (transferRequest.isEmpty()) {
            log.error("Transfer request with uuid: {} not found!", uuid);
            throw new IllegalArgumentException("Transfer request with uuid: " + uuid + " not found!");
        }
        return transferRequest.get();
    }

    public Page<TransferRequest> findByFromUserEqualsUserId(String userId, int page, int size) {
        return transferRequestRepository.findByFromUserEquals(userId, PageRequest.of(page, size));
    }

    public Page<TransferRequest> findByToUserEqualsUserId(String userId, int page, int size) {
        return transferRequestRepository.findByToUserEquals(userId, PageRequest.of(page, size));
    }

    public final TransferRequest create(TransferRequestCreationDto data) {
        // TODO validate target ownership by querying blockchain [targetPublicId, fromPublicId]
        // TODO validate target ownership by calling book service [targetId, targetPublicId, fromId]

        log.info("Transfer request creation...");
        final TransferRequest transferRequest = buildNew(data);
        final TransferRequest result = transferRequestRepository.save(transferRequest);
        log.info("Transfer request created: {}.", result);
        return result;
    }

    public Transfer consent(TransferRequestConsentEnum consent, String uuid, TransferRequestConsentDto consentData) {
        final TransferRequest transferRequest = findOrThrow(UUID.fromString(uuid));

        if (TransferRequestConsentEnum.CANCEL.equals(consent)
                && !isCandidateTheInitiator(transferRequest, consentData)) {
            log.error("Candidate is not the initiator of the transfer request with uuid: {}.", uuid);
            throw new IllegalArgumentException("Candidate is not the initiator of the transfer request with uuid: " + uuid);
        }
        if (!TransferRequestConsentEnum.CANCEL.equals(consent)
                && !isCandidateTheOwner(transferRequest, consentData)) {
            log.error("Candidate is not the owner of the targeted object! Transfer request uuid: {}.", uuid);
            throw new IllegalArgumentException("Candidate is not the owner of the targeted object! Transfer request uuid: " + uuid);
        }

        transferRequest.setStatus(consent.name());
        transferRequest.setConsentDate(Instant.now());

        final Transfer newTransfer = transferService.buildNew(transferRequest);

        transferRequestRepository.save(transferRequest);
        final Transfer result = transferService.save(newTransfer);

        log.info("Transfer created with uuid: {} and status: {}.", result.getId(), result.getStatus());
        return result;
    }


    private boolean isCandidateTheInitiator(TransferRequest transferRequest, TransferRequestConsentDto consentData) {
        return transferRequest.getToUser().equals(consentData.getCandidateId())
                && transferRequest.getToPublicId().equals(consentData.getCandidatePublicId());
    }

    private boolean isCandidateTheOwner(TransferRequest transferRequest, TransferRequestConsentDto consentData) {
        return transferRequest.getFromUser().equals(consentData.getCandidateId())
                && transferRequest.getFromPublicId().equals(consentData.getCandidatePublicId());
    }

    private TransferRequest buildNew(TransferRequestCreationDto data) {
        return TransferRequest.builder()
                .fromUser(data.getFrom())
                .fromPublicId(data.getFromPublicId())
                .toUser(data.getTo())
                .toPublicId(data.getToPublicId())
                .target(data.getTarget())
                .targetPublicId(data.getTargetPublicId())
                .status(TransferRequestStatusEnum.PENDING.name())
                .createdAt(Instant.now())
                .build();
    }
}
