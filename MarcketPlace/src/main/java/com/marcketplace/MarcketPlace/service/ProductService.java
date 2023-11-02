package com.marcketplace.MarcketPlace.service;

import com.marcketplace.MarcketPlace.dto.request.ProductDTOReq;
import com.marcketplace.MarcketPlace.dto.response.ProductDTORes;
import com.marcketplace.MarcketPlace.exception.IdNotFoundException;
import com.marcketplace.MarcketPlace.exception.NameExistsException;
import com.marcketplace.MarcketPlace.model.Account;
import com.marcketplace.MarcketPlace.model.Category;
import com.marcketplace.MarcketPlace.model.Customers;
import com.marcketplace.MarcketPlace.model.Product;
import com.marcketplace.MarcketPlace.repository.AccountRepository;
import com.marcketplace.MarcketPlace.repository.CustomerRepository;
import com.marcketplace.MarcketPlace.repository.ICategoryRepository;
import com.marcketplace.MarcketPlace.repository.IProductRepository;
import com.marcketplace.MarcketPlace.util.IWordsConverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService{

    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ICategoryRepository categoryRepository;
    @Autowired
    private IWordsConverter wordsConverter;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Guarda un producto en base de datos
     * @param productDTO dto de producto
     */
    
    // @Override
    //  public void saveProduct(ProductDTOReq productDTO) throws IdNotFoundException {

    //     // Verifica si el vendedor existe antes de crear el producto
    //     String sellerEmail = productDTO.getSeller().getEmail();
    //     Customers seller = customerRepository.findByEmail(sellerEmail)
    //             .orElseThrow(() -> new IdNotFoundException("El vendedor con el email " + sellerEmail + " no se encuentra registrado"));

    //     // Convierte la primer letra de cada palabra en mayúscula
    //     productDTO.setName(wordsConverter.capitalizeWords(productDTO.getName()));

    //     // Crea el producto y asigna el vendedor
    //     Product product = modelMapper.map(productDTO, Product.class);
    //     product.setSeller(seller);

    //     productRepository.save(product);
    // }


    

    @Override
    public void saveProduct(ProductDTOReq productDTO) throws IdNotFoundException {
        if (customerRepository.findByEmail(productDTO.getSeller().getEmail()).isPresent()) {
            Customers seller = customerRepository.findByEmail(productDTO.getSeller().getEmail()).get();
    
            if (!categoryRepository.existsById(productDTO.getCategory().getId())) {
                throw new IdNotFoundException("La categoría ingresada no se encuentra registrada");
            }
    
            // Convierte la primera letra de cada palabra en mayúscula
            productDTO.setName(wordsConverter.capitalizeWords(productDTO.getName()));
    
            // Asocia el vendedor al producto
            Product product = modelMapper.map(productDTO, Product.class);
            product.setSeller(seller);
    
            // Guarda el producto en la base de datos
            productRepository.save(product);
        } else {
            throw new IdNotFoundException("El vendedor no se encuentra registrado");
        }
    }
    



    /**
     * Busca y devuelve un producto por id
     * @param productId numero de id de producto
     * @return dto de producto
     * @throws IdNotFoundException mensaje de excepcion de id de producto no encontrado
     */
    @Override
    public ProductDTORes getProductById(Long productId) throws IdNotFoundException {
        return modelMapper.map(productRepository.findById(productId)
                .orElseThrow(() -> new IdNotFoundException("El id " + productId + " no exite. Ingrese un nuevo id")), ProductDTORes.class);
    }

    /**
     * Devuelve una lista de productos paginados
     * @param pageable configuracion de paginacion
     * @return lista de productos paginados
     */
    @Override
    public Page<ProductDTORes> getAllProducts(Pageable pageable) {
        var productsDB = productRepository.findAll(pageable);
        var productsDTO = new ArrayList<ProductDTORes>();
        //recorre la lista de productos de la DB, los convierte a DTO y los guarda en una listaDTO
        for (Product product : productsDB) {
            productsDTO.add(modelMapper.map(product, ProductDTORes.class));
        }
        return new PageImpl<>(productsDTO, pageable, productsDB.getTotalElements());
    }

    /**
     * Actualiza un producto por id en base de datos
     * @param productDTO dto de producto
     * @throws IdNotFoundException mensaje de excepcion de id de produccto no encontrado
     * @throws NameExistsException mensaje de excepcion de nombre de producto ya exsiste
     */
    @Override
    public void updateProduct(ProductDTOReq productDTO) throws IdNotFoundException, NameExistsException {
        var productDB = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new IdNotFoundException("El id " + productDTO + " no existe. Ingrese un nuevo id"));
         if (customerRepository.findByEmail(productDTO.getSeller().getEmail()).isEmpty()){
            throw new IdNotFoundException("El vendedor ingresado no se encuentra registrado");
        }
        if (!categoryRepository.existsById(productDTO.getCategory().getId())){
            throw new IdNotFoundException("La categoria ingresada no se encuentra registrada");
        }
        //valida que el nombre del producto no exista y si existe que coincida con el producto encontrado
        if (!productDTO.getName().equals(productDB.getName()) && productRepository.existsByName(productDTO.getName())) {
            throw new NameExistsException("El nombre " + productDTO.getName() + " ya existe. Ingrese un nuevo nombre");
        }
        //convierte la primer letra de cada palabra en mayúscula
        productDTO.setName(wordsConverter.capitalizeWords(productDTO.getName()));
        productRepository.save(modelMapper.map(productDTO, Product.class));
    }

    /**
     * Elimina un producto de base de datos
     * @param productID numero de id de producto
     */
    @Override
    public void deleteProduct(Long productID) {
        productRepository.deleteById(productID);
    }




}
