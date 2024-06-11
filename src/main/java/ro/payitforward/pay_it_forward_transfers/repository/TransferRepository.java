package ro.payitforward.pay_it_forward_transfers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import ro.payitforward.pay_it_forward_transfers.entity.Transfer;

import java.util.Collection;
import java.util.UUID;

public interface TransferRepository
        extends CrudRepository<Transfer, UUID>,
        PagingAndSortingRepository<Transfer, UUID> {

    Page<Transfer> findByFromUserEquals(String fromUser, Pageable pageable);

    Page<Transfer> findByToUserEquals(String toUser, Pageable pageable);

    Collection<Transfer> findByFromUserEqualsOrToUserEquals(String fromUser, String toUser);
}
