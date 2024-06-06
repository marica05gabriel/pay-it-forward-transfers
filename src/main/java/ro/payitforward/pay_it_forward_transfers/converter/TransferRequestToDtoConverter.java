package ro.payitforward.pay_it_forward_transfers.converter;

import org.springframework.stereotype.Component;
import ro.payitforward.pay_it_forward_transfers.dto.TransferRequestDto;
import ro.payitforward.pay_it_forward_transfers.entity.TransferRequest;

@Component
public class TransferRequestToDtoConverter implements Converter<TransferRequest, TransferRequestDto> {

    @Override
    public TransferRequestDto convert(TransferRequest transferRequest) {
        return new TransferRequestDto(
                transferRequest.getId().toString(),
                transferRequest.getCreatedAt(),
                transferRequest.getFromUser(),
                transferRequest.getFromPublicId(),
                transferRequest.getToUser(),
                transferRequest.getToPublicId(),
                transferRequest.getTarget(),
                transferRequest.getTargetPublicId(),
                transferRequest.getStatus(),
                transferRequest.getConsentDate());
    }
}
