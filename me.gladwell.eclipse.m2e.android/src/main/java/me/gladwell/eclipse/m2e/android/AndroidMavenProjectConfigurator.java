/*******************************************************************************
 * Copyright (c) 2009 - 2015 Ricardo Gladwell, Hugo Josefson, Csaba Kozák
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package me.gladwell.eclipse.m2e.android;

import java.util.List;

import me.gladwell.eclipse.m2e.android.configuration.DependencyNotFoundInWorkspace;
import me.gladwell.eclipse.m2e.android.configuration.classpath.ClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.classpath.RawClasspathConfigurer;
import me.gladwell.eclipse.m2e.android.configuration.workspace.WorkspaceConfigurer;
import me.gladwell.eclipse.m2e.android.project.EclipseAndroidProject;
import me.gladwell.eclipse.m2e.android.project.AndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProject;
import me.gladwell.eclipse.m2e.android.project.IDEAndroidProjectFactory;
import me.gladwell.eclipse.m2e.android.project.MavenAndroidProject;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.core.project.configurator.AbstractProjectConfigurator;
import org.eclipse.m2e.core.project.configurator.ProjectConfigurationRequest;
import org.eclipse.m2e.jdt.IClasspathDescriptor;
import org.eclipse.m2e.jdt.IJavaProjectConfigurator;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class AndroidMavenProjectConfigurator extends AbstractProjectConfigurator implements IJavaProjectConfigurator {

    private static final Version M2E_VERSION_NEW_CONFIGURATOR_ORDER = new Version(1, 6, 0);
    
    @Inject private AbstractProjectConfigurator javaProjectConfigurator;

    @Inject private List<WorkspaceConfigurer> workspaceConfigurers;

    @Inject private List<RawClasspathConfigurer> rawClasspathConfigurers;

    @Inject private List<ClasspathConfigurer> classpathConfigurers;

    @Inject private AndroidProjectFactory<MavenAndroidProject, MavenProject> mavenProjectFactory;

    @Inject private IDEAndroidProjectFactory eclipseProjectFactory;
    
    @Inject private IMavenProjectRegistry registry;

    public void configure(ProjectConfigurationRequest request, IProgressMonitor monitor) throws CoreException {
        markerManager.deleteMarkers(request.getPom(), AndroidMavenPlugin.APKLIB_ERROR_TYPE);

        try {
            final MavenAndroidProject mavenProject = mavenProjectFactory
                    .createAndroidProject(request.getMavenProject());
            final IDEAndroidProject eclipseProject = eclipseProjectFactory.createAndroidProject(request
                    .getProject());

            if (mavenProject.isAndroidProject()) {
                if (!usesM2E1_6OrNewer()) {
                    javaProjectConfigurator.configure(request, monitor);
                }

                for (WorkspaceConfigurer configurer : workspaceConfigurers) {
                    try {
                        if (configurer.isValid(mavenProject) && !configurer.isConfigured(eclipseProject)) {
                            configurer.configure(eclipseProject, mavenProject);
                        }
                    } catch (DependencyNotFoundInWorkspace e) {
                        markerManager.addErrorMarkers(request.getPom(), e.getType(), e);
                    }
                }
            }
        } catch (AndroidMavenException e) {
            throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID,
                    "error configuring project", e));
        }
    }

    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade f, MojoExecution e,
            IPluginExecutionMetadata m) {
        return null;
    }

    public void configureClasspath(IMavenProjectFacade facade, IClasspathDescriptor classpath, IProgressMonitor monitor)
            throws CoreException {
        final MavenAndroidProject mavenProject = mavenProjectFactory.createAndroidProject(facade.getMavenProject());
        final IDEAndroidProject eclipseProject = eclipseProjectFactory.createAndroidProject(facade.getProject(), classpath);
        try {
            for (RawClasspathConfigurer configurer : rawClasspathConfigurers) {
                configurer.configure(mavenProject, eclipseProject, classpath);
            }
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID,
                    "error configuring project classpath", e));
        }
    }

    public void configureRawClasspath(ProjectConfigurationRequest request, IClasspathDescriptor classpath,
            IProgressMonitor monitor) throws CoreException {
        final MavenAndroidProject mavenProject = mavenProjectFactory.createAndroidProject(request.getMavenProject());
        final IDEAndroidProject eclipseProject = eclipseProjectFactory.createAndroidProject(request.getProject(), classpath);
        try {
            for (ClasspathConfigurer classpathConfigurer : classpathConfigurers) {
                if (classpathConfigurer.shouldApplyTo(mavenProject)) {
                    classpathConfigurer.configure(mavenProject, eclipseProject);
                }
            }
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, AndroidMavenPlugin.PLUGIN_ID,
                    "error configuring project classpath", e));
        }
    }
    
    public static boolean usesM2E1_6OrNewer() {
        Bundle bundle = Platform.getBundle("org.eclipse.m2e.core");
        
        return bundle.getVersion().compareTo(M2E_VERSION_NEW_CONFIGURATOR_ORDER) >= 0;
    }

}
