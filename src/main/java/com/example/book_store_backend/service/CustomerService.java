package com.example.book_store_backend.service;

import com.example.book_store_backend.entity.Customer;
import com.example.book_store_backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Créer un nouveau client
     */
    public Customer createCustomer(Customer customer) {
        log.info("Création d'un nouveau client: {} {}", customer.getFirstName(), customer.getLastName());

        // Vérifier si l'email existe déjà
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un client avec cet email existe déjà");
        }

        // Vérifier si le numéro de téléphone existe déjà
        if (customerRepository.findByPhoneNumber(customer.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Un client avec ce numéro de téléphone existe déjà");
        }

        // Définir les valeurs par défaut
        if (customer.getIsActive() == null) {
            customer.setIsActive(true);
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Client créé avec succès. ID: {}", savedCustomer.getId());
        return savedCustomer;
    }

    /**
     * Mettre à jour un client existant
     */
    public Customer updateCustomer(Long id, Customer customerDetails) {
        log.info("Mise à jour du client avec ID: {}", id);

        Customer existingCustomer = getCustomerById(id);

        // Vérifier l'unicité de l'email lors de la mise à jour
        if (!existingCustomer.getEmail().equals(customerDetails.getEmail()) &&
                customerRepository.existsByEmailAndIdNot(customerDetails.getEmail(), id)) {
            throw new IllegalArgumentException("Un autre client avec cet email existe déjà");
        }

        // Vérifier l'unicité du numéro de téléphone lors de la mise à jour
        if (!existingCustomer.getPhoneNumber().equals(customerDetails.getPhoneNumber()) &&
                customerRepository.existsByPhoneNumberAndIdNot(customerDetails.getPhoneNumber(), id)) {
            throw new IllegalArgumentException("Un autre client avec ce numéro de téléphone existe déjà");
        }

        // Mettre à jour les champs
        existingCustomer.setFirstName(customerDetails.getFirstName());
        existingCustomer.setLastName(customerDetails.getLastName());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
        existingCustomer.setAddress(customerDetails.getAddress());
        existingCustomer.setCity(customerDetails.getCity());
        existingCustomer.setPostalCode(customerDetails.getPostalCode());
        existingCustomer.setCountry(customerDetails.getCountry());
        existingCustomer.setIsActive(customerDetails.getIsActive());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("Client mis à jour avec succès. ID: {}", updatedCustomer.getId());
        return updatedCustomer;
    }

    /**
     * Récupérer un client par ID
     */
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client non trouvé avec l'ID: " + id));
    }

    /**
     * Récupérer un client par email
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Récupérer un client par numéro de téléphone
     */
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber);
    }

    /**
     * Récupérer tous les clients actifs
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllActiveCustomers() {
        return customerRepository.findByIsActiveTrue();
    }

    /**
     * Récupérer tous les clients
     */
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Rechercher des clients
     */
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String keyword) {
        return customerRepository.searchCustomers(keyword);
    }

    /**
     * Rechercher des clients par nom
     */
    @Transactional(readOnly = true)
    public List<Customer> searchCustomersByName(String keyword) {
        return customerRepository.searchCustomersByName(keyword);
    }

    /**
     * Récupérer les clients par ville
     */
    @Transactional(readOnly = true)
    public List<Customer> getCustomersByCity(String city) {
        return customerRepository.findByCityAndIsActiveTrue(city);
    }

    /**
     * Récupérer les clients par pays
     */
    @Transactional(readOnly = true)
    public List<Customer> getCustomersByCountry(String country) {
        return customerRepository.findByCountryAndIsActiveTrue(country);
    }

    /**
     * Supprimer un client (suppression logique)
     */
    public void deleteCustomer(Long id) {
        log.info("Suppression du client avec ID: {}", id);
        Customer customer = getCustomerById(id);
        customer.setIsActive(false);
        customerRepository.save(customer);
        log.info("Client supprimé (désactivé) avec succès. ID: {}", id);
    }

    /**
     * Supprimer définitivement un client
     */
    public void permanentlyDeleteCustomer(Long id) {
        log.info("Suppression définitive du client avec ID: {}", id);
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Client non trouvé avec l'ID: " + id);
        }
        customerRepository.deleteById(id);
        log.info("Client supprimé définitivement avec succès. ID: {}", id);
    }

    /**
     * Activer/Désactiver un client
     */
    public Customer toggleCustomerStatus(Long id) {
        Customer customer = getCustomerById(id);
        customer.setIsActive(!customer.getIsActive());
        return customerRepository.save(customer);
    }

    /**
     * Récupérer les clients les plus actifs
     */
    @Transactional(readOnly = true)
    public List<Customer> getMostActiveCustomers() {
        return customerRepository.findMostActiveCustomers();
    }

    /**
     * Créer ou récupérer un client (utile pour les commandes)
     * Si un client avec l'email existe, le retourner, sinon créer un nouveau
     */
    public Customer createOrGetCustomer(Customer customerData) {
        Optional<Customer> existingCustomer = getCustomerByEmail(customerData.getEmail());

        if (existingCustomer.isPresent()) {
            log.info("Client existant trouvé avec l'email: {}", customerData.getEmail());
            return existingCustomer.get();
        } else {
            return createCustomer(customerData);
        }
    }

    /**
     * Vérifier si un client existe par email
     */
    @Transactional(readOnly = true)
    public boolean customerExistsByEmail(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    /**
     * Vérifier si un client existe par numéro de téléphone
     */
    @Transactional(readOnly = true)
    public boolean customerExistsByPhoneNumber(String phoneNumber) {
        return customerRepository.findByPhoneNumber(phoneNumber).isPresent();
    }

    /**
     * Obtenir des statistiques sur les clients
     */
    @Transactional(readOnly = true)
    public CustomerStatistics getCustomerStatistics() {
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.findByIsActiveTrue().size();
        List<Object[]> customersByCity = customerRepository.countCustomersByCity();

        return CustomerStatistics.builder()
                .totalCustomers(totalCustomers)
                .activeCustomers(activeCustomers)
                .inactiveCustomers(totalCustomers - activeCustomers)
                .customersByCity(customersByCity)
                .build();
    }

    // Classe d'aide pour les statistiques
    @lombok.Builder
    @lombok.Data
    public static class CustomerStatistics {
        private long totalCustomers;
        private long activeCustomers;
        private long inactiveCustomers;
        private List<Object[]> customersByCity;
    }
}