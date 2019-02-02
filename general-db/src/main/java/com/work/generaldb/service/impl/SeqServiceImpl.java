package com.work.generaldb.service.impl;

import com.work.generaldb.mapper.SequenceMapper;
import com.work.generaldb.service.SeqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeqServiceImpl implements SeqService {
    @Autowired
    SequenceMapper sequenceMapper;

    @Override
    public String getSeqNextVal(String seqName) {
        return sequenceMapper.getSeqNextVal(seqName);
    }
}