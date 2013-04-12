// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.oozie.scheduler.jobsubmission;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.oozie.client.OozieClient;
import org.apache.oozie.client.OozieClientException;
import org.talend.commons.ui.runtime.exception.ExceptionHandler;
import org.talend.core.utils.TalendQuoteUtils;
import org.talend.designer.hdfsbrowse.exceptions.HadoopReflectionException;
import org.talend.designer.hdfsbrowse.reflection.HadoopClassConstants;
import org.talend.designer.hdfsbrowse.reflection.HadoopReflection;
import org.talend.oozie.scheduler.jobsubmission.model.JobContext;
import org.talend.oozie.scheduler.jobsubmission.model.JobSubmission;
import org.talend.oozie.scheduler.jobsubmission.model.JobSubmissionException;
import org.talend.oozie.scheduler.jobsubmission.model.Utils;
import org.talend.oozie.scheduler.utils.OozieClassLoaderFactory;
import org.talend.utils.json.JSONArray;
import org.talend.utils.json.JSONException;
import org.talend.utils.json.JSONObject;

public abstract class AbstractOozieJobSubmission implements JobSubmission {

    @Override
    public String resubmit(String jobHandle, JobContext jobContext) throws JobSubmissionException, InterruptedException,
            URISyntaxException {
        kill(jobHandle, jobContext.getOozieEndPoint());
        return submit(jobContext);
    }

    @Override
    public void kill(String jobHandle, String oozieEndPoint) throws JobSubmissionException {
        OozieClient oozieClient = createOozieClient(oozieEndPoint, 0);
        try {
            oozieClient.kill(jobHandle);
        } catch (OozieClientException e) {
            throw new JobSubmissionException("Error killing job: " + jobHandle, e);
        }
    }

    @Override
    public String getJobLog(String jobHandle, String oozieEndPoint) throws JobSubmissionException {
        try {
            return createOozieClient(oozieEndPoint, 0).getJobLog(jobHandle);
        } catch (OozieClientException e) {
            throw new JobSubmissionException("Error fetching job job for: " + jobHandle, e);
        }
    }

    protected OozieClient createOozieClient(String oozieEndPoint, int debug) {
        OozieClient oozieClient = new OozieClient(oozieEndPoint);
        oozieClient.setDebugMode(debug);

        return oozieClient;
    }

    protected void createWorkflowTemplate(JobContext jobContext) throws IOException, InterruptedException, URISyntaxException {
        Workflow workflow = createWorkflow(jobContext);
        serializeToHDFS(workflow.toXMLString(), "/workflow.xml", jobContext);//$NON-NLS-1$
    }

    protected Workflow createWorkflow(JobContext jobContext) {
        JavaAction action = new JavaAction(jobContext.getJobName(), jobContext.getJobTrackerEndPoint(),
                jobContext.getNameNodeEndPoint(), jobContext.getJobFQClassName());

        action.addArgument("-fs " + jobContext.get("NAMENODE")); //$NON-NLS-1$ //$NON-NLS-2$
        action.addArgument("-jt " + jobContext.get("JOBTRACKER"));//$NON-NLS-1$ //$NON-NLS-2$

        if (jobContext.get("KERBEROS.PRINCIPAL") != null) {//$NON-NLS-1$
            action.addArgument("-D dfs.namenode.kerberos.principal=" + jobContext.get("KERBEROS.PRINCIPAL"));//$NON-NLS-1$ //$NON-NLS-1$
        }
        String jsontest = jobContext.get("HADOOP.PROPERTIES");
        if (jsontest != null) {
            try {
                JSONArray props = new JSONArray(jsontest);
                for (int i = 0; i < props.length(); i++) {
                    String property = TalendQuoteUtils.removeQuotesIfExist((String) ((JSONObject) props.get(i)).get("PROPERTY"));//$NON-NLS-1$
                    String value = TalendQuoteUtils.removeQuotesIfExist((String) ((JSONObject) props.get(i)).get("VALUE"));//$NON-NLS-1$
                    if (!StringUtils.isEmpty(property) && !StringUtils.isEmpty(value)) {
                        action.addArgument("-D " + property + "=" + value);//$NON-NLS-1$ //$NON-NLS-1$
                    }
                }
            } catch (JSONException e) {
                ExceptionHandler.process(e);
            }
        }

        // This directory is just for DistributedCache. Maybe later it need to enhance, because for common java
        // application, it also add this argument.
        action.addArgument("--mr_libs_dir=" + jobContext.get(OozieClient.APP_PATH) + "/lib/");//$NON-NLS-1$ //$NON-NLS-2$

        String tosContextPath = jobContext.getTosContextPath();
        if (tosContextPath != null) {
            action.addArgument("--context=" + tosContextPath);//$NON-NLS-1$
        }
        return new Workflow(jobContext.getJobName(), action);
    }

