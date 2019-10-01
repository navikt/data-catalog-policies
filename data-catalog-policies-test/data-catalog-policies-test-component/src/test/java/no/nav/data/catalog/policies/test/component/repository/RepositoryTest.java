package no.nav.data.catalog.policies.test.component.repository;

import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.test.component.PolicyTestContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AppStarter.class)
@ActiveProfiles("test")
class RepositoryTest {

    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "PUR1";
    private static final String DATASET_TITLE = "DatasetTitle1";
    private static final String DATASET_ID_1 = "cd7f037e-374e-4e68-b705-55b61966b2fc";
    private static final String DATASET_ID_2 = "5992e0d0-1fc9-4d67-b825-d198be0827bf";

    @Container
    static PolicyTestContainer postgreSQLContainer = PolicyTestContainer.getInstance();

    @Autowired
    private PolicyRepository policyRepository;

    @AfterEach
    void setUp() {
        policyRepository.deleteAll();
    }

    @Test
    void getOne() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_ID_1);
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    void getAll() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_ID_1);
        createTestdata("Legal basis 2", "PUR2", "5992e0d0-1fc9-4d67-b825-d198be0827bf");
        assertThat(policyRepository.count(), is(2L));
    }

    @Test
    void getByDataset() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_ID_1);
        createTestdata("Legal basis 2", "PUR2", DATASET_ID_2);
        assertThat(policyRepository.findByDatasetId(PageRequest.of(0, 10), DATASET_ID_1).getTotalElements(), is(1L));
        assertThat(policyRepository.findByDatasetId(PageRequest.of(0, 10), DATASET_ID_2).getTotalElements(), is(1L));
    }

    @Test
    void countByDataset() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_ID_1);
        createTestdata("Legal basis 2", "PUR2", DATASET_ID_2);
        assertThat(policyRepository.countByDatasetId(DATASET_ID_1), is(1L));
        assertThat(policyRepository.countByDatasetId(DATASET_ID_2), is(1L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, String datasetId) {
        Policy policy = new Policy();
        policy.setDatasetId(datasetId);
        policy.setPurposeCode(purposeCode);
        policy.setLegalBasisDescription(legalBasisDescription);
        policyRepository.save(policy);
    }
}
