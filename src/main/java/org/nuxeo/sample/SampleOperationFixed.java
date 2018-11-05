package org.nuxeo.sample;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
@Operation(id=SampleOperationFixed.ID, category=Constants.CAT_DOCUMENT, label="SampleOperation (Fixed)", description="Describe here what your operation does.")
public class SampleOperationFixed {

    public static final String ID = "Document.SampleOperationFixed";

    private static final Log log = LogFactory.getLog(SampleOperationFixed.class);

    @Context
    protected CoreSession session;

    @Param(name = "path", required = false)
    protected String path;

    @OperationMethod
    public DocumentModel run() {
        if (StringUtils.isBlank(path)) {
            return session.getRootDocument();
        } else {
            DocumentModel doc = session.getDocument(new PathRef(path));
            String s = getExpirationDate(doc);
            doc.setPropertyValue("dc:description", s);
            session.save();
            return doc;
        }
    }

    public String getExpirationDate(DocumentModel doc) {
        String formattedDate = "";  
        DocumentModel parent = session.getParentDocument(doc.getRef());
        GregorianCalendar expirationDate = (GregorianCalendar) parent.getPropertyValue("dc:expired");
        GregorianCalendar cloneDate = (GregorianCalendar) expirationDate.clone();
        log.info(expirationDate);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
        if (cloneDate != null) {
            cloneDate.add(Calendar.DATE, 1);
            formattedDate = sdf.format(cloneDate.getTime());
            log.info("Formatted Expiration Date is " + formattedDate);
        }
        return formattedDate;
    }
}
