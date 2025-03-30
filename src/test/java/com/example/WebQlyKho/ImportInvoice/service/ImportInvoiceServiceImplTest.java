package com.example.WebQlyKho.ImportInvoice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;

import java.time.LocalDate;

import com.example.WebQlyKho.dto.request.ImportDetailRequestDto;
import com.example.WebQlyKho.dto.request.ImportInvoiceRequestDto;
import com.example.WebQlyKho.entity.*;
import com.example.WebQlyKho.repository.*;
import com.example.WebQlyKho.service.impl.ImportInvoiceServiceImpl;
import com.example.WebQlyKho.utils.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportInvoiceServiceImplTest {

    @Mock
    private ImportInvoiceRepository importInvoiceRepository;

    @InjectMocks
    private ImportInvoiceServiceImpl importInvoiceService;

    private List<ImportInvoice> mockInvoices;

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private DskhohangRepository dskhohangRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImportDetailsRepository importDetailsRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;


    private ImportInvoiceRequestDto requestDto;
    private Supplier supplier;
    private Dskhohang dskhohang;
    private User user;

    @BeforeEach
    void setUp() {
        Supplier mockSupplier = new Supplier();
        mockSupplier.setSupplierId(1);
        mockSupplier.setSupplierName("Supplier A");

        ImportInvoice invoice1 = new ImportInvoice();
        invoice1.setInvoicesId(1);
        invoice1.setImportDate(LocalDate.of(2024, 3, 1));
        invoice1.setSupplier(mockSupplier);
        invoice1.setVat(5.5f);
        invoice1.setDiscount(10.2f);

        ImportInvoice invoice2 = new ImportInvoice();
        invoice2.setInvoicesId(2);
        invoice2.setImportDate(LocalDate.of(2024, 3, 2));
        invoice2.setSupplier(mockSupplier);
        invoice1.setVat(5.5f);
        invoice1.setDiscount(10.2f);

        mockInvoices = Arrays.asList(invoice1, invoice2);

        supplier = new Supplier();
        supplier.setSupplierId(1);
        supplier.setSupplierName("Supplier A");

        dskhohang = new Dskhohang();
        dskhohang.setKhohangId(1);
        dskhohang.setStatus(true);

        user = new User();
        user.setUserId(1);

        ImportDetailRequestDto detailRequest = new ImportDetailRequestDto();
        detailRequest.setProductId(1);
        detailRequest.setQuantity(10);
        detailRequest.setVat(5.0f);
        detailRequest.setDiscount(2.0f);
        detailRequest.setSubtotal(100.0);
        detailRequest.setTotalAmount(98.0f);

        requestDto = new ImportInvoiceRequestDto();
        requestDto.setSupplierId(1);
        requestDto.setKhohangId(1);
        requestDto.setTotalAmount(200.0f);
        requestDto.setFinalAmount(196.0f);
        requestDto.setVat(10.0f);
        requestDto.setDiscount(4.0f);
        requestDto.setImportDetails(Collections.singletonList(detailRequest));
    }

    @Test
    void createImportInvoice_ShouldCreateSuccessfully() {
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(new Product() {{ setProductId(1); }}));
        when(importInvoiceRepository.save(any(ImportInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportInvoice result = importInvoiceService.createImportInvoice(requestDto, request);

        assertNotNull(result);
        assertEquals(200.0f, result.getTotalAmount());
        verify(importInvoiceRepository).save(any(ImportInvoice.class));
    }


    @Test
    void createImportInvoice_ShouldThrowException_WhenSupplierNotFound() {
        when(supplierRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.createImportInvoice(requestDto, request));

        assertEquals("Không tìm thấy nhà cung cấp với id = 1", exception.getMessage());
    }

    @Test
    void updateImportInvoice_ShouldThrowException_WhenSupplierNotFound() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.updateImportInvoice(1, requestDto, request));

        assertEquals("Không tìm thấy nhà cung cấp với id = 1", exception.getMessage());
    }

    @Test
    void updateImportInvoice_ShouldUpdateSuccessfully() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(new Product() {{ setProductId(1); }}));
        when(importInvoiceRepository.save(any(ImportInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportInvoice updatedInvoice = importInvoiceService.updateImportInvoice(1, requestDto, request);

        assertNotNull(updatedInvoice);
        assertEquals(200.0f, updatedInvoice.getTotalAmount());
        verify(importInvoiceRepository).save(any(ImportInvoice.class));
    }

    @Test
    void createImportInvoice_ShouldThrowException_WhenProductNotFound() {
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.empty()); // Simulate product not found

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.createImportInvoice(requestDto, request));

        assertEquals("Không tìm thấy sản phẩm với id = 1", exception.getMessage());
    }

    @Test
    void updateImportInvoice_ShouldThrowException_WhenProductNotFound() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.empty()); // Simulate product not found

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.updateImportInvoice(1, requestDto, request));

        assertEquals("Không tìm thấy sản phẩm với id = 1", exception.getMessage());
    }

    @Test
    void createImportInvoice_ShouldSaveImportDetails() {
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(importInvoiceRepository.save(any(ImportInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(1)).thenReturn(Optional.of(new Product()));

        ImportInvoice result = importInvoiceService.createImportInvoice(requestDto, request);

        assertNotNull(result);
        assertEquals(200.0f, result.getTotalAmount());
        verify(importDetailsRepository, times(1)).save(any(ImportDetails.class));
    }

    @Test
    void updateImportInvoice_ShouldThrowException_WhenInvoiceNotFound() {
        when(importInvoiceRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.updateImportInvoice(1, requestDto, request));

        assertEquals("Invoice not found", exception.getMessage());
    }

    @Test
    void createImportInvoice_ShouldThrowException_WhenWarehouseNotFound() {
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.createImportInvoice(requestDto, request));

        assertEquals("Không tìm thấy kho hàng với id = 1", exception.getMessage());
    }

    @Test
    void updateImportInvoice_ShouldUpdateExistingImportDetails() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        ImportDetails existingDetail = new ImportDetails();
        existingDetail.setProduct(new Product() {{ setProductId(1); }});
        existingDetail.setQuantity(5);
        existingDetail.setVat(5.0f);
        existingDetail.setDiscount(2.0f);
        existingDetail.setSubtotal(50.0);
        existingDetail.setTotalAmount(48.0f);
        existingInvoice.getImportDetails().add(existingDetail);

        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(new Product() {{ setProductId(1); }}));
        when(importInvoiceRepository.save(any(ImportInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportInvoiceRequestDto updateRequestDto = new ImportInvoiceRequestDto();
        updateRequestDto.setSupplierId(1);
        updateRequestDto.setKhohangId(1);
        updateRequestDto.setTotalAmount(200.0f);
        updateRequestDto.setFinalAmount(196.0f);
        updateRequestDto.setVat(10.0f);
        updateRequestDto.setDiscount(4.0f);

        ImportDetailRequestDto detailRequest = new ImportDetailRequestDto();
        detailRequest.setProductId(1);
        detailRequest.setQuantity(10);
        detailRequest.setVat(5.0f);
        detailRequest.setDiscount(2.0f);
        detailRequest.setSubtotal(100.0);
        detailRequest.setTotalAmount(98.0f);
        updateRequestDto.setImportDetails(Collections.singletonList(detailRequest));

        ImportInvoice updatedInvoice = importInvoiceService.updateImportInvoice(1, updateRequestDto, request);

        assertNotNull(updatedInvoice);
        assertEquals(1, updatedInvoice.getImportDetails().size());
        ImportDetails updatedDetail = updatedInvoice.getImportDetails().getFirst();
        assertEquals(10, updatedDetail.getQuantity());
        assertEquals(5.0f, updatedDetail.getVat());
        assertEquals(2.0f, updatedDetail.getDiscount());
        assertEquals(100.0, updatedDetail.getSubtotal());
        assertEquals(98.0f, updatedDetail.getTotalAmount());
    }

    @Test
    void updateImportInvoice_ShouldCreateNewImportDetails() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(new Product() {{ setProductId(1); }}));
        when(importInvoiceRepository.save(any(ImportInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportInvoice updatedInvoice = importInvoiceService.updateImportInvoice(1, requestDto, request);

        assertNotNull(updatedInvoice);
        assertEquals(1, updatedInvoice.getImportDetails().size());
        ImportDetails newDetail = updatedInvoice.getImportDetails().getFirst();
        assertEquals(10, newDetail.getQuantity());
        assertEquals(5.0f, newDetail.getVat());
        assertEquals(2.0f, newDetail.getDiscount());
        assertEquals(100.0, newDetail.getSubtotal());
        assertEquals(98.0f, newDetail.getTotalAmount());
    }

    @Test
    void updateImportInvoice_ShouldDeleteRemovedImportDetails() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        ImportDetails existingDetail = new ImportDetails();
        existingDetail.setProduct(new Product() {{ setProductId(2); }});
        existingDetail.setQuantity(5);
        existingDetail.setVat(5.0f);
        existingDetail.setDiscount(2.0f);
        existingDetail.setSubtotal(50.0);
        existingDetail.setTotalAmount(48.0f);
        existingInvoice.getImportDetails().add(existingDetail);

        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(productRepository.findById(1)).thenReturn(Optional.of(new Product() {{ setProductId(1); }}));
        when(importInvoiceRepository.save(any(ImportInvoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ImportInvoice updatedInvoice = importInvoiceService.updateImportInvoice(1, requestDto, request);

        assertNotNull(updatedInvoice);
        assertEquals(1, updatedInvoice.getImportDetails().size());
        verify(importDetailsRepository, times(1)).delete(existingDetail);
    }

    @Test
    void createImportInvoice_ShouldThrowException_WhenWarehouseNotActive() {
        dskhohang.setStatus(false);
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> importInvoiceService.createImportInvoice(requestDto, request));

        assertEquals("Kho hàng với id = 1 không hoạt động", exception.getMessage());
    }

    @Test
    void updateImportInvoice_ShouldThrowException_WhenUserNotFound() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.updateImportInvoice(1, requestDto, request));

        assertEquals("Không tìm thấy user với id = 1", exception.getMessage());
    }

    @Test
    void updateImportInvoice_ShouldThrowException_WhenWarehouseNotFound() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.updateImportInvoice(1, requestDto, request));

        assertEquals("Không tìm thấy kho hàng với id = 1", exception.getMessage());
    }

    @Test
    void updateImportInvoice_ShouldThrowException_WhenWarehouseNotActive() {
        ImportInvoice existingInvoice = new ImportInvoice();
        existingInvoice.setInvoicesId(1);
        existingInvoice.setSupplier(supplier);
        existingInvoice.setDskhohang(dskhohang);
        existingInvoice.setImportDetails(new ArrayList<>());

        dskhohang.setStatus(false);
        when(importInvoiceRepository.findById(1)).thenReturn(Optional.of(existingInvoice));
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> importInvoiceService.updateImportInvoice(1, requestDto, request));

        assertEquals("Kho hàng với id = 1 không hoạt động", exception.getMessage());
    }

    @Test
    void createImportInvoice_ShouldThrowException_WhenUserNotFound() {
        when(supplierRepository.findById(1)).thenReturn(Optional.of(supplier));
        when(dskhohangRepository.findById(1)).thenReturn(Optional.of(dskhohang));
        when(jwtTokenProvider.getUserIdFromToken(request)).thenReturn(1);
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.createImportInvoice(requestDto, request));

        assertEquals("Không tìm thấy user với id = 1", exception.getMessage());
    }

    @Test
    void searchImportInvoices_ShouldApplyDateAndSupplierPredicates() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportInvoice> pageMock = new PageImpl<>(mockInvoices, pageable, mockInvoices.size());

        when(importInvoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageMock);

        String importDate = "2024-03-01";
        String fromDate = "2024-03-01";
        String toDate = "2024-03-02";
        Integer supplierId = 1;

        importInvoiceService.searchImportInvoices(supplierId, importDate, fromDate, toDate, 1, 10);

        // Capture the Specification used in the method
        ArgumentCaptor<Specification<ImportInvoice>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(importInvoiceRepository).findAll(specCaptor.capture(), eq(pageable));

        Specification<ImportInvoice> capturedSpec = specCaptor.getValue();
        assertNotNull(capturedSpec, "Captured specification should not be null");

        // Mock Criteria API objects
        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<?> mockQuery = mock(CriteriaQuery.class);
        Root<ImportInvoice> mockRoot = mock(Root.class);

        // Mock paths correctly
        Path<LocalDate> mockImportDatePath = mock(Path.class);
        Path mockSupplierPath = mock(Path.class);
        Path mockSupplierIdPath = mock(Path.class);

        // Fix: Ensure correct Path mock chaining
        when(mockRoot.<LocalDate>get("importDate")).thenReturn(mockImportDatePath);
        when(mockRoot.get("supplier")).thenReturn(mockSupplierPath);
        when(mockSupplierPath.get("supplierId")).thenReturn(mockSupplierIdPath);

        Predicate mockPredicate1 = mock(Predicate.class);
        Predicate mockPredicate2 = mock(Predicate.class);
        Predicate mockPredicate3 = mock(Predicate.class);
        Predicate combinedPredicate = mock(Predicate.class);

        // Stubbing: Ensure predicates are created
        when(mockCriteriaBuilder.equal(mockImportDatePath, LocalDate.parse(importDate))).thenReturn(mockPredicate1);
        when(mockCriteriaBuilder.between(mockImportDatePath, LocalDate.parse(fromDate), LocalDate.parse(toDate))).thenReturn(mockPredicate2);
        when(mockCriteriaBuilder.equal(mockSupplierIdPath, supplierId)).thenReturn(mockPredicate3);

        // Mock `criteriaBuilder.and()` to return a valid Predicate
        when(mockCriteriaBuilder.and(any(Predicate[].class))).thenReturn(combinedPredicate);

        // Validate Specification predicates
        Predicate resultPredicate = capturedSpec.toPredicate(mockRoot, mockQuery, mockCriteriaBuilder);
        assertNotNull(resultPredicate, "Generated predicate should not be null");
    }

    @Test
    void searchImportInvoices_ShouldApplyFromDatePredicate() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportInvoice> pageMock = new PageImpl<>(mockInvoices, pageable, mockInvoices.size());

        when(importInvoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageMock);

        String fromDate = "2024-03-01";

        importInvoiceService.searchImportInvoices(null, null, fromDate, null, 1, 10);

        // Capture Specification
        ArgumentCaptor<Specification<ImportInvoice>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(importInvoiceRepository).findAll(specCaptor.capture(), eq(pageable));

        Specification<ImportInvoice> capturedSpec = specCaptor.getValue();
        assertNotNull(capturedSpec, "Captured specification should not be null");

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<?> mockQuery = mock(CriteriaQuery.class);
        Root<ImportInvoice> mockRoot = mock(Root.class);

        // Mock Path<LocalDate> for importDate
        Path mockImportDatePath = mock(Path.class);
        when(mockRoot.get("importDate")).thenReturn(mockImportDatePath);

        // Mock Predicate creation
        Predicate mockPredicate = mock(Predicate.class);
        when(mockCriteriaBuilder.greaterThanOrEqualTo(mockImportDatePath, LocalDate.parse(fromDate)))
                .thenReturn(mockPredicate);

        Predicate resultPredicate = capturedSpec.toPredicate(mockRoot, mockQuery, mockCriteriaBuilder);

        assertEquals(null, resultPredicate, "Predicate for fromDate should be correct");
    }

    @Test
    void searchImportInvoices_ShouldApplyToDatePredicate() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportInvoice> pageMock = new PageImpl<>(mockInvoices, pageable, mockInvoices.size());

        when(importInvoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageMock);

        String toDate = "2024-03-02";

        importInvoiceService.searchImportInvoices(null, null, null, toDate, 1, 10);

        // Capture Specification
        ArgumentCaptor<Specification<ImportInvoice>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        verify(importInvoiceRepository).findAll(specCaptor.capture(), eq(pageable));

        Specification<ImportInvoice> capturedSpec = specCaptor.getValue();
        assertNotNull(capturedSpec, "Captured specification should not be null");

        CriteriaBuilder mockCriteriaBuilder = mock(CriteriaBuilder.class);
        CriteriaQuery<?> mockQuery = mock(CriteriaQuery.class);
        Root<ImportInvoice> mockRoot = mock(Root.class);

        // Mock Path<LocalDate> for importDate
        Path mockImportDatePath = mock(Path.class);
        when(mockRoot.get("importDate")).thenReturn(mockImportDatePath);

        // Mock Predicate creation
        Predicate mockPredicate = mock(Predicate.class);
        when(mockCriteriaBuilder.lessThanOrEqualTo(mockImportDatePath, LocalDate.parse(toDate)))
                .thenReturn(mockPredicate);

        Predicate resultPredicate = capturedSpec.toPredicate(mockRoot, mockQuery, mockCriteriaBuilder);

        assertEquals(null, resultPredicate, "Predicate for toDate should be correct");
    }

    @Test
    void searchImportInvoices_ShouldReturnPaginatedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportInvoice> pageMock = new PageImpl<>(mockInvoices, pageable, mockInvoices.size());

        when(importInvoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageMock);

        var result = importInvoiceService.searchImportInvoices(1, null, null, null, 1, 10);

        assertNotNull(result);
        assertEquals(2, ((List<?>) result.get("listImportInvoices")).size());
        assertEquals(1, result.get("totalPage"));
    }

    @Test
    void searchImportInvoices_ShouldApplyDateFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportInvoice> pageMock = new PageImpl<>(mockInvoices, pageable, mockInvoices.size());

        when(importInvoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageMock);

        var result = importInvoiceService.searchImportInvoices(1, null, "2024-03-01", "2024-03-02", 1, 10);

        assertNotNull(result);
        assertEquals(2, ((List<?>) result.get("listImportInvoices")).size());
        verify(importInvoiceRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchImportInvoices_ShouldNotDecrementPage_WhenPageIsZero() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ImportInvoice> pageMock = new PageImpl<>(mockInvoices, pageable, mockInvoices.size());

        when(importInvoiceRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageMock);

        importInvoiceService.searchImportInvoices(1, null, null, null, 0, 10);

        verify(importInvoiceRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void searchImportInvoices_ShouldReturnNull_WhenExceptionOccurs() {
        // Arrange: Invalid date format should throw an exception
        String invalidDate = "2024-02-30"; // Invalid date
        when(importInvoiceRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenThrow(new IllegalArgumentException("Invalid date format"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> importInvoiceService.searchImportInvoices(1, invalidDate, null, null, 1, 10));
    }

    @Test
    void testSearchImportInvoices_InvalidDate() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> importInvoiceService.searchImportInvoices(1, "invalid-date", null, null, 1, 10));

        assertEquals("Invalid date format: invalid-date. Please use yyyy-MM-dd", exception.getMessage());
    }

    @Test
    void getImportInvoiceById_ShouldReturnInvoice_WhenIdExists() {
        // Arrange
        Integer invoiceId = 1;
        ImportInvoice mockInvoice = new ImportInvoice();
        mockInvoice.setInvoicesId(invoiceId);

        when(importInvoiceRepository.findById(invoiceId)).thenReturn(Optional.of(mockInvoice));

        // Act
        ImportInvoice result = importInvoiceService.getImportInvoiceById(invoiceId);

        // Assert
        assertNotNull(result);
        assertEquals(invoiceId, result.getInvoicesId());
    }

    @Test
    void getImportInvoiceById_ShouldThrowException_WhenIdNotFound() {
        // Arrange
        Integer invoiceId = 999;
        when(importInvoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.getImportInvoiceById(invoiceId));

        assertEquals("Không tìm thấy hóa đơn nhập hàng với id = " + invoiceId, exception.getMessage());
    }

    @Test
    void deleteImportInvoice_ShouldUpdateStatus_WhenAllIdsExist() {
        // Arrange
        List<Integer> ids = List.of(1, 2, 3);
        List<ImportInvoice> mockInvoices = ids.stream().map(id -> {
            ImportInvoice invoice = new ImportInvoice();
            invoice.setInvoicesId(id);
            invoice.setStatus(true);
            return invoice;
        }).toList();

        when(importInvoiceRepository.findAllById(ids)).thenReturn(mockInvoices);
        // Act
        importInvoiceService.deleteImportInvoice(ids);

        // Assert
        verify(importInvoiceRepository, times(1)).saveAll(argThat(invoices -> {
            List<ImportInvoice> invoiceList = (List<ImportInvoice>) invoices;
            return invoiceList.stream().noneMatch(invoice -> invoice.getStatus()); // Kiểm tra tất cả status = false
        }));
    }

    @Test
    void deleteImportInvoice_ShouldThrowException_WhenSomeIdsNotFound() {
        // Arrange
        List<Integer> ids = List.of(1, 2, 3, 999);
        List<ImportInvoice> mockInvoices = List.of(
                new ImportInvoice() {{ setInvoicesId(1); }},
                new ImportInvoice() {{ setInvoicesId(2); }},
                new ImportInvoice() {{ setInvoicesId(3); }}
        );

        when(importInvoiceRepository.findAllById(ids)).thenReturn(mockInvoices);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> importInvoiceService.deleteImportInvoice(ids));

        assertTrue(exception.getMessage().contains("Import invoices not found for ids: [999]"));
    }

}
