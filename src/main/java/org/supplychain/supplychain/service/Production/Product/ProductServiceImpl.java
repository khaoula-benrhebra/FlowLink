package org.supplychain.supplychain.service.Production.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.supplychain.supplychain.dto.Production.BillOfMaterialDTO;
import org.supplychain.supplychain.dto.Production.ProductDTO;
import org.supplychain.supplychain.exception.DuplicateResourceException;
import org.supplychain.supplychain.exception.ResourceInUseException;
import org.supplychain.supplychain.exception.ResourceNotFoundException;
import org.supplychain.supplychain.mapper.Production.ProductMapper;
import org.supplychain.supplychain.model.BillOfMaterial;
import org.supplychain.supplychain.model.Product;
import org.supplychain.supplychain.model.RawMaterial;
import org.supplychain.supplychain.repository.Production.BillOfMaterialRepository;
import org.supplychain.supplychain.repository.Production.ProductRepository;
import org.supplychain.supplychain.repository.Production.ProductionOrderRepository;
import org.supplychain.supplychain.repository.approvisionnement.RawMaterialRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final BillOfMaterialRepository bomRepository;
    private final RawMaterialRepository rawMaterialRepository;
    private final ProductionOrderRepository productionOrderRepository;

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        // Vérifier si un produit avec le même nom existe déjà
        productRepository.findByName(productDTO.getName())
                .ifPresent(existingProduct -> {
                    throw new DuplicateResourceException("Un produit avec le nom '" + productDTO.getName() + "' existe déjà");
                });

        // Vérifier que le BOM est fourni
        if (productDTO.getBillOfMaterials() == null || productDTO.getBillOfMaterials().isEmpty()) {
            throw new IllegalArgumentException("Le Bill of Materials (BOM) est obligatoire lors de la création d'un produit");
        }

        // Créer le produit (sans toucher au stock)
        Product product = productMapper.toEntity(productDTO);
        Product savedProduct = productRepository.save(product);

        // Créer les entrées BOM
        List<BillOfMaterial> bomList = new ArrayList<>();
        for (BillOfMaterialDTO bomDTO : productDTO.getBillOfMaterials()) {
            // Vérifier que la matière première existe
            RawMaterial rawMaterial = rawMaterialRepository.findById(bomDTO.getMaterialId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Matière première non trouvée avec l'ID : " + bomDTO.getMaterialId()));

            // Créer l'entrée BOM
            BillOfMaterial bom = new BillOfMaterial();
            bom.setProduct(savedProduct);
            bom.setMaterial(rawMaterial);
            bom.setQuantity(bomDTO.getQuantity());

            bomList.add(bom);
        }

        // Sauvegarder le BOM
        bomRepository.saveAll(bomList);
        savedProduct.setBillOfMaterials(bomList);

        return productMapper.toDTO(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        // Vérifier l'unicité du nom
        if (productRepository.existsByNameAndIdProductNot(productDTO.getName(), id)) {
            throw new DuplicateResourceException("Un autre produit avec le nom '" + productDTO.getName() + "' existe déjà");
        }

        // Mettre à jour les informations du produit (sans le BOM)
        productMapper.updateEntityFromDTO(productDTO, existingProduct);

        // Mettre à jour le BOM si fourni
        if (productDTO.getBillOfMaterials() != null && !productDTO.getBillOfMaterials().isEmpty()) {
            // Supprimer l'ancien BOM
            bomRepository.deleteByProduct_IdProduct(id);
            bomRepository.flush();

            // Créer le nouveau BOM
            List<BillOfMaterial> bomList = new ArrayList<>();
            for (BillOfMaterialDTO bomDTO : productDTO.getBillOfMaterials()) {
                // Vérifier que la matière première existe
                RawMaterial rawMaterial = rawMaterialRepository.findById(bomDTO.getMaterialId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Matière première non trouvée avec l'ID : " + bomDTO.getMaterialId()));

                // Créer l'entrée BOM
                BillOfMaterial bom = new BillOfMaterial();
                bom.setProduct(existingProduct);
                bom.setMaterial(rawMaterial);
                bom.setQuantity(bomDTO.getQuantity());

                bomList.add(bom);
            }

            // Sauvegarder le nouveau BOM
            bomRepository.saveAll(bomList);
            existingProduct.setBillOfMaterials(bomList);
        }

        Product updatedProduct = productRepository.save(existingProduct);

        return productMapper.toDTO(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        // Vérifier s'il existe des ordres de production associés
        if (productionOrderRepository.existsByProduct_IdProduct(id)) {
            throw new ResourceInUseException(
                    "Impossible de supprimer le produit. Il existe un ou plusieurs ordres de production associés à ce produit."
            );
        }

        // Supprimer le produit (le BOM sera supprimé automatiquement en cascade)
        productRepository.delete(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID : " + id));

        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductsByName(String name, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(productMapper::toDTO);
    }
}