    protected void createCoordinatorTemplate(JobContext jobContext) throws IOException, InterruptedException, URISyntaxException {
        Coordinator coordinator = createCoordinator(jobContext);
        serializeToHDFS(coordinator.toXMLString(), "/coordinator.xml", jobContext);
    }

    protected Coordinator createCoordinator(JobContext jobContext) {
        Utils.assertTrue(jobContext.getFrequency() > 0, "Frequency has to be greater than 0.");

        return new Coordinator(jobContext.getJobName(), jobContext.getNameNodeEndPoint() + jobContext.getJobPathOnHDFS(),
                jobContext.getStartTime(), jobContext.getEndTime(), jobContext.getFrequency(), jobContext.getTimeUnit());
    }

    protected void serializeToHDFS(String toSerialize, String asFile, JobContext jobContext) throws IOException,
            InterruptedException, URISyntaxException {
        Object fs = null;
        ClassLoader classLoader = OozieClassLoaderFactory.getClassLoader();
        try {
            Object configuration = HadoopReflection.newInstance(HadoopClassConstants.CONFIGURATION, classLoader);
            HadoopReflection.invokeMethod(configuration, "set",
                    new Object[] { "fs.default.name", jobContext.getNameNodeEndPoint() });
            // configuration.set("fs.default.name"FileSystem.FS_DEFAULT_NAME_KEY, jobContext.getNameNodeEndPoint());
            // FileSystem fs = FileSystem.get(configuration);

            String userName = jobContext.get(OozieClient.USER_NAME);
            String appPath = jobContext.get(OozieClient.APP_PATH);
            if (userName != null && !"".equals(userName)) {
                String nnUri = (String) HadoopReflection.invokeMethod(configuration, "get", new Object[] { "fs.default.name" });
                fs = HadoopReflection.invokeStaticMethod(HadoopClassConstants.FILESYSTEM, "get", new Object[] {
                        new java.net.URI(nnUri), configuration, userName }, classLoader);
            } else {
                fs = HadoopReflection.invokeStaticMethod(HadoopClassConstants.FILESYSTEM, "get", new Object[] { configuration },
                        classLoader);
            }

            Object wfFile = HadoopReflection.newInstance(HadoopClassConstants.PATH, new Object[] { appPath + asFile },
                    classLoader);
            Object outputStream = null;
            try {
                if ((Boolean) HadoopReflection.invokeMethod(fs, "exists", new Object[] { wfFile })) {
                    if ((Boolean) HadoopReflection.invokeMethod(fs, "delete", new Object[] { wfFile })) {
                        outputStream = HadoopReflection.invokeMethod(fs, "create", new Object[] { wfFile });
                        HadoopReflection.invokeMethod(outputStream, "writeBytes", new Object[] { toSerialize });
                    } else {

                    }
                } else {
                    outputStream = HadoopReflection.invokeMethod(fs, "create", new Object[] { wfFile });
                    HadoopReflection.invokeMethod(outputStream, "writeBytes", new Object[] { toSerialize });
                }
                // outputStream = fs.create(wfFile);
                // outputStream.writeBytes(toSerialize);
            } finally {
                if (outputStream != null) {
                    HadoopReflection.invokeMethod(outputStream, "close");
                }
            }
        } catch (HadoopReflectionException e) {
            ExceptionHandler.process(e);
        }
    }
}
