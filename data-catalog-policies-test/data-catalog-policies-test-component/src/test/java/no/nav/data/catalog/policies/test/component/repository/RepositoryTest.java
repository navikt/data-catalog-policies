package no.nav.data.catalog.policies.test.component.repository;

import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import no.nav.data.catalog.policies.test.component.PolicyTestContainer;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
public class RepositoryTest {

    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "PUR1";
    private static final String DATASET_TITLE = "DatasetTitle1";
    private static final String DATASET_ID_1 = "cd7f037e-374e-4e68-b705-55b61966b2fc";
    private static final String DATASET_ID_2 = "5992e0d0-1fc9-4d67-b825-d198be0827bf";

    @ClassRule
    public static PolicyTestContainer postgreSQLContainer = PolicyTestContainer.getInstance();

    @Autowired
    private PolicyRepository policyRepository;

    @After
    public void setUp() {
        policyRepository.deleteAll();
    }

    @Test
    public void getOne() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_ID_1, DATASET_TITLE);
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void getAll() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_ID_1, DATASET_TITLE);
        createTestdata("Legal basis 2", "PUR2", "5992e0d0-1fc9-4d67-b825-d198be0827bf", "DatasetTitle2");
        assertThat(policyRepository.count(), is(2L));
    }

    @Test
    public void getByDataset() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_ID_1, DATASET_TITLE);
        createTestdata("Legal basis 2", "PUR2", DATASET_ID_2, "DatasetTitle2");
        assertThat(policyRepository.findByDatasetId(PageRequest.of(0, 10), DATASET_ID_1).getTotalElements(), is(1L));
        assertThat(policyRepository.findByDatasetId(PageRequest.of(0, 10), DATASET_ID_2).getTotalElements(), is(1L));
    }

    @Test
    public void countByDataset() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_ID_1, DATASET_TITLE);
        createTestdata("Legal basis 2", "PUR2", DATASET_ID_2, "DatasetTitle2");
        assertThat(policyRepository.countByDatasetId(DATASET_ID_1), is(1L));
        assertThat(policyRepository.countByDatasetId(DATASET_ID_2), is(1L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, String datasetId, String datasetTitle) {
        Dataset dataset = Dataset.builder().id(datasetId).title(datasetTitle).build();

        Policy policy = new Policy();
        policy.setDatasetId(dataset.getId());
        policy.setPurposeCode(purposeCode);
        policy.setLegalBasisDescription(legalBasisDescription);
        policyRepository.save(policy);
    }
}
