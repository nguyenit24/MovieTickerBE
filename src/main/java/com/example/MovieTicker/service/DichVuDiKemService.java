package com.example.MovieTicker.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.MovieTicker.entity.DichVuDiKem;
import com.example.MovieTicker.repository.DichVuDiKemRepository;

@Service
public class DichVuDiKemService {
    @Autowired
    private DichVuDiKemRepository repository;

    public List<DichVuDiKem> getAll() {
        return repository.findAll();
    }

    public Optional<DichVuDiKem> getById(Long id) {
        return repository.findById(id);
    }

    public DichVuDiKem create(DichVuDiKem dichVuDiKem) {
        return repository.save(dichVuDiKem);
    }

    public DichVuDiKem update(Long id, DichVuDiKem dichVuDiKem) {
        dichVuDiKem.setMaDv(id);
        return repository.save(dichVuDiKem);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<DichVuDiKem> getDichVuDiKemPageable(Pageable pageable) {
        return repository.findAll(pageable);
    }

}