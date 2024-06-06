package ro.payitforward.pay_it_forward_transfers.converter;

import org.springframework.stereotype.Component;
import ro.payitforward.pay_it_forward_transfers.dto.TransferDto;
import ro.payitforward.pay_it_forward_transfers.entity.Transfer;

@Component
public class TransferToDtoConverter implements Converter<Transfer, TransferDto> {
    @Override
    public TransferDto convert(Transfer transfer) {
        return new TransferDto(
                transfer.getId().toString(),
                transfer.getCreatedAt(),
                transfer.getPublicTransactionHash(),
                transfer.getFromUser(),
                transfer.getFromPublicId(),
                transfer.getToUser(),
                transfer.getToPublicId(),
                transfer.getTarget(),
                transfer.getTargetPublicId(),
                transfer.getStatus());
    }
}
