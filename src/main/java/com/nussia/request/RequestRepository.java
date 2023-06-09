package com.nussia.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByUser_Id(Long user_id);

    Page<Request> findAllByUser_Id(Long user_id, Pageable pageable);

    List<Request> findByUser_IdNot(Long userId);

    Page<Request> findByUser_IdNot(Long userId, Pageable pageable);
}
