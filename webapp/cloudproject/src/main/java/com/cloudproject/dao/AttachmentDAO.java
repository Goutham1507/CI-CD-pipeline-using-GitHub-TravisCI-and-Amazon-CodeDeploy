package com.cloudproject.dao;

import com.cloudproject.bean.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.UUID;

@Transactional
@Repository
public interface AttachmentDAO extends CrudRepository<Attachment, UUID> {

    public ArrayList<Attachment> findByTransactionId(UUID transactionId);

    public void deleteById(UUID attachmentid);

}
