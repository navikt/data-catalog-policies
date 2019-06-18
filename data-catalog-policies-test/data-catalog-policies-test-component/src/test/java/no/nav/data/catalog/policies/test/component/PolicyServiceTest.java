package no.nav.data.catalog.policies.test.component;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.ValidationException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.consumer.InformationTypeConsumer;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.InformationType;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.PolicyService;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
public class PolicyServiceTest {

    private static final String INFORMATIONTYPENAME = "Personalia";
    private static final String LEGALBASISDESCRIPTION = "LegalBasis";
    private static final String PURPOSECODE = "AAP";

    @Mock
    private CodelistConsumer codelistConsumer;

    @Mock
    private InformationTypeConsumer informationTypeConsumer;

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService service;

    @Test
    public void shouldValidateInsertRequest() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeConsumer.getInformationTypeByName(request.getInformationTypeName())).thenReturn(InformationType.builder().informationTypeId(1L).build());
        when(policyRepository.existsByInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        service.validateRequests(List.of(request));
    }

    @Test
    public void shouldThrowAllNullValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeConsumer.getInformationTypeByName(request.getInformationTypeName())).thenReturn(InformationType.builder().informationTypeId(1L).build());
        when(policyRepository.existsByInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).size(), is(3));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("informationTypeName"), is("informationTypeName cannot be null"));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("purposeCode"), is("purposeCode cannot be null"));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("legalBasisDescription"), is("legalBasisDescription cannot be null"));
        }
    }

    @Test
    public void shouldThrowNotFoundValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeConsumer.getInformationTypeByName(request.getInformationTypeName())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        when(policyRepository.existsByInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).size(), is(2));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("purposeCode"), is("The purposeCode AAP was not found in the PURPOSE codelist."));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("informationTypeName"), is("An informationType with name " + INFORMATIONTYPENAME + " does not exist"));
        }
    }

    @Test
    public void shouldThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeConsumer.getInformationTypeByName(request.getInformationTypeName())).thenReturn(InformationType.builder().informationTypeId(1L).build());
        when(policyRepository.existsByInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(true);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).size(), is(1));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("InformationTypeAndPurpose"), is("A policy combining InformationType Personalia and Purpose AAP already exists"));
        }
    }

    @Test
    public void shouldThrowAllNullValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeConsumer.getInformationTypeByName(request.getInformationTypeName())).thenReturn(InformationType.builder().informationTypeId(1L).build());
        when(policyRepository.existsByInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).size(), is(3));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("informationTypeName"), is("informationTypeName cannot be null"));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("purposeCode"), is("purposeCode cannot be null"));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("legalBasisDescription"), is("legalBasisDescription cannot be null"));
        }
    }

    @Test
    public void shouldThrowNotFoundValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeConsumer.getInformationTypeByName(request.getInformationTypeName())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        when(policyRepository.existsByInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).size(), is(2));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("purposeCode"), is("The purposeCode AAP was not found in the PURPOSE codelist."));
            assertThat(e.get().get(INFORMATIONTYPENAME + "/" + PURPOSECODE).get("informationTypeName"), is("An informationType with name " + INFORMATIONTYPENAME + " does not exist"));
        }
    }

    @Test
    public void shouldNOTThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .id(1L)
                .build();
        when(informationTypeConsumer.getInformationTypeByName(request.getInformationTypeName())).thenReturn(InformationType.builder().informationTypeId(1L).build());
        when(policyRepository.existsByInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(true);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        service.validateRequests(List.of(request));
    }
}
