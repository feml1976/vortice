package com.transer.vortice.tire.application.service;

import com.transer.vortice.tire.application.dto.response.TireBrandResponse;
import com.transer.vortice.tire.application.dto.response.TireReferenceResponse;
import com.transer.vortice.tire.application.dto.response.TireSupplierResponse;
import com.transer.vortice.tire.application.dto.response.TireTypeResponse;
import com.transer.vortice.tire.application.mapper.TireSpecificationMapper;
import com.transer.vortice.tire.domain.repository.TireBrandRepository;
import com.transer.vortice.tire.domain.repository.TireReferenceRepository;
import com.transer.vortice.tire.domain.repository.TireSupplierRepository;
import com.transer.vortice.tire.domain.repository.TireTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de catálogos del módulo de llantas
 *
 * Proporciona acceso a los catálogos base (marcas, tipos, referencias, proveedores)
 * para ser utilizados en formularios y filtros.
 *
 * @author Vórtice Development Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TireCatalogService {

    private final TireBrandRepository tireBrandRepository;
    private final TireTypeRepository tireTypeRepository;
    private final TireReferenceRepository tireReferenceRepository;
    private final TireSupplierRepository tireSupplierRepository;
    private final TireSpecificationMapper mapper;

    // =====================================================
    // MARCAS DE LLANTAS
    // =====================================================

    /**
     * Obtiene todas las marcas activas
     *
     * @return lista de marcas activas
     */
    @Transactional(readOnly = true)
    public List<TireBrandResponse> getAllActiveBrands() {
        log.info("Obteniendo todas las marcas activas");

        return tireBrandRepository.findAllActive().stream()
                .map(mapper::toBrandResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las marcas (activas e inactivas)
     *
     * @return lista de todas las marcas
     */
    @Transactional(readOnly = true)
    public List<TireBrandResponse> getAllBrands() {
        log.info("Obteniendo todas las marcas");

        return tireBrandRepository.findAll().stream()
                .map(mapper::toBrandResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // TIPOS DE LLANTAS
    // =====================================================

    /**
     * Obtiene todos los tipos activos
     *
     * @return lista de tipos activos
     */
    @Transactional(readOnly = true)
    public List<TireTypeResponse> getAllActiveTypes() {
        log.info("Obteniendo todos los tipos activos");

        return tireTypeRepository.findAllActive().stream()
                .map(mapper::toTypeResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los tipos (activos e inactivos)
     *
     * @return lista de todos los tipos
     */
    @Transactional(readOnly = true)
    public List<TireTypeResponse> getAllTypes() {
        log.info("Obteniendo todos los tipos");

        return tireTypeRepository.findAll().stream()
                .map(mapper::toTypeResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // REFERENCIAS DE LLANTAS
    // =====================================================

    /**
     * Obtiene todas las referencias activas
     *
     * @return lista de referencias activas
     */
    @Transactional(readOnly = true)
    public List<TireReferenceResponse> getAllActiveReferences() {
        log.info("Obteniendo todas las referencias activas");

        return tireReferenceRepository.findAllActive().stream()
                .map(mapper::toReferenceResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las referencias (activas e inactivas)
     *
     * @return lista de todas las referencias
     */
    @Transactional(readOnly = true)
    public List<TireReferenceResponse> getAllReferences() {
        log.info("Obteniendo todas las referencias");

        return tireReferenceRepository.findAll().stream()
                .map(mapper::toReferenceResponse)
                .collect(Collectors.toList());
    }

    // =====================================================
    // PROVEEDORES DE LLANTAS
    // =====================================================

    /**
     * Obtiene todos los proveedores activos
     *
     * @return lista de proveedores activos
     */
    @Transactional(readOnly = true)
    public List<TireSupplierResponse> getAllActiveSuppliers() {
        log.info("Obteniendo todos los proveedores activos");

        return tireSupplierRepository.findAllActive().stream()
                .map(mapper::toSupplierResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los proveedores (activos e inactivos)
     *
     * @return lista de todos los proveedores
     */
    @Transactional(readOnly = true)
    public List<TireSupplierResponse> getAllSuppliers() {
        log.info("Obteniendo todos los proveedores");

        return tireSupplierRepository.findAll().stream()
                .map(mapper::toSupplierResponse)
                .collect(Collectors.toList());
    }
}
