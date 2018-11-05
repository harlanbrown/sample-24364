package org.nuxeo.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

@RunWith(FeaturesRunner.class)
@Features(AutomationFeature.class)
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy("org.nuxeo.sample.sample-operation-core")
public class TestSampleOperation {

    @Inject
    protected CoreSession session;

    @Inject
    protected AutomationService automationService;

    @Before
    public void setUp() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2018);
        cal.set(Calendar.MONTH, 10); // 0-based
        cal.set(Calendar.DAY_OF_MONTH, 17);

        DocumentModel ws1 = session.createDocumentModel("/default-domain/workspaces", "ws1", "Workspace");
        ws1.setPropertyValue("dc:expired", cal);
        ws1 = session.createDocument(ws1);

        DocumentModel side1 = session.createDocumentModel("/default-domain/workspaces/ws1", "side1", "File");
        side1 = session.createDocument(side1);

        DocumentModel side2 = session.createDocumentModel("/default-domain/workspaces/ws1", "side2", "File");
        side2 = session.createDocument(side2);

        session.save();
    }

    @Test
    public void shouldCallWithFile() throws OperationException {
        final String path = "/default-domain/workspaces/ws1/side1";
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();
        params.put("path", path);

        DocumentModel doc = (DocumentModel) automationService.run(ctx, SampleOperation.ID, params);
        assertEquals("11/18/18", doc.getPropertyValue("dc:description"));

        DocumentModel doc2 = (DocumentModel) automationService.run(ctx, SampleOperation.ID, params);
        // rendered date will be 11/19 now
        assertNotEquals("11/18/18", doc2.getPropertyValue("dc:description"));

        Map<String, Object> params2 = new HashMap<>();
        params2.put("path", "/default-domain/workspaces/ws1/side2");

        DocumentModel side2 = (DocumentModel) automationService.run(ctx, SampleOperation.ID, params);
        // and here rendered date will be 11/20
        assertNotEquals("11/18/18", side2.getPropertyValue("dc:description"));

	}

    @Test
    public void shouldCallFixedOperation() throws OperationException {
        final String path = "/default-domain/workspaces/ws1/side1";
        OperationContext ctx = new OperationContext(session);
        Map<String, Object> params = new HashMap<>();
        params.put("path", path);

        DocumentModel doc = (DocumentModel) automationService.run(ctx, SampleOperationFixed.ID, params);
        assertEquals("11/18/18", doc.getPropertyValue("dc:description"));

        DocumentModel doc2 = (DocumentModel) automationService.run(ctx, SampleOperationFixed.ID, params);
        assertEquals("11/18/18", doc2.getPropertyValue("dc:description"));

        Map<String, Object> params2 = new HashMap<>();
        params2.put("path", "/default-domain/workspaces/ws1/side2");

        DocumentModel side2 = (DocumentModel) automationService.run(ctx, SampleOperationFixed.ID, params);
        assertEquals("11/18/18", side2.getPropertyValue("dc:description"));

	}

}
