package no.nav.data.catalog.policies.test.component;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.ValidationException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.PolicyService;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
public class PolicyServiceTest {

    private static final String INFORMATIONTYPENAME = "Personalia";
    private static final String LEGALBASISDESCRIPTION = "LegalBasis";
    private static final String PURPOSECODE = "AAP";

    @Mock
    private CodelistConsumer consumer;

    @Mock
    private InformationTypeRepository informationTypeRepository;

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
        when(informationTypeRepository.findByName(request.getInformationTypeName())).thenReturn(Optional.of(InformationType.builder().informationTypeId(1L).build()));
        when(policyRepository.existsByInformationTypeInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        service.validateRequest(request, false);
    }

    @Test
    public void shouldThrowAllNullValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeRepository.findByName(request.getInformationTypeName())).thenReturn(Optional.of(InformationType.builder().informationTypeId(1L).build()));
        when(policyRepository.existsByInformationTypeInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        try {
            service.validateRequest(request, false);
        } catch (ValidationException e) {
            assertThat(e.get().size(), is(3));
            assertThat(e.get().get("informationTypeName"), is("informationTypeName cannot be null"));
            assertThat(e.get().get("purposeCode"), is("purposeCode cannot be null"));
            assertThat(e.get().get("legalBasisDescription"), is("legalBasisDescription cannot be null"));
        }
    }

    @Test
    public void shouldThrowNotFoundValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeRepository.findByName(request.getInformationTypeName())).thenReturn(Optional.ofNullable(null));
        when(policyRepository.existsByInformationTypeInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        when(consumer.getPurposeCodelistDescription(anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequest(request, false);
        } catch (ValidationException e) {
            assertThat(e.get().size(), is(2));
            assertThat(e.get().get("purposeCode"), is("The purposeCode AAP was not found in the PURPOSE codelist."));
            assertThat(e.get().get("informationTypeName"), is("An informationType with name " + INFORMATIONTYPENAME + " does not exist"));
        }
    }

    @Test
    public void shouldThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeRepository.findByName(request.getInformationTypeName())).thenReturn(Optional.of(InformationType.builder().informationTypeId(1L).build()));
        when(policyRepository.existsByInformationTypeInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(true);
        when(consumer.getPurposeCodelistDescription(anyString())).thenReturn("purpose");
        try {
            service.validateRequest(request, false);
        } catch (ValidationException e) {
            assertThat(e.get().size(), is(1));
            assertThat(e.get().get("InformationTypeAndPurpose"), is("A policy combining InformationType Personalia and Purpose AAP already exists"));
        }
    }

    @Test
    public void shouldThrowAllNullValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeRepository.findByName(request.getInformationTypeName())).thenReturn(Optional.of(InformationType.builder().informationTypeId(1L).build()));
        when(policyRepository.existsByInformationTypeInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        try {
            service.validateRequest(request, true);
        } catch (ValidationException e) {
            assertThat(e.get().size(), is(3));
            assertThat(e.get().get("informationTypeName"), is("informationTypeName cannot be null"));
            assertThat(e.get().get("purposeCode"), is("purposeCode cannot be null"));
            assertThat(e.get().get("legalBasisDescription"), is("legalBasisDescription cannot be null"));
        }
    }

    @Test
    public void shouldThrowNotFoundValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeRepository.findByName(request.getInformationTypeName())).thenReturn(Optional.ofNullable(null));
        when(policyRepository.existsByInformationTypeInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(false);
        when(consumer.getPurposeCodelistDescription(anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequest(request, true);
        } catch (ValidationException e) {
            assertThat(e.get().size(), is(2));
            assertThat(e.get().get("purposeCode"), is("The purposeCode AAP was not found in the PURPOSE codelist."));
            assertThat(e.get().get("informationTypeName"), is("An informationType with name " + INFORMATIONTYPENAME + " does not exist"));
        }
    }

    @Test
    public void shouldNOTThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .informationTypeName(INFORMATIONTYPENAME)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(informationTypeRepository.findByName(request.getInformationTypeName())).thenReturn(Optional.of(InformationType.builder().informationTypeId(1L).build()));
        when(policyRepository.existsByInformationTypeInformationTypeIdAndPurposeCode(anyLong(), anyString())).thenReturn(true);
        when(consumer.getPurposeCodelistDescription(anyString())).thenReturn("purpose");
        service.validateRequest(request, true);
    }
}
