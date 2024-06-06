package ro.payitforward.pay_it_forward_transfers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ro.payitforward.pay_it_forward_transfers.entity.TransferRequest;

import java.util.UUID;

public interface TransferRequestRepository extends
        PagingAndSortingRepository<TransferRequest, UUID>,
        CrudRepository<TransferRequest, UUID> {

    Page<TransferRequest> findByFromUserEquals(String fromUser, Pageable pageable);

    Page<TransferRequest> findByToUserEquals(String toUser, Pageable pageable);
}
